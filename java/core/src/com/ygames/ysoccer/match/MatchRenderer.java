package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGraphics;

public class MatchRenderer {

    GlGraphics glGraphics;

    public MatchCore match;

    public MatchRenderer(GlGraphics glGraphics, MatchCore match) {
        this.glGraphics = glGraphics;
        this.match = match;
    }
}
