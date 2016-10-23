package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.ysdemo.R;

public class MatchSettings {

    GLGame game;

    public float sfxVolume;
    public int time; // Time.DAY, Time.NIGHT, Time.RANDOM
    float shadowAlpha;
    public int sky; // SK.CLEAR, SK.CLOUDY
    public int pitchType; // PT.FROZEN, PT.MUDDY, PT.WET, PT.SOFT, PT.NORMAL,
    // PT.DRY, PT.HARD, PT.SNOWED, PT.WHITE, PT.RANDOM
    public Grass grass;
    public int weatherEffect; // WE.WIND, WE.RAIN, WE.SNOW, WE.FOG, WE.RANDOM
    public int weatherStrength; // WI.NONE, WI.LIGHT, WI.STRONG
    public Wind wind;

    public MatchSettings(GLGame game) {
        this.game = game;
        this.sfxVolume = game.settings.sfxVolume;
        this.grass = new Grass();
        this.wind = new Wind();
    }

    public void rotateTime(int direction) {
        time = Emath.rotate(time, Time.DAY, Time.NIGHT, direction);
    }

    public void rotatePitchType(int direction) {
        pitchType = Emath.rotate(pitchType, Pitch.FROZEN, Pitch.WHITE,
                direction);
        weatherEffect = Weather.WIND;
        weatherStrength = Weather.Strength.NONE;
        sky = Sky.CLEAR;
    }

    public void rotateWeather() {
        if (pitchType == Pitch.RANDOM) {
            return;
        }

        if ((weatherStrength == Weather.Strength.NONE) && (sky == Sky.CLEAR)) {
            sky = Sky.CLOUDY;
        } else {
            boolean found;
            do {
                found = true;
                if (weatherEffect == Weather.RANDOM) {
                    weatherEffect = Weather.WIND;
                    weatherStrength = Weather.Strength.NONE;
                } else if (weatherStrength < Weather.Strength.STRONG) {
                    weatherStrength = weatherStrength + 1;
                } else {
                    weatherEffect = (weatherEffect + 1) % Weather.RANDOM;
                    if (weatherEffect == Weather.WIND) {
                        weatherStrength = Weather.Strength.NONE;
                    } else {
                        weatherStrength = Weather.Strength.LIGHT;
                    }
                }

                // weather possibility check
                if (weatherEffect != Weather.RANDOM) {
                    if (weatherStrength > Weather.cap[pitchType][weatherEffect]) {
                        found = false;
                    }
                    if (weatherStrength > game.settings.weatherMaxStrength) {
                        found = false;
                    }
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

    public int timeStringId() {
        return Time.stringIds[time];
    }

    public int pitchStringId() {
        return Pitch.stringIds[pitchType];
    }

    public boolean isSnowing() {
        if ((pitchType == Pitch.SNOWED) || (pitchType == Pitch.WHITE)) {
            return true;
        }
        if ((pitchType == Pitch.FROZEN) && (weatherEffect == Weather.SNOW)
                && (weatherStrength > Weather.Strength.NONE)) {
            return true;
        }
        return false;
    }

    public int weatherStringId() {
        int stringId = 0;
        if (weatherEffect == Weather.RANDOM) {
            stringId = R.string.RANDOM;
        } else {
            switch (weatherStrength) {
                case Weather.Strength.NONE:
                    if (sky == Sky.CLEAR) {
                        stringId = R.string.CLEAR;
                    } else {
                        stringId = R.string.CLOUDY;
                    }
                    break;

                case Weather.Strength.LIGHT:
                    switch (weatherEffect) {
                        case Weather.WIND:
                            stringId = R.string.LIGHT_WIND;
                            break;

                        case Weather.RAIN:
                            stringId = R.string.LIGHT_RAIN;
                            break;

                        case Weather.SNOW:
                            stringId = R.string.LIGHT_SNOW;
                            break;

                        case Weather.FOG:
                            stringId = R.string.THIN_FOG;
                            break;
                    }
                    break;

                case Weather.Strength.STRONG:
                    switch (weatherEffect) {
                        case Weather.WIND:
                            stringId = R.string.STRONG_WIND;
                            break;

                        case Weather.RAIN:
                            stringId = R.string.STRONG_RAIN;
                            break;

                        case Weather.SNOW:
                            stringId = R.string.STRONG_SNOW;
                            break;

                        case Weather.FOG:
                            stringId = R.string.THICK_FOG;
                            break;
                    }
                    break;

            }
        }
        return stringId;
    }

    public int weatherOffset() {
        if (weatherEffect == Weather.RANDOM) {
            return 10;
        } else {
            if (weatherStrength == Weather.Strength.NONE) {
                return sky;
            } else {
                return 2 * weatherEffect + weatherStrength + 1;
            }
        }
    }

    public void setup() {
        initTime();
        initPitchType();
        initGrass();
        initWeather();
        initWind();
        adjustGrassFriction();
        adjustGrassBounce();
    }

    private void initTime() {
        if (time == Time.RANDOM) {
            time = game.random.nextInt(Time.RANDOM);
        }

        if (time == Time.NIGHT) {
            shadowAlpha = 0.4f;
        } else {
            shadowAlpha = 0.75f;
        }
    }

    public void initPitchType() {
        if (pitchType == Pitch.RANDOM) {
            pitchType = game.random.nextInt(Pitch.RANDOM);
        }
    }

    private void initGrass() {
        grass.copy(Pitch.grasses[pitchType]);
    }

    public void initWeather() {

        if (weatherEffect == Weather.RANDOM) {
            Math.round(Math.log10(game.random.nextInt(16) + 1) / Math.log10(4));
            weatherEffect = game.random.nextInt(Weather.RANDOM);
            weatherStrength = Weather.Strength.STRONG
                    - (int) Math.round(Math.log10(game.random.nextInt(16) + 1)
                    / Math.log10(4));

            // constrain by settings
            weatherStrength = Math.min(weatherStrength,
                    game.settings.weatherMaxStrength);

            // constrain by pitch_type
            weatherStrength = Math.min(weatherStrength,
                    Weather.cap[pitchType][weatherEffect]);

            // sky
            sky = Sky.CLOUDY;
            if (weatherStrength == Weather.Strength.NONE) {
                sky = Sky.CLEAR;
            } else if (weatherEffect == Weather.WIND) {
                sky = Sky.CLEAR;
            }
        }

    }

    private void initWind() {
        if (weatherEffect == Weather.WIND) {
            wind.init(weatherStrength, game.random);
        }
    }

    private void adjustGrassFriction() {
        if (weatherStrength != Weather.Strength.NONE) {
            switch (weatherEffect) {
                case Weather.RAIN:
                    grass.friction = grass.friction - 1 * weatherStrength;
                    break;

                case Weather.SNOW:
                    grass.friction = grass.friction + 1 * weatherStrength;
            }
        }
    }

    private void adjustGrassBounce() {
        if (weatherStrength != Weather.Strength.NONE) {
            switch (weatherEffect) {
                case Weather.RAIN:
                    grass.bounce = grass.bounce - 3 * weatherStrength;
                    break;

                case Weather.SNOW:
                    grass.bounce = grass.bounce - 2 * weatherStrength;
                    break;
            }
        }
    }

}
