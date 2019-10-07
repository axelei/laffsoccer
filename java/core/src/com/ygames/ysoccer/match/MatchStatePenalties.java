package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.EMath;

import static com.badlogic.gdx.Input.Keys.F1;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTIES;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTY_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_PENALTY_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenalties extends MatchState {

    private boolean move;

    MatchStatePenalties(MatchFsm fsm) {
        super(STATE_PENALTIES, fsm);

        displayPenaltiesScore = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        // swap penalty kicking team
        match.penaltyKickingTeam = 1 - match.penaltyKickingTeam;
        match.team[match.penaltyKickingTeam].setSide(1);
        match.team[1 - match.penaltyKickingTeam].setSide(-1);

        // add another round
        if (match.penaltiesLeft(HOME) == 0 && match.penaltiesLeft(AWAY) == 0) {
            match.addPenalties(1);
        }

        match.nextPenalty();

        match.setPointOfInterest(0, match.penalty.side * PENALTY_SPOT_Y);

        setPlayersTargetPositions();
        match.penalty.kicker.setTarget(-40 * match.penalty.side, match.penalty.side * (PENALTY_SPOT_Y - 45));
        match.penalty.keeper.setTarget(0, match.penalty.side * (GOAL_LINE - 4));
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            move = match.updatePlayers(false);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            match.penalty.keeper.setState(STATE_KEEPER_PENALTY_POSITIONING);
            return newAction(NEW_FOREGROUND, STATE_PENALTY_KICK);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            return newAction(HOLD_FOREGROUND, STATE_PAUSE);
        }

        if (Gdx.input.isKeyPressed(F1)) {
            return newAction(HOLD_FOREGROUND, STATE_HELP);
        }

        return checkCommonConditions();
    }

    private void setPlayersTargetPositions() {

        for (int t = HOME; t <= AWAY; t++) {
            Team team = match.team[t];
            int len = team.lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = team.lineupAtPosition(i);
                if (!player.checkState(STATE_OUTSIDE)) {
                    int side = 2 * t - 1;
                    player.tx = 18 * (-team.lineup.size() + 2 * i) + 8 * EMath.cos(70 * (player.number));
                    player.ty = -(i == 0 ? 300 : 100) + side * (15 + 5 * (i % 2)) + 8 * EMath.sin(70 * (player.number));
                    player.setState(STATE_REACH_TARGET);
                }
            }
        }
    }
}
