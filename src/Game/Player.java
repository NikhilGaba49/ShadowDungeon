package Game;

import Rooms.Room;
import bagel.*;
import bagel.util.*;

import java.util.Arrays;
import java.util.Properties;

public class Player {

    private int currentRoomIndex;

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private Point coordinates;
    private Image playerImage = new Image("res/player_right.png");

    public Player(Properties GAME_PROPS, Properties MESSAGE_PROPS){
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
        String[] coordinates = GAME_PROPS.getProperty("player.start").split(",");
        this.coordinates = new Point(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
    }

    public Image getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(String playerImage) {
        this.playerImage = new Image(playerImage);
    }

    public Point getPosition() {
        return coordinates;
    }

    public void setCoordinates(Room room, double x, double y) {
        Rectangle playerBox = playerImage.getBoundingBoxAt(new Point(x,y));
        Rectangle roomBox = room.getBackground().getBoundingBoxAt(new Point(room.getWidth()/2.0, room.getHeight()/2.0));
        if (playerBox.right() <= roomBox.right() && playerBox.left() >= roomBox.left() &&
            playerBox.top() >= roomBox.top() && playerBox.bottom() <= roomBox.bottom()) {
            coordinates = new Point(x, y);
        }
    }

    public void movePlayer(Input input, Room room, Player player,
                                int SPEED) {

        Point nextMove = null;
        if (input.isDown(Keys.W)) {
             nextMove = new Point(getPosition().x, getPosition().y - SPEED);
        } else if (input.isDown(Keys.A)) {
            nextMove = new Point(getPosition().x - SPEED, getPosition().y);
        } else if (input.isDown(Keys.S)) {
            nextMove = new Point(getPosition().x, getPosition().y + SPEED);
        } else if (input.isDown(Keys.D)) {
            nextMove = new Point(getPosition().x + SPEED, getPosition().y);
        }
        if (nextMove != null) {
            if (!room.touchesObstacles(player, nextMove)) {
                setCoordinates(room, nextMove.x, nextMove.y);
            }
        }
    }

    /* Returns an integer array, containing information about whether the player touched an object (1) or not (0)
    and information about which index it collided (if applicable), otherwise -1.
     */
    public int[] touchesObstacle(Image[] objectImage, Point[] objectCoordinates, Point coordinates) {

        Rectangle obstacle;
        Rectangle playerBox;
        for (int i=0; i<objectImage.length; i++) {
            obstacle = objectImage[i].getBoundingBoxAt(objectCoordinates[i]);
            playerBox = playerImage.getBoundingBoxAt(coordinates);
            if (playerBox.intersects(obstacle)) {
                return new int[] {1,i};
            }
        }
        return new int[]{0, -1};
    }
}