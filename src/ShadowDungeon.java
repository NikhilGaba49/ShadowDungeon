import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Properties;

public class ShadowDungeon extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final Player player;
    private final int speed;

    private final Room[] rooms = new Room[4];
    private final Door door;
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

        for (int i=0; i<rooms.length; i++) {
            rooms[i] = new Room(gameProps, messageProps);
        }

        this.door = new Door(rooms[0].getCoordinates("door.prep"));
        this.health = Double.parseDouble(GAME_PROPS.getProperty("initialHealth"));
        this.coins = Integer.parseInt(GAME_PROPS.getProperty("initialCoins"));
    }

    private boolean touchesObject(Image objectImage, Point objectCoordinates) {

        Rectangle door = objectImage.getBoundingBoxAt(objectCoordinates);
        return player.getCoordinates().y <= door.bottom() &&
                player.getCoordinates().y >= door.top() &&
                player.getCoordinates().x >= door.left() &&
                player.getCoordinates().x <= door.right();
    }

    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        rooms[currentRoomIndex].setBackground();

        rooms[currentRoomIndex].printText(MESSAGE_PROPS.getProperty("healthDisplay"), Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")), Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[0]), Integer.parseInt(GAME_PROPS.getProperty("healthStat").split(",")[1]));
        rooms[currentRoomIndex].printText(MESSAGE_PROPS.getProperty("coinDisplay"), Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize")), Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[0]), Integer.parseInt(GAME_PROPS.getProperty("coinStat").split(",")[1]));

        if (currentRoomIndex == 0) {
            rooms[currentRoomIndex].setRestartArea();
            rooms[currentRoomIndex].printTextProperty("title", "title.fontSize", "", "title.y");
            rooms[currentRoomIndex].printTextProperty("moveMessage", "prompt.fontSize", "","moveMessage.y");
        }
        door.drawDoor();

        Image playerImage = player.getPlayerImage();
        playerImage.draw(player.getCoordinates().x,player.getCoordinates().y);

        if (touchesObject(door.getDoorImage(), door.getDoorCoordinates())) {
            if (currentRoomIndex == 0 && door.isDoorUnlocked()) {
                currentRoomIndex++;
            }
        }

        if (input.isDown(Keys.W)) {
            player.setCoordinates(rooms[currentRoomIndex], player.getCoordinates().x, player.getCoordinates().y-speed);
        }
        else if (input.isDown(Keys.A)) {
            player.setCoordinates(rooms[currentRoomIndex],player.getCoordinates().x-speed, player.getCoordinates().y);
        }
        else if (input.isDown(Keys.S)) {
            player.setCoordinates(rooms[currentRoomIndex], player.getCoordinates().x, player.getCoordinates().y+speed);
        }
        else if (input.isDown(Keys.D)) {
            player.setCoordinates(rooms[currentRoomIndex], player.getCoordinates().x+speed, player.getCoordinates().y);
        }
        else if (input.wasPressed(Keys.R) && currentRoomIndex == 0) {
            door.setDoorUnlocked();
        }
        else if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }
    }

//        // set the health display at the right place with the right font
//        Point healthCoordinates = getCoordinates("healthStat");
//
//        // set the coins display at the right place with the right font
//        Point coinStatCoordinates = getCoordinates("coinStat");

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
