package roomComponents;

import bagel.*;
import bagel.util.Point;

public class Door {

    // instance variables describing the state of the door
    private boolean doorUnlocked;
    private Image doorImage;
    private final Point doorCoordinates;

    // constructor
    public Door(Point doorCoordinates) {
        this.doorCoordinates = doorCoordinates;
        this.doorImage = new Image("res/locked_door.png");
        this.doorUnlocked = false;
    }

    public void setDoorUnlocked() {
        doorUnlocked = true;
        doorImage = new Image("res/unlocked_door.png");
    }

    public boolean isDoorUnlocked() {
        return doorUnlocked;
    }

    public Image getUnlockedDoorImages() {
        if (doorUnlocked) {
            return doorImage;
        }
        return null;
    }

    public Image[] getLockedDoorImages() {
        if (!doorUnlocked) {
            return new Image[]{doorImage};
        }
        return null;
    }

    public Point getDoorCoordinates() {
        return doorCoordinates;
    }

    public void drawDoor(){
        doorImage.draw(doorCoordinates.x, doorCoordinates.y);
    }
}
