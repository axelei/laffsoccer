package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CELEBRATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFullTimeStop extends MatchState {

    MatchStateFullTimeStop(MatchFsm fsm) {
        super(fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        checkBenchCall = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.clock = match.length;
        getFsm().matchCompleted = true;

        Assets.Sounds.end.play(Assets.Sounds.volume / 100f);

        match.resetAutomaticInputDevices();
        match.setPlayersState(STATE_IDLE, null);

        Team winner = match.competition.getMatchWinner();
        if (winner != null) {
            for (int i = 1; i < TEAM_SIZE; i++) {
                if (Assets.random.nextFloat() < 0.7f) {
                    Player player = winner.lineup.get(i);
                    player.setState(STATE_CELEBRATION);
                }
            }
        }
    }

    @Override
    void onResume() {
        super.onResume();

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();

            match.updatePlayers(false);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (timer > 3 * SECOND) {
            if (match.competition.getFinalWinner() != null) {
                return newAction(NEW_FOREGROUND, STATE_FINAL_CELEBRATION);
            } else {
                return newAction(NEW_FOREGROUND, STATE_END_POSITIONS);
            }
        }

        return checkCommonConditions();
    }
}
