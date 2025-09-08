package roomComponents;

import bagel.Image;
import bagel.util.Point;

public class River {
    public static class Enemy {

        private Point coordinates;
        private final String filename = "res/key_bullet_kin.png";

        public Enemy(Point coordinates) {
            this.coordinates = coordinates;
        }

        public void drawWall() {
            Image wall = new Image(filename);
            System.out.println(coordinates.x);
            System.out.println(coordinates.y);
            wall.draw(coordinates.x, coordinates.y);
        }

    }
}
