/*
    Created By : iamsubhranil
    Date : 25/1/17
    Time : 12:20 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui.panes;

import com.iamsubhranil.player.db.ContentManager;
import com.iamsubhranil.player.db.Preparation;
import com.iamsubhranil.player.db.SearchScope;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class FirstRun extends VBox {

    private Runnable r;

    public FirstRun() {
        super();

        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);

    }

    public void prepareSearchUI() {
        Label head = new Label("Get us some places to look");
        head.getStyleClass().add("header");
        Label subhead = new Label("Before we continue, we need at least one place to look out for your songs. You can easily add more places in settings.");
        subhead.setWrapText(true);
        Label statusLabel = new Label();
        Button confirm = new Button("Save and continue");
        TextField addressField = new TextField();
        addressField.setMinWidth(300);
        addressField.setOnKeyTyped(e -> {
            String input = addressField.getText();
            if (!(new File(input)).isDirectory()) {
                statusLabel.setText("No directory found in the path specified!");
                confirm.setDisable(true);
            } else {
                statusLabel.setText("");
                confirm.setDisable(false);
            }
        });
        confirm.setOnAction(e -> {
            SearchScope.addScope(addressField.getText());
            prepareIndexUI();
        });
        Button browse = new Button("Browse");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        browse.setOnAction(e -> {
            File selectedFolder = directoryChooser.showDialog(getScene().getWindow());
            if (!(selectedFolder == null))
                addressField.setText(selectedFolder.getAbsolutePath());
        });

        HBox box = new HBox(addressField, browse);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(10);

        getChildren().addAll(new VBox(head, subhead), box, statusLabel, confirm);
    }

    public void setOnFinished(Runnable r1) {
        r = r1;
    }

    public void prepareIndexUI() {
        //   ProgressBar progressBar = new ProgressBar();
        //  progressBar.setProgress(-1);
        Label head2 = new Label("We are preparing your library");
        head2.getStyleClass().add("header");
        Label subhead2 = new Label("This will take a few minutes");

        getChildren().clear();
        setAlignment(Pos.CENTER);
        getChildren().addAll(head2, subhead2);
        createIndex();
    }

    private void createIndex() {
        ExecutorService background = ContentManager.getBackgroundService();
        background.execute(() -> {
            SearchScope.storeSearchScopes();
            boolean test = Preparation.createSongsIndex();
            if (r != null)
                r.run();
        });
    }

}
