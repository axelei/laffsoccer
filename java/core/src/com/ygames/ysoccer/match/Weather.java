package com.ygames.ysoccer.match;

public class Weather {

    public static final int WIND = 0;
    public static final int RAIN = 1;
    public static final int SNOW = 2;
    public static final int FOG = 3;

    public static class Strength {

        public static final int NONE = 0;
        public static final int LIGHT = 1;
        public static final int STRONG = 2;

        public static final String[] names = {"WEATHER.EFFECTS.OFF", "WEATHER.EFFECTS.LIGHT", "WEATHER.EFFECTS.STRONG"};
    }

    public static int[][] cap = {
            //      wind            rain           snow            fog
            {Strength.STRONG, Strength.NONE, Strength.LIGHT, Strength.NONE}, // frozen
            {Strength.STRONG, Strength.STRONG, Strength.NONE, Strength.STRONG}, // muddy
            {Strength.STRONG, Strength.STRONG, Strength.NONE, Strength.STRONG}, // wet
            {Strength.STRONG, Strength.LIGHT, Strength.NONE, Strength.STRONG}, // soft
            {Strength.STRONG, Strength.NONE, Strength.NONE, Strength.STRONG}, // normal
            {Strength.STRONG, Strength.NONE, Strength.NONE, Strength.NONE}, // dry
            {Strength.STRONG, Strength.NONE, Strength.NONE, Strength.STRONG}, // hard
            {Strength.STRONG, Strength.NONE, Strength.STRONG, Strength.STRONG}, // snowed
            {Strength.STRONG, Strength.NONE, Strength.STRONG, Strength.STRONG} // white
    };
}
