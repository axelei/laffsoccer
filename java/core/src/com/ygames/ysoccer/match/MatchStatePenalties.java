package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTIES;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTY_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenalties extends MatchState {

    private boolean move;
    private Player penaltyKeeper;

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

        penaltyKeeper = match.team[1 - match.penaltyKickingTeam].lineupAtPosition(0);

        match.setPointOfInterest(0, match.penalty.side * PENALTY_SPOT_Y);

        setPlayersTargetPositions();
        match.penalty.kicker.setTarget(-40 * match.ball.ySide, match.penalty.side * (PENALTY_SPOT_Y - 45));
        penaltyKeeper.setTarget(0, penaltyKeeper.team.side * (GOAL_LINE - 8));
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
    void checkConditions() {
        if (!move) {
            penaltyKeeper.setState(STATE_KEEPER_POSITIONING);
            fsm.pushAction(NEW_FOREGROUND, STATE_PENALTY_KICK);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(HOLD_FOREGROUND, STATE_PAUSE);
            return;
        }
    }

    private void setPlayersTargetPositions() {

        for (int t = HOME; t <= AWAY; t++) {
            Team team = match.team[t];
            int len = team.lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = team.lineupAtPosition(i);
                if (!player.checkState(STATE_OUTSIDE)) {
                    player.tx = 18 * (-team.lineup.size() + 2 * i) + 8 * Emath.cos(70 * (player.number));
                    player.ty = -100 + team.side * (15 + 5 * (i % 2)) + 8 * Emath.sin(70 * (player.number));
                    player.setState(STATE_REACH_TARGET);
                }
            }
        }
    }
}
