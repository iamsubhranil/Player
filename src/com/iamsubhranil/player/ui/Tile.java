/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 11:44 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class Tile extends BorderPane {

    public Tile(String text) {
        super();

        Label label = new Label(text);
        label.setStyle("-fx-font-family: \"Bariol Bold\";" +
                "\n-fx-font-size: 12pt;");

        setPrefSize(150, 150);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox contrastBox = new HBox(label);
        contrastBox.setPadding(new Insets(10, 10, 10, 10));
        contrastBox.setStyle("-fx-background-color: derive(-fx-background, 30%);");
        setBottom(contrastBox);
        //  setEffect(new DropShadow(2, Color.valueOf("#ffffff")));
    }

}
