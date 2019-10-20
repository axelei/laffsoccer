package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.BALL_PREDICTION;
import static com.ygames.ysoccer.match.Const.CROSSBAR_H;
import static com.ygames.ysoccer.match.Const.GOAL_AREA_H;
import static com.ygames.ysoccer.match.Const.GOAL_AREA_W;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_AREA_H;
import static com.ygames.ysoccer.match.Const.PENALTY_AREA_W;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;

class PlayerStateKeeperPositioning extends PlayerState {

    enum Mode {DEFAULT, COVER_SHOOTING_ANGLE, RECOVER_BALL}

    private Mode mode;
    private int dangerTime;
    private final float reactivity;

    PlayerStateKeeperPositioning(PlayerFsm fsm) {
        super(STATE_KEEPER_POSITIONING, fsm);

        reactivity = (23 - 3 * player.getSkillKeeper()) / 100f * SECOND;
    }

    @Override
    void entryActions() {
        super.entryActions();

        mode = Mode.DEFAULT;
        dangerTime = 0;
    }

    @Override
    void doActions() {
        super.doActions();

        switch (mode) {
            case DEFAULT:
                if ((timer % 100) == 0) {
                    setDefaultTarget();
                }
                break;

            case RECOVER_BALL:
                if (player.frameDistance < BALL_PREDICTION) {
                    player.tx = EMath.clamp(ball.prediction[player.frameDistance].x, 0, ball.xSide * PENALTY_AREA_W / 2f);
                    player.ty = EMath.clamp(ball.prediction[player.frameDistance].y, player.side * (GOAL_LINE - PENALTY_AREA_H), player.side * GOAL_LINE);
                }
                break;

            case COVER_SHOOTING_ANGLE:
                if ((timer % 40) == 0) {
                    setCoveringTarget();
                }
                break;
        }

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

        Player nearestOfAll = player.scene.getNearestOfAll();

        // update mode
        switch (mode) {
            case DEFAULT:
                if (ball.isInsidePenaltyArea(player.side) || ball.ySide * ball.y > GOAL_LINE) {
                    if (player == nearestOfAll) {
                        mode = Mode.RECOVER_BALL;
                    } else if (nearestOfAll != null && nearestOfAll.side != player.side
                            && player == player.team.near1
                            && ball.owner != null) {
                        mode = Mode.COVER_SHOOTING_ANGLE;
                    }
                }
                break;

            case RECOVER_BALL:
                if (nearestOfAll == player) {
                    if (!ball.isInsidePenaltyArea(player.side)) {
                        mode = Mode.DEFAULT;
                    }
                } else {
                    if (ball.isInsidePenaltyArea(player.side)
                            && nearestOfAll != null
                            && nearestOfAll.team != player.team
                            && player == player.team.near1) {
                        mode = Mode.COVER_SHOOTING_ANGLE;
                    } else {
                        mode = Mode.DEFAULT;
                    }
                }
                break;

            case COVER_SHOOTING_ANGLE:
                if (ball.isInsidePenaltyArea(player.side)) {
                    if (ball.owner == null) {
                        if (nearestOfAll == player) {
                            mode = Mode.RECOVER_BALL;
                        }
                    } else if (ball.owner.team == player.team) {
                        mode = Mode.DEFAULT;
                    }
                } else {
                    mode = Mode.DEFAULT;
                }
                break;

        }

        // take action
        switch (mode) {
            case DEFAULT:
            case RECOVER_BALL:
            case COVER_SHOOTING_ANGLE:
                PlayerState save = getSaves();
                if (save != null) return save;
                break;
        }

        // TODO: accept to play the ball instead of catching it if no opponents in penalty area
        if ((player.ballDistance <= 8)
                && EMath.dist(player.x0, player.y0, ball.x0, ball.y0) > 8
                && (ball.z < Const.PLAYER_H)) {
            ball.v = 0;
            ball.vz = 0;
            ball.s = 0;

            scene.setBallOwner(player);
            ball.setHolder(player);

            return fsm.stateKeeperKickAngle;
        }

        return null;
    }

    @Override
    void exitActions() {
        if (ball.holder == player) {
            ball.setHolder(null);
        }
    }

    private PlayerState getSaves() {

        boolean danger = false;
        if (ball.isInsideDirectShotArea(player.side)
                && (ball.owner == null || mode == Mode.COVER_SHOOTING_ANGLE)) {
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
        }

        if (danger) {
            dangerTime = dangerTime + 1;
        } else {
            dangerTime = 0;
        }

        if (dangerTime > reactivity) {
            float predictionX = 0;
            float predictionZ = 0;

            // intersection with keeper diving surface
            int frm;
            boolean found2 = false;
            for (frm = 0; frm < BALL_PREDICTION; frm++) {
                float x = ball.prediction[frm].x;
                float y = ball.prediction[frm].y;
                float z = ball.prediction[frm].z;
                if ((Math.abs(x - player.x) < GOAL_AREA_W / 2) && (Math.abs(z) < 2 * CROSSBAR_H) && ((Math.abs(y) > Math.abs(player.y)) && (Math.abs(y) < Math.abs(player.y) + 15))) {
                    predictionX = x;
                    predictionZ = z;
                    found2 = true;
                }
                if (found2) {
                    break;
                }
            }
            float diffX = predictionX - player.x;

            // kind of save
            if (predictionZ < 2 * CROSSBAR_H) {
                float r = EMath.hypo(diffX, predictionZ);

                if (r < 88) {
                    if (Math.abs(diffX) < 4) {
                        if (predictionZ > 16) {
                            // CATCH HIGH
                            if (frm * GLGame.SUBFRAMES < 0.6f * SECOND) {
                                player.thrustZ = Math.min(predictionZ - 16, 24);
                                return fsm.stateKeeperCatchingHigh;
                            }
                        } else {
                            // CATCH LOW
                            if (frm * GLGame.SUBFRAMES < 0.6f * SECOND) {
                                return fsm.stateKeeperCatchingLow;
                            }
                        }
                    } else if (predictionZ < 7) {
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
                    } else if (predictionZ < 21) {
                        // MIDDLE - TWO HANDS
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X - 8);
                            player.thrustZ = (predictionZ - 7) / 14.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleTwo;
                        }
                    } else if ((predictionZ < 27) && (Math.abs(diffX) < POST_X + 16)) {
                        // MIDDLE - ONE HAND
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X + 8);
                            player.thrustZ = (predictionZ - 17) / 6.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleOne;
                        }
                    } else if ((predictionZ < 44) && (Math.abs(diffX) < POST_X + 16)) {
                        // HIGH - ONE HAND
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X + 8);
                            player.thrustZ = (float) Math.min((predictionZ - 27) / 8.0, 1);
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
        float referenceY = player.side * (GOAL_LINE + 22);
        float deltaX = ball.x * 30 / Math.abs(ball.y - referenceY);

        float tx = Math.signum(deltaX) * Math.min(Math.abs(deltaX), 50);
        float ty = player.side * (GOAL_LINE - 8);

        if (EMath.dist(tx, ty, player.tx, player.ty) > 1.5f) {
            player.tx = tx;
            player.ty = ty;
        }
    }

    private void setCoveringTarget() {
        float referenceY = player.side * GOAL_LINE;
        float deltaX = ball.x * (GOAL_AREA_H - 20) / Math.abs(ball.y - referenceY);

        float tx = Math.signum(deltaX) * Math.min(Math.abs(deltaX), (GOAL_AREA_W / 2f - 10));
        float ty = player.side * EMath.clamp(Math.abs(ball.y), GOAL_LINE - (GOAL_AREA_H - 20), GOAL_LINE);

        if (EMath.dist(tx, ty, player.tx, player.ty) > 1.5f) {
            player.tx = tx;
            player.ty = ty;
        }
    }
}
