import bagel.*;

import java.util.Properties;

public class PrepRoom {

    private boolean isPrepRoom;
    private Properties GAME_PROPS;
    private Properties MESSAGE_PROPS;


    public PrepRoom(Properties GAME_PROPS, Properties MESSAGE_PROPS) {
        this.GAME_PROPS = GAME_PROPS;
        this.MESSAGE_PROPS = MESSAGE_PROPS;
    }

    public boolean getIsPrepRoom() {
        return isPrepRoom;
    }

    public void setPrepRoom(boolean prepRoom) {
        isPrepRoom = prepRoom;
    }

    public void setBackground() {

        // set the restart area image at the right coordinates
        String[] centreRestart = GAME_PROPS.getProperty("restartarea.prep").split(",");
        Image restartArea = new Image("res/restart_area.png");
        restartArea.draw(Integer.parseInt(centreRestart[0]), Integer.parseInt(centreRestart[1]));

        // set the door image at the right place
        String[] centreDoor = GAME_PROPS.getProperty("door.prep").split(",");
        Image door = new Image("res/locked_door.png");
        door.draw(Integer.parseInt(centreDoor[0]), Integer.parseInt(centreDoor[1]));

        // set the title text at the right place with the right font
        int titleFontSize = Integer.parseInt(GAME_PROPS.getProperty("title.fontSize"));
        int titleY = Integer.parseInt(GAME_PROPS.getProperty("title.y"));
        int titleX = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        String title = MESSAGE_PROPS.getProperty("title");
        Font titleText = new Font("res/wheaton.otf", titleFontSize);
        titleText.drawString(title, 250, titleY);

        // set the controls text at the right place with the right font
        String moveMessage = MESSAGE_PROPS.getProperty("moveMessage");
        int controlsFontSize = Integer.parseInt(GAME_PROPS.getProperty("prompt.fontSize"));
        int controlsY = Integer.parseInt(GAME_PROPS.getProperty("moveMessage.y"));
        Font controls = new Font("res/wheaton.otf", controlsFontSize);
        controls.drawString(moveMessage, 375, controlsY);
    }
}
