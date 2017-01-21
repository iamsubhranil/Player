/*
    Created By : iamsubhranil
    Date : 21/1/17
    Time : 11:02 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;

public class SwapPane extends BorderPane {

    private final HBox buttonBox;
    private final ArrayList<Pane> panes;
    private Pane current = null;
    private Button disabledButton = null;

    public SwapPane() {
        super();

        buttonBox = new HBox();
        panes = new ArrayList<>();
        setTop(buttonBox);
    }

    public VBox addNewPane(String title) {
        Button switchButton = new Button(title);
        switchButton.getStyleClass().add("button-noborder");
        VBox pane = new VBox();
        switchButton.setOnAction(e -> {
            switchToPane(pane);
            if (disabledButton != null)
                disabledButton.setDisable(false);
            switchButton.setDisable(true);
            disabledButton = switchButton;
        });
        buttonBox.getChildren().add(switchButton);
        if (current == null) {
            current = pane;
            setCenter(current);
            //  switchButton.fire();
        }
        return pane;
    }

    private void switchToPane(Pane p) {
        if (current == null)
            return;
        int curIndex = panes.indexOf(current);
        int switchIndex = panes.indexOf(p);
        movePane(current, p, switchIndex > curIndex);
        current = p;
    }

    private void movePane(Pane from, Pane to, boolean fromRight) {
        TranslateTransition enterTransition = new TranslateTransition(Duration.millis(100), to);
        enterTransition.setToX(from.getLayoutX());
        enterTransition.setToY(from.getLayoutY());
        enterTransition.setInterpolator(Interpolator.EASE_IN);
        enterTransition.setFromX(from.getLayoutX() + ((fromRight ? 1 : -1) * 50));
        enterTransition.setFromY(from.getLayoutY());
        FadeTransition fadeEnter = new FadeTransition(Duration.millis(150), to);
        fadeEnter.setFromValue(0.0);
        fadeEnter.setToValue(1.0);
        ParallelTransition paneEnter = new ParallelTransition(enterTransition, fadeEnter);

        TranslateTransition exitTransition = new TranslateTransition(Duration.millis(100), from);
        exitTransition.setFromX(from.getLayoutX());
        exitTransition.setFromY(from.getLayoutY());
        exitTransition.setInterpolator(Interpolator.EASE_OUT);
        exitTransition.setToX(from.getLayoutX() - ((fromRight ? 1 : -1) * 50));
        exitTransition.setToY(from.getLayoutY());
        exitTransition.setOnFinished(e -> {
            to.setOpacity(0.0);
            setCenter(to);
        });
        FadeTransition fadeExit = new FadeTransition(Duration.millis(50), from);
        fadeExit.setFromValue(1.0);
        fadeExit.setToValue(0.0);
        ParallelTransition paneExit = new ParallelTransition(exitTransition, fadeExit);

        SequentialTransition swapAnimation = new SequentialTransition(paneExit, paneEnter);
        swapAnimation.play();

    }

}
