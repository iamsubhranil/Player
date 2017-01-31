/*
    Created By : iamsubhranil
    Date : 25/1/17
    Time : 3:42 PM
    Package : com.iamsubhranil.player.core
    Project : Player
*/
package com.iamsubhranil.player.core;

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

public class Bundle {

    private static final Background defaultBackground = new Background(createBackgroundImage(new Image(ArtPuller.getDefaultImage())));
    private final String name;
    private final ArrayList<String> songs;
    private final String hash;
    private final ObjectProperty<Background> backgroundObjectProperty;
    private final BundleType bundleType;

    public Bundle(String name, String hash, BundleType type) {
        this.name = name;
        this.hash = hash;
        bundleType = type;
        songs = new ArrayList<>(0);
        backgroundObjectProperty = new SimpleObjectProperty<>(defaultBackground);
    }

    private static BackgroundImage createBackgroundImage(Image background) {
        //Construct a BackgroundImage
        return new BackgroundImage(background, BackgroundRepeat.NO_REPEAT
                , BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public BundleType getBundleType() {
        return bundleType;
    }

    public ReadOnlyObjectProperty<Background> backgroundObjectProperty() {
        return backgroundObjectProperty;
    }

    public ArrayList<String> getSongs() {
        return songs;
    }

    public void setBackgroundImage(String url) {
        try {
            Image image = new Image(url);
            setBackground(image);
        } catch (Exception e) {
        }
    }

    private void setBackground(Image background) {
        //Remove existing images
        try {
            backgroundObjectProperty.get().getImages().clear();
        } catch (Exception e) {
        }
        //Update the backgroundObjectProperty to propagate the updation to existing listeners,
        //typically the associated artist or album tile.
        Platform.runLater(() -> backgroundObjectProperty.set(new Background(createBackgroundImage(background))));
    }

    public void setBackgroundImage(byte[] imageBytes) {
        try {
            //Read the byte[] as an InputStream and produce corresponding BufferedImage
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            //Call the method
            setBackground(SwingFXUtils.toFXImage(image, null));
            //flush the image
            image.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void addSong(String hash) {
        songs.add(hash);
    }

    public String toString() {
        return "Bundle[name=" + name + ",hash=" + hash + ",songs=" + songs.size() + "]";
    }

    public enum BundleType {
        ARTIST,
        ALBUM
    }
}
