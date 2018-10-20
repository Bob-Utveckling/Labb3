package DrawShapesProgram;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.util.ArrayList;
import java.util.List;

public class Model {


    //private List<Point2D> points = new ArrayList<>();
    //private ObservableList<Point2D> observableList =
    //        FXCollections.observableList(points);
    private List<ShapeVariation> shapes = new ArrayList<>();
    private ObservableList<ShapeVariation> observableList =
            FXCollections.observableList(shapes);
    //ListView<ShapeVariation> shapeVariationListView = new ListView<>(observableList);

    //public ObservableList<Point2D> getObservableList() { return observableList; }
    //public void setObservableList(ObservableList<Point2D> observableList) {
    //    this.observableList = observableList;
    //}
    public ObservableList<ShapeVariation> getObservableList() { return observableList; }
    public void setObservableList(ObservableList<ShapeVariation> observableList) {
        this.observableList = observableList;
    }


    private ObservableList<String> choiceOptions =
            FXCollections.observableList(new ArrayList<String>());

    public ObservableList<String> getChoiceOptions() {
        return choiceOptions;
    }
    public void setChoiceOptions(ObservableList<String> choiceOptions) {
        this.choiceOptions = choiceOptions;
    }

}
