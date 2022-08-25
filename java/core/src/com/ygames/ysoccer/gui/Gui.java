package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gui {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public int screenWidth;
    public int screenHeight;
    public int originX;
    public int originY;

    public TextureRegion logo;
    public final TextureRegion[] lightIcons = new TextureRegion[2];
    public final TextureRegion[] pitchIcons = new TextureRegion[9];
    public final TextureRegion[] weatherIcons = new TextureRegion[10];

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

        TextureAtlas.AtlasRegion region;
        region = guiAtlas.findRegion("light");
        for (int i = 0; i < 2; i++) {
            lightIcons[i] = new TextureRegion(region, 47 * i, 0, 46, 46);
            lightIcons[i].flip(false, true);
        }

        region = guiAtlas.findRegion("pitches");
        for (int i = 0; i < 9; i++) {
            pitchIcons[i] = new TextureRegion(region, 47 * i, 0, 46, 46);
            pitchIcons[i].flip(false, true);
        }

        region = guiAtlas.findRegion("weather");
        for (int i = 0; i < 10; i++) {
            weatherIcons[i] = new TextureRegion(region, 47 * i, 0, 46, 46);
            weatherIcons[i].flip(false, true);
        }
    }
}