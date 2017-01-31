/*
    Created By : iamsubhranil
    Date : 24/1/17
    Time : 3:11 PM
    Package : com.iamsubhranil.player.db
    Project : Player
*/
package com.iamsubhranil.player.db;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;

public class Environment {

    private static final File directoryToSongStore = new File("tempSongStore");
    private static final File directoryToArtistArtStore = new File("tempArtistArts");
    private static final File directoryToAlbumArtStore = new File("tempAlbumArts");
    private static final File searchScopeFile = new File("search.scopes");

    public static boolean hasSongIndex() {
        return hasIndex(directoryToSongStore);
    }

    public static boolean hasArtistImageIndex() {
        return hasIndex(directoryToArtistArtStore);
    }

    public static boolean hasAlbumImageIndex() {
        return hasIndex(directoryToAlbumArtStore);
    }

    private static boolean hasIndex(File dirToLook) {
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(dirToLook.toPath()));
            reader.close();
            return true;
        } catch (Exception e) {
            dirToLook.delete();
            return false;
        }
    }

    public static boolean hasSearchScope() {
        return SearchScope.loadSearchScopes();
    }

    public static File getDirectoryToAlbumArtStore() {
        return directoryToAlbumArtStore;
    }

    public static File getDirectoryToArtistArtStore() {
        return directoryToArtistArtStore;
    }

    public static File getDirectoryToSongStore() {
        return directoryToSongStore;
    }

    public static File getSearchScopeFile() {
        return searchScopeFile;
    }

    public static void main(String[] args) {
        System.out.println("hasSongsIndex : " + hasSongIndex());
        System.out.println("hasArtistArtsIndex : " + hasArtistImageIndex());
    }
}
