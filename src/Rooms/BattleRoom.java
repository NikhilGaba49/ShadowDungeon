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

    private final String WALL_STEM = "wall.";
    private final String RIVER_STEM = "river.";
    private final String ENEMY_STEM = "keyBulletKin.";

    private final int COLLIDING_INDEX = 1;

    // constants needed for other files as well
    public static int ANY_OBJECT = 0;
    public static final int TRUE = 1;
    public static final int FIRST_INSTANCE = 0;
    public static final String SPLIT_COORDINATE_PAIRS = ";";

    // instantiate a specific battle room (either A or B) as indicated by room
    public BattleRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS,
                      String room) {

        // initiate common functionality in Room
        super(GAME_PROPS, MESSAGE_PROPS);

        final String PRIMARY_DOOR_STEM = "primarydoor.";
        final String SECONDARY_DOOR_STEM = "secondarydoor.";
        final String TREASURE_BOX_STEM = "treasurebox.";

        // a structured way to extract the coordinates for the primary door and
        // the secondary door from GAME_PROPS regardless of room A or room B
        primaryDoor = new Door(getCoordinates(PRIMARY_DOOR_STEM.concat(room),
                GAME_PROPS)[FIRST_INSTANCE]);
        secondaryDoor = new Door(getCoordinates(SECONDARY_DOOR_STEM.concat(room),
                GAME_PROPS)[FIRST_INSTANCE]);

        // when entering, primary door is unlocked.
        primaryDoor.setDoorUnlocked();

        // initiate stationary objects for the battle room
        walls = initiateArrays(GAME_PROPS, WALL_STEM, room);
        rivers = initiateArrays(GAME_PROPS, RIVER_STEM, room);
        enemies = initiateArrays(GAME_PROPS, ENEMY_STEM, room);

        // initialise treasure boxes array separately accounting for coin reward
        treasureBoxes = new TreasureBox[
                getCoordinates(TREASURE_BOX_STEM.concat(room), GAME_PROPS).length];

        // read rewards from GAME_PROPS
        String[] rewards = GAME_PROPS.getProperty(
                TREASURE_BOX_STEM.concat(room)).split(SPLIT_COORDINATE_PAIRS);

        // assign a new treasure box object to each element in array
        // with their coin reward added as part of constructor
        for (int i = 0; i < treasureBoxes.length; i++) {
            // coin rewards always in (zero-based) 2nd index of coordinate pair
            final int COIN_REWARD_INDEX = 2;

            treasureBoxes[i] = new TreasureBox(
                    getCoordinates(TREASURE_BOX_STEM.concat(room), GAME_PROPS)[i],
                    Integer.parseInt(
                            rewards[i].split(",")[COIN_REWARD_INDEX]));
        }
    }

    /* A method that initiates the StationaryObject array depending on type
    of object, as specified by object, a string.
     */
    private StationaryObject[] initiateArrays(Properties GAME_PROPS,
                                                String object, String room) {

        StationaryObject[] objects;
        // read coordinate pairs for object from file dynamically
        Point[] coordinates = getCoordinates(object.concat(room), GAME_PROPS);

        // to determine which type of objects to initialise
        switch (object) {
            case (WALL_STEM):
                // array of walls
                objects = new Wall[coordinates.length];
                for (int i = 0; i < objects.length; i++) {
                    // creating a new wall object within objects array
                    objects[i] = new Wall(coordinates[i]);
                }
                break;
            case (RIVER_STEM):
                // array of rivers
                objects = new River[coordinates.length];
                for (int i = 0; i < objects.length; i++) {
                    // creating a new river object within objects array
                    objects[i] = new River(coordinates[i]);
                }
                break;
            case (ENEMY_STEM):
                // array of enemies
                objects = new Enemy[coordinates.length];
                for (int i = 0; i < objects.length; i++) {
                    // creating a new enemy within objects array
                    objects[i] = new Enemy(coordinates[i]);
                }
                break;

            // if called by mistake, we manage by returning null
            default:
                return null;
        }
        return objects; // returning initialised array
    }

    // only primary door coordinates need to be returned, used to set the
    // player's position when entering a new battle room
    public Point getDoorCoordinates() {

        return primaryDoor.getPositionCoordinates();
    }

    // supports information hiding
    public StationaryObject[] getEnemies() {
        return enemies;
    }


    /* this method takes an array of stationary objects and removes the object
    at some given position. returns the new objects array. */
    private static StationaryObject[] removeIndex(StationaryObject[] objects,
                                                  int indexToRemove) {

        final int START_INDEX = 0;
        final int NEXT = 1;

        // ensuring that index is within bounds
        assert (indexToRemove < objects.length && indexToRemove > START_INDEX);

        // new objects array will have one less length
        StationaryObject[] newObjects = new StationaryObject[objects.length-1];

        // copy the elements until indexToRemove (not incl. obviously)
        System.arraycopy(objects, START_INDEX, newObjects, START_INDEX,
                indexToRemove);
        // copy the elements after indexToRemove (skipping index)
        if (objects.length - (indexToRemove + NEXT) >= START_INDEX)
            System.arraycopy(objects, indexToRemove + NEXT, newObjects,
                    indexToRemove, objects.length - (indexToRemove + NEXT));
        // return shortened objects array
        return newObjects;
    }

    /* We need to check if the player, currently in this battle room, is
     * going to collide with any obstacles in the gameplay, including locked
     * doors and walls. Returns a boolean truth value. */
    public boolean touchesObstacles(Player player, Point nextMove) {

        // locked doors are obstacles
        // used to determine length of obstacles array
        int numberDoorsLocked = 0;
        if (!primaryDoor.isDoorUnlocked()) {
            numberDoorsLocked++; // primary door is locked
        }
        if (!secondaryDoor.isDoorUnlocked()) {
            numberDoorsLocked++; // secondary door is locked
        }

        // arrays for images and central coordinates that'll be needed to
        // determine bounding boxes & whether the player will intersect.
        Image[] obstacleImages = getImages(walls,
                walls.length + numberDoorsLocked);
        Point[] obstacleCoordinates = getObjectsCoordinates(walls,
                walls.length + numberDoorsLocked);

        // obstacle image and obstacle coordinates will correspond by index
        int i = walls.length;
        // adding the primary & secondary locked doors to obstacle arrays
        if (!primaryDoor.isDoorUnlocked()) {
            obstacleImages[i] = primaryDoor.getImage();
            obstacleCoordinates[i] = primaryDoor.getPositionCoordinates();
            i++;
        }
        // adding secondary locked door (if applicable) to obstacles arrays
        if (!secondaryDoor.isDoorUnlocked()) {
            obstacleImages[i] = secondaryDoor.getImage();
            obstacleCoordinates[i] = secondaryDoor.getPositionCoordinates();
        }
        // does the next move of the player touch any of the above obstacles?
        return player.touchesObstacle(obstacleImages, obstacleCoordinates,
                nextMove)[ANY_OBJECT] == TRUE;
    }

    /* Returns an array of booleans with key properties.
     *       - Index 0: Player touches at least one unlocked door
     *       - Index 1: All doors within room are unlocked
     *       - Index 2: Player needs to go to the next room
     * */
    @Override
    public boolean[] touchesUnlockedDoor(Player player, int currentRoomIndex) {

        if (primaryDoor.isDoorUnlocked() && secondaryDoor.isDoorUnlocked()) {
            // all doors are unlocked!
            // get unlocked door images & coordinates to check player touches
            Image[] unlockedDoorImages = {primaryDoor.getImage(),
                    secondaryDoor.getImage()};
            Point[] unlockedDoorCoordinates =
                    {primaryDoor.getPositionCoordinates(),
                            secondaryDoor.getPositionCoordinates()};

            // how does the player interact with the unlocked doors
            int[] touchResult = player.touchesObstacle(unlockedDoorImages,
                    unlockedDoorCoordinates, player.getPosition());

            // Recall touchResult[0] == 1 checks if touches any unlocked door
            // touchResult[1] == 1 returns if secondary door has been touched
            final int SECONDARY_DOOR = 1;
            return new boolean[]{touchResult[ANY_OBJECT] == TRUE, true,
                    touchResult[SECONDARY_DOOR] == TRUE};
        }
        // only one door unlocked, possible if player is now entering this room
        else if (primaryDoor.isDoorUnlocked()) {
            Image[] unlockedDoorImages = {primaryDoor.getImage()};
            Point[] unlockedDoorCoordinates =
                    {primaryDoor.getPositionCoordinates()};
            // does the player interact with this only unlocked door?
            int[] touchResult = player.touchesObstacle(unlockedDoorImages,
                    unlockedDoorCoordinates, player.getPosition());
            // Recall touchResult[0] == 1 checks if touches any unlocked door
            // not moving to next room since both doors aren't unlocked
            return new boolean[]{touchResult[ANY_OBJECT] == TRUE, false, false};
        }
        // no unlocked doors, then player can't interact with unlocked doors
        return new boolean[]{false, primaryDoor.isDoorUnlocked() &&
                secondaryDoor.isDoorUnlocked(), false};
    }

    /* Checks whether the player touches a specific treasure box. Appropriate
    number of coin rewards are returned and the treasure box is removed from
    the array.
     */
    public int touchesTreasureBoxes(Player player) {

        Image[] treasureBoxesImages = getImages(
                treasureBoxes, treasureBoxes.length);
        Point[] treasureBoxCoordinates = getObjectsCoordinates(treasureBoxes,
                treasureBoxes.length);

        // does the player come into contact with any of the treasure boxes?
        int[] touchesResult = player.touchesObstacle(treasureBoxesImages,
                treasureBoxCoordinates, player.getPosition());

        if (touchesResult[ANY_OBJECT] == TRUE) {
            // player is colliding with a treasure box
            // touchesResult[1] stores index at which player collides
            int coinRewards = ((TreasureBox) treasureBoxes[touchesResult[
                    COLLIDING_INDEX]]).getCoinReward();
            // remove the treasure box from array, coin reward already taken
            treasureBoxes = removeIndex(treasureBoxes,
                    touchesResult[COLLIDING_INDEX]);
            return coinRewards;
        }
        // doesn't intersect, take 0 coins!
        final int FALSE = 0;
        return touchesResult[FALSE];
    }

    /* Checks whether the player touches any enemy in this battle room. */
    public void touchesEnemy(Player player) {
        Image[] enemyImages = getImages(enemies, enemies.length);
        Point[] enemyCoordinates = getObjectsCoordinates(enemies,
                enemies.length);

        int[] touchesResult = player.touchesObstacle(enemyImages,
                enemyCoordinates, player.getPosition());

        // player touches (defeats) enemy, remove enemy from gameplay
        if (touchesResult[ANY_OBJECT] == TRUE){
            enemies = removeIndex(enemies, touchesResult[COLLIDING_INDEX]);
            setDoorsUnlocked(); // unlock all doors
        }
    }

    /* Does the player touch any river within battle room? Specific method
    * required to exact riverImages and river coordinates. */
    public boolean touchesRiver(Player player) {
        Image[] riverImages = getImages(rivers, rivers.length);
        Point[] riverCoordinates = getObjectsCoordinates(rivers, rivers.length);
        return player.touchesObstacle(riverImages, riverCoordinates,
                player.getPosition())[FIRST_INSTANCE] == TRUE;
    }

    /* get images for an array of objects, placing them starting from index 0
    * till applicable. Array of images is of size length since more elements
    * may be added later. */
    private Image[] getImages(StationaryObject[] objects, int length) {

        Image[] objectImage = new Image[length];

        // getting images for each object in the array
        for (int i=0; i<objects.length; i++) {
            objectImage[i] = objects[i].getImage();
        }

        return objectImage; // returning images array
    }

    // Very similar to above, but now for object's coordinates, not images
    private Point[] getObjectsCoordinates(StationaryObject[] objects,
                                          int length) {

        Point[] objectCoordinates = new Point[length];

        // getting coordinates for each object and placing into array
        for (int i=0; i<objects.length; i++) {
            objectCoordinates[i] = objects[i].getPositionCoordinates();
        }

        return objectCoordinates; // returning coordinates array
    }

    // to set game play for a battle room, these objects must be displayed
    @Override
    public void drawStationaryObjects() {

        primaryDoor.drawObject();
        secondaryDoor.drawObject();

        // all walls should be visible from entering the room
        for (StationaryObject wall : walls) {
            wall.drawObject();
        }
        // all rivers should be visible from entering the room
        for (StationaryObject river : rivers) {
            river.drawObject();
        }
    }

    // only draw treasure boxes once the game has been won (see assumptions)
    public void drawTreasureBoxes() {
        for (StationaryObject treasureBox : treasureBoxes) {
            treasureBox.drawObject();
        }
    }

    // unlock both primary and secondary doors
    public void setDoorsUnlocked() {
        primaryDoor.setDoorUnlocked();
        secondaryDoor.setDoorUnlocked();
    }

    // lock primary door, needed when entering battle room
    public void setDoorLocked() {
        primaryDoor.setDoorLocked();
    }
}