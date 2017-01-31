/*
    Created By : iamsubhranil
    Date : 26/1/17
    Time : 12:39 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class SVGTest extends StackPane {

    public SVGTest() {
        super();
        SVGPath cover = new SVGPath();
        cover.setContent("M10,64.4v871.1h980V64.4H10z M46.3,100.8h90.8v798.5H46.3V100.8L46.3,100.8z M953.7,899.2H173.3V100.7h780.4V899.2z");
        SVGPath discCircleOut = new SVGPath();
        discCircleOut.setContent("M572.6,844.8c190.6,0,344.8-154.2,344.8-344.8S763.1,155.2,572.6,155.2C382,155.2,227.8,309.4,227.8,500S382,844.8,572.6,844.8z M572.6,191.5c170.6,0,308.5,137.9,308.5,308.5c0,170.6-137.9,308.5-308.5,308.5C402,808.5,264.1,670.6,264.1,500C264.1,329.4,402,191.5,572.6,191.5L572.6,191.5z");
        SVGPath discCircleIn = new SVGPath();
        discCircleIn.setContent("M572.6,608.9c59.9,0,108.9-49,108.9-108.9c0-59.9-49-108.9-108.9-108.9c-59.9,0-108.9,49-108.9,108.9C463.7,559.9,512.7,608.9,572.6,608.9z M572.6,427.4c39.9,0,72.6,32.7,72.6,72.6c0,39.9-32.7,72.6-72.6,72.6S500,539.9,500,500C500,460.1,532.7,427.4,572.6,427.4L572.6,427.4z");
        SVGPath centerDot = new SVGPath();
        centerDot.setContent("M590.8,500c0,10-8.1,18.1-18.1,18.1c-10,0-18.1-8.1-18.1-18.1c0-10,8.1-18.1,18.1-18.1C582.6,481.9,590.8,490,590.8,500L590.8,500z");
        setAlignment(Pos.CENTER);
        cover.scaleYProperty().bind(cover.scaleXProperty());
        discCircleOut.scaleYProperty().bind(discCircleOut.scaleXProperty());
        discCircleIn.scaleYProperty().bind(discCircleIn.scaleXProperty());
        centerDot.scaleYProperty().bind(centerDot.scaleXProperty());

        discCircleOut.scaleXProperty().bind(cover.scaleXProperty());
        discCircleIn.scaleXProperty().bind(cover.scaleXProperty());
        centerDot.scaleXProperty().bind(cover.scaleXProperty());
        cover.setScaleX(.1f);

        discCircleIn.layoutXProperty().bind(discCircleOut.layoutXProperty());
        discCircleIn.layoutYProperty().bind(discCircleOut.layoutYProperty());
        centerDot.layoutXProperty().bind(discCircleOut.layoutXProperty());
        centerDot.layoutYProperty().bind(discCircleOut.layoutYProperty());

        discCircleOut.layoutXProperty().bind(cover.layoutXProperty().subtract(66));
        discCircleOut.layoutYProperty().bind(cover.layoutYProperty());

        discCircleIn.setManaged(false);
        centerDot.setManaged(false);
        discCircleOut.setManaged(false);

        discCircleOut.fillProperty().bind(cover.fillProperty());
        discCircleIn.fillProperty().bind(cover.fillProperty());
        centerDot.fillProperty().bind(cover.fillProperty());


        cover.setFill(Color.BROWN);

        getChildren().addAll(cover, discCircleOut, discCircleIn, centerDot);
    }

}
