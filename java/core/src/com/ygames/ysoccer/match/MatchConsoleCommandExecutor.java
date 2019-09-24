package com.ygames.ysoccer.match;

import com.strongjoshua.console.annotation.HiddenCommand;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_AREA_H;
import static com.ygames.ysoccer.match.Const.PENALTY_AREA_W;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchConsoleCommandExecutor extends ConsoleCommandExecutor {

    private Match match;

    public MatchConsoleCommandExecutor(Match match) {
        this.match = match;
    }

    @HiddenCommand
    public void ballPosition() {
        console.log("(" + match.ball.x + ", " + match.ball.y + ", " + match.ball.z + ")");
    }

    public void ballPosition(float x, float y, float z) {
        match.ball.setPosition(x, y, z);
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
        newFoul(match.team[AWAY].side);
    }

    public void awayFreeKick() {
        match.newTackle(match.team[HOME].lineup.get(0), match.team[AWAY].lineup.get(0), 0, 0);
        newFoul(match.team[HOME].side);
    }

    private void newFoul(int side) {
        float a, d, x, y;
        do {
            a = Emath.rand(-90, 90) - 90 * side;
            d = Emath.rand(PENALTY_AREA_H, (int) Emath.hypo(PENALTY_AREA_H + 110, PENALTY_AREA_W / 2 + POST_X + 110));
            x = d * Emath.cos(a);
            y = side * GOAL_LINE + d * Emath.sin(a);
        } while (Const.isInsidePenaltyArea(x, y, side) || !Const.isInsideDirectShotArea(x, y, side));
        match.newFoul(x, y);
    }
}
