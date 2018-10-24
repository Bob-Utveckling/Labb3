package DrawShapesProgram;

import javafx.scene.paint.Color;

public class ShapeVariation extends Shape {

    //Using composition to make instances of classes
    //available to methods of another class
    public ShapeRectangle rectangle;
    public ShapePoint point;
    public ShapeOval oval;

    public String shapeType;

    //count shape and have this counting as the shapeId
    //count all shapes and access any shape with this count.
    public int shapeIdWithCounting = -1;

    //new shape id that will be the main id
    public int shapeId;

    //Constructor
    public ShapeVariation() {}

    //Constructor
    //can have different constructors depending on shape
    public ShapeVariation (int givenShapeId, String givenShapeType, double getX, double getY, double width, double height, Color givenColor) {
        this.shapeId = givenShapeId;

        switch (givenShapeType) {
            case "rectangle":
                System.out.println("now making a rectangle shape variation");
                this.shapeType = "rectangle";
                this.rectangle = new ShapeRectangle(width, height);
                break;
            case "point":
                System.out.println("now making a point shape variation");
                this.shapeType = "point";
                this.point = new ShapePoint(width, height);
                break;
            case "oval":
                System.out.println("now making an oval shape variation");
                this.shapeType = "oval";
                this.oval = new ShapeOval(width, height);
                break;
        }
        this.color = givenColor;
        this.posX = getX;
        this.posY = getY;
        this.shapeIdWithCounting ++;
        System.out.println("shapeIdWithCounting set to: "
            + this.shapeIdWithCounting);
        System.out.println("created shape with id: " + this.shapeId);
    }

    public String toSvgString() {
        String svgColor = this.color.toString();
        svgColor = "#" + svgColor.substring(2);
        System.out.println("SVG Color: " + svgColor);
        if (this.shapeType == "rectangle") {
            /*System.out.println ("<rect x = '" + this.posX + "' " +
                    "y = '" + this.posY + "' " +
                    "width = '" + this.rectangle.width + "' " +
                    "height = '" + this.rectangle.height + "' " +
                    "fill = '" + this.color + "' / >");
            */
            // <rect x = "25" y = "25" width = "150" height = "150" fill = "orange" / >


            return ("<rect x = '" + this.posX + "' " +
                    "y = '" + this.posY + "' " +
                    "width = '" + this.rectangle.width + "' " +
                    "height = '" + this.rectangle.height + "' " +
                    "fill = '" + svgColor + "' />");

            // <rect x = "25" y = "25" width = "150" height = "150" fill = "orange" / >
        } else if (this.shapeType == "oval") {
            return ("<ellipse cx = '" + this.posX + "' " +
                    "cy = '" + this.posY + "' " +
                    "rx = '" + this.oval.width + "' " +
                    "ry = '" + this.oval.height + "' " +
                    "fill = '" + svgColor + "' />");
            // <circle cx="50" cy="50" r="40" fill="red" />
        } else if (this.shapeType == "point") {
            return ("<ellipse cx = '" + this.posX + "' " +
                    "cy = '" + this.posY + "' " +
                    "rx = '" + this.oval.width + "' " +
                    "ry = '" + this.oval.height + "' " +
                    "fill = '" + svgColor + "' />");
            // <circle cx="50" cy="50" r="40" fill="red" />
            //System.out.println("object's svg to string...");
        }
        return ("Shape not recognized. toSvgString empty");
    }

}
