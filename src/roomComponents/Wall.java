package roomComponents;

import bagel.util.Point;

/* Similar to enemy, defined as a separate class because of potentially extending
 * functionality for Project 2 upon discussion with Stefan. */
public class Wall extends StationaryObject {

    // initialise the wall with the correct file
    public Wall(Point coordinates) {
        super(coordinates, "res/wall.png");
    }
}
