package roomComponents;

import Game.RoomContext;
import bagel.util.Point;

/* Similar to enemy, defined as a separate class because of potentially
 * extending functionality for Project 2 upon discussion with Stefan. */
public class TreasureBox extends StationaryObject {

    // each treasure box has a given coin reward attached
    private final int coinReward;

    // initialise the treasure box with the correct file
    public TreasureBox(Point coordinates, int coinReward) {
        super(coordinates, RoomContext.TREASURE_BOX_FILENAME);
        this.coinReward = coinReward;
    }

    // needed to update coins on the gameplay
    public int getCoinReward() {
        return coinReward;
    }
}
