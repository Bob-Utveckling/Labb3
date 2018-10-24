package DrawShapesProgram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SaveSvgStrategy {
    public void save(List<ShapeVariation> shapeList, double canvasWidth, double canvasHeight, String filePath) {
        System.out.println("Saving Svg file...");
        String SvgFileContent;
        SvgFileContent = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" +
                canvasWidth + "\" height=\"" + canvasHeight + "\" version=\"1.1\" >\n" +
                "";
        for (ShapeVariation shape: shapeList) {
            System.out.println("adding shape to Svg content...");
            SvgFileContent += shape.toSvgString() + "\n";
        }
        SvgFileContent += "" +
                "</svg>";

        System.out.println("Finished. File Content:\n " + SvgFileContent);
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(filePath));
            os.write(SvgFileContent.getBytes(), 0, SvgFileContent.length());
            System.out.println("Saved .svg file: '" + filePath + "'");
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
}
