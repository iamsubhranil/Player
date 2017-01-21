/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:54 PM
    Package : com.iamsubhranil.player.core
    Project : Player
*/
package com.iamsubhranil.player.core;

import java.util.ArrayList;
import java.util.HashSet;

public class Artist {

    private final String name;
    private final HashSet<String> albums = new HashSet<>();
    private final ArrayList<String> songs = new ArrayList<>();

    public Artist(String albumName) {
        name = albumName;
    }

    public void addAlbum(String name) {
        albums.add(name);
    }

    public void addSong(String path) {
        songs.add(path);
    }

    public String getName() {
        return name;
    }

    public HashSet<String> getAlbums() {
        return albums;
    }

    public ArrayList<String> getSongs() {
        return songs;
    }
}
