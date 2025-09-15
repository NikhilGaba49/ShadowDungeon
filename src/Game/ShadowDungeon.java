package Game;

import Rooms.BattleRoom;
import Rooms.EdgeRoom;
import Rooms.Room;

import bagel.*;
import bagel.util.Point;

import roomComponents.StationaryObject;

import java.util.Properties;

public class ShadowDungeon extends AbstractGame {

    // property files to read from for dynamic objects & their placing
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // the game has exactly one player that moves at a constant speed
    private final Player player;
    private final int SPEED ;
    public static final int NUMBER_ROOMS = 4; // exactly 4 rooms in the game

    // health decreases at a constant, non-changing, rate
    private final double HEALTH_DECREASE;

    // a flag indicating whether player has died or has successfully completed
    private boolean gameWon;

    // an array of rooms, with currentIndex specifying which room player is in
    private Room[] rooms;
    private int currentRoomIndex;

    // a flag indicating whether a room change has recently been undertaken
    private boolean roomChange = false;

    public static final int PREP_ROOM = 0;

    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty(RoomContext.WIDTH_PROPERTY)),
                Integer.parseInt(gameProps.getProperty(
                        RoomContext.HEIGHT_PROPERTY)),
                messageProps.getProperty(RoomContext.TITLE_PROPERTY));

        // setting property files for gameplay
        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        // instantiate all rooms, ensuring that door in the prep room is locked
        this.rooms = instantiateRooms(GAME_PROPS, MESSAGE_PROPS);
        rooms[PREP_ROOM].setDoorLocked();

        // instantiating a player for the gameplay
        this.player = new Player(gameProps);
        this.SPEED = Integer.parseInt(gameProps.getProperty(
                RoomContext.SPEED_PROPERTY));

        // reading in health decrease rate from properties file
        this.HEALTH_DECREASE = Double.parseDouble(
                GAME_PROPS.getProperty(RoomContext.RIVER_DAMAGE_PROPERTY));

        // at the start, the game is not won
        this.gameWon = false;
    }

    /* A method that instantiates all the rooms for the gameplay with their
     appropriate elements. Returns the array of rooms. */
    private Room[] instantiateRooms(Properties GAME_PROPS,
                        Properties MESSAGE_PROPS) {

        final int BATTLE_ROOM_A = 1;
        final int BATTLE_ROOM_B = 2;
        final int END_ROOM = 3;

        Room[] rooms = new Room[NUMBER_ROOMS];
        rooms[PREP_ROOM] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS,
                RoomContext.PREP_ROOM_PROPERTY);
        rooms[BATTLE_ROOM_A] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS,
                RoomContext.BATTLE_ROOM_A_PROPERTY);
        rooms[BATTLE_ROOM_B] = new BattleRoom(GAME_PROPS, MESSAGE_PROPS,
                RoomContext.BATTLE_ROOM_B_PROPERTY);
        rooms[END_ROOM] = new EdgeRoom(GAME_PROPS, MESSAGE_PROPS,
                RoomContext.END_ROOM_PROPERTY);
        return rooms;
    }

    /**
     * Render the relevant screen based on the keyboard input given by the user
     * and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        rooms[currentRoomIndex].setBackground();

        // game has been lost
        if (player.getHealth() < 0 && currentRoomIndex != NUMBER_ROOMS-1) {
            currentRoomIndex = NUMBER_ROOMS - 1; // transfer to the end room

            // when moved to end room, player's coordinates are the same as
            // starting position in prep room
            final int FIRST_INSTANCE = 0;

            Point startingCoordinates = Room.getCoordinates(
                    RoomContext.PLAYER_STARTING_COORDINATES,
                    GAME_PROPS)[FIRST_INSTANCE];

            player.setCoordinates(rooms[currentRoomIndex],
                    startingCoordinates.x, startingCoordinates.y);

            // the room for the end room shall be locked
            rooms[currentRoomIndex].setDoorLocked();
            gameWon = false; // obviously, we have not won the game
        }

        // checking if the game needs to be restarted
        if (rooms[currentRoomIndex] instanceof EdgeRoom &&
                input.wasPressed(Keys.ENTER)) {

            EdgeRoom currentRoom = (EdgeRoom) rooms[currentRoomIndex];

            if (currentRoom.touchesRestartArea(player)) {
                // instantiate new rooms for game play, moving to prep room
                this.rooms = instantiateRooms(GAME_PROPS, MESSAGE_PROPS);
                currentRoomIndex = PREP_ROOM;
                rooms[currentRoomIndex].setDoorLocked();

                // reset health & coins stats
                player.setHealth();
                player.setCoins();
            }
        }

        // displaying all objects on rooms and drawing player on current room
        rooms[currentRoomIndex].displayRoom(player.getHealth(),
                player.getCoins(), currentRoomIndex, gameWon);
        player.drawPlayer();

        // add functionality for player to face the mouse position
        player.movePlayer(input, rooms[currentRoomIndex], player, SPEED);
        if (input.getMousePosition().x >= player.getPosition().x) {
            player.setPlayerImage(RoomContext.PLAYER_RIGHT_FILENAME);
        }
        else if (input.getMousePosition().x < player.getPosition().x) {
            player.setPlayerImage(RoomContext.PLAYER_LEFT_FILENAME);
        }

        // get stats about state of the player & whether touching unlocked door
        boolean[] touchesUnlocked = rooms[currentRoomIndex].touchesUnlockedDoor(
                player, currentRoomIndex);

        // checks if the player collides with at least one unlocked door &
        // all doors are unlocked (i.e. room needs to be changed)
        final int ANY_ROOM = 0;
        final int SECONDARY_ROOM = 1;
        final int MOVE_TO_NEXT_ROOM = 2;

        if (touchesUnlocked[ANY_ROOM] && touchesUnlocked[SECONDARY_ROOM]
                && !roomChange) {

            // needs to be moved to the next room
            if (touchesUnlocked[MOVE_TO_NEXT_ROOM]) {
                currentRoomIndex++;
                // we have reached the last room possible after moving from
                // Battle room B to the end room (so we won the game)
                if (currentRoomIndex == NUMBER_ROOMS-1) {
                    this.gameWon=true;
                }
            }
            // needs to be moved to the previous room
            else {
                currentRoomIndex--;
            }

            // set the player to the correct place in the next room
            Point doorCoordinates = rooms[currentRoomIndex].getDoorCoordinates();
            player.setCoordinates(rooms[currentRoomIndex], doorCoordinates.x,
                    doorCoordinates.y);

            // thus, room changing is in progress
            roomChange = true;
        }

        // logic to lock door if you move away from unlocked door after room
        // change, thereby completing the room change process.
        else if (rooms[currentRoomIndex] instanceof BattleRoom
                && !touchesUnlocked[ANY_ROOM] &&
                !touchesUnlocked[SECONDARY_ROOM] ) {

            // lock the door as the player has moved away
            rooms[currentRoomIndex].setDoorLocked();
            roomChange = false; // room changing process is complete

            // draw the enemies now that the player is within battle room
            for (StationaryObject enemy :
                    ((BattleRoom) rooms[currentRoomIndex]).getEnemies()) {
                enemy.drawObject();
            }
        }

        // just been a room change & we have moved away from unlocked door
        // (edge room case)
        else if (roomChange && !touchesUnlocked[ANY_ROOM]) {
            roomChange = false; // complete room changing process
        }

        if (rooms[currentRoomIndex] instanceof BattleRoom) {
            BattleRoom currentRoom = (BattleRoom) rooms[currentRoomIndex];

            // add coins if a treasure box has been opened
            if (input.wasPressed(Keys.K)) {
                player.setCoins(player.getCoins() +
                        currentRoom.touchesTreasureBoxes(player));
            }

            // remove the enemy if it's touched by the player
            currentRoom.touchesEnemy(player);


            // if the player touches the river, update health
            if (currentRoom.touchesRiver(player)) {
                player.setHealth(player.getHealth() - HEALTH_DECREASE);
                // this was inspired from a StackOverflow question
                final double DECIMAL_SYSTEM = 10;
                player.setHealth(Math.round(
                        (player.getHealth() * DECIMAL_SYSTEM)) / DECIMAL_SYSTEM);
            }
        }

        // only draw the treasure boxes if game has been won (see assumptions)
        if (gameWon && rooms[currentRoomIndex] instanceof BattleRoom) {
            ((BattleRoom) rooms[currentRoomIndex]).drawTreasureBoxes();
            player.drawPlayer();
        }

        // setting door unlocked for the prep room if R is pressed
        if (input.wasPressed(Keys.R) && currentRoomIndex == PREP_ROOM) {
            rooms[currentRoomIndex].setDoorsUnlocked();
        }
        // end game window command
        else if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }
    }

    /**
     * The main entry point of the Shadow Dungeon game.
     *
     * This method loads the game properties and message files, initializes the
     * game, and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile(
                "res/message.properties");
        ShadowDungeon game = new ShadowDungeon(gameProps, messageProps);
        game.run();
    }
}