package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.ygames.ysoccer.match.Weather;

import java.util.ArrayList;
import java.util.Arrays;

public class Settings {

    private Preferences preferences;
    private Json json;

    public final String APP_NAME = "YSoccer";
    public final String VERSION = "16";
    public boolean fullScreen;
    public String locale;
    public static Integer[] matchLengths = {3, 5, 7, 10};
    public int matchLength;
    public int benchSize;
    public boolean useFlags;
    public double maxPlayerValue;
    public String currency;
    public int weatherMaxStrength;
    public int zoom;
    public boolean radar;
    public boolean autoReplays;
    private String keyboardConfigs;
    private String joystickConfigs;

    Settings() {
        preferences = Gdx.app.getPreferences(APP_NAME + VERSION);

        json = new Json();
        json.addClassTag("KeyboardConfig", KeyboardConfig.class);
        json.addClassTag("JoystickConfig", JoystickConfig.class);

        fullScreen = preferences.getBoolean("fullScreen", false);
        locale = preferences.getString("locale", "en");
        matchLength = preferences.getInteger("matchLength", matchLengths[0]);
        benchSize = preferences.getInteger("benchSize", 5);
        useFlags = preferences.getBoolean("useFlags", true);
        maxPlayerValue = preferences.getInteger("maxPlayerValueM", 1)
                * Math.pow(10, preferences.getInteger("maxPlayerValueE", 8));
        currency = preferences.getString("currency", "€");
        weatherMaxStrength = preferences.getInteger("weatherMaxStrength", Weather.Strength.LIGHT);
        zoom = preferences.getInteger("zoom", 100);
        radar = preferences.getBoolean("radar", true);
        autoReplays = preferences.getBoolean("autoReplays", true);
        keyboardConfigs = preferences.getString("keyboardConfigs", defaultKeyboardConfigs());
        joystickConfigs = preferences.getString("joystickConfigs", "[]");
    }

    public void save() {
        preferences.putBoolean("fullScreen", fullScreen);
        preferences.putString("locale", locale);
        preferences.putInteger("matchLength", matchLength);
        preferences.putInteger("benchSize", benchSize);
        preferences.putBoolean("useFlags", useFlags);
        int e = (int) Math.log10(maxPlayerValue);
        int m = (int) (maxPlayerValue / Math.pow(10, e));
        preferences.putInteger("maxPlayerValueM", m);
        preferences.putInteger("maxPlayerValueE", e);
        preferences.putString("currency", currency);
        preferences.putInteger("weatherMaxStrength", weatherMaxStrength);
        preferences.putInteger("zoom", zoom);
        preferences.putBoolean("radar", radar);
        preferences.putBoolean("autoReplays", autoReplays);
        preferences.putString("keyboardConfigs", keyboardConfigs);
        preferences.putString("joystickConfigs", joystickConfigs);
        preferences.flush();
    }

    private String defaultKeyboardConfigs() {
        ArrayList<KeyboardConfig> keyboardConfigs = new ArrayList<KeyboardConfig>();
        keyboardConfigs.add(new KeyboardConfig(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.M, Input.Keys.N));
        keyboardConfigs.add(new KeyboardConfig(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.V, Input.Keys.B));
        return json.toJson(keyboardConfigs);
    }

    public ArrayList<KeyboardConfig> getKeyboardConfigs() {
        return new ArrayList<KeyboardConfig>(Arrays.asList(json.fromJson(KeyboardConfig[].class, keyboardConfigs)));
    }

    public void setKeyboardConfigs(ArrayList<KeyboardConfig> keyboardConfigs) {
        this.keyboardConfigs = json.toJson(keyboardConfigs);
    }

    private ArrayList<JoystickConfig> getJoystickConfigs() {
        return new ArrayList<JoystickConfig>(Arrays.asList(json.fromJson(JoystickConfig[].class, joystickConfigs)));
    }

    public JoystickConfig getJoystickConfigByName(String name) {
        for (JoystickConfig joystickConfig : getJoystickConfigs()) {
            if (joystickConfig.name.equals(name)) {
                return joystickConfig;
            }
        }
        return null;
    }

    public void setJoystickConfigs(ArrayList<JoystickConfig> joystickConfigs) {
        this.joystickConfigs = json.toJson(joystickConfigs);
    }
}
