/*
    Created By : iamsubhranil
    Date : 18/1/17
    Time : 9:49 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import com.iamsubhranil.player.db.Environment;
import com.iamsubhranil.player.db.SearchScope;
import com.iamsubhranil.player.ui.panes.FirstRun;
import com.iamsubhranil.player.ui.panes.ReUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
//import radams.gracenote.webapi.GracenoteException;
//import radams.gracenote.webapi.GracenoteWebAPI;

public class Starter extends Application {

    private static final String clientID = "104218039"; // Put your clientID here.
    private static final String clientTag = "46B06339C3A1CAC7BDC707A32F30367B"; // Put your clientTag here.
    private static String UID = "";

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadFonts();

        Scene scene;

        SearchScope.loadSearchScopes();

        if (!Environment.hasSongIndex()) {
            System.out.println("No index");
            FirstRun firstRun = new FirstRun();
            firstRun.getStylesheets().add("/styles/MyMetro.css");
            if (Environment.hasSearchScope()) {
                System.out.println("Has search scope");
                firstRun.prepareIndexUI();

            } else {
                System.out.println("No search scope");
                firstRun.prepareSearchUI();
            }
            scene = new Scene(firstRun, 700, 500);

            firstRun.setOnFinished(() -> {
                Platform.runLater(() -> {
                    ReUI reUI = new ReUI();
                    reUI.getStylesheets().add("/styles/MyMetro.css");
                    scene.setRoot(reUI);
                });
            });
        } else {
            System.out.println("Have index");
            ReUI reUI = new ReUI();
            reUI.getStylesheets().add("/styles/MyMetro.css");
            scene = new Scene(reUI, 700, 500);
        }



        //  primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        //   primaryStage.setScene(new Scene(new SVGTest(),700,500));
        primaryStage.show();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/styles/fonts/Bariol_Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/styles/fonts/Bariol_Light.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/styles/fonts/Bariol_Bold.ttf"), 14);
    }

 /*   private void prepareGracenote() {
        GracenoteWebAPI api = null; // If you have a userID, you can specify it as the third parameter to constructor.
        try {
            api = new GracenoteWebAPI(clientID, clientTag);
            UID = api.register();
            ArtPuller.setGracenoteWebAPI(api);
        } catch (GracenoteException e) {
            e.printStackTrace();
        }
    }
    */
}
