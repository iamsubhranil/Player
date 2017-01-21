/*
    Created By : iamsubhranil
    Date : 18/1/17
    Time : 9:49 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import com.iamsubhranil.player.ui.ReUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
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

        ReUI reUI = new ReUI();
        reUI.getStylesheets().add("/styles/MyMetro.css");

        Scene primaryScene = new Scene(reUI, 700, 500);

        //  primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/styles/fonts/Bariol_Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/styles/fonts/Bariol_Light.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/styles/fonts/Bariol_Bold.ttf"), 14);
    }
}
