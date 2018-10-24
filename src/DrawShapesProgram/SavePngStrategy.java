package DrawShapesProgram;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.List;

public class SavePngStrategy {
    public void save(WritableImage givenWim, String filePath) {

        System.out.println("Saving as png file...");
        //GraphicsContext gc = canvas.snapshot()
        //GraphicsContext gc = canvas.getGraphicsContext2D();
        ////WritableImage wim = new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight());
        //...WritableImage image = model.getObservableList().
        ////canvas.snapshot(null,wim);
        //String pathname = "/Users/Bob/Desktop/CanvasImage.png";
        ////String pathname = "CanvasImage.png";
        File file = new File(filePath);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(givenWim, null), "png", file);
            System.out.println("saved png file: " + filePath);
        } catch (Exception e) {
            System.out.println("error: " + e);
        }













    }
}
