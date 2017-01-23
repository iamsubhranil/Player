/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 11:48 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import com.iamsubhranil.player.core.Album;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AlbumTile extends Tile {

    public AlbumTile(Album album) {
        super(album.getName());

        VBox box = new VBox(new Label(album.getArtists().size() + " artists")
                , new Label(album.getSongs().size() + " songs"));
        box.setPadding(new Insets(5, 5, 5, 5));
        box.setSpacing(5);
        box.setAlignment(Pos.BOTTOM_LEFT);
        try {
            String image = ArtPuller.pullAlbumArt("", album.getName());
            box.setStyle("-fx-background-image: url('" + image + "'); " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: stretch;");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCenter(box);
    }

}
