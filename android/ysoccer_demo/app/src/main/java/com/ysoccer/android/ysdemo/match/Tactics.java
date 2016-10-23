package com.ysoccer.android.ysdemo.match;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tactics {

    public static final String[] fileNames = {"T442", "T541", "T451", "T532",
            "T352", "T433", "T424", "T343", "TLIBERO", "T523", "TATTACK",
            "TDEFEND", "T442", "T442", "T442", "T442", "T442", "T442"};

    String name;
    int[][][] target = new int[Const.TEAM_SIZE][Const.BALL_ZONES][2];
    int[] pairs = new int[Const.TEAM_SIZE];
    int based_on;

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
        boolean end_of_name = false;
        for (int c = 0; c < 9; c++) {
            int i = bytes[index++] & 0xFF;
            if (i == 0) {
                end_of_name = true;
            }
            if (!end_of_name) {
                name += (char) (i);
            }
        }

        // targets
        for (int player = 1; player < Const.TEAM_SIZE; player++) {
            for (int ball_zone = 0; ball_zone < Const.BALL_ZONES; ball_zone++) {
                int i = bytes[index++] & 0xFF;

                // read unsigned values
                int x = i >> 4; // 0 to 14
                int y = i & 15; // 0 to 15

                // convert to signed values
                x = x - 7; // -7 to +7
                y = 2 * y - 15; // -15 to +15

                // convert to pitch coordinates
                target[player][ball_zone][0] = x * Const.TACT_DX;
                target[player][ball_zone][1] = y * Const.TACT_DY;
            }
        }

        // pairs
        for (int i = 1; i < Const.TEAM_SIZE; i++) {
            pairs[i] = bytes[index++] & 0xFF;
        }

        // base tactics
        based_on = bytes[index++] & 0xFF;

    }

    @Override
    public String toString() {

        String s = name + "\n";

        // target positions
        for (int ball_zone = 0; ball_zone < Const.BALL_ZONES; ball_zone++) {
            s += String.format("%02d", ball_zone) + ":";
            for (int player = 1; player < Const.TEAM_SIZE; player++) {
                // convert from coordinates
                int x = target[player][ball_zone][0] / Const.TACT_DX;
                int y = target[player][ball_zone][1] / Const.TACT_DY;

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
        for (int player = 1; player < Const.TEAM_SIZE; player++) {
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
        s += "Based on: " + based_on + "\n";

        return s;

    }
}
