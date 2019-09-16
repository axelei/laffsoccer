package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Settings;

class SceneSettings {

    public enum Time {DAY, NIGHT}

    public Time time;
    public Grass grass;
    public Pitch.Type pitchType;
    public Wind wind;
    int weatherEffect; // Weather.WIND, Weather.RAIN, Weather.SNOW, Weather.FOG
    int weatherStrength; // Weather.Strength.NONE, Weather.Strength.LIGHT, Weather.Strength.STRONG

    public boolean commentary;

    boolean fullScreen;
    public int zoom;
    float shadowAlpha;

    SceneSettings(Settings gameSettings) {
        this.zoom = gameSettings.zoom;
        this.fullScreen = gameSettings.fullScreen;

        this.grass = new Grass();
        this.pitchType = Pitch.random();
        this.wind = new Wind();
    }

    public static Time randomTime() {
        float dayProbability = 0.7f;
        return Assets.random.nextFloat() < dayProbability ? Time.DAY : Time.NIGHT;
    }

    public int getZoom() {
        return zoom;
    }
}
