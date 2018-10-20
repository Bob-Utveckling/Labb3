package DrawShapesProgram;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;

import java.util.ArrayList;


public class Controller {

    @FXML
    Label label1, label2;
    @FXML
    Canvas canvas;
    @FXML
    ListView list1;
    @FXML
    ColorPicker colors1;
    @FXML
    Slider sizeSliderX, sizeSliderY;
    @FXML
    TextField shapePosX, shapePosY;
    @FXML
    Button undoButton, redoButton;

    Model model = new Model();

    //the id, that refers to the shape active for interactions:
    public int currentActiveShapeId = -1;
    public String activeShapeIs = "oval"; //user selects a shape to draw

    /** if user has just clicked to draw a new shape,
     * change of shape property should not occur
     * because user is on new shape and not the previous.
     * with initial start, there is no disabling so:   */
    boolean disableShapePropertyUpdate = false;





    public void init() {
        draw(); System.out.println("Initial draw");
        undoButton.setDisable(true); //disable the Undo button at start
        redoButton.setDisable(true); //disable the Redo button at start

        model.getObservableList().addListener((ListChangeListener<ShapeVariation>) c -> {

            //draw:
            draw();
            disableShapePropertyUpdate = false; //adding a shape occured, plus drawing. User can update shape!
            //interact with last item in list, i.e. last shape:
            currentActiveShapeId = model.getObservableList().size()-1;
            interactWithCurrentlySelectedObject(currentActiveShapeId);

            //ListView -- show list of Observed shapes.
            //fill ListView list1.
            //fill via CellFactory
            //thanks to:
            // listView descriptions available via lectures + online!
            // and at: https://stackoverflow.com/questions/36657299/how-can-i-populate-a-listview-in-javafx-using-custom-objects
            // and at: https://docs.oracle.com/javafx/2/ui_controls/list-view.htm

            list1.setItems(model.getObservableList());
            list1.setCellFactory(param ->
                    new ListCell<ShapeVariation>() {
                        @Override
                        protected void updateItem(ShapeVariation shapevar, boolean empty) {
                            super.updateItem(shapevar, empty);
                            if (empty || shapevar == null) {
                                setText(null);
                            } else {
                                setText(shapevar.shapeType +
                                        " at X: " +
                                        (int) shapevar.posX +
                                        ", Y: " +
                                        (int) shapevar.posY);
                            }
                        }
                    }
            );

        });

        //});
        //model.getObservableList().addListener((ListChangeListener<Point2D>) c ->
        //        System.out.println("c: " + c));


        //        model.getObservableList().addListener(new ListChangeListener() {
        //          @Override
        //        public void onChanged(ListChangeListener.Change change) {
        //          System.out.println("test");
        //    }
        //});

    }

    //variable to use for undo/redo:
    //declare an original -- instantiated at user/canvas interaction time.
    //This variable will be the reference when redos are added one by one up to the original level.
    //The variable only becomes a final version when user draws on canvas.
    ArrayList<ShapeVariation> originalNotObservedListOfShapes;


    /**
     * undoing drawing an object activates the Redo button,
     * removes the last most object from observable list,
     * checks if any errors might occur.
     */
    public void undoRequested() {
        System.out.println("Undo requested");
        redoButton.setDisable(false);
        System.out.println("keep original: " + originalNotObservedListOfShapes);
        int listSize = model.getObservableList().size();
        System.out.println("working with: " +model.getObservableList());
        try {
            model.getObservableList().remove(listSize - 1);
            if (model.getObservableList().size() == 0) { undoButton.setDisable(true);}
            System.out.println("working actual: " + model.getObservableList());
            System.out.println("original: " + originalNotObservedListOfShapes);
        } catch (Exception e) {
            System.out.println("did not undo. " + e);
            undoButton.setDisable(true);
        }
    }

    /**
     * redoing works with a copy of the observed list
     * that comes with every canvas click,
     * and while tracking the observed list's size,
     * add one object to it based on the not-observed-and-original copy
     * of the observed list.
     * also disables redo button if redo already done with last most object.
     */
    public void redoRequested() {
        System.out.println("Redo requested");
        try {
            undoButton.setDisable(false);
            int originalListSize = originalNotObservedListOfShapes.size();
            System.out.println("original list size: " + originalNotObservedListOfShapes.size());
            int currentListSize = model.getObservableList().size();
            System.out.println("current list size: " + currentListSize);
            if ((originalListSize - currentListSize) > 0) {
                System.out.println("do a redo");
                System.out.println("return object with id: " + currentListSize);

                //shape exists in original copy which does not yet exist in
                //the working copy/the model.getObservableList copy of list.
                // so add up one object to the observed list:
                model.getObservableList().add(originalNotObservedListOfShapes.get(currentListSize));
                if (model.getObservableList().size() == originalNotObservedListOfShapes.size()) {
                    redoButton.setDisable(true);
                }
            } else {
                System.out.println("no object to redo.");
                redoButton.setDisable(true);

            }
        } catch (Exception e) {
            System.out.println("error occured: " + e);
            redoButton.setDisable(true);
        }
    }



    /** on change of value for position y
     / update the current object's position y
     */
    public void updateTheShapePosY() {
        if (!disableShapePropertyUpdate) {
            System.out.println("current shape id:" + currentActiveShapeId);
            double newPosY;
            String getPosY = shapePosY.getText();
            try {
                newPosY = Double.parseDouble(getPosY);
                System.out.println("new pos Y: " + getPosY);
                ShapeVariation shapeToUpdate = model.getObservableList().get(currentActiveShapeId);
                shapeToUpdate.posY = newPosY;
                draw();
                highlightTheSelectedObject(currentActiveShapeId);
            } catch (Exception e) {
                System.out.println("wrong value type. No change made to shape value y");
            }
        }
    }

    /** on change of value for position x
     / update the current object's position x
     */
    public void updateTheShapePosX() {
        if (!disableShapePropertyUpdate) {
            System.out.println("current shape id:" + currentActiveShapeId);
            double newPosX;
            String getPosX = shapePosX.getText();
            //if (!getPosx.matches("\\d*")) {
            //    getPosx = getPosx.replaceAll("[^\\d]", "");
            //    //double newPosX = (double) getPosx;
            //}
            try {
                newPosX = Double.parseDouble(getPosX);
                System.out.println("new pos X: " + getPosX);
                ShapeVariation shapeToUpdate = model.getObservableList().get(currentActiveShapeId);
                shapeToUpdate.posX = newPosX;
                draw();
                highlightTheSelectedObject(currentActiveShapeId);
            } catch (Exception e) {
                System.out.println("wrong value type. No change made to shape value x");
            }
        }
    }


    /** on change of slider for size y (height)
    / update the current object's height
    */
    public void updateTheShapeHeight() {
        if (!disableShapePropertyUpdate) {
            double newSizeY = sizeSliderY.getValue();
            System.out.println("new height: " + newSizeY);
            System.out.println("current shape id:" + currentActiveShapeId);
            ShapeVariation shapeToUpdate = model.getObservableList().get(currentActiveShapeId);
            if (shapeToUpdate.shapeType.equals("oval")) {
                shapeToUpdate.oval.height = newSizeY;
            } else if (shapeToUpdate.shapeType.equals("rectangle")) {
                shapeToUpdate.rectangle.height = newSizeY;
            } else if (shapeToUpdate.shapeType.equals("point")) {
                shapeToUpdate.point.height = newSizeY;
            }
            draw();
            highlightTheSelectedObject(currentActiveShapeId);
        }
    }

    /** on change of slider for size x (width)
    / update the current object's width
    */
    public void updateTheShapeWidth() {
        if (!disableShapePropertyUpdate) {
            double newSizeX = sizeSliderX.getValue();
            System.out.println("new width: " + newSizeX);
            System.out.println("current shape id:" + currentActiveShapeId);
            ShapeVariation shapeToUpdate = model.getObservableList().get(currentActiveShapeId);
            if (shapeToUpdate.shapeType.equals("oval")) {
                shapeToUpdate.oval.width = newSizeX;
            } else if (shapeToUpdate.shapeType.equals("rectangle")) {
                shapeToUpdate.rectangle.width = newSizeX;
            } else if (shapeToUpdate.shapeType.equals("point")) {
                shapeToUpdate.point.width = newSizeX;
            }
            draw();
            highlightTheSelectedObject(currentActiveShapeId);
        }
    }

    /** given a shape id, update property section to reflect
     * the shape.
     */
    public void interactWithCurrentlySelectedObject (int shapeId) {
        try {
            //which shape to interact with:
            System.out.println("Shape Id received to interact with: " + shapeId);
            ShapeVariation shapeToInteractWith = model.getObservableList().get(shapeId);
            highlightTheSelectedObject(shapeId);
            switch (shapeToInteractWith.shapeType) {
                //can even have without switch as long as all properties are common
                case "oval":
                    sizeSliderX.adjustValue(shapeToInteractWith.oval.width);
                    sizeSliderY.adjustValue(shapeToInteractWith.oval.height);
                    shapePosX.setText(Double.toString(shapeToInteractWith.posX));
                    shapePosY.setText(Double.toString(shapeToInteractWith.posY));
                    //ColorPicker.
                    break;
                case "rectangle":
                    sizeSliderX.adjustValue(shapeToInteractWith.rectangle.width);
                    sizeSliderY.adjustValue(shapeToInteractWith.rectangle.height);
                    shapePosX.setText(Double.toString(shapeToInteractWith.posX));
                    shapePosY.setText(Double.toString(shapeToInteractWith.posY));
                    break;
                case "point":
                    sizeSliderX.adjustValue(shapeToInteractWith.oval.width);
                    sizeSliderY.adjustValue(shapeToInteractWith.oval.height);
                    shapePosX.setText(Double.toString(shapeToInteractWith.posX));
                    shapePosY.setText(Double.toString(shapeToInteractWith.posY));
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error. could not interact with shape id: " + shapeId +
                    " error: " + e);
        }
    }


    /** draw a border around the currently selected object
     * so that it is obvious which shape the user is working with
     */
    public void highlightTheSelectedObject (int givenCurrentActiveShapeId) {
        ShapeVariation highlightedShape = model.getObservableList().get(givenCurrentActiveShapeId);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Paint p = Color.BLACK;
        gc.setStroke(p);
        gc.setLineWidth(0.2);
        gc.strokeLine (highlightedShape.posX - 15,
                highlightedShape.posY,
                highlightedShape.posX + 30,
                highlightedShape.posY);

        gc.strokeLine(highlightedShape.posX,
                highlightedShape.posY - 15,
                highlightedShape.posX,
                highlightedShape.posY + 30 );
        //gc.setLineDashes();
        //gc.setLineDashes(100,100,100,150,23);
    }


    /** As mouse moves around the canvas,
     * make an X and Y description and show it.
     * */
    public void followMouseAndShowDetails (MouseEvent event) {
        //System.out.println("mouse entered");
        label2.setText("X: " + event.getX() + ", Y: " + event.getY());
    }


    public void resetCanvas (ActionEvent actionEvent) {

        originalNotObservedListOfShapes.clear(); //clear the original copy object. helps with undo/redo
        redoButton.setDisable(true);
        undoButton.setDisable(true);

        System.out.println("reset canvas");
        System.out.println("before: " + model.getObservableList());
        model.getObservableList().clear();
        System.out.println("after: " + model.getObservableList());
    }


    public void setActiveShapeAsRectangle(ActionEvent actionEvent) {
        disableShapePropertyUpdate = true; //disable shape property change. this is a new shape
        activeShapeIs = "rectangle"; //System.out.println("Rectangle");
        label1.setText("Draw a new " + activeShapeIs);
        //issue a draw() command so the highlighting can be taken off
        draw();

    }

    public void setActiveShapeAsOval(ActionEvent actionEvent) {
        disableShapePropertyUpdate = true; //disable shape property change. this is a new shape
        activeShapeIs = "oval"; //System.out.println("Rectangle");
        label1.setText("Draw a new " + activeShapeIs);
        //issue a draw() command so the highlighting can be taken off
        draw();
    }

    public void setActiveShapeAsPoint(ActionEvent actionEvent) {
        disableShapePropertyUpdate = true; //disable shape property change. this is a new shape
        activeShapeIs = "point"; //System.out.println("Rectangle");
        label1.setText("Draw a new " + activeShapeIs);
        //issue a draw() command so the highlighting can be taken off
        draw();
    }


    public void canvasClicked(MouseEvent event) {
        System.out.println("Draw a: " + activeShapeIs);
        //ShapeCircle newCircle = new ShapeCircle(Color.ORANGE, event.getX(), event.getY(), 100);
        //ShapeRectangle theNewRectangle = new ShapeRectangle(Color.AQUA, event.getX(), event.getY(), 100, 30);

        //mechanism to know what kind of object to draw:
        // add shapeType + shape object to ShapeVariation
        ShapeVariation newShape = new ShapeVariation(
                activeShapeIs,
                event.getX(), event.getY(),
                sizeSliderX.getValue(), sizeSliderY.getValue(),
                colors1.getValue()
        );

        model.getObservableList().add(newShape);

        undoButton.setDisable(false); //activate the Undo button on the way

        // use for knowing original list and its items at undo/redo process:
        // have an original version of observable list.
        // this helps knowing when canvas-clicking has occured and a
        // new user/canvas interaction occured and thus
        // what is on canvas is default and final copy of interaction:
        originalNotObservedListOfShapes =
                new ArrayList<ShapeVariation>(model.getObservableList());
        System.out.println("class for the originalNotObse.. is: " + originalNotObservedListOfShapes.getClass());
        System.out.println("is: " + originalNotObservedListOfShapes);


        //Point2D point = new Point2D( event.getX(), event.getY() );
        //model.getObservableList().add(point);

        //System.out.println(point);
        //System.out.println("Observable list: " + model.getObservableList());
        //draw();
    }


    public void draw() {
        //System.out.println("draw: ...");
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //gc.setFill(Color.WHITE);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (ShapeVariation theShape : model.getObservableList()) {
            drawShape(theShape);
        }

        Paint p = Color.BLACK;
        gc.setStroke(p);
        //Paint p = colors1.getValue();
        //gc.setFill(p);
        gc.setLineWidth(5.0);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public void drawShape(ShapeVariation givenShape) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        switch (givenShape.shapeType) {
            case "rectangle" :
                System.out.println("DRAW RECTANGLE");
                gc.setFill(givenShape.color);
                gc.fillRect(
                        givenShape.posX,
                        givenShape.posY,
                        givenShape.rectangle.width,
                        givenShape.rectangle.height);
                break;
            case "oval" :
                System.out.println("DRAW OVAL");
                //gc.fillOval(point.getX() - 10, point.getY() - 10, 20, 20);
                gc.setFill(givenShape.color);
                gc.fillOval(
                        givenShape.posX,
                        givenShape.posY,
                        givenShape.oval.width,
                        givenShape.oval.height);
                break;
            case "point" :
                System.out.println("DRAW POINT");
                //gc.fillOval(point.getX() - 10, point.getY() - 10, 20, 20);
                gc.setFill(givenShape.color);
                gc.fillOval(
                        givenShape.posX,
                        givenShape.posY,
                        givenShape.point.width,
                        givenShape.point.height
                );
                        //point.getX() - 10, point.getY() - 10, 20, 20);
                break;
        }
    }

}



