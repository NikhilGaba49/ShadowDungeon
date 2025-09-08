package Rooms;

import roomComponents.Door;
import roomComponents.River;
import roomComponents.Wall;

import java.util.Properties;

public class BattleRoom extends Room {

    Door primaryDoor;
    Door secondaryDoor;

    Wall[] walls;
    River[] rivers;

    public BattleRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS, String room) {
        super(GAME_PROPS, MESSAGE_PROPS);

        String primaryDoorStem = "primarydoor.";
        String secondaryDoorStem = "secondarydoor.";
        primaryDoor = new Door(getCoordinates(primaryDoorStem.concat(room))[0]);
        secondaryDoor = new Door(getCoordinates(secondaryDoorStem.concat(room))[0]);

        walls = new Wall[getCoordinates("wall.".concat(room)).length];
        for (int i=0; i<walls.length; i++) {
            walls[i] = new Wall(getCoordinates("wall.".concat(room))[i]);
        }
    }

    @Override
    public void drawDoors() {
        primaryDoor.drawDoor();
        secondaryDoor.drawDoor();
    }

    public void drawWalls() {
        for (Wall wall : walls) {
            wall.drawWall();
        }
    }

    @Override
    public Door[] getDoors() {
        return new Door[]{primaryDoor, secondaryDoor};
    }
}
