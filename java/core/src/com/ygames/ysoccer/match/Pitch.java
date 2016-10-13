package com.ygames.ysoccer.match;

public class Pitch {

    public static final int FROZEN = 0;
    public static final int MUDDY = 1;
    public static final int WET = 2;
    public static final int SOFT = 3;
    public static final int NORMAL = 4;
    public static final int DRY = 5;
    public static final int HARD = 6;
    public static final int SNOWED = 7;
    public static final int WHITE = 8;
    public static final int RANDOM = 9;
    public static final String[] names = {
            "PITCH.FROZEN",
            "PITCH.MUDDY",
            "PITCH.WET",
            "PITCH.SOFT",
            "PITCH.NORMAL",
            "PITCH.DRY",
            "PITCH.HARD",
            "PITCH.SNOWED",
            "PITCH.WHITE",
            "RANDOM"
    };

    public static int[][] probabilityByMonth = new int[][]{
            // frozen, muddy, wet, soft, normal, dry, hard, snowed, white, cloudy
            {20, 20, 20, 5, 10, 0, 5, 10, 10, 50}, //january
            {20, 20, 20, 5, 10, 0, 5, 10, 10, 40}, //february
            {10, 20, 30, 10, 15, 0, 5, 5, 5, 30}, //march
            {0, 15, 35, 20, 25, 0, 5, 0, 0, 20}, //april
            {0, 10, 30, 25, 25, 5, 5, 0, 0, 10}, //may
            {0, 5, 20, 30, 35, 5, 5, 0, 0, 10}, //june
            {0, 0, 20, 30, 30, 10, 10, 0, 0, 10}, //july
            {0, 0, 10, 30, 30, 15, 15, 0, 0, 5}, //august
            {0, 5, 20, 25, 35, 5, 10, 0, 0, 10}, //september
            {5, 15, 20, 20, 30, 0, 5, 5, 0, 20}, //october
            {5, 20, 20, 15, 25, 0, 5, 5, 5, 30}, //november
            {10, 20, 20, 10, 15, 0, 5, 10, 10, 40} //december
    };
}
