/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:49 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.core;

import com.iamsubhranil.player.Preparation;
import com.iamsubhranil.player.ui.ArtPuller;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ContentManager {

    private static IndexReader contentReader;
    private static ArrayList<Album> albumArrayList = new ArrayList<>();
    private static ArrayList<Artist> artistArrayList = new ArrayList<>();
    private static int totalContent = 0;
    private static ExecutorService backgroundService;

    public static void startLoadingContent(Runnable onSucceed, Runnable onFailed) {
        ThreadFactory factory = r -> {
            Thread thread = new Thread(r);
            thread.setName("PlayerBackgroundThread");
            thread.setDaemon(true);
            return thread;
        };
        System.out.println("Creating background thread..");
        backgroundService = Executors.newSingleThreadExecutor();

        backgroundService.execute(() -> {
            try {
                System.out.println("Loading contents..");
                loadContents();
                onSucceed.run();
                retrieveArtistImages();
            } catch (IOException e) {
                e.printStackTrace();
                onFailed.run();
            }
        });
    }

    public static ExecutorService getBackgroundService() {
        return backgroundService;
    }

    public static void loadContents() throws IOException {
        contentReader = Preparation.getIndex();
        HashSet<String> albums = new HashSet<>();
        HashSet<String> artists = new HashSet<>();
        totalContent = contentReader.numDocs();
        int totDocs = contentReader.numDocs();
        while (totDocs > 0) {
            Document song = contentReader.document(totDocs - 1);
            albums.add(song.get("AlbumHash"));
            artists.add(song.get("ArtistHash"));
            totDocs--;
        }
        System.out.println("Total albums : " + albums.size());
        System.out.println("Total artists : " + artists.size());
        albums.forEach(album -> {
            if (album != null) {
                try {
                    TopDocs songs = searchFor("AlbumHash", album);
                    Album album1 = new Album(contentReader.document(songs.scoreDocs[0].doc).get("Album"));
                    for (ScoreDoc scoreDoc : songs.scoreDocs) {
                        Document song = contentReader.document(scoreDoc.doc);
                        album1.addSong(song.get("SongHash"));
                        album1.addArtist(song.get("ArtistHash"));
                    }
                    albumArrayList.add(album1);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Sorted albums : " + albumArrayList.size());
        artists.forEach(artist -> {
            if (artist != null) {
                try {
                    TopDocs songs = searchFor("ArtistHash", artist);
                    Artist artist1 = new Artist(contentReader.document(songs.scoreDocs[0].doc).get("Artist"), artist);
                    for (ScoreDoc scoreDoc : songs.scoreDocs) {
                        Document song = contentReader.document(scoreDoc.doc);
                        artist1.addSong(song.get("SongHash"));
                        artist1.addAlbum(song.get("AlbumHash"));
                    }
                    artistArrayList.add(artist1);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Sorted artists : " + artistArrayList.size());
    }

    private static void retrieveArtistImages() {
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
        System.out.println("Retrieving artist images..");
        try {
            Directory picIndex = FSDirectory.open(new File("testimages").toPath());
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
                    BinaryDocValuesField bdvf = new BinaryDocValuesField("Image", new BytesRef(res));
                    document.add(field);
                    document.add(bdvf);
                    System.out.println("Adding document..\n");
                    iw.addDocument(document);
                    System.gc();
                } catch (MalformedURLException mue) {
                    System.out.println("URl : " + url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Writing index..");
            iw.commit();
            picIndex.close();
            System.out.println("Done..");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  artistArrayList.forEach(Artist::applyImageToPane);
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

    public static ArrayList<Album> getAlbumArrayList() {
        return albumArrayList;
    }

    public static ArrayList<Artist> getArtistArrayList() {
        return artistArrayList;
    }

    public static TopDocs searchFor(String field, String text) throws ParseException, IOException {
        IndexSearcher indexSearcher = new IndexSearcher(contentReader);
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);

        TermQuery termQuery = new TermQuery(new Term(field, text));
        return indexSearcher.search(termQuery, totalContent);
    }

    public static void main(String[] args) {
        System.out.println("Starting load..");
        startLoadingContent(() ->
                System.out.println("Content loaded.."), () -> System.out.println("Failed.."));
    }

}
