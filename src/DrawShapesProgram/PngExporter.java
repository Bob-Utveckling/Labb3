package DrawShapesProgram;

import javafx.fxml.FXML;

/**
 * File Exporter using Strategy Pattern.
 * Based on Lecture Code and explanations.
 */

import java.io.File;
import java.util.List;

public class PngExporter implements FileExporter {
    @Override
    public void saveToFile(File file, List<ShapeVariation> shapeList) {
        //add code to save as png
        //...WritebleImage
        System.out.println("save as png... Png Exporter");
    }
}