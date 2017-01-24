/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:54 PM
    Package : com.iamsubhranil.player.core
    Project : Player
*/
package com.iamsubhranil.player.core;

import com.iamsubhranil.player.ui.ArtPuller;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Artist {

    private final String name;
    private final HashSet<String> albums = new HashSet<>();
    private final ArrayList<String> songs = new ArrayList<>();
    private final String artistHash;
    private final ObjectProperty<Background> backgroundObjectProperty;
    private Pane p;

    public Artist(String artistName, String atHh) {
        name = artistName;
        artistHash = atHh;
        backgroundObjectProperty = new SimpleObjectProperty<>();
        backgroundObjectProperty.setValue(new Background(new BackgroundImage(new Image(ArtPuller.getDefaultImage()), BackgroundRepeat.NO_REPEAT
                , BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
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

    public ReadOnlyObjectProperty<Background> backgroundImageProperty() {
        return backgroundObjectProperty;
    }

    /*
        This method accepts raw byte[], and constructs a BackgroundImage by converting the byte[]
        first to a BufferedImage, and then the BufferedImage to javafx.scene.image.Image using SwingFXUtils
        as
            i) Being a JavaFX scene object, BackgroundImage only accepts javafx.scene.image.Image,
                rather than java.awt.image which is basically the superclass of BufferedImage
            ii) No suitable methods have been found till now which converts byte[] to directly
                javafx.scene.image.Image
     */
    public void setBackgroundImage(byte[] imageBytes) {
        try {
            //Read the byte[] as an InputStream and produce corresponding BufferedImage
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            //Construct a BackgroundImage using the BufferedImage
            BackgroundImage backgroundImage = new BackgroundImage(SwingFXUtils.toFXImage(image, null), BackgroundRepeat.NO_REPEAT
                    , BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            //Update the backgroundObjectProperty to propagate the updation to existing listeners,
            //typically the associated artist tile.
            Platform.runLater(() -> backgroundObjectProperty.set(new Background(backgroundImage)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getArtistHash() {
        return artistHash;
    }
}
