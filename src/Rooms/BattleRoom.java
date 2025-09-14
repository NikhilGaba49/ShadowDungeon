package Rooms;

import Game.Player;
import bagel.Image;
import bagel.util.Point;
import roomComponents.*;

import java.util.Properties;

/* Out of the 4 rooms for this project, the middle two are battle rooms.
* Battle Room may have objects such as walls, rivers, enemies & treasure boxes.
* Another key property is having two doors, primary and secondary. */
public class BattleRoom extends Room {

    private final Door primaryDoor;
    private final Door secondaryDoor;

    // each battle room can have multiple of these stationary objects in them
    private final StationaryObject[] walls;
    private final StationaryObject[] rivers;
    private StationaryObject[] enemies;
    private StationaryObject[] treasureBoxes;

    public final int COIN_REWARD_INDEX = 2;

    // instantiate a specific battle room (either A or B) as indicated by room
    public BattleRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS,
                      String room) {

        // initiate common functionality in Room
        super(GAME_PROPS, MESSAGE_PROPS);

        // a structured way to extract the coordinates for the primary door and
        // the secondary door from GAME_PROPS regardless of room A or room B
        primaryDoor = new Door(getCoordinates("primarydoor.".concat(room),
                GAME_PROPS)[0]);
        secondaryDoor = new Door(getCoordinates("secondarydoor.".concat(room),
                GAME_PROPS)[0]);

        primaryDoor.setDoorUnlocked();

        // initiate stationary objects for the battle room
        walls = initiateArrays(GAME_PROPS, "wall.", room);
        rivers = initiateArrays(GAME_PROPS, "river.", room);
        enemies = initiateArrays(GAME_PROPS, "keyBulletKin.", room);

        // initialise treasure boxes array separately accounting for coin reward
        treasureBoxes = new TreasureBox[
                getCoordinates("treasurebox.".concat(room), GAME_PROPS).length];

        // read rewards from GAME_PROPS
        String[] rewards = GAME_PROPS.getProperty(
                "treasurebox.".concat(room)).split(";");

        // assign a new treasure box object to each element in array
        for (int i = 0; i < treasureBoxes.length; i++) {
            treasureBoxes[i] = new TreasureBox(
                    getCoordinates("treasurebox.".concat(room), GAME_PROPS)[i],
                    Integer.parseInt(rewards[i].split(",")[COIN_REWARD_INDEX]));
        }
    }

    /* A method that initiates the StationaryObject array depending on type
    of object, as specified by object, a string.
     */
    public static StationaryObject[] initiateArrays(Properties GAME_PROPS,
                                                    String object, String room) {

        StationaryObject[] objects;
        Point[] coordinates = getCoordinates(object.concat(room), GAME_PROPS);

        // to determine which type of objects to initialise
        switch (object) {
            case ("wall."):
                objects = new Wall[coordinates.length];
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = new Wall(coordinates[i]);
                }
                break;
            case ("river."):
                objects = new River[coordinates.length];
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = new River(coordinates[i]);
                }
                break;
            case ("keyBulletKin."):
                objects = new Enemy[coordinates.length];
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = new Enemy(coordinates[i]);
                }
                break;

            // if called by mistake, we manage by returning null
            default:
                return null;
        }
        return objects; // returning initialised array
    }

    public Point getDoorCoordinates() {
        return primaryDoor.getPositionCoordinates();
    }


    /* We need to check if the player, currently in this battle room, is
     * going to collide with any obstacles in the gameplay, including locked
     * doors and walls. Returns a boolean truth value. */
    public boolean touchesObstacles(Player player, Point nextMove) {

        // used to determine length of obstacles array
        int numberDoorsLocked = 0;
        if (!primaryDoor.isDoorUnlocked()) {
            numberDoorsLocked++;
        }
        if (!secondaryDoor.isDoorUnlocked()) {
            numberDoorsLocked++;
        }

        // arrays for images and central coordinates that'll be needed to
        // determine bounding boxes & whether the player will intersect.
        Image[] obstacleImages = getImages(walls, walls.length + numberDoorsLocked);
        Point[] obstacleCoordinates = getObjectsCoordinates(walls, walls.length + numberDoorsLocked);

        // obstacle image and obstacle coordinates will correspond by index
        int i = walls.length;
        // adding the primary & secondary locked doors to obstacle images
        if (!primaryDoor.isDoorUnlocked()) {
            obstacleImages[i] = primaryDoor.getImage();
            obstacleCoordinates[i] = primaryDoor.getPositionCoordinates();
            i++;
        }
        if (!secondaryDoor.isDoorUnlocked()) {
            obstacleImages[i] = secondaryDoor.getImage();
            obstacleCoordinates[i] = secondaryDoor.getPositionCoordinates();
        }
        // does the next move of the player touch any of the above obstacles?
        return player.touchesObstacle(obstacleImages, obstacleCoordinates,
                nextMove)[0] == 1;
    }

    /* checks whether the player is colliding with any unlocked door. If only
     * one door is unlocked, this means that room has just been changed, so we
     * should not change room instead. Returns a boolean array, with first index
     * being if the player intersects with any of the unlocked doors and the
     * second index is if both doors are unlocked in the room. */
    @Override
    public boolean[] touchesUnlockedDoor(Player player) {

        if (primaryDoor.isDoorUnlocked() && secondaryDoor.isDoorUnlocked()) {
            Image[] unlockedDoorImages = {primaryDoor.getImage(), secondaryDoor.getImage()};
            Point[] unlockedDoorCoordinates = {primaryDoor.getPositionCoordinates(), secondaryDoor.getPositionCoordinates()};
            return new boolean[]{player.touchesObstacle(unlockedDoorImages, unlockedDoorCoordinates, player.getPosition())[0] == 1, true};
        } else if (primaryDoor.isDoorUnlocked()) {
            Image[] unlockedDoorImages = {primaryDoor.getImage()};
            Point[] unlockedDoorCoordinates = {primaryDoor.getPositionCoordinates()};
            return new boolean[]{player.touchesObstacle(unlockedDoorImages, unlockedDoorCoordinates, player.getPosition())[0] == 1, false};
        }
        return new boolean[]{false, primaryDoor.isDoorUnlocked() && secondaryDoor.isDoorUnlocked()};
    }

    /* Checks whether the player touches a specific treasure box. Appropriate
    number of coin rewards are returned and the treasure box is removed from
    the array.
     */
    public int touchesTreasureBoxes(Player player) {

        Image[] treasureBoxesImages = getImages(treasureBoxes, treasureBoxes.length);
        Point[] treasureBoxCoordinates = getObjectsCoordinates(treasureBoxes, treasureBoxes.length);
        int[] touchesResult = player.touchesObstacle(treasureBoxesImages,
                treasureBoxCoordinates, player.getPosition());
        if (touchesResult[0] == 1) { // the player is colliding with a treasure box
            int coinRewards = ((TreasureBox) treasureBoxes[touchesResult[1]]).getCoinReward();
            treasureBoxes = removeIndex(treasureBoxes, touchesResult[1]);
            return coinRewards;
        }
        return touchesResult[0];
    }

    public void touchesEnemy(Player player) {
        Image[] enemyImages = getImages(enemies, enemies.length);
        Point[] enemyCoordinates = getObjectsCoordinates(enemies, enemies.length);
        int[] touchesResult = player.touchesObstacle(enemyImages, enemyCoordinates, player.getPosition());
        if (touchesResult[0] == 1){
            enemies = removeIndex(enemies, touchesResult[1]);
            setDoorsUnlocked();
        }
    }

    /* this method takes an array of stationary objects and removes the object
    at some given position. returns the new objects array. */
    public static StationaryObject[] removeIndex(StationaryObject[] objects, int indexToRemove) {
        assert (indexToRemove < objects.length && indexToRemove > 0);
        StationaryObject[] newObjects = new StationaryObject[objects.length-1];
        System.arraycopy(objects, 0, newObjects, 0, indexToRemove);
        if (objects.length - (indexToRemove + 1) >= 0)
            System.arraycopy(objects, indexToRemove + 1, newObjects, indexToRemove + 1 - 1, objects.length - (indexToRemove + 1));
        return newObjects;
    }

    public boolean touchesRiver(Player player) {
        Image[] riverImages = getImages(rivers, rivers.length);
        Point[] riverCoordinates = getObjectsCoordinates(rivers, rivers.length);
        return player.touchesObstacle(riverImages, riverCoordinates, player.getPosition())[0] == 1;
    }

    private Image[] getImages(StationaryObject[] objects, int length) {
        Image[] objectImage = new Image[length];
        for (int i=0; i<objects.length; i++) {
            objectImage[i] = objects[i].getImage();
        }
        return objectImage;
    }

    private Point[] getObjectsCoordinates(StationaryObject[] objects, int length) {
        Point[] objectCoordinates = new Point[length];
        for (int i=0; i<objects.length; i++) {
            objectCoordinates[i] = objects[i].getPositionCoordinates();
        }
        return objectCoordinates;
    }

    // to set game play for a battle room, these objects must be displayed
    @Override
    public void drawStationaryObjects() {

        primaryDoor.drawObject();
        secondaryDoor.drawObject();

        for (StationaryObject wall : walls) {
            wall.drawObject();
        }
        for (StationaryObject enemy : enemies) {
            enemy.drawObject();
        }
        for (StationaryObject river : rivers) {
            river.drawObject();
        }
        for (StationaryObject treasureBox : treasureBoxes) {
            treasureBox.drawObject();
        }
    }

    public void setTreasureBoxes() {
        StationaryObject[] newTreasureBoxes = new TreasureBox[treasureBoxes.length - 1];
        System.arraycopy(treasureBoxes, 1, newTreasureBoxes, 0, newTreasureBoxes.length);
        treasureBoxes = newTreasureBoxes;
    }

    public void setDoorsUnlocked() {
        primaryDoor.setDoorUnlocked();
        secondaryDoor.setDoorUnlocked();
    }

    public void setDoorLocked() {
        primaryDoor.setDoorLocked();
    }

    public StationaryObject[] getTreasureBoxes() {
        return treasureBoxes;
    }
}