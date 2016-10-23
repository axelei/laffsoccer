package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.ysdemo.R;

public final class Pitch {

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

    public static final int[] stringIds = {R.string.FROZEN, R.string.MUDDY,
            R.string.WET, R.string.SOFT, R.string.NORMAL, R.string.DRY,
            R.string.HARD, R.string.SNOWED, R.string.WHITE, R.string.RANDOM};

    public static final String[] names = {"frozen", "muddy", "wet", "soft",
            "normal", "dry", "hard", "snowed", "white"};

    public static final Grass[] grasses = {new Grass(0x58584C, 0x3C3C34, 4, 70), // frozen
            new Grass(0x5C3800, 0x442C00, 12, 55), // muddy
            new Grass(0x486C00, 0x3C5800, 6, 60), // wet
            new Grass(0x3C5800, 0x2C4400, 10, 60), // soft
            new Grass(0x486C00, 0x3C5800, 8, 65), // normal
            new Grass(0x486C00, 0x3C5800, 6, 65), // dry
            new Grass(0x684C00, 0x463C00, 6, 70), // hard
            new Grass(0x58584C, 0x3C3C34, 4, 70), // snowed
            new Grass(0x3C5800, 0x2C4400, 10, 60) // white
    };

    public static final int[][][] startingPositions = {
            {
                    // kickoff team
                    {0, -630}, {-240, -180}, {-120, -320},
                    {+120, -320}, {240, -180}, {-378, 0}, {-100, -140},
                    {100, -140}, {+212, 0}, {-58, 0}, {10, -10}},
            {
                    // opponent team
                    {0, -630}, {-240, -370}, {-120, -480},
                    {+120, -480}, {240, -370}, {-320, -150},
                    {-60, -240}, {+60, -240}, {+320, -150},
                    {-100, -86}, {100, -86}}};

}
