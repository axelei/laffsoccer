package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {
    public final String APP_NAME = "YSoccer";
    public final int VERSION = 16;
    public final int GUI_WIDTH = 1280;
    public final int GUI_HEIGHT = 720;

    Preferences preferences;

    public Settings() {
        preferences = Gdx.app.getPreferences(APP_NAME + VERSION);
    }

    public void save() {
        preferences.flush();
    }
}
