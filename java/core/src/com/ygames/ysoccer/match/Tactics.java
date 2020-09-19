package com.ygames.ysoccer.match;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.ygames.ysoccer.match.Const.BALL_ZONES;
import static com.ygames.ysoccer.match.Const.TACT_DX;
import static com.ygames.ysoccer.match.Const.TACT_DY;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;

public class Tactics {

    public static String[] codes = new String[]{
            "4-4-2", "5-4-1",
            "4-5-1", "5-3-2",
            "3-5-2", "4-3-3",
            "4-2-4", "3-4-3",
            "SWEEP", "5-2-3",
            "ATTACK", "DEFEND",
            "USER A", "USER B",
            "USER C", "USER D",
            "USER E", "USER F"
    };

    public static String[] fileNames = {
            "T442", "T541",
            "T451", "T532",
            "T352", "T433",
            "T424", "T343",
            "TLIBERO", "T523",
            "TATTACK", "TDEFEND",
            "T442", "T442",
            "T442", "T442",
            "T442", "T442"
    };

    public static int[][] order = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, // 442
            {0, 1, 2, 3, 6, 4, 5, 7, 9, 8, 10}, // 541
            {0, 1, 2, 3, 4, 5, 6, 7, 9, 8, 10}, // 451
            {0, 1, 2, 3, 6, 4, 5, 7, 8, 9, 10}, // 532
            {0, 1, 2, 4, 5, 3, 6, 7, 8, 9, 10}, // 352
            {0, 1, 2, 3, 4, 5, 6, 8, 7, 9, 10}, // 433
            {0, 1, 2, 3, 4, 6, 7, 5, 9, 10, 8}, // 424
            {0, 1, 2, 4, 5, 3, 6, 8, 7, 9, 10}, // 343
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, // sweep
            {0, 1, 2, 3, 6, 4, 7, 9, 5, 10, 8}, // 523
            {0, 1, 2, 4, 3, 6, 5, 7, 9, 10, 8}, // attack
            {0, 5, 1, 2, 3, 4, 8, 6, 7, 9, 10}  // defend
    };

    public String name;
    public int[][][] target = new int[TEAM_SIZE][BALL_ZONES][2];
    public int[] pairs = new int[TEAM_SIZE];
    public int basedOn;

    public void loadFile(InputStream in) throws IOException {

        // copy to byte array
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[16384];

        int len;
        while ((len = in.read(buffer, 0, buffer.length)) != -1) {
            os.write(buffer, 0, len);
        }

        os.flush();

        byte[] bytes = os.toByteArray();

        // name
        int index = 0;
        name = "";
        boolean endOfString = false;
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < 9; c++) {
            int i = bytes[index++] & 0xFF;
            if (i == 0) {
                endOfString = true;
            }
            if (!endOfString) {
                sb.append((char) (i));
            }
        }
        name = sb.toString();

        // targets
        for (int player = 1; player < TEAM_SIZE; player++) {
            for (int ball_zone = 0; ball_zone < BALL_ZONES; ball_zone++) {
                int i = bytes[index++] & 0xFF;

                // read unsigned values
                int x = i >> 4; // 0 to 14
                int y = i & 15; // 0 to 15

                // convert to signed values
                x = x - 7; // -7 to +7
                y = 2 * y - 15; // -15 to +15

                // convert to pitch coordinates
                target[player][ball_zone][0] = x * TACT_DX;
                target[player][ball_zone][1] = y * TACT_DY;
            }
        }

        // pairs
        for (int i = 1; i < TEAM_SIZE; i++) {
            pairs[i] = bytes[index++] & 0xFF;
        }

        // base tactics
        basedOn = bytes[index++] & 0xFF;
    }

    public void saveFile(String filename) {
        FileHandle file = Assets.tacticsFolder.child(filename);
        OutputStream file_tactics = file.write(false);

        try {
            // name
            for (int i = 0; i < 9; i++) {
                if (i < name.length()) {
                    file_tactics.write(name.charAt(i));
                } else {
                    file_tactics.write(0);
                }
            }

            // targets
            for (int player = 1; player < TEAM_SIZE; player++) {
                for (int ball_zone = 0; ball_zone < BALL_ZONES; ball_zone++) {

                    // convert from coordinates
                    int x = target[player][ball_zone][0] / TACT_DX;
                    int y = target[player][ball_zone][1] / TACT_DY;

                    // convert to unsigned values
                    x = x + 7;
                    y = (y + 15) / 2;

                    file_tactics.write((x << 4) + y);
                }
            }

            // pairs
            for (int i = 1; i < TEAM_SIZE; i++) {
                file_tactics.write(pairs[i]);
            }

            // base tactics
            file_tactics.write(basedOn);

            file_tactics.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {

        String s = name + "\n";

        // target positions
        for (int ball_zone = 0; ball_zone < BALL_ZONES; ball_zone++) {
            s += String.format("%02d", ball_zone) + ":";
            for (int player = 1; player < TEAM_SIZE; player++) {
                // convert from coordinates
                int x = target[player][ball_zone][0] / TACT_DX;
                int y = target[player][ball_zone][1] / TACT_DY;

                // convert to unsigned values
                x = x + 7;
                y = (y + 15) / 2;

                s += " " + String.format("%02d", x);
                s += "-" + String.format("%02d", y);
            }
            s += "\n";
        }

        // editor pairs
        s += "Pairs:";
        for (int player = 1; player < TEAM_SIZE; player++) {
            int i = pairs[player];
            if (i == 255) {
                i = 0;
            } else {
                i = i + 1;
            }
            s += " " + String.format("%02d", i);
        }
        s += "\n";

        // base tactics
        s += "Based on: " + basedOn + "\n";

        return s;
    }

    public void copyFrom(Tactics t) {
        name = t.name;

        // targets
        for (int player = 1; player < TEAM_SIZE; player++) {
            for (int zone = 0; zone < BALL_ZONES; zone++) {
                target[player][zone][0] = t.target[player][zone][0];
                target[player][zone][1] = t.target[player][zone][1];
            }
        }

        // pairs
        for (int i = 1; i < TEAM_SIZE; i++) {
            pairs[i] = t.pairs[i];
        }

        // base tactics
        basedOn = t.basedOn;
    }

    public void addDeletePair(int p1, int p2) {

        // old pairs
        int oldPair1 = pairs[p1];
        int oldPair2 = pairs[p2];

        // delete pair
        if ((oldPair1 == oldPair2) && (oldPair1 != 255)) {
            pairs[p1] = 255;
            pairs[p2] = 255;
            return;
        }

        // delete pairs
        for (int i = 1; i < TEAM_SIZE; i++) {
            if (pairs[i] == oldPair1) {
                pairs[i] = 255;
            }
            if (pairs[i] == oldPair2) {
                pairs[i] = 255;
            }
        }

        // add pair
        int newPairValue = 0;
        boolean found;
        do {
            found = true;
            for (int i = 1; i < TEAM_SIZE; i++) {
                if (pairs[i] == newPairValue) {
                    found = false;
                }
            }
            if (!found) {
                newPairValue = newPairValue + 1;
            }
        } while (!found);

        pairs[p1] = newPairValue;
        pairs[p2] = newPairValue;
    }

    public boolean isPaired(int p) {
        return pairs[p] != 255;
    }

    public int getPaired(int p) {
        int pairValue = pairs[p];
        if (pairValue == 255) {
            throw new GdxRuntimeException("This player is not paired");
        }
        for (int i = 1; i < TEAM_SIZE; i++) {
            if ((i != p) && (pairs[i] == pairValue)) {
                return i;
            }
        }
        throw new GdxRuntimeException("Paired player not found");
    }
}
