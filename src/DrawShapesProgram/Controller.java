package DrawShapesProgram;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    Button undoButton, redoButton, saveAsPngButton;

    Model model = new Model();

    //the id, that refers to the shape active for interactions:
    public int currentActiveShapeId = -1;
    public String activeShapeIs = "oval"; //user selects a shape to draw

    public int newShapeId = -1;


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
            //interact with shapes in list, initially, the Controller valued shapeId:
            currentActiveShapeId = newShapeId;
            System.out.println("-- new shape id:" + newShapeId);
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
                                setText(
                                        shapevar.shapeId + 1 + ". " +
                                        shapevar.shapeType +
                                        " at X: " +
                                        (int) shapevar.posX +
                                        ", Y: " +
                                        (int) shapevar.posY);
                            }
                        }
                    }
            );

            /*
            list1.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<String>() {
                        public void changed(ObservableValue<? extends String> ov,
                                            String old_val, String new_val) {
                            label1.setText(new_val);
                        }}
            );
            */

            //thanks to: https://stackoverflow.com/questions/40230840/after-observable-list-change-javafx8-listview-still-shows-portion-of-old-list
            //with user selected list item, start interacting with item.
            list1.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<ShapeVariation>() {
                        public void changed(ObservableValue<? extends ShapeVariation> ov,
                                            ShapeVariation old_val, ShapeVariation new_val) {
                            label1.setText(new_val.shapeType);
                            currentActiveShapeId = new_val.shapeId;
                            interactWithCurrentlySelectedObject(currentActiveShapeId);
                            System.out.println("current active shape id has become: " + currentActiveShapeId);
                        }
                    }
            );

            /*
            list1.getItems().addListener(new ListChangeListener() {
                @Override
                public void onChanged (ListChangeListener.Change change) {
                    System.out.println("detected a change");
                }
            });
            */

            /*
            list1.getSelectionModel().selectedItemProperty().addListener((
                prop, old, new1 ) -> {
                System.out.println("Ok");
                System.out.println(prop.getClass());
                System.out.println(old);
                System.out.println(new1.getClass());
            });
            */
        });

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
            System.out.println("remove last object...");
            model.getObservableList().remove(listSize - 1);
            newShapeId = newShapeId - 1; //bug because newshapeId is changed via addListener
            System.out.println("new shape id: " + newShapeId);
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


    /** Open a dialog box to give alternative
     * to save canvas either as an SVG XML based image
     * or PNG Snapshot of canvas
     */
    public void saveAs() {
        System.out.println("Save as...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        String path = System.getProperty("user.home") + File.separator;
        File initialDir = new File(path);
        fileChooser.setInitialDirectory(initialDir);

        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter("PNG-image (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SVG-image (*.svg)", "*.svg"));

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());

        if (file.getName().endsWith(".png")) {
            System.out.println("Start png strategy, saving '" + file.getName() + "'...");
            //(!) can, instead of passing detailed parameters like
            //canvas, do a call that is similar in all strategies
            //for example: all strategies take a File path and a ListOfShapes.
            WritableImage wim = new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight());
            System.out.println(("class of object to be passed on is: " +
                    canvas.snapshot(null,wim)).getClass());
            new SavePngStrategy().save(
                    canvas.snapshot(null,wim)
                    , file.getPath());

        } else if (file.getName().endsWith(".svg")) {
            System.out.println("Start Svg strategy, saving " + file.getName() + "...");
            new SaveSvgStrategy().save(model.getObservableList(),
                    canvas.getWidth(),
                    canvas.getHeight(),
                    file.getPath()
            );
        }
    }

    /*OBSOLETE code:
    public void saveAsPng() {
        System.out.println("Saving as png file...");
        //GraphicsContext gc = canvas.snapshot()
        //GraphicsContext gc = canvas.getGraphicsContext2D();
        WritableImage wim = new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight());
        //...WritableImage image = model.getObservableList().
        canvas.snapshot(null,wim);
        //String pathname = "/Users/Bob/Desktop/CanvasImage.png";
        String pathname = "CanvasImage.png";
        File file = new File(pathname);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
            System.out.println("saved png file: " + pathname);
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    }
    */

    /* OBSOLETE Code:
    public void saveAsSvg() {
        System.out.println("save as Svg");
        String pathname = "/Users/Bob/Desktop/CanvasImage.svg";
        String SvgFileContent;
        SvgFileContent = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" +
                canvas.getWidth() + "\" height=\"" + canvas.getHeight() + "\" version=\"1.1\" >\n" +
                "";
        for (ShapeVariation shape: model.getObservableList()) {
           System.out.println("adding shape...");
            SvgFileContent += shape.toSvgString() + "\n";
        }
        SvgFileContent += "" +
                "</svg>";
        System.out.println("File Content: " + SvgFileContent);
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(pathname));
            os.write(SvgFileContent.getBytes(), 0, SvgFileContent.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */


    /** on change of value for position y
     / update the current object's position y
     */
    public void updateTheShapePosY() {
        if (!disableShapePropertyUpdate) {
            System.out.println("update pos Y. active shape id:" + currentActiveShapeId);
            String givenPosY = shapePosY.getText();
            try {
                double okPosY = Double.parseDouble(givenPosY);
                model.getObservableList().get(currentActiveShapeId).posY = okPosY;
                draw();
                highlightTheSelectedObject(currentActiveShapeId);
            } catch (Exception e) {
                System.out.println("Wrong value." +
                        "No change made to shape value y. " +
                        e);
            }
        }
    }

    /** on change of value for position x with a new x
     / call function to update the current shape's position x
     */
    public void updateTheShapePosX() {
        if (!disableShapePropertyUpdate) {
            System.out.println("update pos X. active shape id:" + currentActiveShapeId);
            String givenPosX = shapePosX.getText();
            try {
                double okPosX = Double.parseDouble(givenPosX);
                model.getObservableList().get(currentActiveShapeId).posX = okPosX;
                draw();
                highlightTheSelectedObject(currentActiveShapeId);
            } catch (Exception e) {
                System.out.println("Wrong value." +
                        "No change made to shape value x. " +
                        e);
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


    /** do a new canvas */
    public void resetCanvas (ActionEvent actionEvent) {
        originalNotObservedListOfShapes.clear(); //clear the original copy object. helps with undo/redo
        newShapeId = -1;
        redoButton.setDisable(true);
        undoButton.setDisable(true);

        System.out.println("reset canvas");
        System.out.println("before: " + model.getObservableList());
        model.getObservableList().clear();
        System.out.println("after: " + model.getObservableList());
    }

    /** set the active shape to
     *  "a rectangle" for user interaction
     */
    public void setActiveShapeAsRectangle(ActionEvent actionEvent) {
        disableShapePropertyUpdate = true; //disable shape property change. this is a new shape
        activeShapeIs = "rectangle"; //System.out.println("Rectangle");
        label1.setText("Draw a new " + activeShapeIs);
        //issue a draw() command so the highlighting can be taken off
        draw();

    }

    /** set the active shape to
     * "an oval" for user interaction
     */
    public void setActiveShapeAsOval(ActionEvent actionEvent) {
        disableShapePropertyUpdate = true; //disable shape property change. this is a new shape
        activeShapeIs = "oval"; //System.out.println("Rectangle");
        label1.setText("Draw a new " + activeShapeIs);
        //issue a draw() command so the highlighting can be taken off
        draw();
    }

    /** set the active shape to
     * "a point" for user interaction
     */
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
                ++newShapeId,
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



