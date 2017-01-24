/*
    Created By : iamsubhranil
    Date : 24/1/17
    Time : 3:11 PM
    Package : com.iamsubhranil.player.db
    Project : Player
*/
package com.iamsubhranil.player.db;

import java.io.File;

public class Environment {

    private static final File directoryToSongStore = new File("tempSongStore");
    private static final File directoryToArtistArtStore = new File("tempArtistArts");
    private static final File directoryToAlbumArtStore = new File("tempAlbumArts");

    public static boolean hasSongIndex() {
        return hasIndex(directoryToSongStore, "_0.cfs", "_0.cfe");
    }

    public static boolean hasArtistImageIndex() {
        return hasIndex(directoryToArtistArtStore, "_0.cfs", "_0.cfe");
    }

    public static boolean hasAlbumImageIndex() {
        return hasIndex(directoryToAlbumArtStore, "_0.cfs", "_0.cfe");
    }

    private static boolean hasIndex(File dirToLook, String... expectedFiles) {
        if (!dirToLook.exists() || !dirToLook.isDirectory())
            return false;
        String[] indexFiles = dirToLook.list();
        if (indexFiles == null || indexFiles.length == 0)
            return false;
        int c = 0;
        boolean ret = true;
        for (String expect : expectedFiles) {
            ret = (ret && indexFiles[c].equals(expect));
            c++;
        }
        return ret;
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

    public static void main(String[] args) {
        System.out.println("hasSongsIndex : " + hasSongIndex());
        System.out.println("hasArtistArtsIndex : " + hasArtistImageIndex());
    }
}
