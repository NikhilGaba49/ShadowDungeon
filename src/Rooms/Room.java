package Rooms;

import Game.Player;
import Game.RoomContext;
import bagel.*;
import bagel.util.*;
import java.util.Properties;

/* This class defines commonalities in attributes and methods for all rooms. */
public abstract class Room {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // define the dimensions of gameplay
    private final int height;
    private final int width;

    // the font that'll be used to display all text
    private final String font;

    // common background across all rooms
    private final Image background = new Image(RoomContext.BACKGROUND_PROPERTY);

    public Room(Properties GAME_PROPS, Properties MESSAGE_PROPS) {

        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;

        // read in properties from GAME properties file
        this.width = Integer.parseInt(GAME_PROPS.getProperty(
                RoomContext.WIDTH_PROPERTY));
        this.height = Integer.parseInt(GAME_PROPS.getProperty(
                RoomContext.HEIGHT_PROPERTY));
        this.font = GAME_PROPS.getProperty(RoomContext.STANDARD_FONT_PROPERTY);
    }

    // getters support information hiding
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getBackground() {
        return background;
    }

    // we must set the background to middle of room dimensions, hence midpoint
    public void setBackground() {
        background.draw(width / Player.MIDPOINT, height / Player.MIDPOINT);
    }

    /* method that reads in a property from MESSAGE_PROPS, and displays it on
    * the room at the prescribed coordinates as read from GAME properties file.
    * If coordinateX must be centralised, "" is passed to indicate this. */
    public void displayTextProperty(String textProperty, String fontSizeProperty,
                                    String coordinateX, String coordinateY) {

        // read font dynamically from property files
        int fontSize = Integer.parseInt(GAME_PROPS.getProperty(fontSizeProperty));
        Font textFont = new Font(font, fontSize);

        /* also read the text and y coordinate from property files needing
         to be displayed */
        String text = MESSAGE_PROPS.getProperty(textProperty);
        int yCoordinate = Integer.parseInt(GAME_PROPS.getProperty(coordinateY));
        
        // logic to centralise text if coordinateX is ""
        double xCoordinate;
        if (coordinateX.isEmpty()) {
            // room width - text width is available space and half to centralise
            xCoordinate = (width - textFont.getWidth(text)) / Player.MIDPOINT;
        } else {
            // read in from property files
            xCoordinate = Double.parseDouble(GAME_PROPS.getProperty(coordinateX));
        }

        // finally display text at calculated coordinates
        textFont.drawString(text, xCoordinate, yCoordinate);
    }

    /* Method responsible for displaying text with predetermined coordinates
      and needs to go after text already displayed. (Eg: Coin & Health stats) */
    private void displayText(String text, int fontSize, double coordinateX,
                            double coordinateY, String previousText) {

        final double RIGHT_ALIGN = 1.25;

        Font textFont = new Font(font, fontSize);
        // coordinateX is placed sufficiently to the right of the previous text
        coordinateX += RIGHT_ALIGN*textFont.getWidth(previousText);
        displayText(text, fontSize, coordinateX, coordinateY);
    }

    /* Overloaded method to above, simply displaying text at prescribed
     coordinates, without logic to come after some previous displayed text. */
    private void displayText(String text, int fontSize, double coordinateX,
                            double coordinateY) {
        Font textFont = new Font(font, fontSize);
        textFont.drawString(text, coordinateX, coordinateY);
    }

    /* A generalised static method that reads in coordinates from game property
    * files in a customised way. Used to simplify repetitive logic. */
    public static Point[] getCoordinates(String property, Properties GAME_PROPS){

        // constant for local use within method
        final int COMMA_ASCII = 44;

        // all coordinate pairs are separate by ";" (esp. if multiple pairs)
        String[] coordinates = GAME_PROPS.getProperty(property).split(
                BattleRoom.SPLIT_COORDINATE_PAIRS);

        // no coordinates found (e.g. River.B = 0)
        if (coordinates[BattleRoom.FIRST_INSTANCE].length() <= 1) {
            return new Point[]{}; // empty array returned
        }

        // at least one coordinate pair exists, define array of that length
        Point[] coordinatePairs = new Point[coordinates.length];

        // iteratively find all coordinate pairs using string logic
        for (int i=0; i<coordinates.length; i++) {

            // the x-coordinate is until the first comma instance
            String x = coordinates[i].substring(0,
                            coordinates[i].indexOf(COMMA_ASCII));

            // strip off the x-coordinate part that's already read (and comma)
            String remainingString = coordinates[i].substring(
                    coordinates[i].indexOf(COMMA_ASCII)+1);

            // is there another comma coming within this coordinate pair
            // Example: treasure boxes have rewards after another comma
            int indexComma = remainingString.indexOf(COMMA_ASCII);
            if (indexComma < 0) {
                indexComma = remainingString.length(); // no comma left
            }
            // y-coordinate can be found until the comma index (or end element)
            String y = remainingString.substring(0, indexComma);

            // concludes the coordinate pair
            coordinatePairs[i] = new Point(Double.parseDouble(x),
                    Double.parseDouble(y));
        }
        return coordinatePairs;
    }

    /* Each room needs to display the background, stationary objects, health
    * and coin stats. */
    public void displayRoom(double health, double coins, int roomIndex,
                            boolean gameWon) {

        setBackground();
        drawStationaryObjects();

        int fontSize = Integer.parseInt(GAME_PROPS.getProperty(
                RoomContext.PLAYER_STAT_FONT));

        // display "health display" as read dynamically from property files
        displayText(MESSAGE_PROPS.getProperty(RoomContext.HEALTH_DISPLAY_PROPERTY),
                fontSize,
                // reading in coordinates dynamically as well
                getCoordinates(RoomContext.HEALTH_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].x,
                getCoordinates(RoomContext.HEALTH_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].y);

        // display "coin display" as well at prescribed coordinates
        displayText(MESSAGE_PROPS.getProperty(RoomContext.COIN_DISPLAY_PROPERTY),
                fontSize,
                getCoordinates(RoomContext.COIN_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].x,
                getCoordinates(RoomContext.COIN_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].y);

        // the actual health points a player has must be displayed
        displayText(Double.toString(health), fontSize,
                getCoordinates(RoomContext.HEALTH_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].x,
                getCoordinates(RoomContext.HEALTH_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].y,
                MESSAGE_PROPS.getProperty(RoomContext.HEALTH_DISPLAY_PROPERTY));

        // the actual coins a player has must be displayed
        displayText(Double.toString(coins), fontSize,
                getCoordinates(RoomContext.COIN_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].x,
                getCoordinates(RoomContext.COIN_STAT_PROPERTY, GAME_PROPS)
                        [BattleRoom.FIRST_INSTANCE].y,
                MESSAGE_PROPS.getProperty(RoomContext.COIN_DISPLAY_PROPERTY));
    }

    // the implementation of room specific methods isn't provided, but required

    // each room has objects needing to be displayed (walls, doors, enemies)
    public abstract void drawStationaryObjects();
    // unlocking all doors
    public abstract void setDoorsUnlocked();
    // does the player touches any of obstacles in that room?
    public abstract boolean touchesObstacles(Player player,
                                             Point nextMove);
    // has the player interacted with an unlocked door? How shall game respond?
    public abstract boolean[] touchesUnlockedDoor(Player player,
                                                  int currentRoomIndex);
    // set primary door locked after entering locked room
    public abstract void setDoorLocked();
    public abstract Point getDoorCoordinates();
}