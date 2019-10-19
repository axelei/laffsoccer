package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;

import static com.ygames.ysoccer.match.Const.PASSING_SPEED_FACTOR;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KICK;

class PlayerStateKick extends PlayerState {

    enum Mode {UNKNOWN, PASSING, KICKING, SHOOTING}

    private Mode mode;
    private boolean startedShooting;

    PlayerStateKick(PlayerFsm fsm) {
        super(STATE_KICK, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        mode = Mode.UNKNOWN;
        startedShooting = false;

        ball.a = player.kickAngle;
    }

    @Override
    void doActions() {
        super.doActions();

        float angle;
        if (player.inputDevice.value) {
            angle = player.inputDevice.angle;
        } else {
            angle = player.kickAngle;
        }
        float angle_diff = ((angle - player.kickAngle + 540.0f) % 360.0f) - 180.0f;

        switch (mode) {
            case UNKNOWN:
                if (timer > 0.15 * Const.SECOND) {
                    if (player.inputDevice.fire11) {
                        if (ball.isInsideDirectShotArea(-player.side) && player.seesTheGoal()) {
                            mode = Mode.SHOOTING;

                            Assets.Sounds.kick.play(Assets.Sounds.volume / 100f);
                        } else {
                            mode = Mode.KICKING;
                            ball.v = 250.0f;

                            Assets.Sounds.kick.play(0.8f * Assets.Sounds.volume / 100f);
                        }

                    } else {
                        mode = Mode.PASSING;
                        ball.v = 240f;

                        // automatic angle correction
                        if (angle_diff == 0) {
                            player.searchPassingMate();
                            if (player.passingMate != null) {
                                ball.a += player.passingMateAngleCorrection;
                                ball.v += PASSING_SPEED_FACTOR * player.passingMate.distanceFrom(player);
                            }
                        }

                        Assets.Sounds.kick.play(0.6f * Assets.Sounds.volume / 100f);
                    }
                }
                break;

            case PASSING:
                if (timer < 0.25 * Const.SECOND) {
                    // horizontal
                    if (player.inputDevice.fire11) {
                        ball.v += (960.0f + 2 * player.skills.shooting) / Const.SECOND;
                    }

                    // vertical
                    float f = 1.0f; // medium shoots
                    if (player.inputDevice.value) {
                        // low shoots
                        if (Math.abs(angle_diff) < 67.5f) {
                            f = 0;
                        }
                        // high shoots
                        else if (Math.abs(angle_diff) > 112.5f) {
                            f = 1.33f;
                        }
                    }
                    ball.vz = f * (120.0f + ball.v * (1 - 0.05f * player.skills.shooting) * timer / Const.SECOND);

                    // spin
                    if (player.inputDevice.value) {
                        if ((Math.abs(angle_diff) > 22.5f) && (Math.abs(angle_diff) < 157.5f)) {
                            ball.s += 130.0f / Const.SECOND * Math.signum(angle_diff);
                        }
                    }
                }
                break;

            case KICKING:
                if (timer < 0.25 * Const.SECOND) {
                    // horizontal speed
                    if (player.inputDevice.fire11) {
                        ball.v += (960.0f + 2 * player.skills.shooting) / Const.SECOND;
                    }

                    // vertical speed
                    // medium shots
                    float base = 120f;
                    float factor = 0.8f;
                    if (player.inputDevice.value) {
                        // low shots
                        if (Math.abs(angle_diff) < 67.5f) {
                            factor = 0.4f;
                        }
                        // high shots
                        else if (Math.abs(angle_diff) > 112.5f) {
                            factor = 1.2f;
                        }
                    }
                    ball.vz = factor * (base + ball.v * (1 - 0.05f * player.skills.shooting) * timer / Const.SECOND);

                    // spin
                    if (player.inputDevice.value) {
                        if ((Math.abs(angle_diff) > 22.5f) && (Math.abs(angle_diff) < 157.5f)) {
                            ball.s += 130.0f / Const.SECOND * Math.signum(angle_diff);
                        }
                    }
                }
                break;

            case SHOOTING:
                if (timer > 0.20f * SECOND && timer < 0.30f * SECOND) {
                    // horizontal speed
                    if (!startedShooting) {
                        ball.v = 270f;
                        startedShooting = true;
                    }

                    // no shooting skill
                    // 270 + (0 : 51) * 650 / 512 = 270 + 0 : 64 = 270 : 334

                    // top shooting skill
                    // 270 + (0 : 51) * 1000 / 512 = 270 + 0 : 97 = 270 : 367
                    if (player.inputDevice.fire11) {
                        ball.v += (650.0f + 50 * player.skills.shooting) / SECOND;
                    }

                    // vertical speed
                    // medium shots
                    float base = 110.0f;
                    float factor = 0.8f;
                    if (player.inputDevice.value) {
                        // low shots
                        if (Math.abs(angle_diff) < 67.5f) {
                            base = 120.0f;
                            factor = 0.35f;
                        }
                        // high shots
                        else if (Math.abs(angle_diff) > 112.5f) {
                            base = 100.0f;
                            factor = 1.2f;
                        }
                    }
                    ball.vz = factor * (base + ball.v * (1 - 0.05f * player.skills.shooting) * timer / SECOND);

                    // spin
                    if (player.inputDevice.value) {
                        if ((Math.abs(angle_diff) > 22.5f) && (Math.abs(angle_diff) < 157.5f)) {
                            ball.s += 130.0f / SECOND * Math.signum(angle_diff);
                        }
                    }
                }
                break;
        }

        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (timer > 0.35 * Const.SECOND) {
            return fsm.stateStandRun;
        }

        if (ballLost()) {
            return fsm.stateStandRun;
        }

        return null;
    }

    private boolean ballLost() {
        return ball.owner != null && ball.owner.team.side != player.team.side;
    }
}
