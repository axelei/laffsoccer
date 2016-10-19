package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGame;

public class MatchCore {

    GlGame game;

    public final Team team[];

    public MatchSettings settings;

    public MatchCore(GlGame game, Team[] team, MatchSettings matchSettings) {
        this.game = game;
        this.team = team;
        this.settings = matchSettings;
    }
}
