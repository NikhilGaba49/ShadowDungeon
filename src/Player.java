import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class Player {

    private Properties GAME_PROPS;
    private Properties MESSAGE_PROPS; // is this static? investigate

    private Point coordinates;
    private Image playerImage = new Image("res/player_left.png");

    public Image getPlayerImage() {
        return playerImage;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public void setPlayerImage(Image playerImage) {
        this.playerImage = playerImage;
    }

    public Player(Properties GAME_PROPS, Properties MESSAGE_PROPS){
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
        String[] coordinates = GAME_PROPS.getProperty("player.start").split(",");
        this.coordinates = new Point(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
    }

}
