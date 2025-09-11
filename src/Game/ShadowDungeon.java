package Game;

import Rooms.BattleRoom;
import Rooms.EdgeRoom;
import Rooms.Room;

import bagel.*;
import bagel.util.Point;
import roomComponents.Door;
import roomComponents.StationaryObjects;

import java.util.Arrays;
import java.util.Properties;

public class ShadowDungeon extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final Player player;
    public final int SPEED ;

    private static final int NUMBER_ROOMS = 4;
    private final double HEALTH_DECREASE;

    private final boolean gameWon;

    private final Room[] rooms = new Room[NUMBER_ROOMS];
    private int currentRoomIndex;

    private double health;
    private double coins;

    private boolean roomChange = false;

    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        this.player = new Player(gameProps, messageProps);
        this.SPEED = Integer.parseInt(gameProps.getProperty("movingSpeed"));

        rooms[0] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS, "prep");
        rooms[1] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS, "A");
        rooms[2] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS, "B");
        rooms[3] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS, "end");

        this.health = Double.parseDouble(GAME_PROPS.getProperty("initialHealth"));
        this.coins = Integer.parseInt(GAME_PROPS.getProperty("initialCoins"));
        this.HEALTH_DECREASE = Double.parseDouble(GAME_PROPS.getProperty("riverDamagePerFrame"));
        this.gameWon = false;
    }

    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        rooms[currentRoomIndex].setBackground();

        if (health < 0) {
            currentRoomIndex = NUMBER_ROOMS-1;
        }

        switch (currentRoomIndex){
            case 0:
                rooms[currentRoomIndex].displayTextProperty("title", "title.fontSize", "", "title.y");
                rooms[currentRoomIndex].displayTextProperty("moveMessage", "prompt.fontSize", "","moveMessage.y");
                rooms[currentRoomIndex].setImage("res/restart_area.png", "restartarea.prep");
                break;
            case (1), (2):
                rooms[currentRoomIndex].drawStationaryObjects();
                break;
            case (3):
                rooms[currentRoomIndex].displayTextProperty("gameEnd.lost", "title.fontSize", "", "title.y");
                rooms[currentRoomIndex].setImage("res/restart_area.png", "restartarea.prep");
        }
        rooms[currentRoomIndex].displayText(MESSAGE_PROPS.getProperty("healthDisplay"),
                                            Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")),
                                            Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[0]),
                                            Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[1]));
        rooms[currentRoomIndex].displayText(MESSAGE_PROPS.getProperty("coinDisplay"),
                                            Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")),
                                            Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[0]),
                                            Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[1]));
        rooms[currentRoomIndex].displayText(Double.toString(health),
                                            Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")), Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[0]),
                                            Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[1]),
                                            MESSAGE_PROPS.getProperty("healthDisplay"));
        rooms[currentRoomIndex].displayText(Double.toString(coins),
                                            Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")),
                                            Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[0]),
                                            Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[1]),
                                            MESSAGE_PROPS.getProperty("coinDisplay"));
        rooms[currentRoomIndex].drawDoors();

        Image playerImage = player.getPlayerImage();
        playerImage.draw(player.getPosition().x,player.getPosition().y);

        Image[] wallImages = null;
        Point[] wallCoords = null;
        Image[] lockedDoorImages = null;

        Image[] unlockedDoorImages = rooms[currentRoomIndex].getUnlockedImages();
        Point[] doorCoords = rooms[currentRoomIndex].getDoorCoords();

        if (unlockedDoorImages != null) {
            if (rooms[currentRoomIndex] instanceof BattleRoom) {
                if (player.touchesObject(new Image[]{unlockedDoorImages[1]}, new Point[]{doorCoords[1]})) {
                    currentRoomIndex--;
                    roomChange = true;
                    doorCoords = rooms[currentRoomIndex].getDoorCoords();
                } else if (player.touchesObject(new Image[]{unlockedDoorImages[0]}, new Point[]{doorCoords[0]})) {
                    currentRoomIndex++;
                    roomChange = true;
                    doorCoords = rooms[currentRoomIndex].getDoorCoords();
                }
            } else if (rooms[currentRoomIndex] instanceof EdgeRoom) {
                if (player.touchesObject(new Image[]{unlockedDoorImages[0]}, new Point[]{doorCoords[0]})) {
                    currentRoomIndex = currentRoomIndex == NUMBER_ROOMS - 1 ? currentRoomIndex - 1 : currentRoomIndex + 1;
                    roomChange = true;
                    doorCoords = rooms[currentRoomIndex].getDoorCoords();
                }
            }
        }
        if (roomChange) {
            if (currentRoomIndex == 1) {
                player.setCoordinates(rooms[currentRoomIndex], 512, 720);
            }
            else if (currentRoomIndex == 0) {
                player.setCoordinates(rooms[currentRoomIndex],512,272);
            }
            else if (currentRoomIndex == 2) {
                player.setCoordinates(rooms[currentRoomIndex], 992,384);
            }
            else {
                player.setCoordinates(rooms[currentRoomIndex], 992, 384);
            }
        }
        roomChange = false;

        if (rooms[currentRoomIndex] instanceof EdgeRoom) {
            EdgeRoom room = (EdgeRoom) rooms[currentRoomIndex];
            lockedDoorImages = room.getLockedDoorImages();
        } else if (rooms[currentRoomIndex] instanceof BattleRoom) {
            BattleRoom room = (BattleRoom) rooms[currentRoomIndex];
            Image[] enemyImages = room.getEnemyImages();
            Point[] enemyCoords = room.getEnemyCoords();
            Image[] riverImages = room.getRiverImages();
            Point[] riverCoords = room.getRiverCoords();
            Image[] treasureBoxImages = room.getTreasureBoxImages();
            Point[] treasureBoxCoords = room.getTreasureBoxCoords();
            wallImages = room.getWallImages();
            wallCoords = room.getWallCoords();

            int[] touchesResult = player.touchesObstacle(treasureBoxImages, treasureBoxCoords, player.getPosition());

            if (player.touchesObject(enemyImages, enemyCoords)) {
                rooms[currentRoomIndex].setDoorsUnlocked();
                ((BattleRoom) rooms[currentRoomIndex]).setEnemies();
            }
            else if (player.touchesObject(riverImages, riverCoords)) {
                health -= HEALTH_DECREASE;
                // this was inspired from a StackOverflow question
                health = Math.round((health * 10)) / 10.0;
            }

            else if (touchesResult[0] == 1) {
                BattleRoom battleRoom = (BattleRoom)rooms[currentRoomIndex];
                int reward = (battleRoom.removeElement(battleRoom.getTreasureBoxes(),treasureBoxCoords[touchesResult[1]]));
                coins += reward;
            }
        }
        if (rooms[currentRoomIndex] instanceof BattleRoom) {
            player.movePlayer(input, rooms[currentRoomIndex], SPEED, wallImages, wallCoords);
        }
        else {
            player.movePlayer(input, rooms[currentRoomIndex], SPEED, lockedDoorImages, doorCoords);
        }
        if (input.wasPressed(Keys.R) && currentRoomIndex == 0) {
            rooms[currentRoomIndex].getDoors()[0].setDoorUnlocked();
        }
        else if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }
    }

    /**
     * The main entry point of the Shadow Dungeon game.
     *
     * This method loads the game properties and message files, initializes the game,
     * and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message.properties");
        ShadowDungeon game = new ShadowDungeon(gameProps, messageProps);
        game.run();
    }
}
