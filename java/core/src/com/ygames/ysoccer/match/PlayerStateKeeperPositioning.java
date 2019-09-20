package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;

class PlayerStateKeeperPositioning extends PlayerState {

    private int dangerTime;
    private float reactivity;

    PlayerStateKeeperPositioning(PlayerFsm fsm) {
        super(STATE_KEEPER_POSITIONING, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        dangerTime = 0;
        reactivity = (0.2f - 0.03f * player.getSkillKeeper()) * Const.SECOND;
    }

    private void updateTarget() {

        float referenceY = player.side * (GOAL_LINE + 22);
        float deltaX = ball.x * 30 / Math.abs(ball.y - referenceY);

        // default positioning
        float new_tx = Math.signum(deltaX) * Math.min(Math.abs(deltaX), 50);
        float new_ty = player.side * (GOAL_LINE - 8);

        // penalty area positioning
        if ((Math.abs(ball.x) < Const.PENALTY_AREA_W / 2)
                && Emath.isIn(ball.y, player.team.side * (GOAL_LINE - Const.PENALTY_AREA_H), player.team.side * GOAL_LINE)) {

            // if ball is approaching
            if (Emath.dist(ball.x, ball.y, 0, player.team.side * GOAL_LINE) < Emath.dist(ball.x0, ball.y0, 0, player.team.side * GOAL_LINE)) {

                // if ball is reachable reach the point where it will go
                if (player.frameDistance < Const.BALL_PREDICTION) {

                    new_tx = ball.prediction[player.frameDistance].x;

                } else {
                    // try to reach it anyway
                    new_tx = ball.x;
                    new_ty = player.team.side * (GOAL_LINE - 0.5f * Math.abs(GOAL_LINE - Math.abs(ball.y)));
                }
            }
        }

        // goal area positioning: reach the ball!
        if ((Math.abs(ball.x) < Const.GOAL_AREA_W / 2)
                && Emath.isIn(ball.y, player.team.side * (GOAL_LINE - Const.GOAL_AREA_H), player.team.side * GOAL_LINE)) {
            if (player.frameDistance < Const.BALL_PREDICTION) {
                new_tx = ball.prediction[player.frameDistance].x;
                new_ty = ball.prediction[player.frameDistance].y;
            }
        }

        if (Emath.dist(new_tx, new_ty, player.tx, player.ty) > 1.5f) {
            player.tx = new_tx;
            player.ty = new_ty;
        }
    }

    @Override
    void doActions() {
        super.doActions();
        if ((timer % 200) == 0) {
            updateTarget();
        }

        // distance from target position
        float dx = Math.round(player.tx - player.x);
        float dy = Math.round(player.ty - player.y);

        if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
            // reach target position
            player.v = (0.25f + 0.60f * ((Emath.hypo(dx, dy) > 4) ? 1 : 0)) * player.speed;
            player.a = (Emath.aTan2(dy, dx) + 360.0f) % 360.0f;
        } else {
            // position reached
            dx = ball.x - player.x;
            dy = ball.y - player.y;
            player.v = 0;
            if (timer > 0.5f * Const.SECOND) {
                player.a = Emath.aTan2(dy, dx);
            }
        }

        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice != player.ai) {
            return fsm.stateStandRun;
        }

        // detect danger
        boolean found = false;
        if ((Math.abs(ball.y) < GOAL_LINE)
                && (Math.abs(ball.y) > 0.5f * GOAL_LINE)
                && (Math.signum(ball.y) == Math.signum(player.y))
                && (ball.owner == null)) {
            for (int frm = 0; frm < Const.BALL_PREDICTION; frm++) {
                float x = ball.prediction[frm].x;
                float y = ball.prediction[frm].y;
                float z = ball.prediction[frm].z;
                if ((Math.abs(x) < Const.GOAL_AREA_W / 2)
                        && (Math.abs(z) < 2 * Const.CROSSBAR_H)
                        && ((Math.abs(y) > GOAL_LINE) && (Math.abs(y) < GOAL_LINE + 15))) {
                    found = true;
                }
            }
        }

        if (found) {
            dangerTime = dangerTime + 1;
        } else {
            dangerTime = 0;
        }

        if (dangerTime > reactivity) {
            float predX = 0;
            float predZ = 0;

            //intersection with keeper diving surface
            int frm;
            boolean found2 = false;
            for (frm = 0; frm < Const.BALL_PREDICTION; frm++) {
                float x = ball.prediction[frm].x;
                float y = ball.prediction[frm].y;
                float z = ball.prediction[frm].z;
                if ((Math.abs(x - player.x) < Const.GOAL_AREA_W / 2) && (Math.abs(z) < 2 * Const.CROSSBAR_H) && ((Math.abs(y) > Math.abs(player.y)) && (Math.abs(y) < Math.abs(player.y) + 15))) {
                    predX = x;
                    predZ = z;
                    found2 = true;
                }
                if (found2) {
                    break;
                }
            }
            float diffX = predX - player.x;

            //kind of save
            if (predZ < 2 * Const.CROSSBAR_H) {
                float r = Emath.hypo(diffX, predZ);

                if (r < 88) {
                    if (Math.abs(diffX) < 4) {
                        if (predZ > 30) {
                            //CATCH HIGH
                            if (frm * GLGame.SUBFRAMES < 0.6f * Const.SECOND) {
                                return fsm.stateKeeperCatchingHigh;
                            }
                        } else {
                            //CATCH LOW
                            if (frm * GLGame.SUBFRAMES < 0.6f * Const.SECOND) {
                                return fsm.stateKeeperCatchingLow;
                            }
                        }
                    } else if (predZ < 7) {
                        if (Math.abs(diffX) > Const.POST_X) {
                            //LOW - ONE HAND
                            if ((frm * GLGame.SUBFRAMES < 0.7f * Const.SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * Const.SECOND)) {
                                player.thrustX = (Math.abs(diffX) - Const.POST_X) / (Const.GOAL_AREA_W / 2f - Const.POST_X);
                                player.a = (diffX < 0) ? 180 : 0;
                                return fsm.stateKeeperDivingLowSingle;
                            }
                        } else {
                            //LOW - TWO HANDS
                            if (frm * GLGame.SUBFRAMES < 0.5f * Const.SECOND) {
                                player.thrustX = (Math.abs(diffX) - 8) / (Const.POST_X - 8);
                                player.a = (diffX < 0) ? 180 : 0;
                                return fsm.stateKeeperDivingLowDouble;
                            }
                        }
                    } else if (predZ < 21) {
                        //MIDDLE - TWO HANDS
                        if ((frm * GLGame.SUBFRAMES < 0.7f * Const.SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * Const.SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (Const.POST_X - 8);
                            player.thrustZ = (predZ - 7) / 14.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleTwo;
                        }
                    } else if ((predZ < 27) && (Math.abs(diffX) < Const.POST_X + 16)) {
                        //MIDDLE - ONE HAND
                        if ((frm * GLGame.SUBFRAMES < 0.7f * Const.SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * Const.SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (Const.POST_X + 8);
                            player.thrustZ = (predZ - 17) / 6.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleOne;
                        }
                    } else if ((predZ < 44) && (Math.abs(diffX) < Const.POST_X + 16)) {
                        //HIGH - ONE HAND
                        if ((frm * GLGame.SUBFRAMES < 0.7f * Const.SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * Const.SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (Const.POST_X + 8);
                            player.thrustZ = (float) Math.min((predZ - 27) / 8.0, 1);
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingHighOne;
                        }
                    }
                }
            }
        }

        return null;
    }
}
