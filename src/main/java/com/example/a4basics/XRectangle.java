package com.example.a4basics;

import javafx.scene.paint.Color;

public class XRectangle extends XShape {
    public XRectangle(double x, double y, Color shapeColor, boolean isShown) {
        super(x, y,shapeColor, isShown);
    }

    @Override
    public boolean contains(double x, double y) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }
}
