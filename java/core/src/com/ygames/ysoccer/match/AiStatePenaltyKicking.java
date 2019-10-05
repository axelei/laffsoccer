package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_PENALTY_KICKING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_SPEED;
import static com.ygames.ysoccer.framework.EMath.randomPick;

class AiStatePenaltyKicking extends AiState {

    private float targetAngle;
    private float controlsAngle;
    private float kickDuration;

    enum Step {
        TURNING,
        KICKING
    }

    private Step step;

    enum KickType {
        LOW,
        MEDIUM,
        HIGH
    }

    private KickType kickType;

    enum KickAngle {
        LEFT,
        CENTER,
        RIGHT
    }

    private KickAngle kickAngle;

    AiStatePenaltyKicking(AiFsm fsm) {
        super(STATE_PENALTY_KICKING, fsm);
    }

    @Override
    void entryActions() {
        step = Step.TURNING;

        kickType = randomPick(KickType.class);
        KickAngle[] kickAngles = {KickAngle.LEFT, KickAngle.RIGHT};
        switch (kickType) {
            case LOW:
                kickDuration = EMath.rand(100, 200) / 640f * GLGame.VIRTUAL_REFRESH_RATE;
                kickAngle = randomPick(kickAngles);
                break;

            case MEDIUM:
                kickDuration = EMath.rand(50, 200) / 640f * GLGame.VIRTUAL_REFRESH_RATE;
                kickAngle = randomPick(kickAngles);
                break;

            case HIGH:
                kickDuration = EMath.rand(10, 200) / 640f * GLGame.VIRTUAL_REFRESH_RATE;
                kickAngle = randomPick(kickAngles);
                break;
        }

        switch (kickAngle) {
            case LEFT:
                controlsAngle = 90 * player.ball.ySide - 45;
                targetAngle = 90 * player.ball.ySide - EMath.rand(10, 30);
                break;

            case CENTER:
                controlsAngle = 90 * player.ball.ySide;
                targetAngle = 90 * player.ball.ySide;
                break;

            case RIGHT:
                controlsAngle = 90 * player.ball.ySide + 45;
                targetAngle = 90 * player.ball.ySide + EMath.rand(10, 30);
                break;
        }
        Gdx.app.debug(player.shirtName,
                "Kick type: " + kickType +
                        ", kick angle: " + kickAngle +
                        ", targetDuration: " + kickDuration +
                        ", kickDuration: " + kickDuration);
    }

    @Override
    void doActions() {
        super.doActions();

        switch (step) {
            case TURNING:
                if (timer > 10) {
                    ai.x0 = Math.round(EMath.cos(controlsAngle));
                    ai.y0 = Math.round(EMath.sin(controlsAngle));
                    Gdx.app.debug(
                            player.shirtName,
                            "angle: " + player.a +
                                    ", controlsAngle: " + (controlsAngle + 360) % 360 +
                                    ", targetAngle: " + (targetAngle + 360) % 360
                    );
                    if (EMath.angleDiff(player.a, targetAngle) < 3) {
                        step = Step.KICKING;
                        timer = 0;
                    }
                }
                break;

            case KICKING:
                switch (kickType) {
                    case LOW:
                        ai.x0 = Math.round(EMath.cos(controlsAngle));
                        ai.y0 = Math.round(EMath.sin(controlsAngle));
                        break;

                    case MEDIUM:
                        ai.x0 = 0;
                        ai.y0 = 0;
                        break;

                    case HIGH:
                        ai.x0 = Math.round(EMath.cos(controlsAngle + 180));
                        ai.y0 = Math.round(EMath.sin(controlsAngle + 180));
                }
                ai.fire10 = timer < kickDuration;
                break;
        }
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.getState();
        if (playerState.checkId(STATE_PENALTY_KICK_ANGLE)) {
            return null;
        }
        if (playerState.checkId(STATE_PENALTY_KICK_SPEED)) {
            return null;
        }
        return fsm.stateIdle;
    }
}
