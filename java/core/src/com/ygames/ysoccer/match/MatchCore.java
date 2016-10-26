package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGame;

import java.util.ArrayList;
import java.util.List;

public class MatchCore {

    private GlGame game;

    private MatchFsm fsm;

    final Ball ball;
    public final Team team[];
    public int benchSide; // 1 = home upside, -1 = home downside

    public MatchRenderer renderer;
    public MatchSettings settings;

    final List<Goal> goals;

    public int subframe;

    public MatchCore(GlGame game, Team[] team, MatchSettings matchSettings) {
        this.game = game;
        this.team = team;
        this.settings = matchSettings;

        fsm = new MatchFsm(this);

        ball = new Ball(this);

        goals = new ArrayList<Goal>();
    }

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    public void start() {
        fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_INTRO);
        fsm.pushAction(MatchFsm.ActionType.FADE_IN);
    }
}
