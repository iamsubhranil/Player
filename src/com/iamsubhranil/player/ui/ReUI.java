/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 1:45 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class ReUI extends BorderPane {

    private final BorderPane topBox;

    public ReUI() {
        super();
        setPadding(new Insets(10, 10, 10, 10));
        setMinWidth(500);
        topBox = new BorderPane();
        prepareTopBox();

        prepareContentBox();
        setTop(topBox);
    }

    private void prepareTopBox() {
        Label title = new Label("Player");
        title.setStyle("-fx-font-family: \"Bariol Bold\";");
        topBox.setLeft(title);
        topBox.setMaxWidth(Double.MAX_VALUE);

        HBox buttonBox = new HBox();
        Button minimize = drawResizeButton("#fba71b");
        Button maximize = drawResizeButton("#2d89ef");
        Button close = drawResizeButton("#ff1d77");
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(minimize, maximize, close);
        buttonBox.setMaxWidth(Double.MAX_VALUE);

        topBox.setRight(buttonBox);
    }

    private Button drawResizeButton(String color) {
        Button button = new Button();
        button.setId("resize-buttons");
        button.setPadding(new Insets(0, 10, 0, 10));
        button.setGraphic(drawCircle(color));
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private Circle drawCircle(String color) {
        Circle circle = new Circle();
        circle.setRadius(5);
        circle.setFill(Paint.valueOf(color));
        return circle;
    }

    private void prepareContentBox() {
        setCenter(new ContentBox());
    }

}
