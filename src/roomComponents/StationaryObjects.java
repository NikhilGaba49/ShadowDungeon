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
    public Image getRiverImage() {
        if (filename.equals("res/river.png")) {
            return object;
        }
        return null;
    }

    public Image getTreasureBoxImage() {
        if (filename.equals("res/treasure_box.png")) {
            return object;
        }
        return null;
    }

    public Image getWallImage() {
        if (filename.equals("res/wall.png")) {
            return object;
        }
        return null;
    }

}