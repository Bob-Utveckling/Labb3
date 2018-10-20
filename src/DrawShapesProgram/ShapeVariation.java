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


    //Constructor
    //can have different constructors depending on shape
    public ShapeVariation (String givenShapeType, double getX, double getY, double width, double height, Color givenColor) {
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
        System.out.println("shape id set to: "
            + this.shapeIdWithCounting);

    }

}
