package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import java.util.Collections;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_TACTICS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_STANDING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchFormation extends MatchState {

    MatchStateBenchFormation(MatchFsm fsm) {
        super(fsm);

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
    }

    @Override
    void entryActions() {
        super.entryActions();
        displayBenchFormation = true;
        sceneRenderer.actionCamera.setTarget(getFsm().benchStatus.targetX, getFsm().benchStatus.targetY);
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

            sceneRenderer.actionCamera.update(REACH_TARGET);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        // change selected position
        if (getFsm().benchStatus.inputDevice.yMoved()) {
            getFsm().benchStatus.selectedPosition = EMath.rotate(getFsm().benchStatus.selectedPosition, -1, TEAM_SIZE - 1, getFsm().benchStatus.inputDevice.y1);
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (getFsm().benchStatus.inputDevice.fire1Down()) {

            // switch to tactics mode
            if (getFsm().benchStatus.selectedPosition == -1) {

                // reset eventually pending swap or substitution
                getFsm().benchStatus.swapPosition = -1;
                if (getFsm().benchStatus.substPosition != -1) {
                    Player player = getFsm().benchStatus.team.lineup.get(getFsm().benchStatus.substPosition);
                    player.setState(STATE_BENCH_STANDING);

                    getFsm().benchStatus.selectedPosition = getFsm().benchStatus.substPosition - TEAM_SIZE;
                    getFsm().benchStatus.substPosition = -1;
                }

                return newAction(NEW_FOREGROUND, STATE_BENCH_TACTICS);
            }

            // swap and substitutions
            else {
                // if already selected for swap then deselect
                if (getFsm().benchStatus.swapPosition == getFsm().benchStatus.selectedPosition) {
                    getFsm().benchStatus.swapPosition = -1;
                }

                // if no swap nor substitution then select for swap
                else if ((getFsm().benchStatus.swapPosition == -1) && (getFsm().benchStatus.substPosition == -1)) {
                    getFsm().benchStatus.swapPosition = getFsm().benchStatus.selectedPosition;
                }

                // swap or substitution
                else {
                    int i1 = getFsm().benchStatus.team.playerIndexAtPosition(getFsm().benchStatus.selectedPosition);
                    int i2;

                    // if substitution
                    Coach coach = getFsm().benchStatus.team.coach;
                    if (getFsm().benchStatus.substPosition != -1) {
                        coach.status = Coach.Status.CALL;
                        coach.timer = 500;

                        getFsm().benchStatus.team.substitutionsCount += 1;

                        i2 = getFsm().benchStatus.team.playerIndexAtPosition(getFsm().benchStatus.substPosition);
                        getFsm().benchStatus.team.lineup.get(i1).setState(STATE_OUTSIDE);
                        getFsm().benchStatus.team.lineup.get(i2).setState(STATE_REACH_TARGET);
                        getFsm().benchStatus.substPosition = -1;

                        if (match.settings.commentary) {
                            int size = Assets.Commentary.playerSubstitution.size();
                            if (size > 0) {
                                Assets.Commentary.playerSubstitution.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                            }
                        }
                    }

                    // if swap
                    else {
                        coach.status = Coach.Status.SWAP;
                        coach.timer = 500;

                        i2 = getFsm().benchStatus.team.playerIndexAtPosition(getFsm().benchStatus.swapPosition);
                        getFsm().benchStatus.team.lineup.get(i1).setState(STATE_REACH_TARGET);
                        getFsm().benchStatus.team.lineup.get(i2).setState(STATE_REACH_TARGET);
                        getFsm().benchStatus.swapPosition = -1;

                        if (match.settings.commentary) {
                            int size = Assets.Commentary.playerSwap.size();
                            if (size > 0) {
                                Assets.Commentary.playerSwap.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                            }
                        }
                    }

                    // swap players
                    Collections.swap(getFsm().benchStatus.team.lineup, i1, i2);

                    // swap positions
                    Player ply1 = getFsm().benchStatus.team.lineup.get(i1);
                    Player ply2 = getFsm().benchStatus.team.lineup.get(i2);

                    float tx = ply1.tx;
                    ply1.tx = ply2.tx;
                    ply2.tx = tx;

                    float ty = ply1.ty;
                    ply1.ty = ply2.ty;
                    ply2.ty = ty;
                }
            }
        }

        // go back to bench substitutions
        if (getFsm().benchStatus.inputDevice.xReleased()) {
            getFsm().benchStatus.selectedPosition = -1;

            // reset eventually pending swap or substitution
            getFsm().benchStatus.swapPosition = -1;
            if (getFsm().benchStatus.substPosition != -1) {
                Player player = getFsm().benchStatus.team.lineup.get(getFsm().benchStatus.substPosition);
                player.setState(STATE_BENCH_STANDING);

                getFsm().benchStatus.selectedPosition = getFsm().benchStatus.substPosition - TEAM_SIZE;
                getFsm().benchStatus.substPosition = -1;
            }

            return newAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        }

        return checkCommonConditions();
    }
}
