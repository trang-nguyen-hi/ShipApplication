package com.example.a4basics;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ShipView extends StackPane implements ShipModelSubscriber {
    Canvas myCanvas;
    GraphicsContext gc;
    ShipModel model;
    InteractionModel iModel;
    Slider slider;

    public ShipView() {
        myCanvas = new Canvas(1000,700);
        gc = myCanvas.getGraphicsContext2D();

        slider = new Slider();
        slider.setMin(-180);
        slider.setMax(180);
        slider.setValue(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(30);
        slider.setMinorTickCount(10);

        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().addAll(myCanvas, slider);
        this.setStyle("-fx-background-color: black");
    }

    public void setModel(ShipModel newModel) {
        model = newModel;
    }

    public void setInteractionModel(InteractionModel newIModel) {
        iModel = newIModel;
    }

    public void setController(ShipController controller) {
        slider.valueProperty().addListener((ov, old_val, new_val) -> {
            controller.handleSlider((double) new_val);
        });
        myCanvas.setOnMousePressed(e -> controller.handlePressed(e.getX(),e.getY(), e));
        myCanvas.setOnMouseDragged(e -> controller.handleDragged(e.getX(),e.getY(), e));
        myCanvas.setOnMouseReleased(e -> controller.handleReleased(e.getX(),e.getY(), e));
    }

    private void drawShip(Ship ship, boolean selected){
        if (selected) {
            gc.setFill(Color.YELLOW);
            gc.setStroke(Color.CORAL);
        } else {
            gc.setStroke(Color.YELLOW);
            gc.setFill(Color.CORAL);
        }
        gc.fillPolygon(ship.displayXs, ship.displayYs, ship.displayXs.length);
        gc.strokePolygon(ship.displayXs, ship.displayYs, ship.displayXs.length);
    }

    private void drawGroup(ShipGroup group, boolean selected){
        for (Groupable element: group.getChildren()){
            if (element.hasChildren()){
                drawGroup((ShipGroup) element, selected);
            }
            else{
                drawShip((Ship) element, selected);
            }
        }
    }

    public void draw() {
        gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        model.elements.forEach(element -> {
            switch (element){
                case Ship ship -> {this.drawShip(ship,iModel.getSelected().contains(ship));}
                case ShipGroup group -> {
                    this.drawGroup(group, iModel.getSelected().contains(group));
                    if (iModel.getSelected().contains(group)) gc.setStroke(Color.YELLOW);
                    else gc.setStroke(Color.CORAL);
                    gc.strokeRect(group.getLeft(), group.getTop(), group.getRight()- group.getLeft(), group.getBottom()- group.getTop());
                    gc.strokeOval(group.centreX - 5, group.centreY - 5, 10, 10);
                }
                case Groupable groupable -> {
                    System.out.println("Error: object is neither ship nor shipgroup");
                }
            }
        });
        if (iModel.isRubberband()){
            gc.setGlobalAlpha(0.2);
            gc.setFill(iModel.getRubberBand().myColor);
            gc.fillRect(iModel.getRubberBand().left,
                    iModel.getRubberBand().top,
                    iModel.getRubberBand().right - iModel.getRubberBand().left,
                    iModel.getRubberBand().bottom - iModel.getRubberBand().top);
            gc.setGlobalAlpha(1.0);
        }
    }

    @Override
    public void modelChanged() {
        draw();
    }

    @Override
    public void resetSlider(){
        slider.setValue(0);
    }
}
