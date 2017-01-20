/*
    Created By : iamsubhranil
    Date : 18/1/17
    Time : 9:49 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Starter extends Application {

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadFonts();
        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.getStylesheets().add("/styles/MyMetro.css");

        UITest uiTest = new UITest();
        uiTest.getStylesheets().add("/styles/MyMetro.css");

        Scene primaryScene = new Scene(uiTest, 700, 500, Color.TRANSPARENT);

        //  primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/styles/OpenSans-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/styles/OpenSans-Light.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/styles/OpenSans-Semibold.ttf"), 14);
    }
}
