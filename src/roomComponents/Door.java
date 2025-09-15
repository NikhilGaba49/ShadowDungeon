package roomComponents;

import Game.RoomContext;
import Rooms.Room;
import bagel.util.Point;

/* Doors are stationary objects. Functionality extended by below attributes
 and methods. */
public class Door extends StationaryObject {

    // added attributes include doorUnlocked flag as well as locked and
    // unlocked door filenames (constant for class)
    private boolean doorUnlocked;

    // setting the door at the correct coordinates with the right filename
    public Door(Point doorCoordinates) {
        super(doorCoordinates, RoomContext.LOCKED_DOOR_FILENAME);
        this.doorUnlocked = false; // start off with unlocked door
    }

    // check whether a door is unlocked or not
    public boolean isDoorUnlocked() {
        return doorUnlocked;
    }

    // updating the doorUnlocked boolean and the corresponding images
    public void setDoorUnlocked() {
        doorUnlocked = true;
        setImageFilename(RoomContext.UNLOCKED_DOOR_FILENAME);
    }
    // similar, but complementing it to set door locked, rather than unlocked
    public void setDoorLocked() {
        doorUnlocked = false;
        setImageFilename(RoomContext.LOCKED_DOOR_FILENAME);
    }
}
