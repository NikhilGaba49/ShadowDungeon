import bagel.*;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

public class ShadowDungeon extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;
    private final Player player;

    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        player = new Player(gameProps, messageProps);
    }

    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        Image background = new Image("res/background.png");
        background.draw(512, 384);

        Image playerImage = player.getPlayerImage();
        playerImage.draw(player.getCoordinates().x,player.getCoordinates().y);

        Room prepRoom = new Room(GAME_PROPS, MESSAGE_PROPS);
        prepRoom.setBackground();

        if (input.isDown(Keys.W)) {
            player.setCoordinates(prepRoom, player.getCoordinates().x, player.getCoordinates().y-3);
        }
        else if (input.isDown(Keys.A)) {
            player.setCoordinates(prepRoom,player.getCoordinates().x-3, player.getCoordinates().y);
        }
        else if (input.isDown(Keys.S)) {
            player.setCoordinates(prepRoom, player.getCoordinates().x, player.getCoordinates().y+3);
        }
        else if (input.isDown(Keys.D)) {
            player.setCoordinates(prepRoom, player.getCoordinates().x+3, player.getCoordinates().y);
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
