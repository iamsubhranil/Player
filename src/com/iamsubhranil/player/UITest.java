/*
    Created By : iamsubhranil
    Date : 20/1/17
    Time : 3:30 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import com.iamsubhranil.player.ui.HorizontalOptions;
import com.iamsubhranil.player.ui.VerticalOptions;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UITest extends HBox {

    public UITest() {
        super();

        VBox section1 = new VBox();
        section1.setMinSize(300, 500);
        section1.setMaxWidth(500);
        HBox top1 = new HBox();
        top1.setFillHeight(true);
        top1.setMinHeight(100);
        Label artistName = new Label("Enrique Iglesias");
        artistName.getStyleClass().add("header");
        artistName.setStyle("-fx-text-fill: #ffffff;");
      /*  Image fullImage = new Image(this.getClass().getResource("enrique.png").toExternalForm());

        // define crop in image coordinates:
        Rectangle2D croppedPortion = new Rectangle2D(0, 0, fullImage.getWidth(), fullImage.getWidth() / 3);

        ImageView imageView = new ImageView(fullImage);
        imageView.setViewport(croppedPortion);
        imageView.setFitWidth(300);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);
        top1.getChildren().add(imageView);*/
        top1.setPadding(new Insets(20, 20, 20, 20));
        top1.setStyle("-fx-background-color: -accent-milanored;");
        top1.getChildren().add(artistName);
        // top1.setEffect(new BoxBlur(10,10,3));

        HBox down1 = new HBox();
        down1.setFillHeight(true);
        down1.setMinHeight(400);
        down1.setMaxWidth(100);

        VerticalOptions verticalOptions = new VerticalOptions("Options");
        Button musicButton = verticalOptions.addNewButton("Songs");
        Button albumButton = verticalOptions.addNewButton("Albums");
        Button artistButton = verticalOptions.addNewButton("Artists");
        Button playlistButton = verticalOptions.addNewButton("Playlists");
        verticalOptions.setStyle("-fx-background-color: -accent-abbey;");
        VBox rside = new VBox();
        rside.setFillWidth(true);
        rside.setAlignment(Pos.CENTER);
        rside.setMinWidth(100);
        rside.getChildren().add(new Label("Details/Hide"));
        down1.getChildren().addAll(verticalOptions, rside);
        section1.setFillWidth(true);
        section1.getChildren().addAll(top1, down1);

        VBox section2 = new VBox();
        section2.setFillWidth(true);
        section2.setMinSize(250, 500);
        HorizontalOptions horizontalOptions = new HorizontalOptions();
        Button albumSongs = horizontalOptions.addNewButton("Songs");
        Button albumArtists = horizontalOptions.addNewButton("Artists");
        BorderPane down2 = new BorderPane();
        down2.setMinHeight(450);
        down2.setPrefWidth(500);
        down2.setCenter(generateSongsList());
        down2.setMaxHeight(Double.MAX_VALUE);
        section2.getChildren().addAll(horizontalOptions, down2);

        VBox section3 = new VBox();
        section3.setFillWidth(true);
        section3.setMinSize(150, 500);
        section3.setMaxHeight(650);
        section3.setStyle("-fx-background-color: -accent-rum;");
        section3.setPadding(new Insets(30, 0, 0, 10));
        Label queueHeader = new Label("Queue");
        queueHeader.getStyleClass().add("header");
        queueHeader.setStyle("-fx-text-fill: #ffffff;");
        section3.getChildren().addAll(queueHeader, generateNowPlaying());

        setFillHeight(true);
        setHgrow(section1, Priority.ALWAYS);
        setHgrow(section2, Priority.ALWAYS);
        setHgrow(section3, Priority.ALWAYS);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getChildren().addAll(section1, section2, section3);
    }

    private ListView generateNowPlaying() {
        ListView<Document> listView = new ListView<>();
        listView.setCellFactory(new Callback<ListView<Document>, ListCell<Document>>() {
            @Override
            public ListCell<Document> call(ListView<Document> documentListView) {
                return new ListCell<Document>() {
                    @Override
                    public void updateItem(Document document, boolean empty) {
                        super.updateItem(document, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            double duration = 0;
                            try {
                                duration = Double.parseDouble(document.get("Duration")) / 1000;
                            } catch (NullPointerException npe) {
                            }
                            int min = (int) duration / 60;
                            int sec = (int) duration % 60;
                            BorderPane borderPane = new BorderPane();
                            setStyle("-fx-background-color: -accent-rum;");
                            VBox side = new VBox(generateLabel(document.get("Title"), 200),
                                    generateLabel(document.get("Artist"), 200));
                            side.setSpacing(10);
                            borderPane.setLeft(side);
                            borderPane.setRight(generateLabel((min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec), 0));

                            setAlignment(Pos.CENTER);
                            setGraphic(borderPane);

                            //   setText(document.get("Title")+"\t"+min+":"+sec);
                        }
                    }

                    private Label generateLabel(String text1, double maxWidth) {
                        Label ret = new Label(text1);
                        if (maxWidth > 0) {
                            ret.setEllipsisString("...");
                            ret.setMaxWidth(maxWidth);
                        }
                        ret.getStyleClass().add("list-cell-small");
                        ret.setStyle("-fx-text-fill: white;");
                        return ret;
                    }
                };
            }
        });
        listView.setFixedCellSize(80);
        listView.setBorder(Border.EMPTY);
        loadItems(listView);
        return listView;
    }

    private ListView generateSongsList() {
        ListView<Document> listView = new ListView<>();
        listView.setCellFactory(new Callback<ListView<Document>, ListCell<Document>>() {
            @Override
            public ListCell<Document> call(ListView<Document> documentListView) {
                return new ListCell<Document>() {
                    @Override
                    public void updateItem(Document document, boolean empty) {
                        super.updateItem(document, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            double duration = 0;
                            try {
                                duration = Double.parseDouble(document.get("Duration")) / 1000;
                            } catch (NullPointerException npe) {
                            }
                            int min = (int) duration / 60;
                            int sec = (int) duration % 60;
                            BorderPane borderPane = new BorderPane();
                            borderPane.getStyleClass().addAll(getStyleClass());
                            borderPane.backgroundProperty().bind(backgroundProperty());
                            borderPane.setLeft(generateLabel(document.get("Title"), 400));
                            borderPane.setRight(generateLabel((min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec), 0));

                            setAlignment(Pos.CENTER);
                            setGraphic(borderPane);

                            //   setText(document.get("Title")+"\t"+min+":"+sec);
                        }
                    }

                    private Label generateLabel(String text1, double maxWidth) {
                        Label ret = new Label(text1);
                        ret.setAlignment(Pos.CENTER);
                        if (maxWidth > 0) {
                            ret.setEllipsisString("...");
                            ret.setMaxWidth(maxWidth);
                        }
                        ret.setTextAlignment(TextAlignment.CENTER);
                        ret.getStyleClass().add("list-cell-small");
                        ret.styleProperty().bind(Bindings.when(hoverProperty().or(selectedProperty())).then("-fx-text-fill: #ffffff;")
                                .otherwise("-fx-text-fill: #000000;"));
                        return ret;
                    }
                };
            }
        });
        listView.setFixedCellSize(50);
        loadItems(listView);
        return listView;
    }

    private void loadItems(ListView<Document> listView) {
        ExecutorService background = Executors.newCachedThreadPool();
        background.execute(() -> {
            try {
                IndexReader reader = Preparation.getIndex();
                ArrayList<Document> arrayList = new ArrayList<>();
                int totDocs = reader.numDocs();
                while (totDocs > 0) {
                    arrayList.add(reader.document(totDocs - 1));
                    totDocs--;
                }
                Platform.runLater(() -> {
                    listView.setItems(FXCollections.observableArrayList(arrayList));
                    listView.setPrefHeight(11 * 50 + 2);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //  background.shutdown();
    }

}
