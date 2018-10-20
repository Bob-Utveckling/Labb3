package DrawShapesProgram;

/**
 * File Exporter using Strategy Pattern.
 * Based on Lecture Code and explanations.
 */

public class FileExporterFactory {

    public static FileExporter createAlfaBeta(String val) {
        if (val.equals("png"))
            return new PngExporter();
        else
            return new SvgExporter();
    }
}
