package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.gl.Frame;
import com.ysoccer.android.framework.gl.SpriteBatcher;
import com.ysoccer.android.ysdemo.Assets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CrowdRenderer {

    class Position {
        int x;
        int y;
        int type;
        int rank;
    }

    int maxRank;
    ArrayList<Position> positions;

    public CrowdRenderer(InputStream in) {
        positions = new ArrayList<Position>();
        loadPositions(in);
    }

    void loadPositions(InputStream in) {

        DataInputStream is = new DataInputStream(in);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = "";

        try {

            // skip heading
            br.readLine();

            line = br.readLine();

            while (line != null) {

                if (line.length() > 0) {
                    Position position = new Position();
                    position.x = -Const.CENTER_X + Integer.parseInt(line.substring(0, 6).trim());
                    position.y = -Const.CENTER_Y + Integer.parseInt(line.substring(6, 12).trim());
                    position.type = line.charAt(12) - 97;
                    position.rank = Integer.parseInt(line.substring(18).trim());
                    positions.add(position);
                }

                line = br.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void setMaxRank(int l) {
        maxRank = Math.max(0, l);
    }

    void draw(SpriteBatcher batcher) {
        batcher.beginBatch(Assets.crowd);
        int len = positions.size();
        for (int i = 0; i < len; i++) {
            Position position = positions.get(i);
            Frame frame = Assets.crowdFrames[position.type];
            if (position.rank <= maxRank) {
                batcher.drawSprite(position.x, position.y, frame.width,
                        frame.height, frame);
            }
        }
        batcher.endBatch();
    }

}
