import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class Room {

    private final Properties GAME_PROPS ;
    private final Properties MESSAGE_PROPS;

    private final int height;
    private final int width;

    private Image doorImage;

    private final Point doorCoordinates;

    public Point getDoorCoordinates() {
        return doorCoordinates;
    }

    public Image getDoorImage() {
        return doorImage;
    }

    public Room(Properties GAME_PROPS, Properties MESSAGE_PROPS) {
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
        this.width = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        this.height = Integer.parseInt(GAME_PROPS.getProperty("window.height"));
        this.doorCoordinates = new Point(getCoordinates("door.prep").x, getCoordinates("door.prep").y);
        this.doorImage = new Image("res/locked_door.png");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private Point getCoordinates(String property) {
        String[] coordinatesString = GAME_PROPS.getProperty(property).split(",");
        return new Point(Double.parseDouble(coordinatesString[0]), Double.parseDouble(coordinatesString[1]));
    }

    public void setDoorImage(String filename) {
        this.doorImage = new Image(filename);
    }

    public void setBackground() {

        String font = GAME_PROPS.getProperty("font");

        final Image background = new Image("res/background.png");
        background.draw(width/2.0, height/2.0);

        // set the restart area image at the right coordinates
        Point coordinatesRestart = getCoordinates("restartarea.prep");
        Image restartArea = new Image("res/restart_area.png");
        restartArea.draw(coordinatesRestart.x, coordinatesRestart.y);

        // set the door image at the right place
        Point centreDoor = getCoordinates("door.prep");
        doorImage.draw(centreDoor.x, centreDoor.y);

        // set the title text at the right place with the right font
        int titleFontSize = Integer.parseInt(GAME_PROPS.getProperty("title.fontSize"));
        int titleY = Integer.parseInt(GAME_PROPS.getProperty("title.y"));
        int titleX = Integer.parseInt(GAME_PROPS.getProperty("window.width"));

        String title = MESSAGE_PROPS.getProperty("title");
        Font titleText = new Font(font, titleFontSize);

        double centreTitleX = (width-titleText.getWidth(title))/2;
        titleText.drawString(title, centreTitleX, titleY);

        // set the controls text at the right place with the right font
        String moveMessage = MESSAGE_PROPS.getProperty("moveMessage");
        int controlsFontSize = Integer.parseInt(GAME_PROPS.getProperty("prompt.fontSize"));
        int controlsY = Integer.parseInt(GAME_PROPS.getProperty("moveMessage.y"));

        Font controls = new Font(font, controlsFontSize);

        double centreMessageX = (width-controls.getWidth(moveMessage))/2;
        controls.drawString(moveMessage, centreMessageX, controlsY);

        // set the health display at the right place with the right font
        String healthDisplay = MESSAGE_PROPS.getProperty("healthDisplay");
        int playerStatsFontSize = Integer.parseInt(GAME_PROPS.getProperty("playerStats.fontSize"));
        Point healthCoordinates = getCoordinates("healthStat");
        Font healthStat = new Font("res/wheaton.otf", playerStatsFontSize);
        healthStat.drawString(healthDisplay, healthCoordinates.x, healthCoordinates.y);

        // set the coins display at the right place with the right font
        String coinDisplay = MESSAGE_PROPS.getProperty("coinDisplay");
        Point coinStatCoordinates = getCoordinates("coinStat");
        Font coinStat = new Font(font, playerStatsFontSize);
        coinStat.drawString(coinDisplay, coinStatCoordinates.x, coinStatCoordinates.y);

        double initialHealth = Double.parseDouble(GAME_PROPS.getProperty("initialHealth"));
        int initialCoins = Integer.parseInt(GAME_PROPS.getProperty("initialCoins"));
    }
}