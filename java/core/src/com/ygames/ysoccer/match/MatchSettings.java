package com.ygames.ysoccer.match;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.math.Emath;

public class MatchSettings {

    public int time;
    public int sky; // Sky.CLEAR, Sky.CLOUDY
    public int pitchType;

    public int weatherEffect; // Weather.WIND, Weather.RAIN, Weather.SNOW, Weather.FOG, Weather.RANDOM
    public int weatherStrength; // Weather.Strength.NONE, Weather.Strength.LIGHT, Weather.Strength.STRONG
    private int weatherMaxStrength;
    public Wind wind;

    public MatchSettings(Competition competition, int weatherMaxStrength) {
        this.time = competition.time;
        this.pitchType = competition.resolvePitchType();
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
        pitchType = Emath.rotate(pitchType, Pitch.FROZEN, Pitch.RANDOM, direction);
        weatherEffect = Weather.RANDOM;
        sky = Sky.CLOUDY;
    }

    public void rotateWeather(boolean includeRandom) {
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
                    if (weatherStrength > Weather.cap[pitchType][weatherEffect]) {
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
}
