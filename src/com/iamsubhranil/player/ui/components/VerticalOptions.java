/*
    Created By : iamsubhranil
    Date : 20/1/17
    Time : 7:23 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VerticalOptions extends VBox {

    public VerticalOptions(String header) {
        super();
        setMinWidth(200);
        setPrefHeight(550);
        setFillWidth(true);
        setMaxHeight(Double.MAX_VALUE);

        Label head = new Label();
        head.getStyleClass().add("item-title-small");
        head.setText(header.toUpperCase());

        getChildren().add(head);
    }

    public Button addNewButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button-noborder");
        getChildren().add(button);
        setVgrow(button, Priority.ALWAYS);
        return button;
    }

}
