package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_AREA_H;
import static com.ygames.ysoccer.match.Const.PENALTY_AREA_W;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchConsoleCommandExecutor extends ConsoleCommandExecutor {

    private Match match;

    public MatchConsoleCommandExecutor(Match match) {
        this.match = match;
    }

    public void homePenalty() {
        match.newTackle(match.team[AWAY].lineup.get(0), match.team[HOME].lineup.get(0), 0, 0);
        match.newFoul(0, match.team[AWAY].side * PENALTY_SPOT_Y);
    }

    public void awayPenalty() {
        match.newTackle(match.team[HOME].lineup.get(0), match.team[AWAY].lineup.get(0), 0, 0);
        match.newFoul(0, match.team[HOME].side * PENALTY_SPOT_Y);
    }

    public void homeFreeKick() {
        match.newTackle(match.team[AWAY].lineup.get(0), match.team[HOME].lineup.get(0), 0, 0);
        match.newFoul(Emath.rand(-PENALTY_AREA_W / 2, PENALTY_AREA_W / 2), match.team[AWAY].side * (GOAL_LINE - PENALTY_AREA_H - Emath.rand(1, 110)));
    }

    public void awayFreeKick() {
        match.newTackle(match.team[HOME].lineup.get(0), match.team[AWAY].lineup.get(0), 0, 0);
        match.newFoul(Emath.rand(-PENALTY_AREA_W / 2, PENALTY_AREA_W / 2), match.team[HOME].side * (GOAL_LINE - PENALTY_AREA_H - Emath.rand(1, 110)));
    }
}
