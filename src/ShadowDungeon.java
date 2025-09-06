import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

public class ShadowDungeon extends AbstractGame {

    private final Player player;
    private final int speed;

    private final Room[] rooms = new Room[5];
    private int currentRoomIndex;

    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("title"));

        player = new Player(gameProps, messageProps);
        speed = Integer.parseInt(gameProps.getProperty("movingSpeed"));

        rooms[0] = new Room(gameProps, messageProps);
        rooms[1] = new Room(gameProps, messageProps);
        rooms[2] = new Room(gameProps, messageProps);
        rooms[3] = new Room(gameProps, messageProps);
    }

    private boolean touchesDoor(Image doorImage, Point doorCoordinates) {

        Rectangle door = doorImage.getBoundingBoxAt(doorCoordinates);
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

        Image playerImage = player.getPlayerImage();
        playerImage.draw(player.getCoordinates().x,player.getCoordinates().y);

        if (touchesDoor(rooms[currentRoomIndex].getDoorImage(), rooms[currentRoomIndex].getDoorCoordinates())){
            if (currentRoomIndex == 0) { // also add constraint for unlocked door
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
            rooms[currentRoomIndex].setDoorImage("res/unlocked_door.png");
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
