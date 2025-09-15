package roomComponents;

import bagel.Image;
import bagel.util.Point;

/* A template class for multiple stationary objects within this gameplay,
* including rivers, walls, treasure boxes as well as enemies. */
public abstract class StationaryObject {

    // A stationary point is identified by coordinates and image drawn
    private final Point coordinates;
    private Image object;

    // constructor to declare instance variables regardless of type of
    // stationary object. Note that this will never be called.
    public StationaryObject(Point coordinates, String filename) {
        this.coordinates = coordinates;
        this.object = new Image(filename);
    }

    // a getter return objects' coordinates, supporting information hiding
    public Point getPositionCoordinates() {
        return coordinates;
    }

    // getter returning the image formed for a given stationary object
    public Image getImage() {
        return object;
    }

    // setter to change the filename, and thus the image drawn
    public void setImageFilename(String filename) {
        this.object = new Image(filename);
    }

    // draw the object at specified coordinates
    public void drawObject() {
        object.draw(coordinates.x, coordinates.y);
    }

}