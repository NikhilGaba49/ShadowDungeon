package Game;

import Rooms.Room;
import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class Player {

    private int currentRoomIndex;

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

    public void movePlayer(Input input, Room room, int SPEED, Image[] obstacleImages, Point[] obstacleCoordinates) {

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
            if (allowMovement(obstacleImages, obstacleCoordinates, nextMove)) {
                setCoordinates(room, nextMove.x, nextMove.y);
            }
        }
    }

//    public void movePlayer(Input input, Room room, int SPEED) {
//        if (input.isDown(Keys.W)) {
//            setCoordinates(room, getPosition().x, getPosition().y - SPEED);
//        } else if (input.isDown(Keys.A)) {
//            setCoordinates(room, getPosition().x - SPEED, getPosition().y);
//        } else if (input.isDown(Keys.S)) {
//            setCoordinates(room, getPosition().x, getPosition().y + SPEED);
//        } else if (input.isDown(Keys.D)) {
//            setCoordinates(room, getPosition().x + SPEED, getPosition().y);
//        }
//    }


    private boolean allowMovement(Image[] obstacleImages, Point[] obstacleCoordinates, Point nextMove) {

        // the obstacles don't exist on the room, free to move the player as required
        if (obstacleImages == null) {
            return true;
        }
        int[] collisionObstacle = touchesObstacle(obstacleImages, obstacleCoordinates);
        if (collisionObstacle[0] == 0) {
            return true;
        }
        // the player has definitely collided with an obstacle
        // define the boundaries/perimeter of the obstacle
        Rectangle obstacle = obstacleImages[collisionObstacle[1]].getBoundingBoxAt(obstacleCoordinates[collisionObstacle[1]]);
        return !(nextMove.y <= obstacle.bottom()) || !(nextMove.y >= obstacle.top()) ||
                !(nextMove.x >= obstacle.left()) || !(nextMove.x <= obstacle.right());
    }

    /* Returns an integer array, containing information about whether the player touched an object (1) or not (0)
    and information about which index it collided (if applicable), otherwise -1.
     */
    public int[] touchesObstacle(Image[] objectImage, Point[] objectCoordinates) {

        Rectangle door;
        for (int i=0; i<objectImage.length; i++) {
            door = objectImage[i].getBoundingBoxAt(objectCoordinates[i]);
            if (getPosition().y <= door.bottom() && getPosition().y >= door.top() &&
                    getPosition().x >= door.left() && getPosition().x <= door.right()) {
                return new int[]{1, i};
            }
        }
        return new int[]{0, -1};
    }

    public boolean touchesObject(Image[] objectImage, Point[] objectCoordinates) {
        int[] output = touchesObstacle(objectImage, objectCoordinates);
        return output[0] == 1;
    }
}