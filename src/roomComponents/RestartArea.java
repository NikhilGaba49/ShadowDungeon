package roomComponents;

import Game.RoomContext;
import bagel.util.Point;

/* In this project, a restart area is a stationary object, and only needs
 to be touched & press enter to restart the gameplay.

Defined as a separate class, upon discussion with Stefan since there will
likely be a need to be adjusted in Project 2 - unsure at this stage.  */
public class RestartArea extends StationaryObject{

    // define a new restart area with given filename
    public RestartArea(Point coordinates) {
        super(coordinates, RoomContext.RESTART_AREA_FILENAME);
    }
}
