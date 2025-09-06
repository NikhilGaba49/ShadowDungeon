import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class PrepRoom {

    private boolean isPrepRoom;
    private Properties GAME_PROPS;
    private Properties MESSAGE_PROPS;


    public PrepRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS) {
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
    }

    private Point getCoordinates(String property) {
        String[] coordinatesString = GAME_PROPS.getProperty(property).split(",");
        Point coordinatesInt = new Point(Double.parseDouble(coordinatesString[0]), Double.parseDouble(coordinatesString[1]));
        return coordinatesInt;
    }

    public void setBackground() {

        String font = GAME_PROPS.getProperty("font");

        // set the restart area image at the right coordinates
        Point coordinatesRestart = getCoordinates("restartarea.prep");
        Image restartArea = new Image("res/restart_area.png");
        restartArea.draw(coordinatesRestart.x, coordinatesRestart.y);

        // set the door image at the right place
        Point centreDoor = getCoordinates("door.prep");
        Image door = new Image("res/locked_door.png");
        door.draw(centreDoor.x, centreDoor.y);

        // set the title text at the right place with the right font
        int titleFontSize = Integer.parseInt(GAME_PROPS.getProperty("title.fontSize"));
        int titleY = Integer.parseInt(GAME_PROPS.getProperty("title.y"));
        int titleX = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        String title = MESSAGE_PROPS.getProperty("title");
        Font titleText = new Font(font, titleFontSize);
        titleText.drawString(title, 250, titleY);

        // set the controls text at the right place with the right font
        String moveMessage = MESSAGE_PROPS.getProperty("moveMessage");
        int controlsFontSize = Integer.parseInt(GAME_PROPS.getProperty("prompt.fontSize"));
        int controlsY = Integer.parseInt(GAME_PROPS.getProperty("moveMessage.y"));
        Font controls = new Font(font, controlsFontSize);
        controls.drawString(moveMessage, 375, controlsY);

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