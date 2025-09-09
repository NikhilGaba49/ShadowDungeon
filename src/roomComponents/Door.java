package roomComponents;

import bagel.*;
import bagel.util.Point;

public class Door {

    private boolean doorUnlocked;
    private Image doorImage;
    private final Point doorCoordinates;

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

    public Image[] getDoorImages() {
        return new Image[]{doorImage};
    }

    public Point getDoorCoordinates() {
        return doorCoordinates;
    }

    public void drawDoor(){
        doorImage.draw(doorCoordinates.x, doorCoordinates.y);
    }
}
