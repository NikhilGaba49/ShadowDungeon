package Game;

import Rooms.BattleRoom;
import Rooms.EdgeRoom;
import Rooms.Room;

import bagel.*;
import bagel.util.Point;

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
            currentRoomIndex = NUMBER_ROOMS - 1;
        }

        switch (currentRoomIndex) {
            case 0:
                rooms[currentRoomIndex].displayTextProperty("title", "title.fontSize", "", "title.y");
                rooms[currentRoomIndex].displayTextProperty("moveMessage", "prompt.fontSize", "", "moveMessage.y");
                rooms[currentRoomIndex].setImage("res/restart_area.png", "restartarea.prep");
                break;

            case (3):
                rooms[currentRoomIndex].displayTextProperty("gameEnd.lost", "title.fontSize", "", "title.y");
                rooms[currentRoomIndex].setImage("res/restart_area.png", "restartarea.prep");
                break;
        }
        rooms[currentRoomIndex].drawStationaryObjects();

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

        Image playerImage = player.getPlayerImage();
        playerImage.draw(player.getPosition().x, player.getPosition().y);

        // add functionality for player to face the mouse position
        player.movePlayer(input, rooms[currentRoomIndex], player, SPEED);
        if (input.getMousePosition().x >= player.getPosition().x) {
            player.setPlayerImage("res/player_right.png");
        }
        else if (input.getMousePosition().x < player.getPosition().x) {
            player.setPlayerImage("res/player_left.png");
        }

        // checks if the player collides with unlocked door & both doors are unlocked
        if (rooms[currentRoomIndex].touchesUnlockedDoor(player)[0]
                && rooms[currentRoomIndex].touchesUnlockedDoor(player)[1]) {
            currentRoomIndex++;
            Room currentRoom = rooms[currentRoomIndex];
            player.setCoordinates(currentRoom, currentRoom.getDoorCoordinates().x,
                    currentRoom.getDoorCoordinates().y);
            roomChange=true;
        }
        // logic to lock door if you move away from unlocked door after room change
        else if (rooms[currentRoomIndex] instanceof BattleRoom
                && !(rooms[currentRoomIndex].touchesUnlockedDoor(player)[0])
                && !(rooms[currentRoomIndex].touchesUnlockedDoor(player)[1])) {
            rooms[currentRoomIndex].setDoorLocked();
        }

        if (rooms[currentRoomIndex] instanceof BattleRoom) {
            BattleRoom currentRoom = (BattleRoom) rooms[currentRoomIndex];
            if (input.wasPressed(Keys.ENTER)) {
                coins += currentRoom.touchesTreasureBoxes(player);
            }
            currentRoom.touchesEnemy(player);
            if (currentRoom.touchesRiver(player)) {
                health -= HEALTH_DECREASE;
                // this was inspired from a StackOverflow question
                health = Math.round((health * 10)) / 10.0;
            }
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