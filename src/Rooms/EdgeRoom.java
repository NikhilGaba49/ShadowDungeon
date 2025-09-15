package Rooms;

import Game.Player;
import Game.RoomContext;
import Game.ShadowDungeon;
import bagel.Image;
import bagel.util.Point;
import roomComponents.Door;
import roomComponents.RestartArea;
import java.util.Properties;

// specific implementation for end room and prep room
public class EdgeRoom extends Room {

    // an edge room only has one door and one restart area
    private final Door door;
    private final RestartArea restart;

    public EdgeRoom(Properties GAME_PROPS,Properties MESSAGE_PROPS,String room){

        // initialise room instance variables
        super(GAME_PROPS, MESSAGE_PROPS);

        // initialising door and restart areas with their coordinates
        final int FIRST_INSTANCE = 0;
        door = new Door(getCoordinates("door.".concat(room), GAME_PROPS)[
                FIRST_INSTANCE]);
        restart = new RestartArea(getCoordinates("restartarea.".concat(room),
                GAME_PROPS)[FIRST_INSTANCE]);

        // again, a room starts off unlocked
        door.setDoorUnlocked();
    }

    /* adding functionality to parent method, where we display messages
    specific to the room (i.e. end room/prep room), in terms of messages */
    @Override
    public void displayRoom(double health, double coins, int roomIndex,
                            boolean gameWon) {

        super.displayRoom(health, coins, roomIndex, gameWon);

        switch (roomIndex){
            case(ShadowDungeon.PREP_ROOM):
                // game has been started, display starting messages
                displayTextProperty(RoomContext.TITLE_PROPERTY,
                        RoomContext.TITLE_FONT_SIZE, RoomContext.EMPTY_STRING,
                        RoomContext.TITLE_Y_COORDINATE);
                displayTextProperty(RoomContext.MOVE_MESSAGE,
                        RoomContext.PROMPT_FONT_SIZE, RoomContext.EMPTY_STRING,
                        RoomContext.MOVE_MESSAGE_Y);
                break;
            case(ShadowDungeon.NUMBER_ROOMS-1):
                // last room reached (has game been won or lost?)
                if (gameWon) {
                    displayTextProperty(RoomContext.GAME_WON_MSG_PROPERTY,
                            RoomContext.TITLE_FONT_SIZE, RoomContext.EMPTY_STRING,
                            RoomContext.TITLE_Y_COORDINATE);
                }
                else {
                    displayTextProperty(RoomContext.GAME_LOST_MSG_PROPERTY,
                            RoomContext.TITLE_FONT_SIZE, RoomContext.EMPTY_STRING,
                            RoomContext.TITLE_Y_COORDINATE);
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

        // since edge room, only need to check one door
        if (door.isDoorUnlocked()) {

            Image[] unlockedDoorImages = {door.getImage()};
            Point[] unlockedDoorCoordinates = {door.getPositionCoordinates()};

            // how does the player interact with the unlocked door?
            int[] touchesResult = player.touchesObstacle(unlockedDoorImages,
                    unlockedDoorCoordinates, player.getPosition());

            if (currentRoomIndex == ShadowDungeon.NUMBER_ROOMS-1) { // last room
                /* only door is unlocked and we cannot go to the next room
                 touchesResult[0] == 1 returns whether player touches
                 unlocked door */
                return new boolean[]{
                        touchesResult[BattleRoom.ANY_OBJECT] == BattleRoom.TRUE,
                        true, false};
            }
            else {
                // prep room case - only door is unlocked - go to next room
                return new boolean[]{
                        touchesResult[BattleRoom.ANY_OBJECT] == BattleRoom.TRUE,
                        true, true};
            }
        }
        // door is not unlocked, player can't intersect it, nor move next room
        return new boolean[] {false, false, false};
    }

    // only one door to unlock
    @Override
    public void setDoorsUnlocked() {
        door.setDoorUnlocked();
    }

    // again, only one door to lock
    public void setDoorLocked() {
        door.setDoorLocked();
    }

    // self-sufficient documentation
    @Override
    public Point getDoorCoordinates() {
        return door.getPositionCoordinates();
    }

    // specific implementation since only obstacle in edge room is locked door
    @Override
    public boolean touchesObstacles(Player player, Point nextMove) {

        if (!door.isDoorUnlocked()) { // door is locked
            Image[] obstacleImages = {door.getImage()};
            Point[] obstacleCoordinates = {door.getPositionCoordinates()};

            return player.touchesObstacle(obstacleImages, obstacleCoordinates,
                    nextMove)[BattleRoom.ANY_OBJECT] == BattleRoom.TRUE;
        }
        //cannot touch an obstacle if door is unlocked
        return false;
    }

    // does player touch restart area? needed to extract image & coordinates
    public boolean touchesRestartArea(Player player) {
        Image[] restartAreaImages = new Image[] {restart.getImage()};
        Point[] restartAreaCoordinates = new Point[] {
                restart.getPositionCoordinates()};

        return player.touchesObstacle(restartAreaImages,
                restartAreaCoordinates,
                player.getPosition())[BattleRoom.ANY_OBJECT] == BattleRoom.TRUE;
    }

    // only stationary objects is the only door and restart area
    @Override
    public void drawStationaryObjects() {
        door.drawObject();
        restart.drawObject();
    }
}