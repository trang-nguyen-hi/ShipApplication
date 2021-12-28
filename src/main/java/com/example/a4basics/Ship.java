package com.example.a4basics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Ship implements Groupable {
    double translateX, translateY;
    double rotatedTransX, rotatedTransY; //used to calculate the translateX and translateY while user rotates the ship

    double[] xs = {0,20,0,-20,0};
    double[] ys = {24,-20,-12,-20,24};
    // used to calculate the xs, ys while rotating
    double[] rotatedXs = xs.clone();
    double[] rotatedYs = ys.clone();

    double shipWidth, shipHeight;

    double[] originXs, originYs; // the displayXs and displayYS before user starts rotate
    double[] displayXs, displayYs;
    WritableImage buffer;
    PixelReader reader;
    double clickX, clickY;


    public Ship(double newX, double newY) {
        Canvas shipCanvas;
        GraphicsContext gc;

        translateX = newX;
        translateY = newY;
        double minVal = DoubleStream.of(xs).min().getAsDouble();
        double maxVal = DoubleStream.of(xs).max().getAsDouble();
        shipWidth = maxVal - minVal;
        minVal = DoubleStream.of(ys).min().getAsDouble();
        maxVal = DoubleStream.of(ys).max().getAsDouble();
        shipHeight = maxVal - minVal;
        displayXs = new double[xs.length];
        displayYs = new double[ys.length];
        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] = xs[i] + shipWidth/2;
            displayYs[i] = ys[i] + shipHeight/2;
        }

        shipCanvas = new Canvas(shipWidth,shipHeight);
        gc = shipCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillPolygon(displayXs, displayYs, displayXs.length);
        buffer = shipCanvas.snapshot(null,null);
        reader = buffer.getPixelReader();

        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] = xs[i] + translateX;
            displayYs[i] = ys[i] + translateY;
        }

        originXs = displayXs.clone();
        originYs = displayYs.clone();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public ArrayList<Groupable> getChildren() {
        return null;
    }

    public boolean contains(double x, double y) {
        clickX = x - translateX + shipWidth/2;
        clickY = y - translateY + shipHeight/2;
        // check bounding box first, then bitmap
        boolean inside = false;
        if (clickX >= 0 && clickX <= shipWidth && clickY >= 0 && clickY <= shipHeight) {
            if (reader.getColor((int) clickX, (int) clickY).equals(Color.BLACK)) inside = true;
        }
        return inside;
    }

    @Override
    public void move(double dx, double dy) {
        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] += dx;
            displayYs[i] += dy;
        }
        translateX += dx;
        translateY += dy;
        originXs = displayXs.clone();
        originYs = displayYs.clone();
    }

    @Override
    public double getLeft() {
        return DoubleStream.of(displayXs).min().getAsDouble();
    }

    @Override
    public double getRight() {
        return DoubleStream.of(displayXs).max().getAsDouble();
    }

    @Override
    public double getTop() {
        return DoubleStream.of(displayYs).min().getAsDouble();
    }

    @Override
    public double getBottom() {
        return DoubleStream.of(displayYs).max().getAsDouble();
    }

    @Override
    public Groupable duplicate() {
        Ship newShip = new Ship(translateX, translateY);

        // preserve the rotation of the ship
        newShip.xs = xs.clone();
        newShip.ys = ys.clone();
        newShip.rotatedXs = xs.clone();
        newShip.rotatedYs = ys.clone();

        newShip.shipWidth = shipWidth;
        newShip.shipHeight = shipHeight;
        newShip.displayXs = displayXs.clone();
        newShip.displayYs = displayYs.clone();
        newShip.originXs = displayXs.clone();
        newShip.originYs = displayYs.clone();
        return newShip;
    }

    @Override
    public void rotate(double a) {
        rotate(a,translateX,translateY);
    }

    @Override
    // set-up everything for the next rotation
    // finalize the last rotation
    public void setOrigin(){
        // finalize the display coordinates
        originXs = displayXs.clone();
        originYs = displayYs.clone();

        // if the ship has been rotated
        if ( !Arrays.equals(rotatedXs, xs) || !Arrays.equals(rotatedYs, ys)) {
            double minXVal = DoubleStream.of(rotatedXs).min().getAsDouble();
            double maxXVal = DoubleStream.of(rotatedXs).max().getAsDouble();
            shipWidth = maxXVal - minXVal;
            double minYVal = DoubleStream.of(rotatedYs).min().getAsDouble();
            double maxYVal = DoubleStream.of(rotatedYs).max().getAsDouble();
            shipHeight = maxYVal - minYVal;

            // using the rotated normalized coordinates to calculate the display coordinates in the the snapshot
            double[] snapShotXs = new double[xs.length], snapShotYs = new double[xs.length];
            for (int i = 0; i < rotatedXs.length; i++) {
                snapShotXs[i] = rotatedXs[i] + shipWidth/2;
                snapShotYs[i] = rotatedYs[i] + shipHeight/2;
            }

            // make a new snapshot and reader
            Canvas shipCanvas = new Canvas(shipWidth, shipHeight);
            GraphicsContext gc = shipCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillPolygon(snapShotXs, snapShotYs, snapShotXs.length);
            buffer = shipCanvas.snapshot(null, null);
            reader = buffer.getPixelReader();

            // finalize the xs, ys, and the centre point
            xs = rotatedXs.clone();
            ys = rotatedYs.clone();
            translateX = rotatedTransX;
            translateY = rotatedTransY;
        }
    }

    public void rotate(double a, double cx, double cy) {
        double x, y;
        double radians = a * Math.PI / 180;

        for (int i = 0; i < displayXs.length; i++) {
            x = originXs[i] - cx;
            y = originYs[i] - cy;
            displayXs[i] = rotateX(x, y, radians) + cx;
            displayYs[i] = rotateY(x, y, radians) + cy;
            rotatedXs[i] = rotateX(xs[i], ys[i], radians);
            rotatedYs[i] = rotateY(xs[i], ys[i], radians);
        }

        x = translateX - cx;
        y = translateY - cy;
        rotatedTransX = rotateX(x, y, radians) + cx;
        rotatedTransY = rotateY(x, y, radians) + cy;
    }

    private double rotateX(double x, double y, double thetaR) {
        return Math.cos(thetaR) * x - Math.sin(thetaR) * y;
    }

    private double rotateY(double x, double y, double thetaR) {
        return Math.sin(thetaR) * x + Math.cos(thetaR) * y;
    }
}
