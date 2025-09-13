package roomComponents;

import bagel.util.Point;

/* Similar to enemy, defined as a separate class because of potentially extending
* functionality for Project 2 upon discussion with Stefan. */
public class River extends StationaryObject{

    // initialise the river with the correct file
    public River(Point coordinates) {
        super(coordinates, "res/river.png");
    }

}
