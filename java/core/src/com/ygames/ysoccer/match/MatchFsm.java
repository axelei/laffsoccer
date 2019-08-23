package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.InputDevice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.match.Const.TOUCH_LINE;

public class MatchFsm {

    private class Action {
        ActionType type;
        Id stateId;
        int timer;

        Action(ActionType type, Id stateId) {
            this.type = type;
            this.stateId = stateId;
        }

        void update() {
            if (timer > 0) {
                timer -= 1;
            }
        }
    }

    enum ActionType {
        NONE,
        NEW_FOREGROUND,
        FADE_IN,
        FADE_OUT,
        HOLD_FOREGROUND,
        RESTORE_FOREGROUND
    }

    private Match match;
    protected boolean matchCompleted;
    private MatchRenderer matchRenderer;
    private int savedSubframe;

    private List<MatchState> states;
    private MatchState currentState;
    private MatchState holdState;
    BenchStatus benchStatus;
    Team throwInTeam;
    Team cornerKickTeam;
    Team goalKickTeam;

    private ArrayDeque<Action> actions;
    private Action currentAction;

    MatchKeys matchKeys;

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
        STATE_PENALTY_KICK
    }

    MatchFsm(Match match) {
        this.match = match;
        this.matchKeys = new MatchKeys(match);
        matchRenderer = new MatchRenderer(match.game.glGraphics, match);
        states = new ArrayList<>();
        benchStatus = new BenchStatus();
        benchStatus.targetX = -TOUCH_LINE - 140 + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        benchStatus.targetY = -20;

        actions = new ArrayDeque<>();

        states.add(new MatchStateIntro(this));
        states.add(new MatchStateStartingPositions(this));
        states.add(new MatchStateKickOff(this));
        states.add(new MatchStateMain(this));
        states.add(new MatchStateThrowInStop(this));
        states.add(new MatchStateThrowIn(this));
        states.add(new MatchStateGoalKickStop(this));
        states.add(new MatchStateGoalKick(this));
        states.add(new MatchStateCornerStop(this));
        states.add(new MatchStateCornerKick(this));
        states.add(new MatchStateKeeperStop(this));
        states.add(new MatchStateGoal(this));
        states.add(new MatchStateHalfTimeStop(this));
        states.add(new MatchStateHalfTimePositions(this));
        states.add(new MatchStateHalfTimeWait(this));
        states.add(new MatchStateHalfTimeEnter(this));
        states.add(new MatchStateFullTimeStop(this));
        states.add(new MatchStateExtraTimeStop(this));
        states.add(new MatchStateHalfExtraTimeStop(this));
        states.add(new MatchStateFullExtraTimeStop(this));
        states.add(new MatchStateEndPositions(this));
        states.add(new MatchStateEnd(this));
        states.add(new MatchStateReplay(this));
        states.add(new MatchStatePause(this));
        states.add(new MatchStateHighlights(this));
        states.add(new MatchStateBenchEnter(this));
        states.add(new MatchStateBenchExit(this));
        states.add(new MatchStateBenchSubstitutions(this));
        states.add(new MatchStateBenchFormation(this));
        states.add(new MatchStateBenchTactics(this));
        states.add(new MatchStateFreeKickStop(this));
        states.add(new MatchStateFreeKick(this));
        states.add(new MatchStatePenaltyKickStop(this));
        states.add(new MatchStatePenaltyKick(this));
    }

    public MatchState getState() {
        return currentState;
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
        if (currentAction != null && currentAction.type == ActionType.FADE_OUT) {
            match.game.glGraphics.light = 8 * (currentAction.timer - 1);
            doUpdate = false;
        }
        if (currentAction != null && currentAction.type == ActionType.FADE_IN) {
            match.game.glGraphics.light = 255 - 8 * (currentAction.timer - 1);
            doUpdate = false;
        }

        // update current state
        if (currentState != null && doUpdate) {
            if (currentAction != null
                    && (currentAction.type == ActionType.NEW_FOREGROUND || currentAction.type == ActionType.RESTORE_FOREGROUND)) {
                currentState.onResume();
            }
            if (currentAction != null
                    && (currentAction.type == ActionType.HOLD_FOREGROUND)) {
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
                savedSubframe = match.subframe;
                currentState = searchState(currentAction.stateId);
                Gdx.app.debug("HOLD_FOREGROUND", currentState.getClass().getSimpleName());
                break;

            case RESTORE_FOREGROUND:
                match.subframe = savedSubframe;
                currentState = holdState;
                holdState = null;
                break;

        }
    }

    private MatchState searchState(Id id) {
        for (int i = 0; i < states.size(); i++) {
            MatchState s = states.get(i);
            if (s.checkId(id)) {
                s.entryActions();
                return s;
            }
        }
        return null;
    }

    public void pushAction(ActionType type) {
        pushAction(type, null);
    }

    public void pushAction(ActionType type, Id stateId) {
        actions.offer(new Action(type, stateId));
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
