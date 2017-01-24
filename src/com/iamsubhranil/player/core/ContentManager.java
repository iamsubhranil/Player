/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:49 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.core;

import com.iamsubhranil.player.Preparation;
import com.iamsubhranil.player.db.Environment;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ContentManager {

    private static IndexReader songIndexReader;
    private static IndexReader artistArtIndexReader;
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
        backgroundService = Executors.newSingleThreadExecutor(factory);

        backgroundService.execute(() -> {
            try {
                System.out.println("Loading contents..");
                loadSongStore();
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

    public static void loadSongStore() throws IOException {
        songIndexReader = Preparation.getSongIndex();
        HashSet<String> albums = new HashSet<>();
        HashSet<String> artists = new HashSet<>();
        totalContent = songIndexReader.numDocs();
        int totDocs = songIndexReader.numDocs();
        while (totDocs > 0) {
            Document song = songIndexReader.document(totDocs - 1);
            albums.add(song.get("AlbumHash"));
            artists.add(song.get("ArtistHash"));
            totDocs--;
        }
        System.out.println("Total albums : " + albums.size());
        System.out.println("Total artists : " + artists.size());
        albums.forEach(album -> {
            if (album != null) {
                try {
                    TopDocs songs = searchFor("AlbumHash", album, songIndexReader);
                    Album album1 = new Album(songIndexReader.document(songs.scoreDocs[0].doc).get("Album"));
                    for (ScoreDoc scoreDoc : songs.scoreDocs) {
                        Document song = songIndexReader.document(scoreDoc.doc);
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
                    TopDocs songs = searchFor("ArtistHash", artist, songIndexReader);
                    Artist artist1 = new Artist(songIndexReader.document(songs.scoreDocs[0].doc).get("Artist"), artist);
                    for (ScoreDoc scoreDoc : songs.scoreDocs) {
                        Document song = songIndexReader.document(scoreDoc.doc);
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

    /* Method to retrieve and show artist images.
       This method will be user controllable in some future release.
       When called, this method first checks if there is at all any lucene index files present.
       If there is no index files present, this method calls Preparation.createArtistIndex method, which
       retrieves artist images from the internet using last.fm api, and saves the lucene index.
       This method then loads the index, searches for each artist's image using the artist name hash,
       and applies the image to the specific artist tile set by Artist.setPane method.
     */
    private static void retrieveArtistImages() {
        boolean showImages = true;
        //Check to see if there is at all any index files present
        if (!Environment.hasArtistImageIndex()) {
            //If there isn't, first download images and create the index
            showImages = Preparation.createArtistArtIndex();
        }
        //If index creation succeeds, try to show the images
        if (showImages) {
            try {
                //Get the index reader
                artistArtIndexReader = Preparation.getArtistArtIndex();
                artistArrayList.forEach(artist -> {
                    try {
                        //Search for the document in the index containing artist hash
                        TopDocs artistDoc = searchFor("ArtistHash", artist.getArtistHash(), artistArtIndexReader);
                        //If there is at all any documents matched with the corresponding artist hash
                        if (artistDoc.scoreDocs.length > 0) {
                            //Retrieve the document
                            Document artDoc = artistArtIndexReader.document(artistDoc.scoreDocs[0].doc);
                            //Retrieve the binary image data
                            BytesRef imageBytes = artDoc.getBinaryValue("Image");
                            //Call the method
                            artist.setBackgroundImage(imageBytes.bytes);
                            //Wait some time before next image to avoid UI freezing
                            Thread.sleep(100);
                        }
                    } catch (ParseException | IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
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

    public static ArrayList<Album> getAlbumArrayList() {
        return albumArrayList;
    }

    public static ArrayList<Artist> getArtistArrayList() {
        return artistArrayList;
    }

    public static TopDocs searchFor(String field, String text, IndexReader contentReader) throws ParseException, IOException {
        IndexSearcher indexSearcher = new IndexSearcher(contentReader);
        TermQuery termQuery = new TermQuery(new Term(field, text));
        return indexSearcher.search(termQuery, totalContent);
    }

    public static void main(String[] args) {
        System.out.println("Starting load..");
        startLoadingContent(() ->
                System.out.println("Content loaded.."), () -> System.out.println("Failed.."));
    }

}
