package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gui {

    public final int WIDTH = 1280;
    public final int HEIGHT = 720;
    public int screenWidth;
    public int screenHeight;
    public int originX;
    public int originY;

    public TextureRegion logo;

    public void resize(int width, int height) {
        float wZoom = (float) width / WIDTH;
        float hZoom = (float) height / HEIGHT;
        float zoom = Math.min(wZoom, hZoom);
        screenWidth = (int) (width / zoom);
        screenHeight = (int) (height / zoom);
        originX = (screenWidth - WIDTH) / 2;
        originY = (screenHeight - HEIGHT) / 2;
    }

    public void setTextures(TextureAtlas guiAtlas) {
        logo = guiAtlas.findRegion("logo");
        logo.flip(false, true);
    }
}