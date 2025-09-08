package roomComponents;

import bagel.Image;
import bagel.util.Point;

public class StationaryObjects {

    private final Point coordinates;

    public Point getPositionCoordinates() {
        return coordinates;
    }

    private final String filename;
    private final Image object;

    public StationaryObjects(Point coordinates, String filename) {
        this.coordinates = coordinates;
        this.filename = filename;
        object = new Image(filename);
    }

    public void drawObject() {
        object.draw(coordinates.x, coordinates.y);
    }

    public Image getEnemyImage() {
        if (filename.equals("res/key_bullet_kin.png")){
            return object;
        }
        return null;
    }
}