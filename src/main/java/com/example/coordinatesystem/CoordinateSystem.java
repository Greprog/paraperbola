package com.example.coordinatesystem;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;

public class CoordinateSystem extends Controller{

    //static Canvas mainCanvas = super.mainCanvas;
    //static double offSetX;
    //static double offSetY;

    public static void renderCoordinateSystem (GraphicsContext ctx, Affine transform, double zoom){
        // X-axis, Y-axis
        ctx.setLineWidth(0.2 / zoom);
        ctx.setStroke(Color.GRAY);
        ctx.strokePolyline(
                new double[]{0, 0},
                new double[]{-100, 100},
                2
        );

        ctx.setLineWidth(0.2 / zoom);
        ctx.setStroke(Color.GRAY);
        ctx.strokePolyline(
                new double[]{-100, 100},
                new double[]{0, 0},
                2
        );

        // Number line X;Y
        for (int i = -60; i < 61; i ++) {
            if (i == 0){
                ctx.save();
                transform = ctx.getTransform();
                transform.appendTranslation(0, 0);
                transform.appendScale(1.1 / zoom, 1.1 / zoom);
                ctx.setTransform(transform);

                ctx.setFill(Color.GRAY);
                ctx.setTextAlign(TextAlignment.CENTER);
                ctx.fillText(
                        String.format(String.valueOf(i)),
                        -8,
                        -8
                );

                ctx.restore();
            }else {
                ctx.save();
                transform = ctx.getTransform();
                transform.appendTranslation(0, i);
                transform.appendScale(1 / zoom, 1 / -zoom);
                ctx.setTransform(transform);

                ctx.setFill(Color.GRAY);
                ctx.setTextAlign(TextAlignment.LEFT);
                ctx.fillText(
                        String.format(String.valueOf(i)),
                        -15,
                        5
                );
                ctx.restore();
            }
        }

        for (int i = -60; i < 61; i ++) {
            if (i == 0) {

            } else {
                ctx.save();
                transform = ctx.getTransform();
                transform.appendTranslation(i, 0);
                transform.appendScale(1 / zoom, 1 / -zoom);
                ctx.setTransform(transform);

                ctx.setFill(Color.GRAY);
                ctx.setTextAlign(TextAlignment.CENTER);
                ctx.fillText(
                        String.format(String.valueOf(i)),
                        0,
                        20
                );
                ctx.restore();
            }
        }

        //nick
        for (int i = -60; i < 61; i ++) {
            ctx.setLineWidth(0.5 / zoom);
            ctx.strokePolyline(
                    new double[]{-0.05, 0.05},
                    new double[]{i, i},
                    2
            );
        }

        for (int i = -60; i < 61; i ++) {
            ctx.setLineWidth(0.5 / zoom);
            ctx.strokePolyline(
                    new double[]{i, i},
                    new double[]{-0.05, 0.05},
                    2
            );
        }

        for (int i = -60; i < 61; i++) {
            ctx.setStroke(Color.LIGHTGRAY);
            ctx.setLineWidth(0.15 / zoom);
            ctx.strokePolyline(
                    new double[]{i, i},
                    new double[]{-100, 100},
                    2
            );
        }

        for (int i = -60; i < 61; i++) {
            ctx.setStroke(Color.LIGHTGRAY);
            ctx.setLineWidth(0.15 / zoom);
            ctx.strokePolyline(
                    new double[]{-100, 100},
                    new double[]{i, i},
                    2
            );
        }

        ctx.restore();

        //Стрелки
        /*ctx.save();
        transform = ctx.getTransform();
        transform.appendTranslation(mainCanavas.getWidth(), mainCanavas.getHeight() / 2 + offSetX);
        //transform.appendScale(1 / zoom, 1 / zoom);
        ctx.setTransform(transform);

        ctx.setStroke(Color.GRAY);
        ctx.setFill(Color.GRAY);
        //ctx.setLineWidth(1 / zoom);
        ctx.fillPolygon(
                new double[]{0,-200, -200, 0},
                new double[]{0, 200, -200, 0},
                3
        );
        ctx.restore();*/
    }

    public static void drawXYArrow (GraphicsContext ctx,Affine transform, Canvas mainCanvas, double offSetX, double offSetY) {
        ctx.save();
        transform = ctx.getTransform();
        transform.appendTranslation(mainCanvas.getWidth(), mainCanvas.getHeight() / 2 + offSetY);
        ctx.setTransform(transform);

        ctx.setStroke(Color.GRAY);
        ctx.setFill(Color.GRAY);
        ctx.fillPolygon(
                new double[]{0,-15, -15, 0},
                new double[]{0, -5, 5, 0},
                3
        );
        ctx.setFill(Color.GRAY);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.fillText(
                String.valueOf("X"),
                -12,
                -10
        );
        ctx.restore();

        ctx.save();
        transform = ctx.getTransform();
        transform.appendTranslation(mainCanvas.getWidth() / 2 + offSetX, 0);
        ctx.setTransform(transform);

        ctx.setStroke(Color.GRAY);
        ctx.setFill(Color.GRAY);
        ctx.fillPolygon(
                new double[]{0, -5, 5, 0},
                new double[]{0, 15, 15, 0},
                3
        );
        ctx.setFill(Color.GRAY);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.fillText(
                String.valueOf("Y"),
                12,
                14
        );
        ctx.restore();
    }

}
