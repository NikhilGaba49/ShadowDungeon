package roomComponents;

import Game.RoomContext;
import bagel.util.Point;

/* In this project, an enemy is stationary, and only needs to be touched to be
 defeated and removed from the gameplay.

Defined as a separate class, upon discussion with Stefan since there will
likely be a need to be adjusted in Project 2 - unsure at this stage.  */
public class Enemy extends StationaryObject {

    // initialise the enemy with the correct file
    public Enemy(Point coordinates) {
        super(coordinates, RoomContext.ENEMY_FILENAME);
    }
}