package com.ygames.ysoccer.match;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.math.Emath;

public class MatchSettings {

    public int time;
    float shadowAlpha;
    public int sky; // Sky.CLEAR, Sky.CLOUDY
    public Pitch.Type pitchType;

    public Grass grass;
    public int weatherEffect; // Weather.WIND, Weather.RAIN, Weather.SNOW, Weather.FOG, Weather.RANDOM
    public int weatherStrength; // Weather.Strength.NONE, Weather.Strength.LIGHT, Weather.Strength.STRONG
    private int weatherMaxStrength;
    public Wind wind;

    public MatchSettings(Competition competition, int weatherMaxStrength) {
        this.time = competition.time;
        this.pitchType = competition.resolvePitchType();
        this.grass = new Grass();
        this.wind = new Wind();
        this.weatherMaxStrength = weatherMaxStrength;
        if (competition.getType() == Competition.Type.FRIENDLY) {
            weatherEffect = Weather.RANDOM;
        } else {
            for (int i = Emath.rand(0, 2 + 4 * weatherMaxStrength); i >= 0; i--) {
                rotateWeather(false);
            }
        }
    }

    public void rotateTime(int direction) {
        time = Emath.rotate(time, Time.DAY, Time.RANDOM, direction);
    }

    public void rotatePitchType(int direction) {
        pitchType = Pitch.Type.values()[Emath.rotate(pitchType.ordinal(), Pitch.Type.FROZEN.ordinal(), Pitch.Type.RANDOM.ordinal(), direction)];
        weatherEffect = Weather.RANDOM;
        sky = Sky.CLOUDY;
    }

    public void rotateWeather(boolean includeRandom) {
        if (pitchType == Pitch.Type.RANDOM) {
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
                    if (includeRandom) {
                        weatherEffect = (weatherEffect + 1) % 5;
                        weatherStrength = Weather.Strength.LIGHT;
                    } else {
                        weatherEffect = (weatherEffect + 1) % Weather.RANDOM;
                        if (weatherEffect == Weather.WIND) {
                            weatherStrength = Weather.Strength.NONE;
                        } else {
                            weatherStrength = Weather.Strength.LIGHT;
                        }
                    }
                }

                // weather possibility check
                if (weatherEffect != Weather.RANDOM) {
                    if (weatherStrength > Weather.cap[pitchType.ordinal()][weatherEffect]) {
                        found = false;
                    }
                    if (weatherStrength > weatherMaxStrength) {
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

    public boolean isSnowing() {
        if ((pitchType == Pitch.Type.SNOWED) || (pitchType == Pitch.Type.WHITE)) {
            return true;
        }
        if ((pitchType == Pitch.Type.FROZEN) && (weatherEffect == Weather.SNOW)
                && (weatherStrength > Weather.Strength.NONE)) {
            return true;
        }
        return false;
    }

    public String getWeatherLabel() {
        String s = "";
        if (weatherEffect == Weather.RANDOM) {
            s = "RANDOM";
        } else {
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
        }
        return s;
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
            time = Assets.random.nextInt(Time.RANDOM);
        }

        if (time == Time.NIGHT) {
            shadowAlpha = 0.4f;
        } else {
            shadowAlpha = 0.75f;
        }
    }

    private void initPitchType() {
        if (pitchType == Pitch.Type.RANDOM) {
            pitchType = Pitch.Type.values()[Assets.random.nextInt(Pitch.Type.RANDOM.ordinal())];
        }
    }

    private void initGrass() {
        grass.copy(Pitch.grasses[pitchType.ordinal()]);
    }

    private void initWeather() {
        if (weatherEffect == Weather.RANDOM) {
            weatherEffect = Assets.random.nextInt(Weather.RANDOM);
            weatherStrength = Weather.Strength.STRONG - (int) Math.round(Math.log10(Assets.random.nextInt(16) + 1) / Math.log10(4));

            // constrain by settings
            weatherStrength = Math.min(weatherStrength, weatherMaxStrength);

            // constrain by pitch_type
            weatherStrength = Math.min(weatherStrength, Weather.cap[pitchType.ordinal()][weatherEffect]);

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
