package Rooms;

import Game.Player;
import bagel.Image;
import bagel.util.Point;
import roomComponents.Door;

import java.util.Arrays;
import java.util.Properties;

public class EdgeRoom extends Room {

    private final Door door;

    public EdgeRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS, String room) {
        super(GAME_PROPS, MESSAGE_PROPS);

        String roomPropertyStem = "door.";
        door = new Door(getCoordinates(roomPropertyStem.concat(room), GAME_PROPS)[0]);
    }

    public boolean[] touchesUnlockedDoor(Player player) {
        if (door.isDoorUnlocked()) {
            Image[] unlockedDoorImages = {door.getImage()};
            Point[] unlockedDoorCoordinates = {door.getPositionCoordinates()};
            return new boolean[] {player.touchesObstacle(unlockedDoorImages, unlockedDoorCoordinates, player.getPosition())[0] == 1, true};
        }
        return new boolean[] {false};
    }

    @Override
    public void drawDoors() {
        door.drawObject();
    }

    @Override
    public void setDoorsUnlocked() {
        door.setDoorUnlocked();
    }

    public void setDoorLocked() {
        door.setDoorLocked();
    }

    @Override
    public boolean touchesObstacles(Player player, Point nextMove) {
        if (!door.isDoorUnlocked()) {
            Image[] obstacleImages = {door.getImage()};
            Point[] obstacleCoordinates = {door.getPositionCoordinates()};
            return player.touchesObstacle(obstacleImages, obstacleCoordinates, nextMove)[0] == 1;
        }
        return false;
    }

    @Override
    public void drawStationaryObjects() {
        return;
    }
}