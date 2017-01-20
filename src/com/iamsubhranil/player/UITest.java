/*
    Created By : iamsubhranil
    Date : 20/1/17
    Time : 3:30 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class UITest extends HBox {

    public UITest() {
        super();

        VBox section1 = new VBox();
        section1.setMinSize(300, 500);
        HBox top1 = new HBox();
        top1.setFillHeight(true);
        top1.setMinHeight(100);
        Image fullImage = new Image(this.getClass().getResource("enrique.png").toExternalForm());

        // define crop in image coordinates:
        Rectangle2D croppedPortion = new Rectangle2D(0, 0, fullImage.getWidth(), fullImage.getWidth() / 3);

        ImageView imageView = new ImageView(fullImage);
        imageView.setViewport(croppedPortion);
        imageView.setFitWidth(300);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);
        top1.getChildren().add(imageView);
        HBox down1 = new HBox();
        down1.setFillHeight(true);
        down1.setMinHeight(200);
        VBox lside = new VBox();
        lside.setFillWidth(true);
        lside.setAlignment(Pos.CENTER);
        lside.setMinWidth(200);
        lside.getChildren().add(new Label("Options"));
        VBox rside = new VBox();
        rside.setFillWidth(true);
        rside.setAlignment(Pos.CENTER);
        rside.setMinWidth(100);
        rside.getChildren().add(new Label("Details/Hide"));
        down1.getChildren().addAll(lside, rside);
        section1.setFillWidth(true);
        section1.getChildren().addAll(top1, down1);

        VBox section2 = new VBox();
        section2.setFillWidth(true);
        section2.setMinSize(250, 500);
        HBox top2 = new HBox();
        top2.setFillHeight(true);
        top2.setMinHeight(50);
        top2.setAlignment(Pos.CENTER);
        top2.getChildren().add(new Label("Switch"));
        BorderPane down2 = new BorderPane();
        down2.setMinHeight(450);
        down2.setCenter(new Label("List"));
        section2.getChildren().addAll(top2, down2);

        VBox section3 = new VBox();
        section3.setFillWidth(true);
        section3.setAlignment(Pos.CENTER);
        section3.setMinSize(150, 500);
        section3.getChildren().add(new Label("Queue"));

        setFillHeight(true);
        setHgrow(section1, Priority.ALWAYS);
        setHgrow(section2, Priority.ALWAYS);
        setHgrow(section3, Priority.ALWAYS);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getChildren().addAll(section1, section2, section3);
    }

}
