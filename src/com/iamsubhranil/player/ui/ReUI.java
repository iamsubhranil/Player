/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 1:45 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player.ui;

import com.iamsubhranil.player.core.ContentManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ReUI extends BorderPane {

    private final BorderPane topBox;
    private final VBox startPage;
    private final ContentBox contentBox;

    public ReUI() {
        super();
        setPadding(new Insets(10, 10, 10, 10));
        setMinWidth(500);

        startPage = new VBox();

        topBox = new BorderPane();
        contentBox = new ContentBox();
        prepareTopBox();

        setCenter(startPage);
        prepareStartPage();
        //    prepareContentBox();
        //    setTop(topBox);
    }

    private void prepareStartPage() {
        Label head = new Label("Player");
        head.getStyleClass().add("header");
        Label subtitle = new Label("Your music. Our emotion.");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(-1);

        startPage.setAlignment(Pos.CENTER);
        startPage.setSpacing(5);
        startPage.getChildren().addAll(head, subtitle, progressBar);

        FadeTransition labelFade = new FadeTransition(Duration.millis(100), subtitle);
        labelFade.setFromValue(1.0);
        labelFade.setToValue(0.0);
        FadeTransition barFade = new FadeTransition(Duration.millis(100), progressBar);
        barFade.setFromValue(1.0);
        barFade.setToValue(0.0);
        FadeTransition transition = new FadeTransition(Duration.millis(100), head);
        transition.setFromValue(1.0);
        transition.setToValue(0.0);
        ParallelTransition pt = new ParallelTransition(labelFade, barFade, transition);
        pt.setOnFinished(e -> {
            topBox.setOpacity(0.0);
            setTop(topBox);
            contentBox.setOpacity(0.0);
            setCenter(contentBox);
        });
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), contentBox);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        FadeTransition topTransition = new FadeTransition(Duration.millis(100), topBox);
        topTransition.setFromValue(0.0);
        topTransition.setToValue(1.0);
        ParallelTransition pt2 = new ParallelTransition(fadeTransition, topTransition);
        SequentialTransition st1 = new SequentialTransition(pt, pt2);
        st1.setOnFinished(e -> contentBox.layoutContents());
        ContentManager.startLoadingContent(() -> Platform.runLater(st1::play), () -> {
        });
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
