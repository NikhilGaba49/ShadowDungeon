package Game;

import Rooms.Room;
import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class Player {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS; // is this static? investigate

    private Point coordinates;
    private Image playerImage = new Image("res/player_left.png");

    public Image getPlayerImage() {
        return playerImage;
    }

    public Point getPosition() {
        return coordinates;
    }

    public void setCoordinates(Room room, double x, double y) {
        if (x>0 && x<room.getWidth() && y>0 && y<room.getHeight()) {
            coordinates = new Point(x, y);
        }
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