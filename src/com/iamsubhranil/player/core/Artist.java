/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:54 PM
    Package : com.iamsubhranil.player.core
    Project : Player
*/
package com.iamsubhranil.player.core;

import java.util.HashSet;

public class Artist extends Bundle {

    private final HashSet<String> albums = new HashSet<>();

    public Artist(String artistName, String atHh) {
        super(artistName, atHh, BundleType.ARTIST);
    }

    public void addAlbum(String hash) {
        albums.add(hash);
    }

    public HashSet<String> getAlbums() {
        return albums;
    }

    public String toString() {
        return super.toString().replace("]", "").replace("Bundle", "Artist") + ",albums=" + albums.size() + "]";
    }
}
