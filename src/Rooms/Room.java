package Rooms;

import Game.Player;
import bagel.*;
import bagel.util.*;
import roomComponents.Door;

import java.util.Arrays;
import java.util.Properties;

public abstract class Room {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final int height;
    private final int width;

    private final String font;

    public Room(Properties GAME_PROPS, Properties MESSAGE_PROPS) {
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
        this.width = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        this.height = Integer.parseInt(GAME_PROPS.getProperty("window.height"));
        this.font = GAME_PROPS.getProperty("font");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setBackground() {
        final Image background = new Image("res/background.png");
        background.draw(width / 2.0, height / 2.0);
    }

    public void setImage(String filename, String property) {
        Image gameObject = new Image(filename);
        Point[] coordinates = getCoordinates("restartarea.prep");
        gameObject.draw(coordinates[0].x, coordinates[0].y);
    }

    public void displayTextProperty(String textProperty, String fontSizeProperty, String coordinateX,
                                  String coordinateY) {

        int fontSize = Integer.parseInt(GAME_PROPS.getProperty(fontSizeProperty));
        Font textFont = new Font(font, fontSize);

        String text = MESSAGE_PROPS.getProperty(textProperty);

        int yCoordinate = Integer.parseInt(GAME_PROPS.getProperty(coordinateY));
        double xCoordinate;
        if (coordinateX.isEmpty()) {
            xCoordinate = (width - textFont.getWidth(text)) / 2;
        } else {
            xCoordinate = Double.parseDouble(GAME_PROPS.getProperty(coordinateX));
        }

        textFont.drawString(text, xCoordinate, yCoordinate);
    }

    public void displayText(String text, int fontSize, double coordinateX, double coordinateY, String previousText) {
        Font textFont = new Font(font, fontSize);
        coordinateX += 1.25*textFont.getWidth(previousText);
        displayText(text, fontSize, coordinateX, coordinateY);
    }

    public void displayText(String text, int fontSize, double coordinateX, double coordinateY) {
        Font textFont = new Font(font, fontSize);
        textFont.drawString(text, coordinateX, coordinateY);
    }

    public Point[] getCoordinates(String property) {

        final int COMMA_ASCII = 44;

        String[] coordinates = GAME_PROPS.getProperty(property).split(";");

        if (coordinates[0].length() <= 1) {
            return new Point[]{};
        }
        Point[] coordinatePairs = new Point[coordinates.length];

        for (int i=0; i<coordinates.length; i++) {

            String x = coordinates[i].substring(0, coordinates[i].indexOf(COMMA_ASCII));
            String remainingString = coordinates[i].substring(coordinates[i].indexOf(COMMA_ASCII)+1);

            int indexComma = remainingString.indexOf(COMMA_ASCII);
            if (indexComma < 0) {
                indexComma = remainingString.length();
            }
            String y = remainingString.substring(0, indexComma);

            coordinatePairs[i] = new Point(Double.parseDouble(x), Double.parseDouble(y));
        }
        return coordinatePairs;
    }

    public abstract void drawDoors();
    public abstract Door[] getDoors();
    public abstract void drawStationaryObjects();
    public abstract Point[] getDoorCoords();
}