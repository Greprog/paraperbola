package com.example.coordinatesystem;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SaveWindow{


    Stage stage;
    public Label privetLabel;
    public ImageView imageV;
    Image test;

    /*public void displayImage (Image image) {
        imageV.setImage(image);
    }*/


    public void showView(ActionEvent event) {
        imageV.setImage(Test.snapshot);

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        SnapshotParameters ssp = new SnapshotParameters();
        ssp.setTransform(new Translate(0, 0));

        try {
            //shP = new WritableImage((int) screenShotPane.getWidth(), (int) screenShotPane.getHeight());

            //screenShotPane.snapshot(ssp, shP);


            //RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            RenderedImage renderedImage2 = SwingFXUtils.fromFXImage(Test.snapshot, null);
            ImageIO.write(renderedImage2, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
