/*
    Created By : iamsubhranil
    Date : 19/1/17
    Time : 1:22 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import com.iamsubhranil.player.core.Artist;
import com.iamsubhranil.player.core.ContentManager;
import com.iamsubhranil.player.db.Environment;
import com.iamsubhranil.player.db.SearchScope;
import com.iamsubhranil.player.ui.ArtPuller;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Preparation {

    private static final File directoryToSongStore = Environment.getDirectoryToSongStore();
    private static final File directoryToArtistArtStore = Environment.getDirectoryToArtistArtStore();
    private static final Tika tika = new Tika();
    private static final Mp3Parser parser = new Mp3Parser();

    public static void main(String[] args) {
        createSongsIndex();
        //        load();
    }

    public static IndexReader getSongIndex() throws IOException {
        return DirectoryReader.open(FSDirectory.open(directoryToSongStore.toPath()));
    }

    public static IndexReader getArtistArtIndex() throws IOException {
        return DirectoryReader.open(FSDirectory.open(directoryToArtistArtStore.toPath()));
    }

    public static boolean createArtistArtIndex() {
        ArrayList<Artist> artistArrayList = ContentManager.getArtistArrayList();
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
        System.out.println("Retrieving artist images..");
        final boolean[] hasSucceed = {true};
        try {
            Directory picIndex = FSDirectory.open(directoryToArtistArtStore.toPath());
            IndexWriter iw = new IndexWriter(picIndex, config);
            artistArrayList.forEach(artist -> {
                // artist.setImageURL(ArtPuller.getImageURLForArtist(artist.getName()));
                // artist.applyImageToPane();
                System.out.println("Artist : " + artist.getName() + "\nHash : " + artist.getArtistHash());
                String url = ArtPuller.getImageURLForArtist(artist.getName());
                System.out.println("Artwork pulled from " + url);
                ByteArrayOutputStream s = new ByteArrayOutputStream();
                try {
                    System.out.println("Writing image..");
                    BufferedImage image = ImageIO.read(new URL(url));
                    ImageIO.write(image, "png", s);
                    System.out.println("Creating byte array..");
                    byte[] res = s.toByteArray();
                    Document document = new Document();
                    StringField field = new StringField("ArtistHash", artist.getArtistHash(), Field.Store.YES);
                    StoredField field1 = new StoredField("Image", new BytesRef(res));
                    document.add(field);
                    document.add(field1);
                    System.out.println("Adding document..\n");
                    iw.addDocument(document);
                    System.gc();
                } catch (MalformedURLException mue) {
                    System.out.println("URl : " + url);
                    hasSucceed[0] = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    hasSucceed[0] = false;
                }
            });
            System.out.println("Writing index..");
            iw.commit();
            picIndex.close();
            System.out.println("Done..");
        } catch (IOException e) {
            hasSucceed[0] = false;
            e.printStackTrace();
        }
        return hasSucceed[0];
    }

    public static boolean createSongsIndex() {

        if (SearchScope.getSearchScopes().isEmpty())
            return false;

        boolean hasSucceed;

        System.out.println("Starting index..");

        StandardAnalyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w;

        try {
            Directory index = FSDirectory.open(directoryToSongStore.toPath());
            w = new IndexWriter(index, config);
            System.out.println("Analyzing directory..");
            SearchScope.getSearchScopes().forEach(scope -> {
                try {
                    indexFolder(scope, w);
                } catch (IOException | TikaException | SAXException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Writing out..");
            w.commit();
            System.out.println("Closing index..");
            w.close();
            index.close();
            System.out.println("Done..");
            hasSucceed = true;
        } catch (IOException e) {
            e.printStackTrace();
            hasSucceed = false;
        }
        return hasSucceed;
    }

    private static void indexFolder(File f, IndexWriter indexWriter) throws IOException, TikaException, SAXException {
        String[] files = f.list();
        for (String file : files) {
            File fl = new File(f + "/" + file);
            if (fl.isDirectory()) {
                indexFolder(fl, indexWriter);
            } else {
                if (tika.detect(fl).startsWith("audio/mpeg")) {
                    BodyContentHandler handler = new BodyContentHandler();
                    Metadata metadata = new Metadata();
                    ParseContext context = new ParseContext();
                    parser.parse(new FileInputStream(fl), handler, metadata, context);

                    Document document = new Document();
                    TextField field = new TextField("path", fl.getAbsolutePath(), Field.Store.YES);
                    document.add(field);
                    String totalMeta = "Path\t" + fl.getAbsolutePath() + "\n";
                    for (String name : metadata.names()) {
                        String val = metadata.get(name);
                        name = name.contains(":") ? name.split(":")[1] : name;
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        if (val == null || val.equals("")) {
                            val = "Unknown";
                        }
                        totalMeta += name + "\t" + val + "\n";
                        TextField metaField = new TextField(name, val, Field.Store.YES);
                        document.add(metaField);
                        if (name.equals("Album") || name.equals("Artist")) {
                            StringField field2 = new StringField(name + "Hash", generateSHAString(val), Field.Store.YES);
                            document.add(field2);
                        }
                    }
                    StringField hashField = new StringField("SongHash", generateSHAString(totalMeta), Field.Store.YES);
                    document.add(hashField);
                    indexWriter.addDocument(document);
                }
            }
        }
    }

    private static String generateSHAString(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return DatatypeConverter.printHexBinary(hash); // make it printable
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
