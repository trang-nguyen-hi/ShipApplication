package com.example.a4basics;

import javafx.scene.Group;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShipGroup implements Groupable {
    ArrayList<Groupable> children;
    double centreX;
    double centreY;

    public ShipGroup(){
        children = new ArrayList<>();
        centreX = (getRight() + getLeft())/2;
        centreY = (getBottom() + getTop())/2;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    public void addChild(Groupable item){
        children.add(item);
        centreX = (getRight() + getLeft())/2;
        centreY = (getBottom() + getTop())/2;
    }

    @Override
    public ArrayList<Groupable> getChildren() {
        return children;
    }

    @Override
    public boolean contains(double x, double y) {
        for (Groupable element : children){
            if (element.contains(x, y)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void move(double x, double y) {
        for (Groupable element : children){
            element.move(x, y);
        }
        centreX = (getRight() + getLeft())/2;
        centreY = (getBottom() + getTop())/2;
    }

    @Override
    public double getLeft() {
        double minLeft = Double.MAX_VALUE;
        double left;
        for (Groupable element : children){
            left = element.getLeft();
            if ( left < minLeft){
                minLeft = left;
            }
        }
        return minLeft;
    }

    @Override
    public double getRight() {
        double maxRight = Double.MIN_VALUE;
        double right;
        for (Groupable element : children){
            right = element.getRight();
            if ( right > maxRight){
                maxRight = right;
            }
        }
        return maxRight;
    }

    @Override
    public double getTop() {
        double minTop = Double.MAX_VALUE;
        double top;
        for (Groupable element : children){
            top = element.getTop();
            if ( top < minTop){
                minTop = top;
            }
        }
        return minTop;
    }

    @Override
    public double getBottom() {
        double maxBottom = Double.MIN_VALUE;
        double bottom;
        for (Groupable element : children){
            bottom = element.getBottom();
            if ( bottom > maxBottom){
                maxBottom = bottom;
            }
        }
        return maxBottom;
    }

    @Override
    public Groupable duplicate() {
        ShipGroup copy = new ShipGroup();
        children.forEach(child -> {
            copy.addChild(child.duplicate());
        });
        return copy;
    }

    @Override
    public void rotate(double a) {
        rotate (a,centreX,centreY);
    }

    @Override
    public void setOrigin(){
        children.forEach(child -> child.setOrigin());
        centreX = (getRight() + getLeft())/2;
        centreY = (getBottom() + getTop())/2;
    }

    @Override
    public void rotate(double a, double cx, double cy) {
        children.forEach(child -> child.rotate(a, cx, cy));
    }
}
