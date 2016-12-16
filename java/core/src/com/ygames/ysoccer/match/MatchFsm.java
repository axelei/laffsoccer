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
        int stateId;
        int timer;

        Action(ActionType type, int state) {
            this.type = type;
            this.stateId = state;
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

    private ArrayDeque<Action> actions;
    private Action currentAction;

    static final int STATE_INTRO = 1;
    static final int STATE_STARTING_POSITIONS = 2;
    static final int STATE_KICK_OFF = 3;
    static final int STATE_MAIN = 4;
    static final int STATE_THROW_IN_STOP = 5;
    static final int STATE_THROW_IN = 6;
    static final int STATE_GOAL_KICK_STOP = 7;
    static final int STATE_GOAL_KICK = 8;
    static final int STATE_CORNER_STOP = 9;
    static final int STATE_CORNER_KICK = 10;
    static final int STATE_KEEPER_STOP = 11;
    static final int STATE_GOAL = 12;
    static final int STATE_HALF_TIME_STOP = 13;
    static final int STATE_HALF_TIME_POSITIONS = 14;
    static final int STATE_HALF_TIME_WAIT = 15;
    static final int STATE_HALF_TIME_ENTER = 16;
    static final int STATE_FULL_TIME_STOP = 17;
    static final int STATE_EXTRA_TIME_STOP = 18;
    static final int STATE_HALF_EXTRA_TIME_STOP = 19;
    static final int STATE_FULL_EXTRA_TIME_STOP = 20;
    static final int STATE_END_POSITIONS = 21;
    static final int STATE_END = 22;
    static final int STATE_REPLAY = 23;
    static final int STATE_PAUSE = 24;
    static final int STATE_HIGHLIGHTS = 25;
    static final int STATE_BENCH_ENTER = 26;
    static final int STATE_BENCH_EXIT = 27;

    MatchFsm(Match match) {
        this.match = match;
        matchRenderer = new MatchRenderer(match.game.glGraphics, match);
        states = new ArrayList<MatchState>();
        benchStatus = new BenchStatus();
        benchStatus.targetX = -TOUCH_LINE - 140 + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        benchStatus.targetY = -20;

        actions = new ArrayDeque<Action>();

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

    private MatchState searchState(int id) {
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
        pushAction(type, 0);
    }

    public void pushAction(ActionType type, int state) {
        actions.offer(new Action(type, state));
    }

    public class BenchStatus {
        public Team team;
        public InputDevice inputDevice;
        public float targetX;
        public float targetY;
        public float oldTargetX;
        public float oldTargetY;
        public int selectedPos;
        public int forSubs;
    }
}
