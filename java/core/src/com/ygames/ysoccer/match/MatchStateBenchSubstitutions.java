package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Coach.Status.LOOK_BENCH;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_EXIT;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_FORMATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_OUT;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_SITTING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_STANDING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SUBSTITUTED;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static java.lang.Math.min;

class MatchStateBenchSubstitutions extends MatchState {

    MatchFsm.BenchStatus benchStatus;

    MatchStateBenchSubstitutions(MatchFsm fsm) {
        super(fsm);

        displayBenchPlayers = true;

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        benchStatus = getFsm().benchStatus;
        sceneRenderer.actionCamera.setMode(STILL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.updateBall();
            match.ball.inFieldKeep();

            match.updatePlayers(true);

            match.updateCoaches();

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        // move selection
        if (benchStatus.inputDevice.yMoved()) {
            int substitutes = min(match.getSettings().benchSize, benchStatus.team.lineup.size() - TEAM_SIZE);

            // if remaining substitutions
            if (benchStatus.team.substitutionsCount < match.getSettings().substitutions) {
                benchStatus.selectedPosition = EMath.rotate(benchStatus.selectedPosition, -1, substitutes - 1, benchStatus.inputDevice.y1);
            }

            // reset positions
            for (int i = 0; i < substitutes; i++) {
                Player player = benchStatus.team.lineup.get(TEAM_SIZE + i);
                if (!player.getState().checkId(STATE_SUBSTITUTED)) {
                    player.setState(STATE_BENCH_SITTING);
                }
            }

            // move selected player
            if (benchStatus.selectedPosition != -1) {
                Player player = benchStatus.team.lineup.get(TEAM_SIZE + benchStatus.selectedPosition);
                if (!player.getState().checkId(STATE_SUBSTITUTED)) {
                    // coach calls player
                    Coach coach = benchStatus.team.coach;
                    coach.status = LOOK_BENCH;
                    coach.timer = 250;

                    player.setState(STATE_BENCH_STANDING);
                }
            }
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (benchStatus.inputDevice.fire1Down()) {
            if (benchStatus.selectedPosition == -1) {
                return newAction(NEW_FOREGROUND, STATE_BENCH_FORMATION);
            } else {
                // if no previous selection
                if (benchStatus.substPosition == -1) {

                    // out the player for substitution
                    Player player = benchStatus.team.lineup.get(TEAM_SIZE + benchStatus.selectedPosition);

                    if (!player.getState().checkId(STATE_SUBSTITUTED)) {

                        player.setState(STATE_BENCH_OUT);

                        benchStatus.substPosition = TEAM_SIZE + benchStatus.selectedPosition;
                        benchStatus.selectedPosition = benchStatus.team.nearestBenchPlayerByRole(player.role);

                        return newAction(NEW_FOREGROUND, STATE_BENCH_FORMATION);
                    }
                }
            }
        }

        if (benchStatus.inputDevice.xReleased()) {
            return newAction(NEW_FOREGROUND, STATE_BENCH_EXIT);
        }

        return checkCommonConditions();
    }
}
