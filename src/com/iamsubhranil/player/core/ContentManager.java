/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:49 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.core;

import com.iamsubhranil.player.Preparation;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

public class ContentManager {

    private static IndexReader contentReader;
    private static ArrayList<Album> albumArrayList = new ArrayList<>();
    private static ArrayList<Artist> artistArrayList = new ArrayList<>();
    private static int totalContent = 0;

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
                    Artist artist1 = new Artist(contentReader.document(songs.scoreDocs[0].doc).get("Artist"));
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
        try {
            loadContents();
            System.setOut(new PrintStream(new FileOutputStream("output.txt")));
            artistArrayList.forEach(artist -> {
                System.out.println("Artist name : " + artist.getName());
                System.out.println("Total songs : " + artist.getSongs().size());
                System.out.println("Total albums : " + artist.getAlbums().size());
                System.out.println("\n");
            });
            albumArrayList.forEach(album -> {
                System.out.println("Album name : " + album.getName());
                System.out.println("Total songs : " + album.getSongs().size());
                System.out.println("Total artists : " + album.getArtists().size());
                System.out.println("\n");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
