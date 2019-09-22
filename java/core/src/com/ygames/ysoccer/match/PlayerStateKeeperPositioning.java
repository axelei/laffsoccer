package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

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

    enum Positioning {DEFAULT, COVER_SHOOTING_ANGLE, RECOVER_BALL}

    private Positioning positioning;
    private int dangerTime;
    private float reactivity;

    PlayerStateKeeperPositioning(PlayerFsm fsm) {
        super(STATE_KEEPER_POSITIONING, fsm);

        reactivity = (0.2f - 0.03f * player.getSkillKeeper()) * SECOND;
    }

    @Override
    void entryActions() {
        super.entryActions();

        positioning = Positioning.DEFAULT;
        dangerTime = 0;
    }

    @Override
    void doActions() {
        super.doActions();

        switch (positioning) {
            case DEFAULT:
                if ((timer % 100) == 0) {
                    setDefaultTarget();
                }
                break;

            case RECOVER_BALL:
                if (player.frameDistance < BALL_PREDICTION) {
                    player.tx = Emath.clamp(ball.prediction[player.frameDistance].x, 0, ball.xSide * PENALTY_AREA_W / 2f);
                    player.ty = Emath.clamp(ball.prediction[player.frameDistance].y, player.side * (GOAL_LINE - PENALTY_AREA_H), player.side * GOAL_LINE);
                }
                break;

            case COVER_SHOOTING_ANGLE:
                if ((timer % 40) == 0) {
                    setCoveringTarget();
                }
                break;
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
            if (timer > 0.5f * SECOND) {
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

        Player nearestOfAll = getNearestOfAll();

        switch (positioning) {
            case DEFAULT:
                if (ball.isInsidePenaltyArea(player.side)) {
                    if (player == nearestOfAll) {
                        positioning = Positioning.RECOVER_BALL;
                    } else if (nearestOfAll != null && nearestOfAll.team != player.team
                            && player == player.team.near1
                            && ball.owner != null) {
                        positioning = Positioning.COVER_SHOOTING_ANGLE;
                    }
                }
                break;

            case RECOVER_BALL:
                if (nearestOfAll == player) {
                    if (!ball.isInsidePenaltyArea(player.side)) {
                        positioning = Positioning.DEFAULT;
                    }
                } else {
                    if (ball.isInsidePenaltyArea(player.side)
                            && nearestOfAll != null
                            && nearestOfAll.team != player.team
                            && player == player.team.near1) {
                        positioning = Positioning.COVER_SHOOTING_ANGLE;
                    } else {
                        positioning = Positioning.DEFAULT;
                    }
                }
                break;

            case COVER_SHOOTING_ANGLE:
                if (ball.isInsidePenaltyArea(player.side)) {
                    if (nearestOfAll == player) {
                        positioning = Positioning.RECOVER_BALL;
                    } else {
                        if (ball.owner != null && ball.owner.team == player.team) {
                            positioning = Positioning.DEFAULT;
                        }
                    }
                } else {
                    positioning = Positioning.DEFAULT;
                }
                break;

        }

        // detect danger
        boolean found = false;
        if ((Math.abs(ball.y) < GOAL_LINE)
                && (Math.abs(ball.y) > 0.5f * GOAL_LINE)
                && (Math.signum(ball.y) == Math.signum(player.y))
                && (ball.owner == null)) {
            for (int frm = 0; frm < BALL_PREDICTION; frm++) {
                float x = ball.prediction[frm].x;
                float y = ball.prediction[frm].y;
                float z = ball.prediction[frm].z;
                if ((Math.abs(x) < GOAL_AREA_W / 2)
                        && (Math.abs(z) < 2 * CROSSBAR_H)
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

            //kind of save
            if (predZ < 2 * CROSSBAR_H) {
                float r = Emath.hypo(diffX, predZ);

                if (r < 88) {
                    if (Math.abs(diffX) < 4) {
                        if (predZ > 30) {
                            //CATCH HIGH
                            if (frm * GLGame.SUBFRAMES < 0.6f * SECOND) {
                                return fsm.stateKeeperCatchingHigh;
                            }
                        } else {
                            //CATCH LOW
                            if (frm * GLGame.SUBFRAMES < 0.6f * SECOND) {
                                return fsm.stateKeeperCatchingLow;
                            }
                        }
                    } else if (predZ < 7) {
                        if (Math.abs(diffX) > POST_X) {
                            //LOW - ONE HAND
                            if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                                player.thrustX = (Math.abs(diffX) - POST_X) / (GOAL_AREA_W / 2f - POST_X);
                                player.a = (diffX < 0) ? 180 : 0;
                                return fsm.stateKeeperDivingLowSingle;
                            }
                        } else {
                            //LOW - TWO HANDS
                            if (frm * GLGame.SUBFRAMES < 0.5f * SECOND) {
                                player.thrustX = (Math.abs(diffX) - 8) / (POST_X - 8);
                                player.a = (diffX < 0) ? 180 : 0;
                                return fsm.stateKeeperDivingLowDouble;
                            }
                        }
                    } else if (predZ < 21) {
                        //MIDDLE - TWO HANDS
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X - 8);
                            player.thrustZ = (predZ - 7) / 14.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleTwo;
                        }
                    } else if ((predZ < 27) && (Math.abs(diffX) < POST_X + 16)) {
                        //MIDDLE - ONE HAND
                        if ((frm * GLGame.SUBFRAMES < 0.7f * SECOND) && (frm * GLGame.SUBFRAMES > 0.25f * SECOND)) {
                            player.thrustX = (Math.abs(diffX) - 8) / (POST_X + 8);
                            player.thrustZ = (predZ - 17) / 6.0f;
                            player.a = (diffX < 0) ? 180 : 0;
                            return fsm.stateKeeperDivingMiddleOne;
                        }
                    } else if ((predZ < 44) && (Math.abs(diffX) < POST_X + 16)) {
                        //HIGH - ONE HAND
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
        float referenceY = player.side * (GOAL_LINE + 22);
        float deltaX = ball.x * 30 / Math.abs(ball.y - referenceY);

        float tx = Math.signum(deltaX) * Math.min(Math.abs(deltaX), 50);
        float ty = player.side * (GOAL_LINE - 8);

        if (Emath.dist(tx, ty, player.tx, player.ty) > 1.5f) {
            player.tx = tx;
            player.ty = ty;
        }
    }

    private void setCoveringTarget() {
        float referenceY = player.side * GOAL_LINE;
        float deltaX = ball.x * (GOAL_AREA_H - 20) / Math.abs(ball.y - referenceY);

        float tx = Math.signum(deltaX) * Math.min(Math.abs(deltaX), (GOAL_AREA_W / 2f - 10));
        float ty = player.side * Emath.clamp(Math.abs(ball.y), GOAL_LINE - (GOAL_AREA_H - 20), GOAL_LINE);

        if (Emath.dist(tx, ty, player.tx, player.ty) > 1.5f) {
            player.tx = tx;
            player.ty = ty;
        }
    }

    private Player getNearestOfAll() {
        // hack for training mode
        if (player.team.match == null) return player.team.near1;

        Player player0 = player.team.near1;
        Player player1 = player.team.match.team[1 - player.team.index].near1;

        int distance0 = Emath.min(player0.frameDistanceL, player0.frameDistance, player0.frameDistanceR);
        int distance1 = Emath.min(player1.frameDistanceL, player1.frameDistance, player1.frameDistanceR);

        if (distance0 == BALL_PREDICTION && distance1 == BALL_PREDICTION) return null;

        return distance0 < distance1 ? player0 : player1;
    }
}
