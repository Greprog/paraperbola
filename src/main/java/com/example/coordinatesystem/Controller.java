package com.example.coordinatesystem;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;

public class Controller implements Initializable {


    public Stage stage;
    public Pane screenShotPane;
    public Pane solutionPane;
    public Canvas mainCanvas;
    public Slider sldZ;
    public TextField inputText;
    public Button dltButton;
    public ColorPicker colorPicker;
    public Label showSolution;
    public Label functionName;
    public Label solutionLabel;


    double pressedX;
    double pressedY;
    double offSetX;
    double offSetY;
    double currentX;
    double currentY;
    double xForPointMethod;
    double yForPointMethod;

    public List<GraphBuilder> parabolaToDraw = new ArrayList<>();
    public List<GraphBuilder> selectedGraph = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(40),
                this::onTimerTick
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        sldZ.valueProperty().addListener((observable, oldValue, newValue) -> {
            draw();
        });
    }

    private void onTimerTick(ActionEvent event) {
        draw();
    }

    void draw () {
        double dpi = Toolkit.getDefaultToolkit().getScreenResolution(); //does not match the actual dpi
        double zoom = 110.16 / 2.54 * sldZ.getValue() / 100;
        //double zoom = dpi / 2.54 * sldZ.getValue() / 100;


        GraphicsContext ctx = mainCanvas.getGraphicsContext2D();
        ctx.setFill(Color.WHITE);
        ctx.fillRect(0,0, mainCanvas.getWidth(), mainCanvas.getHeight());


        ctx.save();
        Affine transform = ctx.getTransform();
        transform.appendTranslation(mainCanvas.getWidth() / 2 + offSetX, mainCanvas.getHeight() / 2 + offSetY);
        transform.appendScale(zoom, -zoom);
        ctx.setTransform(transform);


        xForPointMethod = (currentX - mainCanvas.getWidth()/2) / zoom;
        yForPointMethod = (currentY - mainCanvas.getHeight()/2) / -zoom;


        for (GraphBuilder x : parabolaToDraw) {
            x.draw(ctx, zoom);
        }

        if (selectedGraph.size() > 0) {
            dltButton.setVisible(true);
            colorPicker.setVisible(true);
            solutionPane.setVisible(true);
            colorPicker.setValue(selectedGraph.get(0).color);
            parabolaToDraw.forEach(x -> x.thickness = 1);
            selectedGraph.get(0).thickness = 1.5;
            selectedGraph.get(0).drawPointPar(ctx, xForPointMethod, transform, zoom);
            functionName.setText(selectedGraph.get(0).graphName);

            if (selectedGraph.get(0).solution != null) {
                solutionLabel.setText(selectedGraph.get(0).solution.toString());
                showSolution.setText(selectedGraph.get(0).solution.toString());
            } else {
                solutionLabel.setText("");
                showSolution.setText("");
            }
        } else {
            dltButton.setVisible(false);
            colorPicker.setVisible(false);
            solutionPane.setVisible(false);
            functionName.setText("");
            solutionLabel.setText("");
            showSolution.setText("");
        }

        CoordinateSystem.renderCoordinateSystem(ctx, transform, zoom);

        //setText("X: " + (currentX - mainCanvas.getWidth()/2) / zoom + "; Y: " + (currentY - mainCanvas.getHeight()/2) / -zoom); Show mouse position

        ctx.restore();

        CoordinateSystem.drawXYArrow(ctx,transform, mainCanvas, offSetX, offSetY);
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        offSetX += mouseEvent.getX() - pressedX;
        offSetY += mouseEvent.getY() - pressedY;
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();
        draw();
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        //System.out.println(pressedX + " XPressed");
        //System.out.println(offSetX + " XOPressed");
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();

        if (parabolaToDraw.size() > 0) {
            for (GraphBuilder x: parabolaToDraw) {
                if (x.selGraph(xForPointMethod, yForPointMethod)) {
                    if (selectedGraph.size() == 0) {
                        selectedGraph.add(x);
                    } else {
                        selectedGraph.set(0, x);
                    }
                    break;
                } else {
                    x.thickness = 1;
                    selectedGraph.clear();
                }
            }
        }
    }

    public void onMouseMoved(MouseEvent mouseEvent) {
        double x = mouseEvent.getX() - offSetX;
        double y = mouseEvent.getY() - offSetY;

        currentX = x;
        currentY = y;

        System.out.println(x + "  " + y);
    }
    public void save(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        SnapshotParameters ssp = new SnapshotParameters();
        ssp.setTransform(new Translate(0, 0));
        showSolution.setVisible(true);

        if (file != null) {
            try {
                WritableImage shP = new WritableImage((int) screenShotPane.getWidth(), (int) screenShotPane.getHeight());

                screenShotPane.snapshot(ssp, shP);

                RenderedImage renderedImage2 = SwingFXUtils.fromFXImage(shP, null);
                ImageIO.write(renderedImage2, "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        showSolution.setVisible(false);
    }

    public void makeGraph(ActionEvent actionEvent) {
        String text = inputText.getText();
        double[] abce = InputAnalyse.inputAnalyse(text);

        if (abce[3] == 2) {
            double[] qEquation = InputAnalyse.parabolaEquation(abce);
            GraphBuilder graphBuilder = new GraphBuilder(qEquation[0], qEquation[1], qEquation[2],
                    qEquation[3], qEquation[4], qEquation[5], qEquation[6], InputAnalyse.output);

            parabolaToDraw.add(graphBuilder);
        } else {
            GraphBuilder graphBuilder = new GraphBuilder(abce[0], abce[1], abce[2], abce[3]);
            graphBuilder.setGraphName(InputAnalyse.output);
            parabolaToDraw.add(graphBuilder);
        }

        /*if (parabolaToDraw.size() == 0) {
            parabolaToDraw.add(parabola);
        }else {
            parabolaToDraw.set(0, parabola);
        }*/
    }

    public void deleteGraph(ActionEvent actionEvent) {
        for (GraphBuilder x : parabolaToDraw) {
            if (selectedGraph.size() > 0 && x.equals(selectedGraph.get(0))) {
                parabolaToDraw.remove(x);
                selectedGraph.clear();
                break;
            }
        }
    }

    public void changeColor(ActionEvent actionEvent) {
        selectedGraph.get(0).color = colorPicker.getValue();
    }

    // метод для отдельного save окна
    /*public void setView(ActionEvent event) throws IOException {
        Test.snapshot = new WritableImage((int) screenShotPane.getWidth(), (int) screenShotPane.getHeight());
        screenShotPane.snapshot(null, Test.snapshot);
        //imageTest.setImage((Image) Test.snapshot);

        Stage stage1 = new Stage();
        stage1.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("saveWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage1.setTitle("Save as...");
        stage1.setScene(scene);
        stage1.show();
    }*/
}