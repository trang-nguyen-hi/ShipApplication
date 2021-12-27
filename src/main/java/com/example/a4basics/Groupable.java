package com.example.a4basics;

import java.util.ArrayList;

public interface Groupable {
    boolean hasChildren();
    ArrayList<Groupable> getChildren();
    boolean contains(double dx, double dy);
    void move(double x, double y);
    double getLeft();
    double getRight();
    double getTop();
    double getBottom();
    Groupable duplicate();
    void rotate (double a);
    void setOrigin();

    void rotate(double a, double cx, double cy);
}
