package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {
    public final String APP_NAME = "YSoccer";
    public final int VERSION = 16;
    public final int GUI_WIDTH = 1280;
    public final int GUI_HEIGHT = 720;
    public String locale;
    public int benchSize;
    public int maxPlayerPrice;
    public String currency;

    Preferences preferences;

    public Settings() {
        preferences = Gdx.app.getPreferences(APP_NAME + VERSION);
        locale = preferences.getString("locale", "en");
        benchSize = preferences.getInteger("benchSize", 5);
        maxPlayerPrice = preferences.getInteger("maxPlayerPrice", 100000000);
        currency = preferences.getString("currency", "€");
    }

    public void save() {
        preferences.putString("locale", locale);
        preferences.putInteger("benchSize", benchSize);
        preferences.putInteger("maxPlayerPrice", maxPlayerPrice);
        preferences.putString("currency", currency);
        preferences.flush();
    }
}
