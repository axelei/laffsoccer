package com.ygames.ysoccer.match;

public class Const {

    public static int[][] goalsProbability = new int[][]{
            {1000, 0, 0, 0, 0, 0, 0},
            {870, 100, 25, 4, 1, 0, 0},
            {730, 210, 50, 7, 2, 1, 0},
            {510, 320, 140, 20, 6, 4, 0},
            {390, 370, 180, 40, 10, 7, 3},
            {220, 410, 190, 150, 15, 10, 5},
            {130, 390, 240, 200, 18, 15, 7},
            {40, 300, 380, 230, 25, 15, 10},
            {20, 220, 240, 220, 120, 100, 80},
            {10, 150, 190, 190, 170, 150, 140},
            {0, 100, 150, 200, 200, 200, 150}
    };

    // teams
    public static final int TEAM_SIZE = 11;
    public static final int BASE_TEAM = 16;
    public static final int FULL_TEAM = 32;
}
