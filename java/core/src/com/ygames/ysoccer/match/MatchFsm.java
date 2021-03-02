package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

public class MatchFsm extends SceneFsm {

    boolean matchCompleted;

    final BenchStatus benchStatus;
    final Vector2 throwInPosition;
    Team throwInTeam;
    Team cornerKickTeam;
    Team goalKickTeam;

    static int STATE_BENCH_ENTER;
    static int STATE_BENCH_EXIT;
    static int STATE_BENCH_FORMATION;
    static int STATE_BENCH_SUBSTITUTIONS;
    static int STATE_BENCH_TACTICS;
    static int STATE_CORNER_KICK;
    static int STATE_CORNER_STOP;
    static int STATE_END;
    static int STATE_END_POSITIONS;
    static int STATE_EXTRA_TIME_STOP;
    static int STATE_FINAL_CELEBRATION;
    static int STATE_FREE_KICK;
    static int STATE_FREE_KICK_STOP;
    static int STATE_FULL_EXTRA_TIME_STOP;
    static int STATE_FULL_TIME_STOP;
    static int STATE_GOAL;
    static int STATE_GOAL_KICK;
    static int STATE_GOAL_KICK_STOP;
    static int STATE_HALF_EXTRA_TIME_STOP;
    static int STATE_HALF_TIME_ENTER;
    static int STATE_HALF_TIME_POSITIONS;
    static int STATE_HALF_TIME_STOP;
    static int STATE_HALF_TIME_WAIT;
    static int STATE_HELP;
    static int STATE_HIGHLIGHTS;
    private static int STATE_INTRO;
    static int STATE_KEEPER_STOP;
    static int STATE_KICK_OFF;
    static int STATE_MAIN;
    static int STATE_PAUSE;
    static int STATE_PENALTIES;
    static int STATE_PENALTIES_END;
    static int STATE_PENALTIES_KICK;
    static int STATE_PENALTIES_STOP;
    static int STATE_PENALTY_KICK;
    static int STATE_PENALTY_KICK_STOP;
    static int STATE_RED_CARD;
    static int STATE_REPLAY;
    static int STATE_STARTING_POSITIONS;
    static int STATE_THROW_IN;
    static int STATE_THROW_IN_STOP;
    static int STATE_YELLOW_CARD;

    MatchFsm(Match match) {
        super(match);

        setHotKeys(new MatchHotKeys(match));
        setSceneRenderer(new MatchRenderer(match.game.glGraphics, match));

        benchStatus = new BenchStatus();
        benchStatus.targetX = -TOUCH_LINE - 140 + getSceneRenderer().screenWidth / (2 * getSceneRenderer().zoom / 100f);
        benchStatus.targetY = -20;
        throwInPosition = new Vector2();

        STATE_BENCH_ENTER = addState(new MatchStateBenchEnter(this));
        STATE_BENCH_EXIT = addState(new MatchStateBenchExit(this));
        STATE_BENCH_FORMATION = addState(new MatchStateBenchFormation(this));
        STATE_BENCH_SUBSTITUTIONS = addState(new MatchStateBenchSubstitutions(this));
        STATE_BENCH_TACTICS = addState(new MatchStateBenchTactics(this));
        STATE_CORNER_KICK = addState(new MatchStateCornerKick(this));
        STATE_CORNER_STOP = addState(new MatchStateCornerStop(this));
        STATE_END = addState(new MatchStateEnd(this));
        STATE_END_POSITIONS = addState(new MatchStateEndPositions(this));
        STATE_EXTRA_TIME_STOP = addState(new MatchStateExtraTimeStop(this));
        STATE_FINAL_CELEBRATION = addState(new MatchStateFinalCelebration(this));
        STATE_FREE_KICK = addState(new MatchStateFreeKick(this));
        STATE_FREE_KICK_STOP = addState(new MatchStateFreeKickStop(this));
        STATE_FULL_EXTRA_TIME_STOP = addState(new MatchStateFullExtraTimeStop(this));
        STATE_FULL_TIME_STOP = addState(new MatchStateFullTimeStop(this));
        STATE_GOAL = addState(new MatchStateGoal(this));
        STATE_GOAL_KICK = addState(new MatchStateGoalKick(this));
        STATE_GOAL_KICK_STOP = addState(new MatchStateGoalKickStop(this));
        STATE_HALF_EXTRA_TIME_STOP = addState(new MatchStateHalfExtraTimeStop(this));
        STATE_HALF_TIME_ENTER = addState(new MatchStateHalfTimeEnter(this));
        STATE_HALF_TIME_POSITIONS = addState(new MatchStateHalfTimePositions(this));
        STATE_HALF_TIME_STOP = addState(new MatchStateHalfTimeStop(this));
        STATE_HALF_TIME_WAIT = addState(new MatchStateHalfTimeWait(this));
        STATE_HELP = addState(new MatchStateHelp(this));
        STATE_HIGHLIGHTS = addState(new MatchStateHighlights(this));
        STATE_INTRO = addState(new MatchStateIntro(this));
        STATE_KEEPER_STOP = addState(new MatchStateKeeperStop(this));
        STATE_KICK_OFF = addState(new MatchStateKickOff(this));
        STATE_MAIN = addState(new MatchStateMain(this));
        STATE_PAUSE = addState(new MatchStatePause(this));
        STATE_PENALTIES = addState(new MatchStatePenalties(this));
        STATE_PENALTIES_END = addState(new MatchStatePenaltiesEnd(this));
        STATE_PENALTIES_KICK = addState(new MatchStatePenaltiesKick(this));
        STATE_PENALTIES_STOP = addState(new MatchStatePenaltiesStop(this));
        STATE_PENALTY_KICK = addState(new MatchStatePenaltyKick(this));
        STATE_PENALTY_KICK_STOP = addState(new MatchStatePenaltyKickStop(this));
        STATE_RED_CARD = addState(new MatchStateRedCard(this));
        STATE_REPLAY = addState(new MatchStateReplay(this));
        STATE_STARTING_POSITIONS = addState(new MatchStateStartingPositions(this));
        STATE_THROW_IN = addState(new MatchStateThrowIn(this));
        STATE_THROW_IN_STOP = addState(new MatchStateThrowInStop(this));
        STATE_YELLOW_CARD = addState(new MatchStateYellowCard(this));
    }

    @Override
    public void start() {
        pushAction(NEW_FOREGROUND, STATE_INTRO);
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
        final Vector2 oldTarget = new Vector2();
        int selectedPosition;
        int substPosition = -1;
        int swapPosition = -1;
        int selectedTactics;
    }
}
