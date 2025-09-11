package Rooms;

import Game.Player;
import bagel.Image;
import bagel.util.Point;
import roomComponents.*;

import java.util.Arrays;
import java.util.Properties;

public class BattleRoom extends Room {

    Door primaryDoor;
    Door secondaryDoor;

    StationaryObjects[] walls;
    StationaryObjects[] rivers;
    StationaryObjects[] enemies;
    StationaryObjects[] treasureBoxes;
    int[] treasureRewards;

    public BattleRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS, String room) {
        super(GAME_PROPS, MESSAGE_PROPS);

        String primaryDoorStem = "primarydoor.";
        String secondaryDoorStem = "secondarydoor.";
        primaryDoor = new Door(getCoordinates(primaryDoorStem.concat(room), GAME_PROPS)[0]);
        secondaryDoor = new Door(getCoordinates(secondaryDoorStem.concat(room), GAME_PROPS)[0]);

        walls = new StationaryObjects[getCoordinates("wall.".concat(room), GAME_PROPS).length];
        for (int i=0; i<walls.length; i++) {
            walls[i] = new StationaryObjects(getCoordinates("wall.".concat(room), GAME_PROPS)[i], "res/wall.png");
        }

        rivers = new StationaryObjects[getCoordinates("river.".concat(room), GAME_PROPS).length];
        for (int i=0; i<rivers.length; i++) {
            rivers[i] = new StationaryObjects(getCoordinates("river.".concat(room), GAME_PROPS)[i], "res/river.png");
        }

        enemies = new StationaryObjects[getCoordinates("keyBulletKin.".concat(room), GAME_PROPS).length];
        for (int i=0; i<enemies.length; i++) {
            enemies[i] = new StationaryObjects(getCoordinates("keyBulletKin.".concat(room), GAME_PROPS)[i], "res/key_bullet_kin.png");
        }

        treasureBoxes = new StationaryObjects[getCoordinates("treasurebox.".concat(room), GAME_PROPS).length];
        for (int i=0; i<treasureBoxes.length; i++) {
            treasureBoxes[i] = new StationaryObjects(getCoordinates("treasurebox.".concat(room), GAME_PROPS)[i], "res/treasure_box.png");
        }
        treasureRewards = new int[treasureBoxes.length];
        String[] rewards = GAME_PROPS.getProperty("treasurebox.".concat(room)).split(";");
        for (int i=0; i< rewards.length; i++) {
            treasureRewards[i] = Integer.parseInt(rewards[i].split(",")[2]);
        }
    }

    @Override
    public void drawDoors() {
        primaryDoor.drawDoor();
        secondaryDoor.drawDoor();
    }

    @Override
    public Door[] getDoors() {
        return new Door[]{secondaryDoor, primaryDoor};
    }

    public void setTreasureBoxes() {
        StationaryObjects[] newTreasureBoxes = new StationaryObjects[treasureBoxes.length-1];
        for (int i=0; i< newTreasureBoxes.length; i++) {
            newTreasureBoxes[i] = treasureBoxes[i+1];
        }
        treasureBoxes = newTreasureBoxes;
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
        for (StationaryObjects treasureBox: treasureBoxes) {
            treasureBox.drawObject();
        }
    }

    public void setDoorsUnlocked() {
        primaryDoor.setDoorUnlocked();
        secondaryDoor.setDoorUnlocked();
    }

    @Override
    public Image[] getUnlockedImages() {
        if (primaryDoor.isDoorUnlocked()) {
            return new Image[]{primaryDoor.getUnlockedDoorImages(), secondaryDoor.getUnlockedDoorImages()};
        }
        return null;
    }

    @Override
    public Point[] getDoorCoords() {
        return new Point[]{primaryDoor.getDoorCoordinates(), secondaryDoor.getDoorCoordinates()};
    }

    public Image[] getEnemyImages() {
        Image[] enemyImages = new Image[enemies.length];
        for (int i=0; i<enemies.length; i++) {
            enemyImages[i] = enemies[i].getEnemyImage();
        }
        return enemyImages;
    }
    public Image[] getTreasureBoxImages() {
        Image[] treasureBoxImages = new Image[treasureBoxes.length];
        for (int i=0; i<treasureBoxes.length; i++) {
            treasureBoxImages[i] = treasureBoxes[i].getTreasureBoxImage();
        }
        return treasureBoxImages;
    }

    public Point[] getRiverCoords() {
        Point[] riverCoordinates = new Point[rivers.length];
        for (int i=0; i<rivers.length; i++) {
            riverCoordinates[i] = rivers[i].getPositionCoordinates();
        }
        return riverCoordinates;
    }

    public Point[] getTreasureBoxCoords() {
        Point[] treasureBoxCoords = new Point[treasureBoxes.length];
        for (int i=0; i<treasureBoxes.length; i++) {
            treasureBoxCoords[i] = treasureBoxes[i].getPositionCoordinates();
        }
        return treasureBoxCoords;
    }

    public StationaryObjects[] getTreasureBoxes() {
        return treasureBoxes;
    }

    public double getTreasureRewards(int rewardIndex) {
        return treasureRewards[rewardIndex];
    }


    public int removeElement(StationaryObjects[] objects, Point toRemove) {
        int indexRemove= objects.length;
        for (int i=0; i<objects.length; i++) {
            if (objects[i].getPositionCoordinates().equals(toRemove)) {
                indexRemove = i;
            }
        }
        int treasureRewardCoins = treasureRewards[indexRemove];
        StationaryObjects[] newTreasureBoxes = new StationaryObjects[objects.length-1];
        int[] newTreasureRewards = new int[objects.length-1];
        for (int i=0; i<indexRemove; i++) {
            newTreasureBoxes[i] = objects[i];
            newTreasureRewards[i] = treasureRewards[i];
        }
        for (int i=indexRemove+1; i<objects.length; i++) {
            newTreasureBoxes[i-1] = objects[i];
            newTreasureRewards[i-1] = treasureRewards[i];
        }
        treasureBoxes = newTreasureBoxes;
        treasureRewards = newTreasureRewards;
        return treasureRewardCoins;
    }

    public Image[] getRiverImages() {
        Image[] riverImages = new Image[rivers.length];
        for (int i=0; i<rivers.length; i++) {
            riverImages[i] = rivers[i].getRiverImage();
        }
        return riverImages;
    }

    public Point[] getWallCoords() {
        Point[] wallCoordinates = new Point[walls.length];
        for (int i=0; i<walls.length; i++) {
            wallCoordinates[i] = walls[i].getPositionCoordinates();
        }
        return wallCoordinates;
    }

    public Image[] getWallImages() {
        Image[] wallImages = new Image[walls.length];
        for (int i=0; i<walls.length; i++) {
            wallImages[i] = walls[i].getWallImage();
        }
        return wallImages;
    }

    public Point[] getEnemyCoords() {
        Point[] enemyCoordinates = new Point[enemies.length];
        for (int i=0; i<enemies.length; i++) {
            enemyCoordinates[i] = enemies[i].getPositionCoordinates();
        }
        return enemyCoordinates;
    }

    public void setEnemies() {
        StationaryObjects[] newEnemies = new StationaryObjects[enemies.length-1];
        for (int i=0; i< newEnemies.length; i++) {
            newEnemies[i] = enemies[i+1];
        }
        enemies = newEnemies;
    }
}
