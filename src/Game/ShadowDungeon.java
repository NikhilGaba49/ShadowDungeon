package Game;

import Rooms.BattleRoom;
import Rooms.EdgeRoom;
import Rooms.Room;

import bagel.*;
import bagel.util.Point;
import roomComponents.StationaryObject;

import java.util.Properties;

import static java.lang.System.exit;

public class ShadowDungeon extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final Player player;
    public final int SPEED ;

    public static final int NUMBER_ROOMS = 4;
    private final double HEALTH_DECREASE;

    private boolean gameWon;

    private Room[] rooms;
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

        this.rooms = instantiateRooms(GAME_PROPS, MESSAGE_PROPS);
        rooms[0].setDoorLocked();

        this.health = Double.parseDouble(GAME_PROPS.getProperty("initialHealth"));
        this.coins = Integer.parseInt(GAME_PROPS.getProperty("initialCoins"));

        this.player = new Player(gameProps, messageProps);
        this.SPEED = Integer.parseInt(gameProps.getProperty("movingSpeed"));

        this.HEALTH_DECREASE = Double.parseDouble(GAME_PROPS.getProperty("riverDamagePerFrame"));
        this.gameWon = false;
    }

    public Room[] instantiateRooms(Properties GAME_PROPS, Properties MESSAGE_PROPS) {
        Room[] rooms = new Room[NUMBER_ROOMS];
        rooms[0] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS, "prep");
        rooms[1] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS, "A");
        rooms[2] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS, "B");
        rooms[3] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS, "end");
        return rooms;
    }

    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        rooms[currentRoomIndex].setBackground();

        // game has been lost
        if (health < 0 && currentRoomIndex != NUMBER_ROOMS-1) {
            currentRoomIndex = NUMBER_ROOMS - 1;

            Point startingCoordinates = Room.getCoordinates("player.start", GAME_PROPS)[0];
            player.setCoordinates(rooms[currentRoomIndex], startingCoordinates.x, startingCoordinates.y);

            rooms[currentRoomIndex].setDoorLocked();
            gameWon = false;
        }

        if (rooms[currentRoomIndex] instanceof EdgeRoom && input.wasPressed(Keys.ENTER)) {
            EdgeRoom currentRoom = (EdgeRoom) rooms[currentRoomIndex];
            if (currentRoom.touchesRestartArea(player)) {
                this.rooms = instantiateRooms(GAME_PROPS, MESSAGE_PROPS);
                rooms[0].setDoorLocked();
                currentRoomIndex = 0;
                this.health = Double.parseDouble(GAME_PROPS.getProperty("initialHealth"));
                this.coins = Integer.parseInt(GAME_PROPS.getProperty("initialCoins"));
            }
        }

        rooms[currentRoomIndex].displayRoom(health, coins, currentRoomIndex, gameWon);
        player.drawPlayer();

        // add functionality for player to face the mouse position
        player.movePlayer(input, rooms[currentRoomIndex], player, SPEED);
        if (input.getMousePosition().x >= player.getPosition().x) {
            player.setPlayerImage("res/player_right.png");
        }
        else if (input.getMousePosition().x < player.getPosition().x) {
            player.setPlayerImage("res/player_left.png");
        }

        boolean[] touchesResult = rooms[currentRoomIndex].touchesUnlockedDoor(player, currentRoomIndex);

        // checks if the player collides with at least one unlocked door &
        // all doors are unlocked (i.e. room needs to be changed)
        if (touchesResult[0] && touchesResult[1] && !roomChange) {

            if (touchesResult[2]) {

                // move to the next room
                currentRoomIndex++;
                // we have reached the last room possible after moving from Battle
                // room B to the end room (so we won the game)
                if (currentRoomIndex == NUMBER_ROOMS-1) {
                    this.gameWon=true;
                }
            }

            else {
                currentRoomIndex--;
                // set the player to the correct place in the next room
                Point doorCoordinates = rooms[currentRoomIndex].getDoorCoordinates();
                player.setCoordinates(rooms[currentRoomIndex], doorCoordinates.x, doorCoordinates.y);
            }

            // set the player to the correct place in the next room
            Point doorCoordinates = rooms[currentRoomIndex].getDoorCoordinates();
            player.setCoordinates(rooms[currentRoomIndex], doorCoordinates.x, doorCoordinates.y);

            roomChange = true;
        }

        // logic to lock door if you move away from unlocked door after room change
        else if (rooms[currentRoomIndex] instanceof BattleRoom && !touchesResult[0] && !touchesResult[1]) {
            rooms[currentRoomIndex].setDoorLocked();
            roomChange = false;
            for (StationaryObject enemy : ((BattleRoom) rooms[currentRoomIndex]).getEnemies()) {
                enemy.drawObject();
            }
        }

        // just been a room change & we have moved away from the unlocked door (README FILE PLS)
        else if (roomChange && !touchesResult[0]) {
            roomChange = false;
        }

        if (rooms[currentRoomIndex] instanceof BattleRoom) {
            BattleRoom currentRoom = (BattleRoom) rooms[currentRoomIndex];
            if (input.wasPressed(Keys.K)) {
                coins += currentRoom.touchesTreasureBoxes(player);
            }
            currentRoom.touchesEnemy(player);
            if (currentRoom.touchesRiver(player)) {
                health -= HEALTH_DECREASE;
                // this was inspired from a StackOverflow question
                health = Math.round((health * 10)) / 10.0;
            }
        }

        if (gameWon && rooms[currentRoomIndex] instanceof BattleRoom) {
            ((BattleRoom) rooms[currentRoomIndex]).drawTreasureBoxes();
            player.drawPlayer();
        }

        // setting door unlocked for the prep room
        if (input.wasPressed(Keys.R) && currentRoomIndex == 0) {
            rooms[currentRoomIndex].setDoorsUnlocked();
        }
        // end game window command
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