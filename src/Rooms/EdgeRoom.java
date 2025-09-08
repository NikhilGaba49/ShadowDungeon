package Rooms;

import roomComponents.Door;

import java.util.Properties;

public class EdgeRoom extends Room {

    private Door door;

    public EdgeRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS, String room) {
        super(GAME_PROPS, MESSAGE_PROPS);

        String roomPropertyStem = "door.";
        door = new Door(getCoordinates(roomPropertyStem.concat(room))[0]);
    }

    @Override
    public void drawDoors() {
        door.drawDoor();
    }

    public Door[] getDoors() {
        return new Door[]{door};
    }

    @Override
    public void drawWalls() { // remember to fix up later
        return;
    }

}
