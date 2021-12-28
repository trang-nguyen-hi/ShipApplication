package com.example.a4basics;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Optional;

public class ShipController {
    InteractionModel iModel;
    ShipModel model;
    double prevX, prevY;
    double dX, dY;

    protected enum State {
        READY, MOVE, DRAW_OR_UNSELECT, DRAW_RUBBERBAND, DRAW_RUBBERBAND_CONTROL
    }
    protected State currentState;

    public ShipController() {
        currentState = State.READY;
    }

    public void setInteractionModel(InteractionModel newModel) {
        iModel = newModel;
    }

    public void setModel(ShipModel newModel) {
        model = newModel;
    }

    public void handlePressed(double x, double y, MouseEvent event) {
        // reset the origin coordinates for every element in selection
        iModel.getSelected().forEach(Groupable::setOrigin);
        iModel.notifySlider();

        prevX = x;
        prevY = y;
        if (currentState == State.READY) {// context: on a ship?
            Optional<Groupable> hit = model.detectHit(x, y);
            // on ship
            if (hit.isPresent()) {
                // if it is the first selected
                if (iModel.getSelected().isEmpty()) {
                    iModel.setSelected(hit.get());
                }
                // if this ship is not in selection
                else if (!iModel.getSelected().contains(hit.get())) {
                    //if control is not pressed, clear selection and choose this one
                    if (!event.isControlDown()) {
                        iModel.clearSelection();
                    }
                    iModel.setSelected(hit.get());
                }
                // if this ship is in selection and control is pressed, remove from the selection
                else if (event.isControlDown()) {
                    iModel.removeSelected(hit.get());
                }
                currentState = State.MOVE;
            } else {
                // on background - is Shift down?
                if (event.isShiftDown()) {
                    // create ship
                    Ship newShip = model.createShip(x, y);
                    iModel.clearSelection();
                    iModel.setSelected(newShip);
                    currentState = State.MOVE;
                }
                // if shift is not pressed
                else {
                    currentState = State.DRAW_OR_UNSELECT;
                }
            }
        }
    }

    public void handleDragged(double x, double y, MouseEvent event) {
        dX = x - prevX;
        dY = y - prevY;
        switch (currentState) {
            case MOVE -> {
                for (Groupable ship : iModel.getSelected()) {
                    model.moveElement(ship, dX, dY);
                }
                prevX = x;
                prevY = y;
            }
            case DRAW_OR_UNSELECT -> {
                if (event.isControlDown()){
                    currentState = State.DRAW_RUBBERBAND_CONTROL;
                }
                else {
                    currentState = State.DRAW_RUBBERBAND;
                }
                iModel.newRubberband(prevX, prevY);
                iModel.resizeRubberband(x, y);
                prevX = x;
                prevY = y;
            }
            case DRAW_RUBBERBAND -> iModel.resizeRubberband(x, y);
            case DRAW_RUBBERBAND_CONTROL -> {
                if (event.isControlDown()){
                    iModel.resizeRubberband(x, y);
                }
            }

        }
    }

    public void handleReleased(double x, double y, MouseEvent event) {
        switch (currentState) {
            case MOVE -> currentState = State.READY;
            // have not switch to draw, so it's just a regular click -> unselect
            case DRAW_OR_UNSELECT -> {
                iModel.clearSelection();
                currentState = State.READY;
            }
            case DRAW_RUBBERBAND -> {
                iModel.clearSelection();
                for (Groupable ship : model.elements) {
                    if (iModel.isContained(ship)) {
                        iModel.setSelected(ship);
                    }
                }
                iModel.removeRubberband();
                // select the ones inside the rubberband
                currentState = State.READY;
            }
            case DRAW_RUBBERBAND_CONTROL -> {
                for (Groupable element : model.elements) {
                    if (iModel.isContained(element)) {
                        // if ship is in selection, unselect
                        if ( iModel.getSelected().contains(element)){
                            iModel.getSelected().remove(element);
                        }
                        // otherwise, select it
                        else{
                            iModel.getSelected().add(element);
                        }
                    }
                }
                iModel.removeRubberband();
                // select the ones inside the rubberband
                currentState = State.READY;
            }
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        // reset the origin coordinates for every element in selection
        iModel.getSelected().forEach(Groupable::setOrigin);
        iModel.notifySlider();

        if (keyEvent.getCode().equals(KeyCode.G)){
            // if the selected list is not empty, group all of them together
            if (!iModel.getSelected().isEmpty()){
                // change in model
                ShipGroup newGroup = model.createGroup(iModel.getSelected());
                iModel.clearSelection();
                iModel.setSelected(newGroup);
            }
        }
        if (keyEvent.getCode().equals(KeyCode.U) &&
                iModel.getSelected().size() == 1 &&
                iModel.getSelected().get(0) instanceof ShipGroup){
            //ungroup and get the list of children of the ungrouped group
            ArrayList<Groupable> list = model.ungroup((ShipGroup) iModel.getSelected().get(0));
            // clear selection and add all children to selected list
            iModel.clearSelection();
            for (Groupable element : list){
                iModel.setSelected(element);
            }
        }
        if (keyEvent.getCode().equals(KeyCode.X) && keyEvent.isControlDown() && !iModel.getSelected().isEmpty()){
            System.out.println("cut");
            iModel.cutting();
            iModel.clearSelection();
            model.removeElements(iModel.clipboard.getItems());
        }
        if (keyEvent.getCode().equals(KeyCode.C) && keyEvent.isControlDown() && !iModel.getSelected().isEmpty()){
            System.out.println("copy");
            iModel.copying();
        }
        if (keyEvent.getCode().equals(KeyCode.V) && keyEvent.isControlDown()){
            System.out.println("paste");
            ArrayList<Groupable> news = iModel.pasting();
            model.addElements(news);
            iModel.clearSelection();
            news.forEach(item -> iModel.setSelected(item));
        }
    }

    public void handleSlider(Double new_val) {
        if(!iModel.getSelected().isEmpty()){
            model.rotateSelected(new_val, iModel.getSelected());
        }
    }
}
