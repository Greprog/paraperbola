package com.example.coordinatesystem;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;


public class GraphBuilder {

    double expGraph;
    double aGraph;
    double bGraph;
    double cGraph;
    double discX;
    double x1 = -999;
    double x2 = -999;
    double thickness = 1;
    String graphName;
    StringBuilder solution = new StringBuilder();
    Color color = Color.web("#000000");

    public GraphBuilder(double a, double b, double c, double exp) {
        this.aGraph = a;
        this.bGraph = b;
        this.cGraph = c;
        this.expGraph = exp;
    }

    public GraphBuilder(double a, double b, double c, double exp, double discX, double x1, double x2, String graphName) {
        this.aGraph = a;
        this.bGraph = b;
        this.cGraph = c;
        this.expGraph = exp;
        this.discX = discX;
        this.x1 = x1;
        this.x2 = x2;
        this.setGraphName(graphName);
        this.setSolution();
    }

    public double[] parabolaEquation () {
        discX = Math.pow(bGraph, 2)  - (4 * aGraph * cGraph);
        x1 = ((-bGraph) + Math.sqrt(discX)) / (2 * aGraph);
        x2 = ((-bGraph) - Math.sqrt(discX)) / (2 * aGraph);

        bGraph = bGraph / aGraph / 2;
        cGraph = aGraph * (-Math.pow(bGraph, 2) + (cGraph / aGraph));

        return new double[] {bGraph, cGraph, discX, x1, x2};
    }

    public void draw (GraphicsContext ctx, double zoom) {
        for (double i = -10000; i < 10000 ;i ++) {
            double myParabola = i / 100;
            ctx.setStroke(color);
            ctx.setLineWidth(thickness / zoom);
            ctx.strokePolyline(
                    new double[]{myParabola - bGraph, (myParabola + 0.01) - bGraph},
                    new double[]{aGraph * Math.pow(myParabola, expGraph) + cGraph, aGraph * Math.pow(myParabola + 0.01, expGraph) + cGraph},
                    2
            );
        }
    }

    public void drawPointPar(GraphicsContext ctx, double xForP, Affine transform, double zoom) {
        double xCoordinate = (xForP) * zoom;
        double yCoordinate = (aGraph * (Math.pow(xForP + bGraph, expGraph)) + cGraph) * zoom;

        int xCheck;
        if (Math.abs(xForP) - Math.abs((int) xForP) > 0.5) {
            xCheck = Math.abs((int) xForP) + 1;
            xCheck = (int) (xCheck * (xForP / Math.abs(xForP)));
        } else {
            xCheck = Math.abs((int) xForP);
            xCheck = (int) (xCheck * (xForP/ Math.abs(xForP)));
        }

        ctx.save();
        ctx.getTransform();
        transform.appendScale(1 / zoom, 1 / zoom);
        ctx.setTransform(transform);


        ctx.setFill(Color.BLUE);
        ctx.fillOval(
                xCoordinate - 3.5,
                yCoordinate - 3.5,
                7,
                7
        );

        if (x1 != -999) {
            ctx.setFill(Color.GREEN);
            ctx.fillOval(
                    x1 * zoom - 3.5,
                    0 - 3.5,
                    7,
                    7
            );

            ctx.setFill(Color.GREEN);
            ctx.fillOval(
                    x2 * zoom - 3.5,
                    0 - 3.5,
                    7,
                    7
            );
        }

        transform.appendScale(1, -1);
        ctx.setTransform(transform);

        ctx.setFill(Color.BLUE);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.fillText(
                "X: " + String.format("%.2f", xForP) + "; Y: " + String.format("%.2f", yCoordinate / zoom),
                xCoordinate - 50,
                -yCoordinate - 5
        );

        if (x1 != -999) {
            ctx.setFill(Color.GREEN);
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.fillText(
                    "X: " + String.format("%.2f", x1),
                    x1 * zoom - 25,
                    - 10
            );
            if (x1 != x2) {
                ctx.fillText(
                        "X: " + String.format("%.2f", x2),
                        x2 * zoom - 25,
                        -10
                );
            }
        }
        ctx.restore();

        ctx.save();
        ctx.getTransform();
        transform.appendScale(zoom, -zoom);
        ctx.setTransform(transform);
        ctx.restore();
    }

    public boolean selGraph(double xForP, double yForP) {
        /*double xEnd = (Math.pow(yForP, 1/expParabola) - cParabola) / aParabola;
        double yEnd = aParabola * Math.pow(xForP, expParabola) + cParabola;
        double xAbs = Math.abs(Math.abs(xForP) - Math.abs(xEnd));
        double yAbs = Math.abs(Math.abs(yForP) - Math.abs(yEnd));
        System.out.println("yForP: " + yForP);
        System.out.println("xForP: " + xForP);
        System.out.println(xEnd);
        System.out.println(xAbs);

        if (xAbs < 0.2 || yAbs < 0.2) {
            return true;
        }
        return false;*/

        System.out.println(aGraph + " " + bGraph + " " + cGraph);

        double yTest = aGraph * Math.pow((xForP + bGraph), expGraph) + cGraph;
        double yAbsTest = Math.abs(Math.abs(yForP) - Math.abs(yTest));

        System.out.println(yTest);

        return yAbsTest < 0.2;
    }

    public void setGraphName(String s) {
        s = s.replaceAll("1x", "x");
        s = s.replaceAll("\\^2", "²");
        this.graphName = "y=" + s;
    }

    public void setSolution() {
        solution.append(graphName).append("\n");
        solution.append("Disc = ").append(discX).append("\n");
        if (x1 == 0 && x2 == 0) {
            solution.append("X = 0");
        } else {
            solution.append("X1 = ").append(x1).append("\n");
            solution.append("X2 = ").append(x2).append("\n");
        }
    }
}
