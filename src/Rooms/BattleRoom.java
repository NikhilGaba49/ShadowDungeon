package Rooms;

import Game.Player;
import bagel.Image;
import roomComponents.*;

import java.util.Arrays;
import java.util.Properties;

public class BattleRoom extends Room {

    Door primaryDoor;
    Door secondaryDoor;

    StationaryObjects[] walls;
    StationaryObjects[] rivers;
    StationaryObjects[] enemies;

    public BattleRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS, String room) {
        super(GAME_PROPS, MESSAGE_PROPS);

        String primaryDoorStem = "primarydoor.";
        String secondaryDoorStem = "secondarydoor.";
        primaryDoor = new Door(getCoordinates(primaryDoorStem.concat(room))[0]);
        secondaryDoor = new Door(getCoordinates(secondaryDoorStem.concat(room))[0]);

        walls = new StationaryObjects[getCoordinates("wall.".concat(room)).length];
        for (int i=0; i<walls.length; i++) {
            walls[i] = new StationaryObjects(getCoordinates("wall.".concat(room))[i], "res/wall.png");
        }

        rivers = new StationaryObjects[getCoordinates("river.".concat(room)).length];
        for (int i=0; i<rivers.length; i++) {
            rivers[i] = new StationaryObjects(getCoordinates("river.".concat(room))[i], "res/river.png");
        }

        enemies = new StationaryObjects[getCoordinates("keyBulletKin.".concat(room)).length];
        for (int i=0; i<enemies.length; i++) {
            enemies[i] = new StationaryObjects(getCoordinates("keyBulletKin.".concat(room))[i], "res/key_bullet_kin.png");
        }
    }

    @Override
    public void drawDoors() {
        primaryDoor.drawDoor();
        secondaryDoor.drawDoor();
    }

    @Override
    public Door[] getDoors() {
        return new Door[]{primaryDoor, secondaryDoor};
    }

    @Override
    public void drawStationaryObjects() {
        for (StationaryObjects wall: walls) {
            wall.drawObject();
        }
        for (StationaryObjects enemy: enemies) {
            enemy.drawObject();
        }
        for (StationaryObjects river: rivers) {
            river.drawObject();
        }
    }

    public Image getEnemy() {
        return enemies[0].getEnemyImage();
    }

    public boolean touchesEnemy(Player player) {
//        primaryDoor.setDoorUnlocked();
//        secondaryDoor.setDoorUnlocked();
        return touchesObject(player, enemies[0].getEnemyImage(), enemies[0].getPositionCoordinates());
    }
}
