package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;

public class Pitch {

    public enum Type {
        FROZEN,
        MUDDY,
        WET,
        SOFT,
        NORMAL,
        DRY,
        HARD,
        SNOWED,
        WHITE,
        RANDOM
    }

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

    static final Grass[] grasses = {
            new Grass(0x58584C, 0x3C3C34, 4f, 0.70f), // frozen
            new Grass(0x5C3800, 0x442C00, 12f, 0.55f), // muddy
            new Grass(0x486C00, 0x3C5800, 6f, 0.60f), // wet
            new Grass(0x3C5800, 0x2C4400, 10f, 0.60f), // soft
            new Grass(0x486C00, 0x3C5800, 8f, 0.65f), // normal
            new Grass(0x486C00, 0x3C5800, 6f, 0.65f), // dry
            new Grass(0x684C00, 0x463C00, 6f, 0.70f), // hard
            new Grass(0x58584C, 0x3C3C34, 4f, 0.70f), // snowed
            new Grass(0x58584C, 0x3C3C34, 10f, 0.60f) // white
    };

    static final int[][][] startingPositions = {
            // kickoff team
            {
                    {0, -630},
                    {-240, -180}, {-120, -320}, {+120, -320}, {240, -180},
                    {-378, 0}, {-100, -140}, {100, -140}, {+212, 0},
                    {-58, 0}, {10, -10}
            },

            // opponent team
            {
                    {0, -630},
                    {-240, -370}, {-120, -480}, {+120, -480}, {240, -370},
                    {-320, -150}, {-60, -240}, {+60, -240}, {+320, -150},
                    {-100, -86}, {100, -86}
            }
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

    public static Type random() {

        int[] pitchProbabilities = probabilityByMonth[EMath.rand(0, 11)];

        float sum = 0;
        float r = Assets.random.nextFloat();
        int i = -1;
        do {
            i++;
            sum += pitchProbabilities[i] / 100f;
        } while (sum < r);
        return Pitch.Type.values()[i];
    }
}
