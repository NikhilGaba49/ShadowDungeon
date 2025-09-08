package roomComponents;

import bagel.Image;
import bagel.util.Point;

public class Wall {

    private Point coordinates;
    private final String filename = "res/wall.png";

    public Wall(Point coordinates) {
        System.out.println(coordinates);
        this.coordinates = coordinates;
    }

    public void drawWall() {
        Image wall = new Image(filename);
        System.out.println(coordinates.x);
        System.out.println(coordinates.y);
        wall.draw(coordinates.x, coordinates.y);
    }
}
