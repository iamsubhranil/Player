/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:49 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.db;

import com.iamsubhranil.player.core.Album;
import com.iamsubhranil.player.core.Artist;
import com.iamsubhranil.player.core.Bundle;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ContentManager {

    private static IndexReader songIndexReader;
    private static IndexReader artistArtIndexReader;
    private static IndexReader albumArtIndexReader;
    private static ArrayList<Album> albumArrayList = new ArrayList<>(0);
    private static ArrayList<Artist> artistArrayList = new ArrayList<>(0);
    private static int totalContent = 0;
    private static ExecutorService backgroundService;

    public static void startLoadingContent(Runnable onSucceed, Runnable onFailed) {
        prepareBackgroundThread();
        backgroundService.execute(() -> {
            try {
                System.out.println("Loading contents..");
                loadSongs();
                onSucceed.run();
                retrieveArtistImages(artistArrayList);
                retrieveAlbumArtImages(albumArrayList);
            } catch (IOException e) {
                e.printStackTrace();
                onFailed.run();
            }
        });
    }

    private static void prepareBackgroundThread() {
        if (backgroundService == null) {
            ThreadFactory factory = r -> {
                Thread thread = new Thread(r);
                thread.setName("PlayerBackgroundThread");
                thread.setDaemon(true);
                return thread;
            };
            System.out.println("Creating background thread..");
            backgroundService = Executors.newSingleThreadExecutor(factory);
        }
    }

    public static ExecutorService getBackgroundService() {
        prepareBackgroundThread();
        return backgroundService;
    }

    private static Artist getArtist(String name, String hash) {
        final Artist[] ret = new Artist[1];
        artistArrayList.forEach(artist -> {
            try {
                if (artist.getHash().equals(hash))
                    ret[0] = artist;
            } catch (NullPointerException npe) {
                System.out.println(artist);
            }
        });
        if (ret[0] == null) {
            ret[0] = new Artist(name, hash);
            artistArrayList.add(ret[0]);
        }
        return ret[0];
    }

    private static Album getAlbum(String name, String hash) {
        final Album[] ret = new Album[1];
        albumArrayList.forEach(album -> {
            try {
                if (album.getHash().equals(hash))
                    ret[0] = album;
            } catch (NullPointerException npe) {
            }
        });
        if (ret[0] == null) {
            ret[0] = new Album(name, hash);
            albumArrayList.add(ret[0]);
        }
        return ret[0];
    }

    private static void loadSongs() throws IOException {
        songIndexReader = Preparation.getSongIndex();
        totalContent = songIndexReader.numDocs();
        int totDocs = totalContent;
        while (totDocs > 0) {
            Document song = songIndexReader.document(totDocs - 1);
            String songHash = song.get("SongHash");
            String artistHash = song.get("ArtistHash");
            String albumHash = song.get("AlbumHash");
            Artist artist = getArtist(song.get("Artist"), artistHash);
            artist.addAlbum(albumHash);
            artist.addSong(songHash);
            Album album = getAlbum(song.get("Album"), albumHash);
            album.addArtist(artist);
            album.addSong(songHash);
            totDocs--;
        }
        // removeNull();
    }

    private static void removeNull() {
        final int[] index = {0};
        albumArrayList.forEach(album -> {
            if (album.getName() == null)
                index[0] = albumArrayList.indexOf(album);
        });
        System.out.println("Null album found at " + index[0] + " size " + albumArrayList.size());
        albumArrayList.remove(index[0]);
        artistArrayList.forEach((artist -> {
            if (artist.getName() == null)
                index[0] = artistArrayList.indexOf(artist);
        }));
        System.out.println("Null artist found at " + index[0] + " size " + artistArrayList.size());
        artistArrayList.remove(index[0]);
    }
    /* Method to retrieve and show artist images.
       This method will be user controllable in some future release.
       When called, this method first checks if there is at all any lucene index files present.
       If there is no index files present, this method calls Preparation.createArtistIndex method, which
       retrieves artist images from the internet using last.fm api, and saves the lucene index.
       This method then loads the index, searches for each artist's image using the artist name hash,
       and applies the image to the specific artist tile set by Artist.setPane method.
     */
    private static void retrieveArtistImages(ArrayList<Artist> artistSubList) {
        boolean showImages = true;
        //Check to see if there is at all any index files present
        if (!Environment.hasArtistImageIndex()) {
            //If there isn't, first download images and create the index
            showImages = Preparation.createArtistArtIndex(artistSubList);
        }
        //If index creation succeeds, try to show the images
        if (showImages) {
            try {
                //Get the index reader
                artistArtIndexReader = Preparation.getArtistArtIndex();
                artistSubList.forEach(artist -> {
                    retrieveImageFor(artist, artistArtIndexReader);
                });
                //If there is at least one unindexed artist, index it
                //  if(!notFound.isEmpty())
                //       retrieveArtistImages(notFound);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void retrieveAlbumArtImages(ArrayList<Album> albumSubList) {
        boolean showImages = true;
        if (!Environment.hasAlbumImageIndex()) {
            //If there isn't, first download images and create the index
            showImages = Preparation.createAlbumArtIndex(albumSubList);
        }
        //If index creation succeeds, try to show the images
        if (showImages) {
            try {
                //Get the index reader
                albumArtIndexReader = Preparation.getAlbumArtIndex();
                albumSubList.forEach(album -> {
                    retrieveImageFor(album, albumArtIndexReader);
                });
                //If there is at least one unindexed artist, index it
                //  if(!notFound.isEmpty())
                //       retrieveArtistImages(notFound);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void retrieveImageFor(Bundle b, IndexReader reader) {
        try {
            //Search for the document in the index containing item hash
            TopDocs doc = searchFor((b.getBundleType() == Bundle.BundleType.ALBUM ? "Album" : "Artist") + "Hash", b.getHash(), reader);
            //If there is at all any documents matched with the corresponding item hash
            if (doc.scoreDocs.length > 0) {
                //Retrieve the document
                Document artDoc = reader.document(doc.scoreDocs[0].doc);
                //Retrieve the binary image data
                BytesRef imageBytes = artDoc.getBinaryValue("Image");
                //Call the method
                b.setBackgroundImage(imageBytes.bytes);
                //Wait some time before next image to avoid UI freezing
                Thread.sleep(100);
            }
        } catch (ParseException | IOException | InterruptedException e) {
            e.printStackTrace();
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
