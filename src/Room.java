import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class Room {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final int height;
    private final int width;

    private final Door door;

    public Door getDoor() {
        return door;
    }

    private final String font;

    public Room(Properties GAME_PROPS, Properties MESSAGE_PROPS, Point doorCoordinates) {
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
        this.width = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        this.height = Integer.parseInt(GAME_PROPS.getProperty("window.height"));
        this.door = new Door(doorCoordinates);
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

    public void setImage(String filename, Point coordinates) {
        Image gameObject = new Image(filename);
        gameObject.draw(coordinates.x, coordinates.y);
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

    public void displayText(String text, int fontSize, double coordinateX, double coordinateY) {
        Font textFont = new Font(font, fontSize);
        textFont.drawString(text, coordinateX, coordinateY);
    }
}