package roomComponents;

import bagel.*;
import bagel.util.Point;

/* Doors are stationary objects. Functionality extended by below attributes
 and methods. */
public class Door extends StationaryObject {

    private boolean doorUnlocked;
    private final static String lockedDoorFile = "res/locked_door.png";
    private final static String unlockedDoorFile = "res/unlocked_door.png";

    // setting the door at the correct coordinates with the right filename
    public Door(Point doorCoordinates) {
        super(doorCoordinates, lockedDoorFile);
        this.doorUnlocked = false;
    }

    // getter function to check whether a door is unlocked or not
    public boolean isDoorUnlocked() {
        return doorUnlocked;
    }

    // updating the doorUnlocked boolean and the corresponding images
    public void setDoorUnlocked() {
        doorUnlocked = true;
        setImageFilename(unlockedDoorFile);
    }
    // similar, but complementing it to set door locked, rather than unlocked
    public void setDoorLocked() {
        doorUnlocked = false;
        setImageFilename(lockedDoorFile);
    }
}
