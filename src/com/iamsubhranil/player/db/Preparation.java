/*
    Created By : iamsubhranil
    Date : 19/1/17
    Time : 1:22 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.db;

import com.iamsubhranil.player.core.Album;
import com.iamsubhranil.player.core.ArtPuller;
import com.iamsubhranil.player.core.Artist;
import com.iamsubhranil.player.core.Bundle;
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
    private static final File directoryToAlbumArtStore = Environment.getDirectoryToAlbumArtStore();
    private static final Tika tika = new Tika();
    private static final Mp3Parser parser = new Mp3Parser();

    public static void main(String[] args) {
        SearchScope.loadSearchScopes();
        createSongsIndex();
        //        load();
    }

    public static IndexReader getSongIndex() throws IOException {
        return DirectoryReader.open(FSDirectory.open(directoryToSongStore.toPath()));
    }

    public static IndexReader getArtistArtIndex() throws IOException {
        return DirectoryReader.open(FSDirectory.open(directoryToArtistArtStore.toPath()));
    }

    public static IndexReader getAlbumArtIndex() throws IOException {
        return DirectoryReader.open(FSDirectory.open(directoryToAlbumArtStore.toPath()));
    }

    /*
        This method creates artist art index for the artists specified by the argument.

     */
    public static boolean createArtistArtIndex(ArrayList<Artist> artistArrayList) {
        //Create lucene configs
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
        System.out.println("Retrieving artist images..");
        //Flag to check if the index creation is successful
        final boolean[] hasSucceed = {true};
        try {
            //Open directory for index
            Directory picIndex = FSDirectory.open(directoryToArtistArtStore.toPath());
            //Open writer
            IndexWriter iw = new IndexWriter(picIndex, config);
            //Apply for each artist
            artistArrayList.forEach(artist -> {
                hasSucceed[0] = pullImageForBundle(artist, iw, "");
            });
            System.out.println("Writing index..");
            //Write out and close the index
            iw.commit();
            iw.close();
            picIndex.close();
            System.out.println("Done..");
        } catch (IOException e) {
            hasSucceed[0] = false;
            e.printStackTrace();
        }
        return hasSucceed[0];
    }

    public static boolean createAlbumArtIndex(ArrayList<Album> albums) {
        //Create lucene configs
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
        System.out.println("Retrieving album images..");
        //Flag to check if the index creation is successful
        final boolean[] hasSucceed = {true};
        try {
            //Open directory for index
            Directory picIndex = FSDirectory.open(directoryToAlbumArtStore.toPath());
            //Open writer
            IndexWriter iw = new IndexWriter(picIndex, config);
            //Apply for each artist
            albums.forEach(album -> {
                hasSucceed[0] = pullImageForBundle(album, iw, album.getArtistNames().iterator().next());
            });
            System.out.println("Writing index..");
            //Write out and close the index
            iw.commit();
            iw.close();
            picIndex.close();
            System.out.println("Done..");
        } catch (IOException e) {
            hasSucceed[0] = false;
            e.printStackTrace();
        }
        return hasSucceed[0];
    }

    private static boolean pullImageForBundle(Bundle b, IndexWriter writer, String additionalInfo) {
        boolean[] hasSucceed = {true};
        String type = b.getBundleType() == Bundle.BundleType.ALBUM ? "Album" : "Artist";
        // System.out.println("Bundle : " + b.getName() + "\nHash : " + b.getHash());
        //Pull artwork from last.fm api, if any matching artwork is found
        String url;
        if (type.equals("Artist"))
            url = ArtPuller.getImageURLForArtist(b.getName());
        else
            url = ArtPuller.pullAlbumArt(b.getName(), additionalInfo);
        if (!url.equals(ArtPuller.getDefaultImage())) {
            //System.out.println("Artwork pulled from " + url);
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            try {
                //System.out.println("Writing image..");
                //Read the image using the received url
                BufferedImage image = ImageIO.read(new URL(url));
                //Write the image to the ByteArrayOutputStream to convert it to raw byte[]
                ImageIO.write(image, "png", s);
                //System.out.println("Creating byte array..");
                byte[] res = s.toByteArray();
                //Create a new document for storing to the index
                Document document = new Document();
                //This is the primary key of the index, the artist hash
                StringField field = new StringField(type + "Hash", b.getHash(), Field.Store.YES);
                //This is the raw image data field
                StoredField field1 = new StoredField("Image", new BytesRef(res));
                //Add the fields to the document
                document.add(field);
                document.add(field1);
                //System.out.println("Adding document..\n");
                //Add the document to index
                writer.addDocument(document);
                //Dispose and flush any leftover image objects, as they are very memory hungry
                image.flush();
                image.getGraphics().dispose();
                s.flush();
            } catch (MalformedURLException mue) {
                //   System.out.println("URl : " + url);
                hasSucceed[0] = false;
            } catch (Exception e) {
                e.printStackTrace();
                hasSucceed[0] = false;
            }
        } else {
            // System.out.println("Default image used!");
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
                    w.flush();
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
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                indexFolder(file, indexWriter);
            } else {
                if (tika.detect(file).startsWith("audio/mpeg")) {
                    BodyContentHandler handler = new BodyContentHandler();
                    Metadata metadata = new Metadata();
                    ParseContext context = new ParseContext();
                    parser.parse(new FileInputStream(file), handler, metadata, context);

                    Document document = new Document();
                    TextField field = new TextField("path", file.getAbsolutePath(), Field.Store.YES);
                    document.add(field);
                    String totalMeta = "Path\t" + file.getAbsolutePath() + "\n";
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
                    String[] hashable = {"Artist", "Album"};
                    for (String fie : hashable) {
                        String val = document.get(fie);
                        if (val == null) {
                            val = "Unknown";
                            totalMeta += fie + "\t" + val + "\n";
                            TextField field3 = new TextField(fie, val, Field.Store.YES);
                            document.add(field3);
                        }
                        String hash = generateSHAString(val);
                        StringField hashF = new StringField(fie + "Hash", hash, Field.Store.YES);
                        document.add(hashF);
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
