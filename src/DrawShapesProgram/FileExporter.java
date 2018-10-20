package DrawShapesProgram;

/**
 * File Exporter using Strategy Pattern.
 * Based on Lecture Code and explanations.
 */

import java.io.File;
import java.util.List;

public interface FileExporter {
    void saveToFile (File file, List<ShapeVariation> shapeList);

}
