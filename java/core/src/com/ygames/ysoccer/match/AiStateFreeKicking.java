package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_FREE_KICKING;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FREE_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FREE_KICK_SPEED;

class AiStateFreeKicking extends AiState {

    private float targetDistance;
    private float targetAngle;
    private int preparingTime;
    private float kickDuration;
    private float kickSpin;

    enum Step {
        TURNING,
        PREPARING,
        KICKING
    }

    private Step step;

    enum KickType {
        PASS,
        MEDIUM,
        LONG
    }

    private KickType kickType;

    AiStateFreeKicking(AiFsm fsm) {
        super(STATE_FREE_KICKING, fsm);
    }

    @Override
    void entryActions() {
        step = Step.TURNING;
        boolean playerHasShootingConfidence = (Assets.random.nextFloat() < (0.13f * player.skills.shooting));

        // CASE A: CLEAN AREA
        if (player.getMatch().foul.isNearOwnGoal()) {
            targetDistance = 500;
            targetAngle = -90 * player.team.side;
            Gdx.app.debug(player.shirtName, "Cleaning area");
        }

        // CASE B: DIRECT SHOT
        else if (player.getMatch().foul.isDirectShot() && playerHasShootingConfidence) {
            // goal target distance range: 174 (PENALTY_AREA_H) .. 450
            setGoalTarget();
            Gdx.app.debug(player.shirtName, "Kicking to goal");
        } else {
            Player nearPlayer = player.searchNearPlayer();
            float nearPlayerDistance = nearPlayer == null ? 0 : player.distanceFrom(nearPlayer);
            Gdx.app.debug(player.shirtName, "Near player: " + (nearPlayer == null ? " none" : " at distance: " + player.distanceFrom(nearPlayer)));

            // CASE C: PASS TO MATE
            if (nearPlayer != null && nearPlayerDistance < 350) {
                targetDistance = 0.7f * nearPlayerDistance;
                targetAngle = EMath.angle(player.x, player.y, nearPlayer.x, nearPlayer.y);
                Gdx.app.debug(player.shirtName, "Passing to mate");
            }

            // CASE D: LAUNCH FAR AWAY (should rarely happen)
            else {
                targetDistance = 500;
                targetAngle = -90 * player.team.side;
                Gdx.app.debug(player.shirtName, "Launching to the opponent side");
            }
        }

        if (targetDistance <= 150) {
            kickType = KickType.PASS;

            // targetDistance:  0 .. 250
            // kickDuration:    3 + 0 .. 1 = 3 .. 4
            // timer:           77 .. ?
            kickDuration = 3 + targetDistance / 150;
            preparingTime = 20;
        } else if (targetDistance < 300) {
            kickType = KickType.MEDIUM;

            // targetDistance:  150 .. 300
            // kickDuration:    8 .. 11 + 2 .. 5 = 10 .. 17
            // timer:           77 .. 136
            kickDuration = EMath.rand(8, 11) + targetDistance / 60;
            preparingTime = 30;
        } else {
            kickType = KickType.LONG;

            // targetDistance:  300 .. 450
            // kickDuration:    11 .. 13 + 2 .. 3 = 13 .. 16
            // timer:           ? .. ?
            kickDuration = EMath.rand(11, 13) + targetDistance / 120;
            preparingTime = 40;
        }
        Gdx.app.debug(player.shirtName,
                "Kicking type: " + kickType +
                        ", targetDistance: " + targetDistance +
                        ", kickDuration: " + kickDuration);
    }

    @Override
    void doActions() {
        super.doActions();

        switch (step) {
            case TURNING:
                if (timer > 10) {
                    ai.x0 = Math.round(EMath.cos(targetAngle));
                    ai.y0 = Math.round(EMath.sin(targetAngle));
                    step = Step.PREPARING;
                    timer = 0;
                }
                break;

            case PREPARING:
                if (timer > preparingTime) {
                    float signedAngleDiff = EMath.signedAngleDiff(targetAngle, player.a);
                    if (Math.abs(signedAngleDiff) > 3) {
                        kickSpin = Math.signum(signedAngleDiff);
                    } else {
                        kickSpin = 0;
                    }
                    Gdx.app.debug(player.shirtName, "angle: " + player.a + ", targetAngle: " + targetAngle + " signedAngleDiff: " + signedAngleDiff + " kickSpin: " + kickSpin);
                    step = Step.KICKING;
                    timer = 0;
                }
                break;

            case KICKING:
                switch (kickType) {
                    case PASS:
                        ai.x0 = Math.round(EMath.cos(player.a));
                        ai.y0 = Math.round(EMath.sin(player.a));
                        if (kickSpin != 0 && player.getState().checkId(STATE_FREE_KICK_SPEED)) {
                            ai.x0 = Math.round(EMath.cos(player.a + kickSpin * 45));
                            ai.y0 = Math.round(EMath.sin(player.a + kickSpin * 45));
                        }
                        break;

                    case MEDIUM:
                        ai.x0 = 0;
                        ai.y0 = 0;
                        if (kickSpin != 0 && player.getState().checkId(STATE_FREE_KICK_SPEED)) {
                            ai.x0 = Math.round(EMath.cos(player.a + kickSpin * 90));
                            ai.y0 = Math.round(EMath.sin(player.a + kickSpin * 90));
                        }
                        break;

                    case LONG:
                        ai.x0 = Math.round(EMath.cos(player.a + 180));
                        ai.y0 = Math.round(EMath.sin(player.a + 180));
                        if (kickSpin != 0 && player.getState().checkId(STATE_FREE_KICK_SPEED)) {
                            ai.x0 = Math.round(EMath.cos(player.a + 180 - kickSpin * 45));
                            ai.y0 = Math.round(EMath.sin(player.a + 180 - kickSpin * 45));
                        }
                }
                ai.fire10 = timer < kickDuration;
                break;
        }
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.getState();
        if (playerState.checkId(STATE_FREE_KICK_ANGLE)) {
            return null;
        }
        if (playerState.checkId(STATE_FREE_KICK_SPEED)) {
            return null;
        }
        return fsm.stateIdle;
    }

    private void setGoalTarget() {
        final float TARGET_X = POST_X - 3 * BALL_R;
        float nearPostAngle = EMath.angle(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
        float farPostAngle = EMath.angle(player.ball.x, player.ball.y, -player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
        float nearPostCorrection = Math.abs((nearPostAngle - EMath.roundBy(nearPostAngle, 45f)));
        float farPostCorrection = Math.abs((farPostAngle - EMath.roundBy(farPostAngle, 45f)));
        Gdx.app.debug(player.shirtName,
                "nearPostAngle: " + nearPostAngle +
                        ", farPostAngle: " + farPostAngle +
                        ", nearPostCorrection: " + nearPostCorrection +
                        ", farPostCorrection: " + farPostCorrection);

        // CASE A: LAUNCH TO PENALTY AREA
        if (nearPostCorrection > 12 && farPostCorrection > 12) {
            targetDistance = EMath.dist(player.ball.x, player.ball.y, 0, -player.team.side * PENALTY_SPOT_Y);
            targetAngle = EMath.angle(player.ball.x, player.ball.y, 0, -player.team.side * PENALTY_SPOT_Y);
            Gdx.app.debug(player.shirtName, "Penalty spot post distance: " + targetDistance + ", angle: " + targetAngle);
        }

        // CASE B: TARGET NEAR POST
        else if (nearPostCorrection < farPostCorrection) {
            targetDistance = EMath.dist(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            targetAngle = EMath.angle(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            Gdx.app.debug(player.shirtName, "Near post distance: " + targetDistance + ", angle: " + targetAngle);
        }

        // CASE C: TARGET FAR POST
        else {
            targetDistance = EMath.dist(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            targetAngle = EMath.angle(player.ball.x, player.ball.y, -player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            Gdx.app.debug(player.shirtName, "Far post distance: " + targetDistance + ", angle: " + targetAngle);
        }
    }
}
