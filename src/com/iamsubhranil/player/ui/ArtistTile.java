/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 11:51 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import com.iamsubhranil.player.core.Artist;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ArtistTile extends Tile {

    public ArtistTile(Artist artist) {
        super(artist.getName());

        VBox box = new VBox(new Label(artist.getAlbums().size() + " albums")
                , new Label(artist.getSongs().size() + " songs"));
        box.setPadding(new Insets(5, 5, 5, 5));
        box.setSpacing(5);
        box.setAlignment(Pos.BOTTOM_LEFT);

        setCenter(box);
    }

}
