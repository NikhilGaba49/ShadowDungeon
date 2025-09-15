package Game;

import Rooms.Room;
import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class Player {

    // property files to place player at correct place
    private final Properties GAME_PROPS;

    // a player has its coordinates and player image
    private Point coordinates;
    private Image playerImage = new Image(RoomContext.PLAYER_RIGHT_FILENAME);

    // a player also has a given health & coins
    private double health;
    private double coins;

    public static final double MIDPOINT = 2;

    public Player(Properties GAME_PROPS){
        this.GAME_PROPS = GAME_PROPS;

        // initialise the player's starting coordinates
        final int FIRST_INSTANCE = 0;
        Point coordinates = Room.getCoordinates(
                RoomContext.PLAYER_STARTING_COORDINATES, GAME_PROPS)[FIRST_INSTANCE];
        this.coordinates = new Point(coordinates.x, coordinates.y);

        // initialise player health and coin to initial values
        this.health = Double.parseDouble(GAME_PROPS.getProperty(
                RoomContext.INITIAL_HEALTH_PROPERTY));
        this.coins = Integer.parseInt(GAME_PROPS.getProperty(
                RoomContext.INITIAL_COIN_PROPERTY));
    }

    public double getHealth() {
        return health;
    }

    // if health is not provided, set to initial value
    public void setHealth() {
        this.health = Double.parseDouble(GAME_PROPS.getProperty(
                RoomContext.INITIAL_HEALTH_PROPERTY));
    }

    // set health to provided value (overloaded method)
    public void setHealth(double health) {
        this.health = health;
    }

    public double getCoins() {
        return coins;
    }

    // if coins aren't provided, set to initial value
    public void setCoins() {
        this.coins = Integer.parseInt(GAME_PROPS.getProperty(
                RoomContext.INITIAL_COIN_PROPERTY));
    }

    // set coins to provided value
    public void setCoins(double coins) {
        this.coins = coins;
    }


    private Image getPlayerImage() {
        return playerImage;
    }

    // used to add mouse facing functionality
    public void setPlayerImage(String playerImage) {

        this.playerImage = new Image(playerImage);
    }

    public Point getPosition() {
        return coordinates;
    }

    // to set player's position, we must ensure that the player cannot go
    // outside room boundaries
    public void setCoordinates(Room room, double x, double y) {

        // player and room's bounding boxes
        Rectangle playerBox = playerImage.getBoundingBoxAt(new Point(x,y));

        Rectangle roomBox = room.getBackground().getBoundingBoxAt(new Point(
                room.getWidth()/ MIDPOINT, room.getHeight()/ MIDPOINT));

        // checking that player's bounding box is fully
        // within the rooms' boundaries
        if (playerBox.right() <= roomBox.right() &&
                playerBox.left() >= roomBox.left() &&
                playerBox.top() >= roomBox.top() &&
                playerBox.bottom() <= roomBox.bottom()) {

            coordinates = new Point(x, y); // setting the player's coordinates
        }
    }

    // draw the player at the player's coordinates
    public void drawPlayer() {
        Image playerImage = getPlayerImage();
        playerImage.draw(getPosition().x, getPosition().y);
    }

    /* To move the player, we check if the next move will intersect with an
    obstacle and thus, restrict movement, otherwise, setting players'
    coordinates to the new position */
    public void movePlayer(Input input, Room room, Player player,
                                int SPEED) {

        Point nextMove = null;
        // depending on direction key pressed (and SPEED) we calculate nextMove
        if (input.isDown(Keys.W)) {
             nextMove = new Point(getPosition().x, getPosition().y - SPEED);
        } else if (input.isDown(Keys.A)) {
            nextMove = new Point(getPosition().x - SPEED, getPosition().y);
        } else if (input.isDown(Keys.S)) {
            nextMove = new Point(getPosition().x, getPosition().y + SPEED);
        } else if (input.isDown(Keys.D)) {
            nextMove = new Point(getPosition().x + SPEED, getPosition().y);
        }

        // W,A,S or D pressed
        if (nextMove != null) {
            if (!room.touchesObstacles(player, nextMove)) {
                // free to move
                setCoordinates(room, nextMove.x, nextMove.y);
            }
        }
    }

    /* Returns integer array, with stats about whether player touched object
            - first Index: player intersects with any obstacle of the gamplay
            - second Index: index of obstacle with which player collided
                (if applicable), otherwise -1.
     */
    public int[] touchesObstacle(Image[] objectImage, Point[] objectCoordinates,
                                    Point coordinates) {

        // constants needed for this method
        final int TRUE = 1;
        final int FALSE = 0;
        final int DOES_NOT_INTERSECT = -1;

        Rectangle obstacle;
        Rectangle playerBox;

        // get bounding boxes for the player and each obstacle
        for (int i=0; i<objectImage.length; i++) {
            obstacle = objectImage[i].getBoundingBoxAt(objectCoordinates[i]);
            playerBox = playerImage.getBoundingBoxAt(coordinates);

            if (playerBox.intersects(obstacle)) {
                return new int[] {TRUE,i}; // intersected at index i
            }
        }

        return new int[]{FALSE, DOES_NOT_INTERSECT}; // did not intersect
    }
}