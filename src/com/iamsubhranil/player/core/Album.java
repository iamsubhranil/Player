/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:51 PM
    Package : com.iamsubhranil.player.core
    Project : Player
*/
package com.iamsubhranil.player.core;

import java.util.ArrayList;
import java.util.HashSet;

public class Album {

    private final String name;
    private final HashSet<String> artists = new HashSet<>();
    private final ArrayList<String> songs = new ArrayList<>();

    public Album(String albumName) {
        name = albumName;
    }

    public void addArtist(String name) {
        artists.add(name);
    }

    public void addSong(String path) {
        songs.add(path);
    }

    public String getName() {
        return name;
    }

    public HashSet<String> getArtists() {
        return artists;
    }

    public ArrayList<String> getSongs() {
        return songs;
    }
}
