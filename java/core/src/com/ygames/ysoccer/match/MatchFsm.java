package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

public class MatchFsm extends SceneFsm {

    protected boolean matchCompleted;

    final BenchStatus benchStatus;
    final Vector2 throwInPosition;
    Team throwInTeam;
    Team cornerKickTeam;
    Team goalKickTeam;

    enum Id {
        STATE_INTRO,
        STATE_STARTING_POSITIONS,
        STATE_KICK_OFF,
        STATE_MAIN,
        STATE_THROW_IN_STOP,
        STATE_THROW_IN,
        STATE_GOAL_KICK_STOP,
        STATE_GOAL_KICK,
        STATE_CORNER_STOP,
        STATE_CORNER_KICK,
        STATE_KEEPER_STOP,
        STATE_GOAL,
        STATE_HALF_TIME_STOP,
        STATE_HALF_TIME_POSITIONS,
        STATE_HALF_TIME_WAIT,
        STATE_HALF_TIME_ENTER,
        STATE_FULL_TIME_STOP,
        STATE_EXTRA_TIME_STOP,
        STATE_HALF_EXTRA_TIME_STOP,
        STATE_FULL_EXTRA_TIME_STOP,
        STATE_END_POSITIONS,
        STATE_END,
        STATE_REPLAY,
        STATE_PAUSE,
        STATE_HELP,
        STATE_HIGHLIGHTS,
        STATE_BENCH_ENTER,
        STATE_BENCH_EXIT,
        STATE_BENCH_SUBSTITUTIONS,
        STATE_BENCH_FORMATION,
        STATE_BENCH_TACTICS,
        STATE_FREE_KICK_STOP,
        STATE_FREE_KICK,
        STATE_PENALTY_KICK_STOP,
        STATE_PENALTY_KICK,
        STATE_PENALTY_KICK_END,
        STATE_PENALTIES_STOP,
        STATE_PENALTIES
    }

    MatchFsm(Match match) {
        super(match);

        setHotKeys(new MatchHotKeys(match));
        setSceneRenderer(new MatchRenderer(match.game.glGraphics, match));

        benchStatus = new BenchStatus();
        benchStatus.targetX = -TOUCH_LINE - 140 + getSceneRenderer().screenWidth / (2 * getSceneRenderer().zoom / 100f);
        benchStatus.targetY = -20;
        throwInPosition = new Vector2();

        new MatchStateIntro(this);
        new MatchStateStartingPositions(this);
        new MatchStateKickOff(this);
        new MatchStateMain(this);
        new MatchStateThrowInStop(this);
        new MatchStateThrowIn(this);
        new MatchStateGoalKickStop(this);
        new MatchStateGoalKick(this);
        new MatchStateCornerStop(this);
        new MatchStateCornerKick(this);
        new MatchStateKeeperStop(this);
        new MatchStateGoal(this);
        new MatchStateHalfTimeStop(this);
        new MatchStateHalfTimePositions(this);
        new MatchStateHalfTimeWait(this);
        new MatchStateHalfTimeEnter(this);
        new MatchStateFullTimeStop(this);
        new MatchStateExtraTimeStop(this);
        new MatchStateHalfExtraTimeStop(this);
        new MatchStateFullExtraTimeStop(this);
        new MatchStateEndPositions(this);
        new MatchStateEnd(this);
        new MatchStateReplay(this);
        new MatchStatePause(this);
        new MatchStateHelp(this);
        new MatchStateHighlights(this);
        new MatchStateBenchEnter(this);
        new MatchStateBenchExit(this);
        new MatchStateBenchSubstitutions(this);
        new MatchStateBenchFormation(this);
        new MatchStateBenchTactics(this);
        new MatchStateFreeKickStop(this);
        new MatchStateFreeKick(this);
        new MatchStatePenaltyKickStop(this);
        new MatchStatePenaltyKick(this);
        new MatchStatePenaltyKickEnd(this);
        new MatchStatePenaltiesStop(this);
        new MatchStatePenalties(this);
    }

    @Override
    public void start() {
        pushAction(NEW_FOREGROUND, Id.STATE_INTRO);
        pushAction(FADE_IN);
    }

    public MatchState getState() {
        return (MatchState) super.getState();
    }

    MatchState getHoldState() {
        return (MatchState) super.getHoldState();
    }

    public Match getMatch() {
        return (Match) getScene();
    }

    class BenchStatus {
        Team team;
        InputDevice inputDevice;
        float targetX;
        float targetY;
        float oldTargetX;
        float oldTargetY;
        int selectedPosition;
        int substPosition = -1;
        int swapPosition = -1;
        int selectedTactics;
    }
}
