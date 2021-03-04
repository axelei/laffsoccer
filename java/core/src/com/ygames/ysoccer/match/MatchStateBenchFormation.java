package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import java.util.Collections;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_TACTICS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_STANDING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SENT_OFF;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SUBSTITUTED;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchFormation extends MatchState {

    MatchFsm.BenchStatus benchStatus;

    MatchStateBenchFormation(MatchFsm fsm) {
        super(fsm);

        displayBenchFormation = true;

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

        // change selected position
        if (benchStatus.inputDevice.yMoved()) {
            benchStatus.selectedPosition = EMath.rotate(benchStatus.selectedPosition, -1, TEAM_SIZE - 1, benchStatus.inputDevice.y1);
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (benchStatus.inputDevice.fire1Down()) {

            // switch to tactics mode
            if (benchStatus.selectedPosition == -1) {

                // reset eventually pending swap or substitution
                benchStatus.swapPosition = -1;
                if (benchStatus.substPosition != -1) {
                    Player player = benchStatus.team.lineup.get(benchStatus.substPosition);
                    player.setState(STATE_BENCH_STANDING);

                    benchStatus.selectedPosition = benchStatus.substPosition - TEAM_SIZE;
                    benchStatus.substPosition = -1;
                }

                return newAction(NEW_FOREGROUND, STATE_BENCH_TACTICS);
            }

            // swap and substitutions
            else {
                // if already selected for swap then deselect
                if (benchStatus.swapPosition == benchStatus.selectedPosition) {
                    benchStatus.swapPosition = -1;
                }

                // if no swap nor substitution then select for swap
                else if ((benchStatus.swapPosition == -1) && (benchStatus.substPosition == -1)) {
                    benchStatus.swapPosition = benchStatus.selectedPosition;
                }

                // swap or substitution
                else {

                    // substitution
                    if (benchStatus.substPosition != -1) {
                        int selectedPlayerIndex = benchStatus.team.playerIndexAtPosition(benchStatus.selectedPosition);
                        Player selectedPlayer = benchStatus.team.lineup.get(selectedPlayerIndex);

                        if (!selectedPlayer.checkState(STATE_SENT_OFF)) {
                            Coach coach = benchStatus.team.coach;
                            coach.status = Coach.Status.CALL;
                            coach.timer = 500;

                            benchStatus.team.substitutionsCount += 1;

                            int benchPlayerIndex = benchStatus.team.playerIndexAtPosition(benchStatus.substPosition);
                            Player benchPlayer = benchStatus.team.lineup.get(benchPlayerIndex);

                            selectedPlayer.setState(STATE_SUBSTITUTED);
                            benchPlayer.setState(STATE_REACH_TARGET);

                            benchStatus.substPosition = -1;

                            if (match.settings.commentary) {
                                int size = Assets.Commentary.playerSubstitution.size();
                                if (size > 0) {
                                    Assets.Commentary.playerSubstitution.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                                }
                            }

                            Collections.swap(benchStatus.team.lineup, selectedPlayerIndex, benchPlayerIndex);
                            selectedPlayer.swapTargetWith(benchPlayer);
                        }
                    }

                    // if swap
                    else {
                        Coach coach = benchStatus.team.coach;
                        coach.status = Coach.Status.SWAP;
                        coach.timer = 500;

                        int selectedPlayerIndex = benchStatus.team.playerIndexAtPosition(benchStatus.selectedPosition);
                        int otherPlayerIndex = benchStatus.team.playerIndexAtPosition(benchStatus.swapPosition);

                        Player selectedPlayer = benchStatus.team.lineup.get(selectedPlayerIndex);
                        Player otherPlayer = benchStatus.team.lineup.get(otherPlayerIndex);

                        selectedPlayer.setState(STATE_REACH_TARGET);
                        otherPlayer.setState(STATE_REACH_TARGET);

                        benchStatus.swapPosition = -1;

                        if (match.settings.commentary) {
                            int size = Assets.Commentary.playerSwap.size();
                            if (size > 0) {
                                Assets.Commentary.playerSwap.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                            }
                        }

                        Collections.swap(benchStatus.team.lineup, selectedPlayerIndex, otherPlayerIndex);
                        selectedPlayer.swapTargetWith(otherPlayer);
                    }
                }
            }
        }

        // go back to bench substitutions
        if (benchStatus.inputDevice.xReleased()) {
            benchStatus.selectedPosition = -1;

            // reset eventually pending swap or substitution
            benchStatus.swapPosition = -1;
            if (benchStatus.substPosition != -1) {
                Player player = benchStatus.team.lineup.get(benchStatus.substPosition);
                player.setState(STATE_BENCH_STANDING);

                benchStatus.selectedPosition = benchStatus.substPosition - TEAM_SIZE;
                benchStatus.substPosition = -1;
            }

            return newAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        }

        return checkCommonConditions();
    }
}
