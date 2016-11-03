package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.ygames.ysoccer.match.Weather;

public class Settings {
    public final String APP_NAME = "YSoccer";
    public final int VERSION = 16;
    public final int GUI_WIDTH = 1280;
    public final int GUI_HEIGHT = 720;
    public String locale;
    public int benchSize;
    public boolean useFlags;
    public double maxPlayerValue;
    public String currency;
    public int weatherMaxStrength;
    public boolean autoReplays;

    private Preferences preferences;

    Settings() {
        preferences = Gdx.app.getPreferences(APP_NAME + VERSION);
        locale = preferences.getString("locale", "en");
        benchSize = preferences.getInteger("benchSize", 5);
        useFlags = preferences.getBoolean("useFlags", true);
        maxPlayerValue = preferences.getInteger("maxPlayerValueM", 1)
                * Math.pow(10, preferences.getInteger("maxPlayerValueE", 8));
        currency = preferences.getString("currency", "€");
        weatherMaxStrength = preferences.getInteger("weatherMaxStrength", Weather.Strength.LIGHT);
        autoReplays = preferences.getBoolean("autoReplays", true);
    }

    public void save() {
        preferences.putString("locale", locale);
        preferences.putInteger("benchSize", benchSize);
        preferences.putBoolean("useFlags", useFlags);
        int e = (int) Math.log10(maxPlayerValue);
        int m = (int) (maxPlayerValue / Math.pow(10, e));
        preferences.putInteger("maxPlayerValueM", m);
        preferences.putInteger("maxPlayerValueE", e);
        preferences.putString("currency", currency);
        preferences.putInteger("weatherMaxStrength", weatherMaxStrength);
        preferences.putBoolean("autoReplays", autoReplays);
        preferences.flush();
    }
}
