package com.example.a4basics;
import javafx.scene.paint.Color;

public abstract class XShape {
    double left,top,right,bottom;
    double startX,startY;
    public Color myColor;
    boolean show;

    public XShape(double x, double y, Color newColor, boolean isShown) {
        startX = x;
        startY = y;
        left = x;
        top = y;
        right = x;
        bottom = y;
        myColor = newColor;
        show = isShown;
    }

    public void setStart(double x, double y){
        startX = x;
        startY = y;
    }

    public void resize(double x, double y) {
        left = Math.min(x, startX);
        right = Math.max(x, startX);
        top = Math.min(y, startY);
        bottom = Math.max(y, startY);
    }

    public abstract boolean contains(double x, double y);

    public void setShow(boolean isShown) {
        show = isShown;
    }

    public boolean getShow() {
        return show;
    }

}
