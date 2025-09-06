import bagel.*;
import bagel.util.*;

import java.util.Properties;

public class Room {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final int height;
    private final int width;

    private final Door door;
    private final String font;

    public Room(Properties GAME_PROPS, Properties MESSAGE_PROPS) {
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
        this.width = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        this.height = Integer.parseInt(GAME_PROPS.getProperty("window.height"));
        door = new Door(getCoordinates("door.prep"));
        font = GAME_PROPS.getProperty("font");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getCoordinates(String property) {
        String[] coordinatesString = GAME_PROPS.getProperty(property).split(",");
        return new Point(Double.parseDouble(coordinatesString[0]), Double.parseDouble(coordinatesString[1]));
    }

    public void setBackground() {
        final Image background = new Image("res/background.png");
        background.draw(width / 2.0, height / 2.0);
    }

    public void setRestartArea() {
        // set the restart area image at the right coordinates
        Point coordinatesRestart = getCoordinates("restartarea.prep");
        Image restartArea = new Image("res/restart_area.png");
        restartArea.draw(coordinatesRestart.x, coordinatesRestart.y);
    }

    public void printTextProperty(String textProperty, String fontSizeProperty, String coordinateX,
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

    public void printText(String text, int fontSize, double coordinateX, double coordinateY) {
        Font textFont = new Font(font, fontSize);
        textFont.drawString(text, coordinateX, coordinateY);
    }
}