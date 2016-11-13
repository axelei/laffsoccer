package com.ygames.ysoccer.gui;

public class Gui {

    public final int WIDTH = 1280;
    public final int HEIGHT = 720;
    public int screenWidth;
    public int screenHeight;
    public int originX;
    public int originY;

    public void resize(int width, int height) {
        int wZoom = 20 * (int) (5.0f * width / WIDTH);
        int hZoom = 20 * (int) (5.0f * height / HEIGHT);
        int zoom = Math.min(wZoom, hZoom);
        screenWidth = width * 100 / zoom;
        screenHeight = height * 100 / zoom;
        originX = (screenWidth - WIDTH) / 2;
        originY = (screenHeight - HEIGHT) / 2;
    }
}
