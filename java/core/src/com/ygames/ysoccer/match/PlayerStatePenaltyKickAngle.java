package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_ANGLE;

class PlayerStatePenaltyKickAngle extends PlayerState {

    private float defaultAngle;

    PlayerStatePenaltyKickAngle(PlayerFsm fsm) {
        super(STATE_PENALTY_KICK_ANGLE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.setBallOwner(player);
        defaultAngle = 90 * (2 - ball.ySide);
        player.a = defaultAngle;
    }

    @Override
    void doActions() {
        super.doActions();

        float turningSpeed = 0.5f;
        if (controlsAreTargetingTheGoal()) {
            moveToRequestedAngle(turningSpeed);
        } else {
            returnToDefaultAngle(turningSpeed);
        }
        player.x = ball.x - 7 * EMath.cos(player.a);
        player.y = ball.y - 7 * EMath.sin(player.a);
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice.fire1Down()) {
            return fsm.statePenaltyKickSpeed;
        }
        return null;
    }

    private boolean controlsAreTargetingTheGoal() {
        return player.inputDevice.y1 == ball.ySide;
    }

    private void moveToRequestedAngle(float turningSpeed) {
        final int delta = 5;
        if (player.inputDevice.value) {
            int targetAngle = (player.inputDevice.angle + 360) % 360;
            switch (targetAngle) {
                case 45:
                    player.a = Math.max(player.a - turningSpeed, 45 + delta);
                    break;
                case 135:
                    player.a = Math.min(player.a + turningSpeed, 135 - delta);
                    break;
                case 225:
                    player.a = Math.max(player.a - turningSpeed, 225 + delta);
                    break;
                case 315:
                    player.a = Math.min(player.a + turningSpeed, 315 - delta);
                    break;
                default:
                    break;
            }
        }
    }

    private void returnToDefaultAngle(float turningSpeed) {
        if (Math.abs(player.a - defaultAngle) > 1) {
            player.a -= turningSpeed * Math.signum(player.a - defaultAngle);
        }
    }
}
