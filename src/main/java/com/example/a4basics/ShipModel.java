package com.example.a4basics;

import java.util.ArrayList;
import java.util.Optional;

public class ShipModel {
    public ArrayList<Groupable> elements;
    ArrayList<ShipModelSubscriber> subscribers;

    public ShipModel() {
        subscribers = new ArrayList<>();
        elements = new ArrayList<>();
    }

    public Ship createShip(double x, double y) {
        Ship s = new Ship(x,y);
        elements.add(s);
        notifySubscribers();
        return s;
    }

    public ShipGroup createGroup (ArrayList<Groupable> list){
        ShipGroup group = new ShipGroup();
        for (Groupable element : list){
            group.addChild(element);
            if ( !this.elements.remove(element)) System.out.println("element in a given group list, is not in model's list");
        }
        this.elements.add(group);
        return group;
    }

    public void removeElements (ArrayList<Groupable> list){
        elements.removeAll(list);
        notifySubscribers();
    }

    public void addElements (ArrayList<Groupable> list){
        elements.addAll(list);
        notifySubscribers();
    }

    public ArrayList<Groupable> ungroup (ShipGroup group){
        /// might need to return a new list, using for-loop to add elements to that loop
        //ArrayList<Groupable> children = new ArrayList<>();
        this.elements.remove(group);
        this.elements.addAll(group.getChildren());
        return group.getChildren();
    }

    public Optional<Groupable> detectHit(double x, double y) {
        return elements.stream().filter(s -> s.contains(x, y)).reduce((first, second) -> second);
    }

    public void moveElement(Groupable b, double dX, double dY) {
        b.move(dX,dY);
        notifySubscribers();
    }

    public void rotateSelected(double a, ArrayList<Groupable> list){
        list.forEach(groupable -> groupable.rotate(a));
        notifySubscribers();
    }

    public void addSubscriber (ShipModelSubscriber aSub) {
        subscribers.add(aSub);
    }

    private void notifySubscribers() {
        subscribers.forEach(ShipModelSubscriber::modelChanged);
    }
}
