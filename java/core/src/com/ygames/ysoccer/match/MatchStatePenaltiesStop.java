package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTIES;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTIES_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenaltiesStop extends MatchState {

    MatchStatePenaltiesStop(MatchFsm fsm) {
        super(STATE_PENALTIES_STOP, fsm);

        displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.end.play(Assets.Sounds.volume / 100f);

        match.resetAutomaticInputDevices();
        match.setPlayersState(STATE_IDLE, null);
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

            sceneRenderer.actionCamera.update(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE) {
            match.ball.setPosition(0, -Const.PENALTY_SPOT_Y, 0);
            match.ball.updatePrediction();

            sceneRenderer.actionCamera.setOffset(0, 0);

            match.penaltyKickingTeam = Assets.random.nextInt(2);

            match.period = Match.Period.PENALTIES;

            match.addPenalties(5);

            fsm.pushAction(NEW_FOREGROUND, STATE_PENALTIES);
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
}
