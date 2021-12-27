package com.example.a4basics;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class InteractionModel {
    ArrayList<ShipModelSubscriber> subscribers;
    ArrayList<Groupable> selectedElement;
    XRectangle rubberBand;
    public ShipClipboard clipboard;


    public InteractionModel() {
        subscribers = new ArrayList<>();
        selectedElement = new ArrayList<>();
        rubberBand = new XRectangle(0, 0, Color.YELLOW, false);
        clipboard = new ShipClipboard();
    }

    public void cutting (){
        if (!getSelected().isEmpty()) {
            clipboard.cut(getSelected());
        }
    }

    public void copying (){
        if (!getSelected().isEmpty()) {
            clipboard.copy(getSelected());
        }
    }

    public ArrayList<Groupable> pasting (){
        ArrayList<Groupable> copy = clipboard.paste();
        return copy;
    }

    public XRectangle getRubberBand(){
        return rubberBand;
    }

    public boolean isRubberband(){
        return rubberBand.getShow();
    }

    public boolean isContained(Groupable ship){
        return rubberBand.contains(ship.getLeft(), ship.getTop()) && rubberBand.contains(ship.getRight(), ship.getBottom());
    }

    public void newRubberband(double x, double y){
        rubberBand.setStart(x, y);
        rubberBand.setShow(true);
        notifySubscribers();
    }

    public void resizeRubberband(double x, double y){
        rubberBand.resize(x, y);
        notifySubscribers();
    }

    public void removeRubberband(){
        rubberBand.setShow(false);
        notifySubscribers();
    }

    public void clearSelection() {
        selectedElement.clear();
        notifySubscribers();
    }

    public void setSelected(Groupable newSelection) {
        selectedElement.add(newSelection);
        notifySubscribers();
    }

    public void removeSelected(Groupable selection){
        selectedElement.remove(selection);
        notifySubscribers();
    }

    public ArrayList<Groupable> getSelected(){
        return selectedElement;
    }

    public void addSubscriber(ShipModelSubscriber aSub) {
        subscribers.add(aSub);
    }

    private void notifySubscribers() {
        subscribers.forEach(sub -> sub.modelChanged());
    }

    public void notifySlider(){
        subscribers.forEach(sub -> sub.resetSlider());
    }
}
