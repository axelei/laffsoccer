package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import java.util.Collections;

import static com.ygames.ysoccer.match.ActionCamera.CF_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.CS_FAST;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_TACTICS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_STANDING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;

class MatchStateBenchFormation extends MatchState {

    MatchStateBenchFormation(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_BENCH_FORMATION;
    }

    @Override
    void entryActions() {
        super.entryActions();
        displayBenchFormation = true;
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

            match.save();

            matchRenderer.updateCameraX(CF_TARGET, CS_FAST, fsm.benchStatus.targetX, false);
            matchRenderer.updateCameraY(CF_TARGET, CS_FAST, fsm.benchStatus.targetY);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        // change selected position
        if (fsm.benchStatus.inputDevice.yMoved()) {
            fsm.benchStatus.selectedPosition = Emath.rotate(fsm.benchStatus.selectedPosition, -1, TEAM_SIZE - 1, fsm.benchStatus.inputDevice.y1);
        }
    }

    @Override
    void checkConditions() {

        if (fsm.benchStatus.inputDevice.fire1Down()) {

            // switch to tactics mode
            if (fsm.benchStatus.selectedPosition == -1) {

                // reset eventually pending swap or substitution
                fsm.benchStatus.swapPosition = -1;
                if (fsm.benchStatus.substPosition != -1) {
                    Player player = fsm.benchStatus.team.lineup.get(fsm.benchStatus.substPosition);
                    player.fsm.setState(STATE_BENCH_STANDING);

                    fsm.benchStatus.selectedPosition = fsm.benchStatus.substPosition - TEAM_SIZE;
                    fsm.benchStatus.substPosition = -1;
                }

                fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_TACTICS);
                return;
            }

            // swap and substitutions
            else {
                // if already selected for swap then deselect
                if (fsm.benchStatus.swapPosition == fsm.benchStatus.selectedPosition) {
                    fsm.benchStatus.swapPosition = -1;
                }

                // if no swap nor substitution then select for swap
                else if ((fsm.benchStatus.swapPosition == -1) && (fsm.benchStatus.substPosition == -1)) {
                    fsm.benchStatus.swapPosition = fsm.benchStatus.selectedPosition;
                }

                // swap or substitution
                else {
                    int i1 = fsm.benchStatus.team.playerIndexAtPosition(fsm.benchStatus.selectedPosition);
                    int i2;

                    // if substitution
                    Coach coach = fsm.benchStatus.team.coach;
                    if (fsm.benchStatus.substPosition != -1) {
                        coach.status = Coach.Status.CALL;
                        coach.timer = 500;

                        fsm.benchStatus.team.substitutionsCount += 1;

                        i2 = fsm.benchStatus.team.playerIndexAtPosition(fsm.benchStatus.substPosition);
                        fsm.benchStatus.team.lineup.get(i1).fsm.setState(STATE_OUTSIDE);
                        fsm.benchStatus.team.lineup.get(i2).fsm.setState(STATE_REACH_TARGET);
                        fsm.benchStatus.substPosition = -1;

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

                        i2 = fsm.benchStatus.team.playerIndexAtPosition(fsm.benchStatus.swapPosition);
                        fsm.benchStatus.team.lineup.get(i1).fsm.setState(STATE_REACH_TARGET);
                        fsm.benchStatus.team.lineup.get(i2).fsm.setState(STATE_REACH_TARGET);
                        fsm.benchStatus.swapPosition = -1;

                        if (match.settings.commentary) {
                            int size = Assets.Commentary.playerSwap.size();
                            if (size > 0) {
                                Assets.Commentary.playerSwap.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                            }
                        }
                    }

                    // swap players
                    Collections.swap(fsm.benchStatus.team.lineup, i1, i2);

                    // swap positions
                    Player ply1 = fsm.benchStatus.team.lineup.get(i1);
                    Player ply2 = fsm.benchStatus.team.lineup.get(i2);

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
        if (fsm.benchStatus.inputDevice.xReleased() || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            fsm.benchStatus.selectedPosition = -1;

            // reset eventually pending swap or substitution
            fsm.benchStatus.swapPosition = -1;
            if (fsm.benchStatus.substPosition != -1) {
                Player player = fsm.benchStatus.team.lineup.get(fsm.benchStatus.substPosition);
                player.fsm.setState(STATE_BENCH_STANDING);

                fsm.benchStatus.selectedPosition = fsm.benchStatus.substPosition - TEAM_SIZE;
                fsm.benchStatus.substPosition = -1;
            }

            fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
            return;
        }
    }
}
