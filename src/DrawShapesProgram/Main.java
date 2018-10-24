
/**
 * Draw Simple Shapes Program. Det enkla ritprogrammet.
 *  hos Martin Blomberg. https://github.com/kappsegla
 *  Java Programmering Kurs Labb 3.
 *  Finns på GitHub: https://github.com/Bob-Utveckling/Labb3
 *  24 okt 2018 21.03, av Bob L.
*/


package DrawShapesProgram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.init();
        //controller.setStage(stage);


        //Scene scene = new Scene (root, 500, 500);
        //scene.getStylesheets().add(getClass().getResource("CSS/CSS.css").toExternalForm());

        stage.setTitle("Det enkla ritprogrammet");
        stage.setScene(new Scene(root));
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
