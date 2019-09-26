package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.framework.GLGame.LogType.AI_KICKING;
import static com.ygames.ysoccer.match.AiFsm.Id.STATE_KICKING;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KICK;

class AiStateKicking extends AiState {

    private float targetDistance;
    private float targetAngle;
    private float kickDuration;
    private float spinSign;


    AiStateKicking(AiFsm fsm) {
        super(STATE_KICKING, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        ai.x0 = Math.round(Emath.cos(player.a));
        ai.y0 = Math.round(Emath.sin(player.a));

        setGoalTarget();

        // targetDistance:  150 .. 300
        // kickDuration:    8 .. 11 + 2 .. 5 = 10 .. 17
        // timer:           77 .. 136
        kickDuration = Emath.rand(8, 11) + targetDistance / 60;

        float signedAngleDiff = Emath.signedAngleDiff(targetAngle, player.a);
        if (Math.abs(signedAngleDiff) > 3) {
            spinSign = Emath.sgn(signedAngleDiff);
        } else {
            spinSign = 0;
        }

        GLGame.debug(AI_KICKING, this, player.numberName()
                + ", targetDistance: " + targetDistance
                + ", targetAngle: " + targetAngle
                + ", kickDuration: " + kickDuration
                + ", spinSign: " + spinSign
                + ", ai.x0: " + ai.x0
                + ", ai.y0: " + ai.y0
                + ", ai.angle: " + ai.angle
                + ", player.a: " + player.a
        );
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = 0;
        ai.y0 = 0;
        if (spinSign != 0 && player.getState().checkId(STATE_KICK)) {
            ai.x0 = Math.round(Emath.cos(player.a + spinSign * 90));
            ai.y0 = Math.round(Emath.sin(player.a + spinSign * 90));
        }
        ai.fire10 = timer < kickDuration;
    }

    @Override
    State checkConditions() {
        if (timer > kickDuration) {
            return fsm.stateIdle;
        }
        return null;
    }

    private void setGoalTarget() {
        final float TARGET_X = POST_X - 3 * BALL_R;
        float nearPostAngle = Emath.angle(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
        float farPostAngle = Emath.angle(player.ball.x, player.ball.y, -player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
        float nearPostCorrection = Math.abs((nearPostAngle - player.a));
        float farPostCorrection = Math.abs((farPostAngle - player.a));

        GLGame.debug(AI_KICKING, this, player.numberName() +
                ", nearPostAngle: " + nearPostAngle +
                ", farPostAngle: " + farPostAngle +
                ", nearPostCorrection: " + nearPostCorrection +
                ", farPostCorrection: " + farPostCorrection
        );

        if (nearPostCorrection < farPostCorrection) {
            targetDistance = Emath.dist(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            targetAngle = Emath.angle(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            Gdx.app.debug(player.shirtName, "Near post angle: " + targetAngle);
        } else {
            targetDistance = Emath.dist(player.ball.x, player.ball.y, player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            targetAngle = Emath.angle(player.ball.x, player.ball.y, -player.ball.xSide * TARGET_X, -player.team.side * GOAL_LINE);
            Gdx.app.debug(player.shirtName, "Far post angle: " + targetAngle);
        }
    }
}
