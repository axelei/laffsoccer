package com.ysoccer.android.ysdemo;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.ysdemo.match.Weather;

public class Settings {
    static final int GUI_WIDTH = 960;
    static final int GUI_HEIGHT = 540;

    public static int guiOriginX;
    public static int guiOriginY;
    public static int guiZoom;

    public static int screenWidth;
    public static int screenHeight;

    private SharedPreferences preferences;
    public float musicVolume;
    public float sfxVolume;
    public int gameLengthIndex;
    public int weatherMaxStrength;
    public boolean displayRadar;
    public boolean autoReplay;

    public static final int[] gameLengths = {3, 5, 7, 10};

    public Settings(GLGame glGame) {
        preferences = glGame.getFileIO().getPreferences();

        musicVolume = preferences.getFloat("musicVolume", 0.4f);
        sfxVolume = preferences.getFloat("sfxVolume", 0.4f);
        gameLengthIndex = preferences.getInt("gameLengthIndex", 0);
        weatherMaxStrength = preferences.getInt("weatherMaxStrength", Weather.Strength.LIGHT);
        displayRadar = preferences.getBoolean("displayRadar", false);
        autoReplay = preferences.getBoolean("autoReplay", true);
    }

    public void save() {
        Editor editor = preferences.edit();

        editor.putFloat("musicVolume", musicVolume);
        editor.putFloat("sfxVolume", sfxVolume);
        editor.putInt("gameLengthIndex", gameLengthIndex);
        editor.putInt("weatherMaxStrength", weatherMaxStrength);
        editor.putBoolean("displayRadar", displayRadar);
        editor.putBoolean("autoReplay", autoReplay);

        editor.apply();
    }
}
