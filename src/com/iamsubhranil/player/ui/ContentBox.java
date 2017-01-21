/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:02 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.ui;

import com.iamsubhranil.player.core.ContentManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ContentBox extends BorderPane {

    private final VBox shortcutBox;

    private final VBox centerBox;

    private final VBox rightBox;

    public ContentBox() {
        super();

        shortcutBox = new VBox();
        prepareShortcutBox();

        centerBox = new VBox();
        prepareCenterBox();

        rightBox = new VBox();

        setLeft(shortcutBox);
        setCenter(centerBox);
        setRight(rightBox);
    }

    private void prepareShortcutBox() {
        shortcutBox.setPadding(new Insets(5, 0, 5, 0));
        VerticalOptions search = new VerticalOptions("");
        TextField searchField = new TextField();
        searchField.setPromptText("Search..");
        searchField.setMaxWidth(150);
        searchField.setStyle(" -fx-border-radius: 0 0 0 0;\n" +
                "-fx-border-width: 0;" +
                "  -fx-background-radius: 30 30 30 30;\n");
        search.getChildren().add(searchField);

        VerticalOptions collection = new VerticalOptions("Collection");
        Button library = collection.addNewButton("Library");
        Button history = collection.addNewButton("History");

        VerticalOptions playlists = new VerticalOptions("Playlists");

        VerticalOptions additional = new VerticalOptions("Additional");
        Button settings = additional.addNewButton("Settings");
        Button feedback = additional.addNewButton("Feedback");

        shortcutBox.setSpacing(10);
        shortcutBox.getChildren().addAll(search, new VBox(collection, playlists, additional));
    }

    private void prepareCenterBox() {
        Label libHead = new Label("Library");
        libHead.getStyleClass().add("header");
        centerBox.getChildren().add(libHead);
        try {
            ContentManager.loadContents();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SwapPane swapPane = new SwapPane();
        VBox pane1 = swapPane.addNewPane("Artist");
        pane1.setAlignment(Pos.CENTER);
        TilePane tilePane = new TilePane();
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        ContentManager.getArtistArrayList().forEach(artist -> {
            ArtistTile a = new ArtistTile(artist);
            tilePane.getChildren().add(a);
        });
        pane1.getChildren().add(new ScrollPane(tilePane));
        VBox pane2 = swapPane.addNewPane("Albums");
        pane2.setAlignment(Pos.CENTER);
        TilePane tilePane2 = new TilePane();
        tilePane2.setHgap(20);
        tilePane2.setVgap(20);
        ContentManager.getAlbumArrayList().forEach(album -> {
            AlbumTile a = new AlbumTile(album);
            tilePane2.getChildren().add(a);
        });
        pane2.getChildren().add(new ScrollPane(tilePane2));
        VBox pane3 = swapPane.addNewPane("Songs");
        pane3.setAlignment(Pos.CENTER);
        pane3.getChildren().add(new Label("SongsContent"));
        centerBox.getChildren().add(swapPane);
    }

}
