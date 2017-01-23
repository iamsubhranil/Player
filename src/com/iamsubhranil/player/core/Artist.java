/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:54 PM
    Package : com.iamsubhranil.player.core
    Project : Player
*/
package com.iamsubhranil.player.core;

import com.iamsubhranil.player.ui.ArtPuller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashSet;

public class Artist {

    private final String name;
    private final HashSet<String> albums = new HashSet<>();
    private final ArrayList<String> songs = new ArrayList<>();
    private final StringProperty imageURL;
    private final String artistHash;
    private Pane p;

    public Artist(String artistName, String atHh) {
        name = artistName;
        imageURL = new SimpleStringProperty(ArtPuller.getDefaultImage());
        artistHash = atHh;
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

    public void setImageURL(String newURL) {
        if (!newURL.equals(""))
            imageURL.set(newURL);
    }

    public StringProperty getImageURLProperty() {
        return imageURL;
    }

    public void setPane(Pane pn) {
        p = pn;
        applyImageToPane();
    }

    public void applyImageToPane() {
        p.setStyle("-fx-background-image: url('" + imageURL.get() + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch;");
        System.gc();
    }

    public String getArtistHash() {
        return artistHash;
    }
}
