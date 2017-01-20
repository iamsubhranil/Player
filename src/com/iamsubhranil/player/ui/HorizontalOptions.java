/*
    Created By : iamsubhranil
    Date : 20/1/17
    Time : 7:37 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HorizontalOptions extends HBox {

    public HorizontalOptions() {
        super();
        setMinWidth(200);
        setFillHeight(true);
        setSpacing(40);
        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(60, 10, 0, 10));
        //  setAlignment(Pos.CENTER);
    }


    public Button addNewButton(String text) {
        Button button = new Button(text);
        button.setMinHeight(50);
        button.setMaxHeight(Double.MAX_VALUE);
        button.getStyleClass().add("button-noborder");

        getChildren().add(button);
        setHgrow(button, Priority.ALWAYS);
        return button;
    }

}
