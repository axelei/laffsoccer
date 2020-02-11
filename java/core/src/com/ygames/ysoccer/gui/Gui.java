package com.ygames.ysoccer.gui;

public class Gui {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public int screenWidth;
    public int screenHeight;
    public int originX;
    public int originY;

    public void resize(int width, int height) {
        float wZoom = (float) width / WIDTH;
        float hZoom = (float) height / HEIGHT;
        float zoom = Math.min(wZoom, hZoom);
        screenWidth = (int) (width / zoom);
        screenHeight = (int) (height / zoom);
        originX = (screenWidth - WIDTH) / 2;
        originY = (screenHeight - HEIGHT) / 2;
    }
}
