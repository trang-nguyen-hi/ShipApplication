package com.example.a4basics;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShipClipboard {
    ArrayList<Groupable> items;
    public ShipClipboard(){
        items = new ArrayList<>();
    }

    public ArrayList<Groupable> getItems(){
        return items;
    }


    public void cut (ArrayList<Groupable> list){
        items.clear();
        items.addAll(list);
    }

    public void copy (ArrayList<Groupable> list){
        items.clear();
        list.forEach(element -> items.add(element.duplicate()));
    }

    public ArrayList<Groupable> paste(){
        ArrayList<Groupable> copy = new ArrayList<>();
        items.forEach(item -> {
            Groupable hihi = item.duplicate();
            copy.add(hihi);
        });
        return copy;
    }


}
