package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGraphics;

public class MatchRenderer {

    GlGraphics glGraphics;
    int screenWidth;
    int screenHeight;
    int zoom;
    public ActionCamera actionCamera;
    public int[] vcameraX = new int[Const.REPLAY_SUBFRAMES];
    public int[] vcameraY = new int[Const.REPLAY_SUBFRAMES];

    public MatchCore match;

    public MatchRenderer(GlGraphics glGraphics, MatchCore match) {
        this.glGraphics = glGraphics;

        actionCamera = new ActionCamera(this);
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        this.match = match;
    }
}
