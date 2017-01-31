/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:51 PM
    Package : com.iamsubhranil.player.core
    Project : Player
*/
package com.iamsubhranil.player.core;

import java.util.HashSet;

public class Album extends Bundle {

    private final HashSet<String> artists = new HashSet<>(0);
    private final HashSet<String> artistNames = new HashSet<>(0);

    public Album(String albumName, String albumHash) {
        super(albumName, albumHash, BundleType.ALBUM);
    }

    public void addArtist(Artist artist) {
        artists.add(artist.getHash());
        artistNames.add(artist.getName().trim());
    }

    public HashSet<String> getArtists() {
        return artists;
    }

    public HashSet<String> getArtistNames() {
        return artistNames;
    }

    public String toString() {
        return super.toString().replace("]", "").replace("Bundle", "Album") + ",artists=" + artists.size() + "]";
    }
}
