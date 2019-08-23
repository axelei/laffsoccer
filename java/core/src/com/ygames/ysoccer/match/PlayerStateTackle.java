package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;

class PlayerStateTackle extends PlayerState {

    private boolean hit;

    PlayerStateTackle(PlayerFsm fsm, Player player) {
        super(STATE_TACKLE, fsm, player);
    }

    @Override
    void doActions() {
        super.doActions();

        if (!hit) {
            if ((ball.z < 5) && (player.ballDistance < 18)) {
                float angle = Emath.aTan2(ball.y - player.y, ball.x - player.x);
                float angleDiff = Math.abs((((angle - player.a + 540) % 360)) - 180);
                if ((angleDiff <= 90)
                        && (player.ballDistance * Emath.sin(angleDiff) <= (8 + 0.3f * player.skills.tackling))) {

                    ball.setOwner(player);
                    ball.v = player.v * (1 + 0.02f * player.skills.tackling);
                    hit = true;

                    if ((player.inputDevice.value)
                            && (Math.abs((((player.a - player.inputDevice.angle + 540) % 360)) - 180) < 67.5)) {
                        ball.a = player.inputDevice.angle;
                    } else {
                        ball.a = player.a;
                    }

                    Assets.Sounds.kick.play(0.1f * (1 + 0.03f * timer) * Assets.Sounds.volume / 100f);
                }
            }
        }

        for (int i = 1; i < TEAM_SIZE; i++) {
            Player opponent = player.team.match.team[1 - player.team.index].lineup.get(i);
            if (Emath.dist(player.x, player.y, opponent.x, opponent.y) < 8 &&
                    opponent.checkState(STATE_DOWN) == false) {

                opponent.setState(STATE_DOWN);
                boolean foul = ball.owner != player;
                // TODO
                // float angleDiff = Math.abs(((player.a - opponent.a + 360.0f) % 360.0f));
                // boolean book = angleDiff <= 45 || angleDiff >= 315;
                if (foul) {
                    player.commitFoul(opponent);
                }
            }
        }

        player.v -= (20 + player.matchSettings.grass.friction) / Const.SECOND * Math.sqrt(Math.abs(player.v));

        // animation
        player.fmx = Math.round((((player.a + 360) % 360)) / 45) % 8;
        player.fmy = 4;
    }

    @Override
    State checkConditions() {
        if (player.v < 30) {
            return player.fsm.stateStandRun;
        }
        return null;
    }

    @Override
    void entryActions() {
        super.entryActions();

        hit = false;

        player.v = 1.4f * (player.speed) * (1 + 0.02f * player.skills.tackling);
    }
}
