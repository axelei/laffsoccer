package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

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
    private float kickDuration;
    private float kickSpin;

    enum Step {
        TURNING,
        WAITING,
        KICKING
    }

    private Step step;

    enum KickType {
        SHORT,
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

        // CASE A: CLEAN AREA
        if (player.match.foul.isNearOwnGoal()) {
            targetDistance = 500;
            targetAngle = -90 * player.team.side;
            Gdx.app.debug(player.shirtName, "Cleaning area");
        }


        // CASE B: DIRECT SHOT
        else if (player.match.foul.isDirectShot()) {
            setGoalTarget();
            Gdx.app.debug(player.shirtName, "Kicking to goal");
        } else {
            Player nearPlayer = player.searchNearPlayer();
            float nearPlayerDistance = nearPlayer == null ? 0 : player.distanceFrom(nearPlayer);
            Gdx.app.debug(player.shirtName, "Near player: " + (nearPlayer == null ? " none" : " at distance: " + player.distanceFrom(nearPlayer)));

            // CASE C: PASS TO MATE
            if (nearPlayer != null && nearPlayerDistance < 350) {
                targetDistance = 0.7f * nearPlayerDistance;
                targetAngle = Emath.angle(player.x, player.y, nearPlayer.x, nearPlayer.y);
                Gdx.app.debug(player.shirtName, "Passing to mate");
            }

            // CASE D: LAUNCH FAR AWAY (should rarely happen)
            else {
                targetDistance = 500;
                targetAngle = -90 * player.team.side;
                Gdx.app.debug(player.shirtName, "Launching to the opponent side");
            }
        }

        if (targetDistance <= 250) {
            kickType = KickType.SHORT;
            kickDuration = (250 + targetDistance) / 10000 * GLGame.VIRTUAL_REFRESH_RATE;
        } else if (targetDistance < 500) {
            kickType = KickType.MEDIUM;
            kickDuration = (100 + targetDistance) / 10000 * GLGame.VIRTUAL_REFRESH_RATE;
        } else {
            kickType = KickType.LONG;
            kickDuration = targetDistance / 30000 * GLGame.VIRTUAL_REFRESH_RATE;
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
                if (timer > 20) {
                    ai.x0 = Math.round(Emath.cos(targetAngle));
                    ai.y0 = Math.round(Emath.sin(targetAngle));
                    step = Step.WAITING;
                    timer = 0;
                }
                break;

            case WAITING:
                if (timer > 30) {
                    float signedAngleDiff = Emath.signedAngleDiff(targetAngle, player.a);
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
                    case SHORT:
                        ai.x0 = Math.round(Emath.cos(player.a));
                        ai.y0 = Math.round(Emath.sin(player.a));
                        if (kickSpin != 0 && player.getState().checkId(STATE_FREE_KICK_SPEED)) {
                            ai.x0 = Math.round(Emath.cos(player.a + kickSpin * 45));
                            ai.y0 = Math.round(Emath.sin(player.a + kickSpin * 45));
                        }
                        break;

                    case MEDIUM:
                        ai.x0 = 0;
                        ai.y0 = 0;
                        if (kickSpin != 0 && player.getState().checkId(STATE_FREE_KICK_SPEED)) {
                            ai.x0 = Math.round(Emath.cos(player.a + kickSpin * 90));
                            ai.y0 = Math.round(Emath.sin(player.a + kickSpin * 90));
                        }
                        break;

                    case LONG:
                        ai.x0 = Math.round(Emath.cos(player.a + 180));
                        ai.y0 = Math.round(Emath.sin(player.a + 180));
                        if (kickSpin != 0 && player.getState().checkId(STATE_FREE_KICK_SPEED)) {
                            ai.x0 = Math.round(Emath.cos(player.a + 180 - kickSpin * 45));
                            ai.y0 = Math.round(Emath.sin(player.a + 180 - kickSpin * 45));
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
        float nearPostAngle = Emath.angle(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
        float farPostAngle = Emath.angle(player.ball.x, player.ball.y, -player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
        float nearPostCorrection = Math.abs((nearPostAngle - Emath.roundBy(nearPostAngle, 45f)));
        float farPostCorrection = Math.abs((farPostAngle - Emath.roundBy(farPostAngle, 45f)));
        Gdx.app.debug(player.shirtName,
                "nearPostAngle: " + nearPostAngle +
                        ", farPostAngle: " + farPostAngle +
                        ", nearPostCorrection: " + nearPostCorrection +
                        ", farPostCorrection: " + farPostCorrection);

        float distance;
        float targetMultiplier = Emath.rand(15, 20) / 10f;

        // CASE A: LAUNCH TO PENALTY AREA
        if (nearPostCorrection > 12 && farPostCorrection > 12) {
            distance = Emath.dist(player.ball.x, player.ball.y, 0, -player.team.side * PENALTY_SPOT_Y);
            targetAngle = Emath.angle(player.ball.x, player.ball.y, 0, -player.team.side * PENALTY_SPOT_Y);
            Gdx.app.debug(player.shirtName, "Penalty spot post distance: " + distance + ", angle: " + targetAngle + " targetMultiplier:" + targetMultiplier);
        }

        // CASE B: TARGET NEAR POST
        else if (nearPostCorrection < farPostCorrection) {
            distance = Emath.dist(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            targetAngle = Emath.angle(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            Gdx.app.debug(player.shirtName, "Near post distance: " + distance + ", angle: " + targetAngle + " targetMultiplier:" + targetMultiplier);
        }

        // CASE C: TARGET FAR POST
        else {
            distance = Emath.dist(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            targetAngle = Emath.angle(player.ball.x, player.ball.y, -player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            Gdx.app.debug(player.shirtName, "Far post distance: " + distance + ", angle: " + targetAngle + " targetMultiplier:" + targetMultiplier);
        }

        targetDistance = targetMultiplier * distance;
    }
}
