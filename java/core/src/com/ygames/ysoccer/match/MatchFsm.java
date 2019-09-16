package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.InputDevice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;

public class MatchFsm extends SceneFsm {

    private Match match;
    protected boolean matchCompleted;
    private MatchRenderer matchRenderer;

    private List<MatchState> states;
    private MatchState currentState;
    private MatchState holdState;
    BenchStatus benchStatus;
    Vector2 throwInPosition;
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
        super(match.game);
        this.match = match;
        this.hotKeys = new MatchHotKeys(match);
        matchRenderer = new MatchRenderer(match.game.glGraphics, match);
        states = new ArrayList<>();
        benchStatus = new BenchStatus();
        benchStatus.targetX = -TOUCH_LINE - 140 + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        benchStatus.targetY = -20;
        throwInPosition = new Vector2();

        actions = new ArrayDeque<>();

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

    public MatchState getState() {
        return currentState;
    }

    MatchState getHoldState() {
        return holdState;
    }

    void addState(MatchState state) {
        states.add(state);
    }

    public Match getMatch() {
        return match;
    }

    public MatchRenderer getMatchRenderer() {
        return matchRenderer;
    }

    void think(float deltaTime) {

        boolean doUpdate = true;

        // fade in/out
        if (currentAction != null && currentAction.type == FADE_OUT) {
            game.glGraphics.light = 8 * (currentAction.timer - 1);
            doUpdate = false;
        }
        if (currentAction != null && currentAction.type == FADE_IN) {
            game.glGraphics.light = 255 - 8 * (currentAction.timer - 1);
            doUpdate = false;
        }

        // update current state
        if (currentState != null && doUpdate) {
            if (currentAction != null
                    && (currentAction.type == NEW_FOREGROUND || currentAction.type == RESTORE_FOREGROUND)) {
                currentState.onResume();
            }
            if (currentAction != null
                    && (currentAction.type == HOLD_FOREGROUND)) {
                holdState.onPause();
            }
            currentState.doActions(deltaTime);
            currentState.checkConditions();
        }

        // update current action
        if (currentAction != null) {
            currentAction.update();
            if (currentAction.timer == 0) {
                currentAction = null;
            }
        }

        // get new action
        if (currentAction == null) {
            if (actions.size() > 0) {
                pollAction();
            }
        }
    }

    private void pollAction() {

        currentAction = actions.pop();

        switch (currentAction.type) {

            case NONE:
                break;

            case FADE_IN:
                currentAction.timer = 32;
                break;

            case FADE_OUT:
                currentAction.timer = 32;
                break;

            case NEW_FOREGROUND:
                currentState = searchState(currentAction.stateId);
                Gdx.app.debug("NEW_FOREGROUND", currentState.getClass().getSimpleName());
                break;

            case HOLD_FOREGROUND:
                holdState = currentState;
                currentState = searchState(currentAction.stateId);
                Gdx.app.debug("HOLD_FOREGROUND", currentState.getClass().getSimpleName());
                break;

            case RESTORE_FOREGROUND:
                currentState = holdState;
                holdState = null;
                break;

        }
    }

    private MatchState searchState(Enum id) {
        for (int i = 0; i < states.size(); i++) {
            MatchState s = states.get(i);
            if (s.checkId(id)) {
                s.entryActions();
                return s;
            }
        }
        return null;
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
