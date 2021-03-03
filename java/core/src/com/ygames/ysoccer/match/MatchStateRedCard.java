package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FREE_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PENALTY_KICK_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_RED_CARD;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SENT_OFF;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateRedCard extends MatchState {

    boolean booked;

    MatchStateRedCard(MatchFsm matchFsm) {
        super(matchFsm);

        displayBallOwner = true;
        displayFoulMaker = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        booked = false;

        match.resetAutomaticInputDevices();
    }

    @Override
    void onResume() {
        match.setPointOfInterest(match.foul.position);

        sceneRenderer.actionCamera
                .setMode(REACH_TARGET)
                .setTarget(match.foul.player.x, match.foul.player.y)
                .setSpeed(NORMAL)
                .setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        setPlayerStates();

        if (!booked && timer > SECOND) {
            match.foul.player.setState(STATE_RED_CARD);
            booked = true;
        }

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();
            match.ball.collisionGoal();
            match.ball.collisionJumpers();
            match.ball.collisionNetOut();
            match.ball.collisionNet();

            match.updatePlayers(true);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (booked && match.foul.player.checkState(STATE_IDLE)) {
            match.foul.player.setState(STATE_SENT_OFF);
            if (match.foul.isPenalty()) {
                return newAction(NEW_FOREGROUND, STATE_PENALTY_KICK_STOP);
            } else {
                return newAction(NEW_FOREGROUND, STATE_FREE_KICK_STOP);
            }
        }
        return checkCommonConditions();
    }

    private void setPlayerStates() {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = match.team[t].lineup.get(i);
                PlayerState playerState = player.getState();
                if (playerState.checkId(STATE_STAND_RUN) || playerState.checkId(STATE_KEEPER_POSITIONING)) {
                    player.tx = player.x;
                    player.ty = player.y;
                    player.setState(STATE_REACH_TARGET);
                }
            }
        }
    }
}
