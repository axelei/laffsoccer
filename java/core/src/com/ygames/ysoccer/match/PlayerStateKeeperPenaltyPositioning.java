package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.Const.BALL_PREDICTION;
import static com.ygames.ysoccer.match.Const.CROSSBAR_H;
import static com.ygames.ysoccer.match.Const.GOAL_AREA_W;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_PENALTY_POSITIONING;

class PlayerStateKeeperPenaltyPositioning extends PlayerState {

    enum Mode {DEFAULT, COVER_SHOOTING_ANGLE, RECOVER_BALL}

    private int dangerTime;
    private float reactivity;

    PlayerStateKeeperPenaltyPositioning(PlayerFsm fsm) {
        super(STATE_KEEPER_PENALTY_POSITIONING, fsm);

        reactivity = (23 - 3 * player.getSkillKeeper()) / 100f * SECOND;
    }

    @Override
    void entryActions() {
        super.entryActions();

        dangerTime = 0;
    }

    @Override
    void doActions() {
        super.doActions();

        setDefaultTarget();

        // distance from target position
        float dx = player.tx - player.x;
        float dy = player.ty - player.y;

        if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
            // reach target position
            player.v = (0.25f + 0.60f * ((EMath.hypo(dx, dy) > 4) ? 1 : 0)) * player.speed;
            player.a = EMath.angle(player.x, player.y, player.tx, player.ty);
        } else {
            // position reached
            dx = ball.x - player.x;
            dy = ball.y - player.y;
            player.v = 0;
            if (timer > 0.5f * SECOND) {
                player.a = EMath.aTan2(dy, dx);
            }
        }

        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice != player.ai) {
            return fsm.stateStandRun;
        }

        PlayerState save = getSaves();
        if (save != null) return save;

        return null;
    }

    private PlayerState getSaves() {

        boolean danger = false;
        for (int frm = 0; frm < BALL_PREDICTION; frm++) {
            float x = ball.prediction[frm].x;
            float y = ball.prediction[frm].y;
            float z = ball.prediction[frm].z;
            if ((Math.abs(x) < GOAL_AREA_W / 2)
                    && (Math.abs(z) < 2 * CROSSBAR_H)
                    && ((Math.abs(y) > Math.abs(player.y)) && (Math.abs(y) < Math.abs(player.y) + 15))) {
                danger = true;
                break;
            }
        }

        if (danger) {
            dangerTime = dangerTime + 1;
        } else {
            dangerTime = 0;
        }

        if (dangerTime > reactivity) {
            float predX = 0;
            float predZ = 0;

            // intersection with keeper diving surface
            int frm;
            boolean found2 = false;
            for (frm = 0; frm < BALL_PREDICTION; frm++) {
                float x = ball.prediction[frm].x;
                float y = ball.prediction[frm].y;
                float z = ball.prediction[frm].z;
                if ((Math.abs(x - player.x) < GOAL_AREA_W / 2) && (Math.abs(z) < 2 * CROSSBAR_H) && ((Math.abs(y) > Math.abs(player.y)) && (Math.abs(y) < Math.abs(player.y) + 15))) {
                    predX = x;
                    predZ = z;
                    found2 = true;
                }
                if (found2) {
                    break;
                }
            }
            float diffX = predX - player.x;

            // kind of save
            if (predZ < 2 * CROSSBAR_H) {
                float r = EMath.hypo(diffX, predZ);

                if (r < 88) {
                    if (Math.abs(diffX) < 4) {
                        if (predZ > 30) {
                            // CATCH HIGH
                            if (frm * GLGame.SUBFRAMES < 0.6f * SECOND) {
                                return fsm.stateKeeperCatchingHigh;
                            }
                        } else {
                            // CATCH LOW
                            if (frm * GLGame.SUBFRAMES < 0.6f * SECOND) {
                                return fsm.stateKeeperCatchingLow;
                            }
                        }
                    } else if (predZ < 7) {
                        if (Math.abs(diffX) > POST_X) {
                            // LOW - ONE HAND
                            if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                                player.thrustX = (Math.abs(diffX) - POST_X) / (GOAL_AREA_W / 2f - POST_X);
                                player.a = (diffX < 0) ? 180 : 0;
                                return fsm.stateKeeperDivingLowSingle;
                            }
                        } else {
                            // LOW - TWO HANDS
                            if (frm * GLGame.SUBFRAMES < 0.5f * SECOND) {
                                player.thrustX = (Math.abs(diffX) - 8) / (POST_X - 8);
                                player.a = (diffX < 0) ? 180 : 0;
                                return fsm.stateKeeperDivingLowDouble;
                            }
                        }
                    } else if (predZ < 21) {
                        // MIDDLE - TWO HANDS
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X - 8);
                            player.thrustZ = (predZ - 7) / 14.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleTwo;
                        }
                    } else if ((predZ < 27) && (Math.abs(diffX) < POST_X + 16)) {
                        // MIDDLE - ONE HAND
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X + 8);
                            player.thrustZ = (predZ - 17) / 6.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleOne;
                        }
                    } else if ((predZ < 44) && (Math.abs(diffX) < POST_X + 16)) {
                        // HIGH - ONE HAND
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X + 8);
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

    private void setDefaultTarget() {
        float tx = 0;
        float ty = player.side * (GOAL_LINE - 4);

        if (EMath.dist(tx, ty, player.tx, player.ty) > 1.5f) {
            player.tx = tx;
            player.ty = ty;
        }
    }
}
