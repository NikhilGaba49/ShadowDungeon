package Game;

import Rooms.BattleRoom;
import Rooms.EdgeRoom;
import Rooms.Room;

import bagel.*;

import java.util.Properties;

public class ShadowDungeon extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final Player player;
    private final int speed;

    private final int NUMBER_ROOMS = 4;

    private final Room[] rooms = new Room[NUMBER_ROOMS];
    private int currentRoomIndex;

    private double health;
    private double coins;

    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("title"));
        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        this.player = new Player(gameProps, messageProps);
        this.speed = Integer.parseInt(gameProps.getProperty("movingSpeed"));

        rooms[0] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS, "prep");
        rooms[1] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS, "A");
        rooms[2] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS, "B");
        rooms[3] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS, "end");

        this.health = Double.parseDouble(GAME_PROPS.getProperty("initialHealth"));
        this.coins = Integer.parseInt(GAME_PROPS.getProperty("initialCoins"));
    }

    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        rooms[currentRoomIndex].setBackground();
        switch (currentRoomIndex){
            case 0:
                rooms[currentRoomIndex].displayTextProperty("title", "title.fontSize", "", "title.y");
                rooms[currentRoomIndex].displayTextProperty("moveMessage", "prompt.fontSize", "","moveMessage.y");
                rooms[currentRoomIndex].setImage("res/restart_area.png", "restartarea.prep");
                break;
            case 1:
                rooms[currentRoomIndex].drawStationaryObjects();
                break;
        }

        rooms[currentRoomIndex].displayText(MESSAGE_PROPS.getProperty("healthDisplay"), Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")), Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[0]), Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[1]));
        rooms[currentRoomIndex].displayText(MESSAGE_PROPS.getProperty("coinDisplay"), Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")), Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[0]), Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[1]));
        rooms[currentRoomIndex].drawDoors();

        Image playerImage = player.getPlayerImage();
        playerImage.draw(player.getPosition().x,player.getPosition().y);

        if (rooms[currentRoomIndex] instanceof EdgeRoom) {
            EdgeRoom room = (EdgeRoom) rooms[currentRoomIndex];
            if (room.touchesDoor(player) && currentRoomIndex == 0) {
                currentRoomIndex++;
            }
        }
        else if (rooms[currentRoomIndex] instanceof BattleRoom) {
            BattleRoom room = (BattleRoom) rooms[currentRoomIndex];
            if (room.touchesEnemy(player)) {
                currentRoomIndex++;
            }
        }

        if (input.isDown(Keys.W)) {
            player.setCoordinates(rooms[currentRoomIndex], player.getPosition().x, player.getPosition().y-speed);
        }
        else if (input.isDown(Keys.A)) {
            player.setCoordinates(rooms[currentRoomIndex],player.getPosition().x-speed, player.getPosition().y);
        }
        else if (input.isDown(Keys.S)) {
            player.setCoordinates(rooms[currentRoomIndex], player.getPosition().x, player.getPosition().y+speed);
        }
        else if (input.isDown(Keys.D)) {
            player.setCoordinates(rooms[currentRoomIndex], player.getPosition().x+speed, player.getPosition().y);
        }
        else if (input.wasPressed(Keys.R) && currentRoomIndex == 0) {
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
