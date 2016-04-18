package com.oulu.daussy.broommate.Model;

/**
 * Created by daussy on 18/04/16.
 */
public class Home {

    private static Home HOME;
    private String posX;
    private String posY;

    public String getPosY() {
        return posY;
    }

    public void setPosY(String posY) {
        this.posY = posY;
    }

    public String getPosX() {
        return posX;
    }

    public void setPosX(String posX) {
        this.posX = posX;
    }

    public static Home getInstance() {
        if (HOME == null)
            HOME = new Home();

        return HOME;

    }

}
