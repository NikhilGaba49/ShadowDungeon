package Rooms;

import Game.Player;
import bagel.Image;
import bagel.util.Point;
import roomComponents.Door;
import roomComponents.RestartArea;

import java.util.Properties;

import static Game.ShadowDungeon.NUMBER_ROOMS;

public class EdgeRoom extends Room {

    private final Door door;
    private final RestartArea restartArea;

    public EdgeRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS, String room) {
        super(GAME_PROPS, MESSAGE_PROPS);

        String roomPropertyStem = "door.";
        door = new Door(getCoordinates(roomPropertyStem.concat(room), GAME_PROPS)[0]);
        restartArea = new RestartArea(getCoordinates("restartarea.".concat(room), GAME_PROPS)[0]);

        door.setDoorUnlocked();
    }

    @Override
    public void displayRoom(double health, double coins, int roomIndex, boolean gameWon) {

        super.displayRoom(health, coins, roomIndex, gameWon);

        switch (roomIndex){
            case(0):
                displayTextProperty("title", "title.fontSize", "", "title.y");
                displayTextProperty("moveMessage", "prompt.fontSize", "", "moveMessage.y");
                break;
            case(NUMBER_ROOMS-1):
                if (gameWon) {
                    displayTextProperty("gameEnd.won", "title.fontSize", "", "title.y");
                }
                else {
                    displayTextProperty("gameEnd.lost", "title.fontSize", "", "title.y");
                }
                break;
        }
    }

    /* Returns an array of booleans with key properties.
    *       - Index 0: Player touches at least one unlocked door
    *       - Index 1: All doors within room are unlocked
    *       - Index 2: Player needs to go to the next room
    * */
    @Override
    public boolean[] touchesUnlockedDoor(Player player, int currentRoomIndex) {

        if (door.isDoorUnlocked()) {
            Image[] unlockedDoorImages = {door.getImage()};
            Point[] unlockedDoorCoordinates = {door.getPositionCoordinates()};

            int[] touchesResult = player.touchesObstacle(unlockedDoorImages, unlockedDoorCoordinates, player.getPosition());

            if (currentRoomIndex == NUMBER_ROOMS-1) {
                return new boolean[]{touchesResult[0] == 1, true, false};
            }
            else {
                return new boolean[]{touchesResult[0] == 1, true, true};
            }
        }
        return new boolean[] {false, false, false};
    }

    @Override
    public void setDoorsUnlocked() {
        door.setDoorUnlocked();
    }

    public void setDoorLocked() {
        door.setDoorLocked();
    }

    @Override
    public Point getDoorCoordinates() {
        return door.getPositionCoordinates();
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

    public boolean touchesRestartArea(Player player) {
        Image[] restartAreaImages = new Image[] {restartArea.getImage()};
        Point[] restartAreaCoordinates = new Point[] {restartArea.getPositionCoordinates()};
        return player.touchesObstacle(restartAreaImages, restartAreaCoordinates, player.getPosition())[0] == 1;
    }

    @Override
    public void drawStationaryObjects() {
        door.drawObject();
        restartArea.drawObject();
    }
}