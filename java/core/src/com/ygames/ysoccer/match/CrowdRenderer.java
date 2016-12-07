package com.ygames.ysoccer.match;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.Assets;

public class CrowdRenderer {

    private static class Position {
        int x;
        int y;
        int type;
        int rank;
    }

    private int maxRank;
    private Position[] positions;

    public CrowdRenderer(FileHandle fileHandle) {
        positions = Assets.json.fromJson(Position[].class, fileHandle);
        for (Position position : positions) {
            position.x -= Const.CENTER_X;
            position.y -= Const.CENTER_Y;
        }
    }

    void setMaxRank(int l) {
        maxRank = Math.max(0, l);
    }

    void draw(SpriteBatch batch) {
        for (Position position : positions) {
            if (position.rank <= maxRank) {
                batch.draw(Assets.crowd[position.type], position.x, position.y);
            }
        }
    }
}
