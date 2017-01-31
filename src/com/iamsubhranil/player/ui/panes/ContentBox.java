/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 3:02 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.ui.panes;

import com.iamsubhranil.player.db.ContentManager;
import com.iamsubhranil.player.db.Preparation;
import com.iamsubhranil.player.ui.components.AlbumTile;
import com.iamsubhranil.player.ui.components.ArtistTile;
import com.iamsubhranil.player.ui.components.SwapPane;
import com.iamsubhranil.player.ui.components.VerticalOptions;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;

public class ContentBox extends BorderPane {

    private final VBox shortcutBox;

    private final VBox centerBox;

    private final VBox rightBox;
    private final ListView<Document> songListView;
    private VBox artistPane;
    private VBox albumPane;
    private VBox songsPane;

    public ContentBox() {
        super();

        shortcutBox = new VBox();
        prepareShortcutBox();

        centerBox = new VBox();
        songListView = new ListView<>();
        prepareCenterBox();

        rightBox = new VBox();

        setLeft(shortcutBox);
        setCenter(centerBox);
        setRight(rightBox);
        layoutSongs();
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
        songsPane = swapPane.addNewPane("Songs");
        songsPane.getChildren().addAll(songListView);
        centerBox.getChildren().add(swapPane);
    }

    private void layoutSongs() {
        songListView.setFixedCellSize(50);
        songListView.setMaxHeight(Double.MAX_VALUE);
        songListView.setCellFactory(listView -> new DocumentListCell());
        ObservableList<Document> list = FXCollections.observableArrayList();
        songListView.setItems(list);
        ContentManager.getBackgroundService().execute(() -> {
            try {
                IndexReader indexReader = Preparation.getSongIndex();
                int count = indexReader.numDocs();
                while (count > 0) {
                    Document doc = indexReader.document(count - 1);
                    Platform.runLater(() -> {
                        list.add(doc);
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count--;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
        return new ScrollPane(p);
    }

    private TilePane createTilePane() {
        TilePane tilePane = new TilePane();
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        return tilePane;
    }

    public class DocumentListCell extends ListCell<Document> {

        public DocumentListCell() {
            super();
            setAlignment(Pos.CENTER);
        }

        @Override
        public void updateItem(Document item, boolean isEmpty) {
            super.updateItem(item, isEmpty);
            if (isEmpty) {
                setText(null);
                setGraphic(null);
            } else {
                String name = item.get("Title");
                String artist = item.get("Artist");
                Double duration = Double.parseDouble(item.get("Duration")) / 1000;
                int min = (int) (duration / 60);
                int sec = (int) (duration % 60);
                String dur = (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;

                BorderPane main = new BorderPane();
                Label durLabel = new Label(dur);
                durLabel.setAlignment(Pos.CENTER_RIGHT);
                durLabel.setPrefSize(100, 50);
                main.setRight(durLabel);
                Label leftLabel = new Label(name);
                leftLabel.setAlignment(Pos.CENTER_LEFT);
                leftLabel.setPrefSize(400, 50);
                main.setLeft(leftLabel);
                Label centerLabel = new Label(artist);
                centerLabel.setPrefSize(200, 50);
                main.setCenter(centerLabel);

                centerLabel.styleProperty().bind(Bindings.when(hoverProperty()
                        .or(selectedProperty())
                        .or(main.hoverProperty()))
                        .then("-fx-text-fill: -fx-accent-text;")
                        .otherwise("-fx-text-fill: -fx-text-base-color;"));
                durLabel.styleProperty().bind(centerLabel.styleProperty());
                leftLabel.styleProperty().bind(centerLabel.styleProperty());

                setGraphic(main);
            }
        }

    }

}
