package DrawShapesProgram;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;

public class SavePngStrategy {
    public void save(WritableImage givenWim, String filePath) {

        System.out.println("Saving Png file...");
        File file = new File(filePath);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(givenWim, null), "png", file);
            System.out.println("Saved .png file: " + filePath);
        } catch (Exception e) {
            System.out.println("error: " + e);
        }













    }
}
