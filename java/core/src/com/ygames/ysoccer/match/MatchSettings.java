package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.math.Emath;

public class MatchSettings extends SceneSettings {

    int matchLength;

    public int sky; // Sky.CLEAR, Sky.CLOUDY

    private int weatherMaxStrength;
    public int substitutions;
    public int benchSize;
    boolean autoReplays;
    public boolean radar;
    boolean crowdChants;

    // used in training
    public MatchSettings(Settings gameSettings) {
        super(gameSettings);

        matchLength = gameSettings.matchLength;
        matchLength = Settings.matchLengths[0];
        this.time = randomTime();
        this.pitchType = Pitch.random();
        this.weatherMaxStrength = gameSettings.weatherMaxStrength;
        for (int i = Emath.rand(0, 2 + 4 * weatherMaxStrength); i >= 0; i--) {
            rotateWeather();
        }
        benchSize = gameSettings.benchSize;
        autoReplays = gameSettings.autoReplays;
        radar = gameSettings.radar;
        crowdChants = true;
        commentary = false;
    }

    public MatchSettings(Competition competition, Settings gameSettings) {
        super(gameSettings);

        if (competition.type == Competition.Type.FRIENDLY || competition.category == Competition.Category.DIY_COMPETITION) {
            matchLength = gameSettings.matchLength;
        } else {
            matchLength = Settings.matchLengths[0];
        }
        this.time = competition.getTime();
        this.pitchType = competition.getPitchType();
        this.weatherMaxStrength = gameSettings.weatherMaxStrength;
        for (int i = Emath.rand(0, 2 + 4 * weatherMaxStrength); i >= 0; i--) {
            rotateWeather();
        }
        substitutions = competition.substitutions;
        benchSize = competition.benchSize;
        autoReplays = gameSettings.autoReplays;
        radar = gameSettings.radar;
        crowdChants = true;
        commentary = gameSettings.commentary;
    }

    public void rotateTime(int direction) {
        time = MatchSettings.Time.values()[Emath.rotate(time, MatchSettings.Time.DAY, Time.NIGHT, direction)];
    }

    public void rotatePitchType(int direction) {
        pitchType = Pitch.Type.values()[Emath.rotate(pitchType.ordinal(), Pitch.Type.FROZEN.ordinal(), Pitch.Type.WHITE.ordinal(), direction)];
        weatherEffect = Weather.WIND;
        weatherStrength = Weather.Strength.NONE;
        sky = Sky.CLEAR;
    }

    public void rotateWeather() {
        if ((weatherStrength == Weather.Strength.NONE) && (sky == Sky.CLEAR)) {
            sky = Sky.CLOUDY;
        } else {
            boolean found;
            do {
                found = true;
                if (weatherStrength < Weather.Strength.STRONG) {
                    weatherStrength = weatherStrength + 1;
                } else {
                    weatherEffect = Emath.rotate(weatherEffect, Weather.WIND, Weather.FOG, 1);
                    if (weatherEffect == Weather.WIND) {
                        weatherStrength = Weather.Strength.NONE;
                    } else {
                        weatherStrength = Weather.Strength.LIGHT;
                    }
                }

                // weather possibility check
                if (weatherStrength > Weather.cap[pitchType.ordinal()][weatherEffect]) {
                    found = false;
                }
                if (weatherStrength > weatherMaxStrength) {
                    found = false;
                }

                // sky
                sky = Sky.CLOUDY;
                if (weatherStrength == Weather.Strength.NONE) {
                    sky = Sky.CLEAR;
                } else if (weatherEffect == Weather.WIND) {
                    sky = Sky.CLEAR;
                }
            } while (!found);
        }
    }

    public boolean useOrangeBall() {
        return (pitchType == Pitch.Type.SNOWED) ||
                (pitchType == Pitch.Type.WHITE) ||
                ((pitchType == Pitch.Type.FROZEN)
                        && (weatherEffect == Weather.SNOW)
                        && (weatherStrength > Weather.Strength.NONE));
    }

    public static String getTimeLabel(Time time) {
        switch (time) {
            case DAY:
                return "TIME.DAY";
            case NIGHT:
                return "TIME.NIGHT";
            default:
                throw new GdxRuntimeException("Unknown time");
        }
    }

    public String getWeatherLabel() {
        String s = "";
        switch (weatherStrength) {
            case Weather.Strength.NONE:
                if (sky == Sky.CLEAR) {
                    s = "SKY.CLEAR";
                } else {
                    s = "SKY.CLOUDY";
                }
                break;

            case Weather.Strength.LIGHT:
                switch (weatherEffect) {
                    case Weather.WIND:
                        s = "WIND.LIGHT";
                        break;

                    case Weather.RAIN:
                        s = "RAIN.LIGHT";
                        break;

                    case Weather.SNOW:
                        s = "SNOW.LIGHT";
                        break;

                    case Weather.FOG:
                        s = "FOG.THIN";
                        break;
                }
                break;

            case Weather.Strength.STRONG:
                switch (weatherEffect) {
                    case Weather.WIND:
                        s = "WIND.STRONG";
                        break;

                    case Weather.RAIN:
                        s = "RAIN.STRONG";
                        break;

                    case Weather.SNOW:
                        s = "SNOW.STRONG";
                        break;

                    case Weather.FOG:
                        s = "FOG.THICK";
                        break;
                }
                break;
        }
        return s;
    }

    public int weatherOffset() {
        if (weatherStrength == Weather.Strength.NONE) {
            return sky;
        } else {
            return 2 * weatherEffect + weatherStrength + 1;
        }
    }

    public void setup() {
        initTime();
        initGrass();
        initWind();
        adjustGrassFriction();
        adjustGrassBounce();
    }

    private void initTime() {
        if (time == Time.NIGHT) {
            shadowAlpha = 0.4f;
        } else {
            shadowAlpha = 0.75f;
        }
    }

    private void initGrass() {
        grass.copy(Pitch.grasses[pitchType.ordinal()]);
    }

    private void initWind() {
        if (weatherEffect == Weather.WIND) {
            wind.init(weatherStrength, Assets.random);
        }
    }

    private void adjustGrassFriction() {
        if (weatherStrength != Weather.Strength.NONE) {
            switch (weatherEffect) {
                case Weather.RAIN:
                    grass.friction = grass.friction - weatherStrength;
                    break;

                case Weather.SNOW:
                    grass.friction = grass.friction + weatherStrength;
                    break;
            }
        }
    }

    private void adjustGrassBounce() {
        if (weatherStrength != Weather.Strength.NONE) {
            switch (weatherEffect) {
                case Weather.RAIN:
                    grass.bounce = grass.bounce - 0.03f * weatherStrength;
                    break;

                case Weather.SNOW:
                    grass.bounce = grass.bounce - 0.02f * weatherStrength;
                    break;
            }
        }
    }
}
