package DrawShapesProgram;

/* NOT ACCESSED IN PROGRAM. FOR WORK, LATER ON.
    A class in order to organize shape update
    on new changes
 */

import javafx.collections.ObservableList;

import java.util.List;

public class UpdateShape {

    public static int updateTheShapePosY (
                            ObservableList<ShapeVariation> allShapes,
                            int givenShapeId,
                            String givenNewPosY) {
        double newPosY;
        try {
            newPosY = Double.parseDouble(givenNewPosY);
            System.out.println("new pos Y: " + givenNewPosY);
            ShapeVariation shapeToUpdate = new ShapeVariation();
            //find the shape in shape list which has the given id:
            int i = -1;
            int shapeToUpdate_i = -1;
            for(ShapeVariation shape : allShapes) {
                i++;
                if (shape.shapeId == givenShapeId) {
                    shapeToUpdate_i = i;
                }
            }
            allShapes.get(i).posY = newPosY;

            return (shapeToUpdate_i);

        } catch (Exception e) {
            System.out.println("wrong value type or other error." +
                    "No change made to shape value y. " +
                    e);
            return (-1);
        }
    }
}
