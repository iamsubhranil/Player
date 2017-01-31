/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:02 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.ui.panes;

import com.iamsubhranil.player.db.ContentManager;
import com.iamsubhranil.player.ui.components.AlbumTile;
import com.iamsubhranil.player.ui.components.ArtistTile;
import com.iamsubhranil.player.ui.components.SwapPane;
import com.iamsubhranil.player.ui.components.VerticalOptions;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class ContentBox extends BorderPane {

    private final VBox shortcutBox;

    private final VBox centerBox;

    private final VBox rightBox;
    private VBox artistPane;
    private VBox albumPane;

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
        SwapPane swapPane = new SwapPane();
        artistPane = swapPane.addNewPane("Artist");
        artistPane.setAlignment(Pos.CENTER);

        albumPane = swapPane.addNewPane("Albums");
        albumPane.setAlignment(Pos.CENTER);
        VBox pane3 = swapPane.addNewPane("Songs");
        pane3.setAlignment(Pos.CENTER);
        pane3.getChildren().add(new Label("SongsContent"));
        centerBox.getChildren().add(swapPane);
    }

    public void layoutContents() {
        TilePane tilePane = createTilePane();
        ContentManager.getArtistArrayList().forEach(artist -> {
            ArtistTile a = new ArtistTile(artist);
            tilePane.getChildren().add(a);
        });
        artistPane.getChildren().add(createScrollPane(tilePane));
        TilePane tilePane2 = createTilePane();
        ContentManager.getAlbumArrayList().forEach(album -> {
            AlbumTile a = new AlbumTile(album);
            tilePane2.getChildren().add(a);
        });
        albumPane.getChildren().add(createScrollPane(tilePane2));
    }

    private ScrollPane createScrollPane(TilePane p) {
        ScrollPane sp1 = new ScrollPane(p);
        return sp1;
    }

    private TilePane createTilePane() {
        TilePane tilePane = new TilePane();
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        return tilePane;
    }

}
