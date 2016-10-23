package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

class PlayerStateGoalScorer extends PlayerState {

    PlayerStateGoalScorer(Player player) {
        super(player);
        id = PlayerFsm.STATE_GOAL_SCORER;
    }

    @Override
    void entryActions() {
        super.entryActions();
        boolean[] disallowed = new boolean[8];

        //top
        if (player.y + Const.GOAL_LINE < 375) {
            disallowed[6] = true;
            if (player.x < 0) {
                disallowed[7] = true;
            } else {
                disallowed[5] = true;
            }
        }
        if (player.y + Const.GOAL_LINE < 220) {
            disallowed[5] = true;
            disallowed[6] = true;
            disallowed[7] = true;
            if (player.x < 0) {
                disallowed[0] = true;
            } else {
                disallowed[4] = true;
            }
        }

        //bottom
        if (Const.GOAL_LINE - player.y < 375) {
            disallowed[2] = true;
            if (player.x < 0) {
                disallowed[1] = true;
            } else {
                disallowed[3] = true;
            }
        }
        if (Const.GOAL_LINE - player.y < 220) {
            disallowed[1] = true;
            disallowed[2] = true;
            disallowed[3] = true;
            if (player.x < 0) {
                disallowed[0] = true;
            } else {
                disallowed[4] = true;
            }
        }

        //left
        if (player.x + Const.TOUCH_LINE < 340) {
            disallowed[4] = true;
        }
        if (player.x + Const.TOUCH_LINE < 200) {
            disallowed[3] = true;
            disallowed[4] = true;
            disallowed[5] = true;
        }

        //right
        if (Const.TOUCH_LINE - player.x < 340) {
            disallowed[0] = true;
        }
        if (Const.TOUCH_LINE - player.x < 200) {
            disallowed[7] = true;
            disallowed[0] = true;
            disallowed[1] = true;
        }

        int celebrationType;
        do {
            celebrationType = player.match.random.nextInt(8);
        } while (disallowed[celebrationType]);

        player.v = player.speed;
        player.a = 45 * celebrationType;

        float d = 500;

        //check x limits
        if (Math.abs(Emath.cos(player.a)) > 0.002f) {
            float tx = player.x + d * Emath.cos(player.a);
            tx = Emath.sgn(tx) * Math.min(Math.abs(tx), Const.TOUCH_LINE + 20 - 2 * player.number);
            d = Math.min(d, (tx - player.x) / Emath.cos(player.a));
        }

        //check y limits
        if (Math.abs(Emath.sin(player.a)) > 0.002f) {
            float ty = player.y + d * Emath.sin(player.a);
            ty = Emath.sgn(ty) * Math.min(Math.abs(ty), Const.GOAL_LINE + 10 - player.number);
            d = Math.min(d, (ty - player.y) / Emath.sin(player.a));
        }

        player.tx = player.x + d * Emath.cos(player.a);
        player.ty = player.y + d * Emath.sin(player.a);
    }

    @Override
    void doActions() {
        super.doActions();

        //stop and look the crowd
        if (player.targetDistance() < 1.5f) {
            player.v = 0;
        }
        player.animationScorer();
    }
}
