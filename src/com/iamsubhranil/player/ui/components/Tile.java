/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 11:44 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui.components;

import com.iamsubhranil.player.core.Bundle;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Tile extends BorderPane {

    public Tile(Bundle back) {
        super();

        Label label = new Label(back.getName());
        label.setStyle("-fx-font-family: \"Bariol Bold\";" +
                "\n-fx-font-size: 12pt;");

        setPrefSize(170, 170);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox backBox = new VBox();
        backBox.setPadding(new Insets(5, 5, 5, 5));
        backBox.setSpacing(5);
        backBox.setAlignment(Pos.BOTTOM_LEFT);
        backBox.backgroundProperty().bind(back.backgroundObjectProperty());

        HBox contrastBox = new HBox(label);
        contrastBox.setPadding(new Insets(10, 10, 10, 10));
        contrastBox.setStyle("-fx-background-color: derive(-fx-background, 30%);");
        contrastBox.styleProperty()
                .bind(Bindings.when(hoverProperty().or(contrastBox.hoverProperty().or(backBox.hoverProperty())))
                        .then("-fx-background-color: derive(-fx-contrast, 30%);" +
                                "\n-fx-text-fill: -fx-background;")
                        .otherwise("-fx-background-color: derive(-fx-background, 30%);" +
                                "\n-fx-text-fill: -fx-contrast;"));
        label.styleProperty().bind(contrastBox.styleProperty());
        setBottom(contrastBox);
        setCenter(backBox);
        setEffect(new DropShadow(3, Color.valueOf("#1d1d1d")));
    }

}
