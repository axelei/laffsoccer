package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {
    public final String APP_NAME = "YSoccer";
    public final int VERSION = 16;
    public final int GUI_WIDTH = 1280;
    public final int GUI_HEIGHT = 720;
    public String locale;

    Preferences preferences;

    public Settings() {
        preferences = Gdx.app.getPreferences(APP_NAME + VERSION);
        this.locale = preferences.getString("locale", "en");
    }

    public void save() {
        preferences.putString("locale", locale);
        preferences.flush();
    }
}
