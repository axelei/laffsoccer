package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Screen;
import com.ysoccer.android.framework.impl.GLGame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class YSoccer extends GLGame {
    boolean firstTimeCreate = true;

    public Screen getStartScreen() {
        return new MenuIntro(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        if (firstTimeCreate) {
            settings = new Settings(this);
            int wZoom = 20 * (int) (5.0f * glGraphics.getWidth() / Settings.GUI_WIDTH);
            int hZoom = 20 * (int) (5.0f * glGraphics.getHeight() / Settings.GUI_HEIGHT);
            Settings.guiZoom = Math.min(wZoom, hZoom);
            Settings.screenWidth = glGraphics.getWidth() * 100 / Settings.guiZoom;
            Settings.screenHeight = glGraphics.getHeight() * 100 / Settings.guiZoom;
            Settings.guiOriginX = (Settings.screenWidth - Settings.GUI_WIDTH) / 2;
            Settings.guiOriginY = (Settings.screenHeight - Settings.GUI_HEIGHT) / 2;

            Assets.load(this);
            firstTimeCreate = false;
        } else {
            Assets.reload(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Assets.music.isPlaying())
            Assets.music.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Assets.music != null && !Assets.music.isPlaying()) {
            Assets.music.play();
        }
    }

}