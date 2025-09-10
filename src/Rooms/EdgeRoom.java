package Rooms;

import Game.Player;
import bagel.Image;
import bagel.util.Point;
import roomComponents.Door;

import java.util.Properties;

public class EdgeRoom extends Room {

    private final Door door;

    public EdgeRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS, String room) {
        super(GAME_PROPS, MESSAGE_PROPS);

        String roomPropertyStem = "door.";
        door = new Door(getCoordinates(roomPropertyStem.concat(room), GAME_PROPS)[0]);
    }

    @Override
    public void drawDoors() {
        door.drawDoor();
    }

    public Door[] getDoors() {
        return new Door[]{door};
    }

    public Point[] getDoorCoords() {
        return new Point[]{door.getDoorCoordinates()};
    }

    @Override
    public void setDoorsUnlocked() {
        door.setDoorUnlocked();
    }

    public Image[] getUnlockedImages(){
        if (door.isDoorUnlocked()) {
            return new Image[]{door.getUnlockedDoorImages()};
        }
        return null;
    }

    public Image[] getLockedDoorImages(){
        return door.getLockedDoorImages();
    }

    @Override
    public void drawStationaryObjects() {
        return;
    }
}