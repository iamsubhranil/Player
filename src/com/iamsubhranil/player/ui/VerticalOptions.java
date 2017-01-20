/*
    Created By : iamsubhranil
    Date : 20/1/17
    Time : 7:23 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VerticalOptions extends VBox {

    public VerticalOptions() {
        super();
        setMinWidth(200);
        setPrefHeight(550);
        setFillWidth(true);
        setMaxHeight(Double.MAX_VALUE);
        //setAlignment(Pos.CENTER);
    }

    public Button addNewButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("button-noborder");
        button.setStyle("-fx-text-fill: #ffffff;");
        getChildren().add(button);
        setVgrow(button, Priority.ALWAYS);
        return button;
    }

}
