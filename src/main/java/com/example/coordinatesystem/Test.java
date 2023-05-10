package com.example.coordinatesystem;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import java.awt.image.RenderedImage;

public class Test {

    static WritableImage snapshot;
    static RenderedImage renderedImage;

    public static void makePicture() {
        renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
    }

}
