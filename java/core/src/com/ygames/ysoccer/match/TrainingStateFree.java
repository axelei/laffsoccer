package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import java.util.Collections;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.ygames.ysoccer.match.ActionCamera.CF_BALL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.NORMAL;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.CROSSBAR_H;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;

class TrainingStateFree extends TrainingState {

    private Player lastTrained;

    TrainingStateFree(TrainingFsm fsm) {
        super(fsm);
        id = TrainingFsm.STATE_FREE;
    }

    @Override
    void entryActions() {
        super.entryActions();

        training.setIntroPositions();

        lastTrained = team.lineup.get(0);

        trainingRenderer.actionCamera.setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (training.subframe % GLGame.SUBFRAMES == 0) {
                team.lineup.get(0).updateAi();

                training.updateFrameDistance();
            }

            training.updatePlayers();
            training.findNearest();
            training.updateBall();

            ball.collisionFlagposts();
            ball.collisionGoal();
            ball.inFieldKeep();

            // goal/corner/goal-kick
            if (ball.y * ball.ySide >= (GOAL_LINE + BALL_R)) {
                ball.collisionNet();
                // goal
                if (Emath.isIn(ball.x, -POST_X, POST_X) && (ball.z <= CROSSBAR_H)) {
                } else {
                    // corner/goal-kick
                    ball.collisionJumpers();
                    ball.collisionNetOut();
                }
                if (ball.v == 0) {
                    ball.x = lastTrained.x + (BALL_R - 1) * Emath.cos(lastTrained.a);
                    ball.y = lastTrained.y + (BALL_R - 1) * Emath.sin(lastTrained.a);
                }
            }

            for (Player player : team.lineup) {
                if (player.inputDevice == player.ai && player != team.lineup.get(0)) {
                    player.watchBall();
                }
            }

            if ((training.subframe % GLGame.VIRTUAL_REFRESH_RATE) == 0) {
                training.ball.updatePrediction();
            }

            training.nextSubframe();

            training.save();

            trainingRenderer.updateCameraX(CF_BALL);
            trainingRenderer.updateCameraY(CF_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if ((ball.owner == null) && (ball.ownerLast != null)) {
            if (team.near1.ballDistance < ball.ownerLast.ballDistance && team.near1.ballDistance < 20) {
                if (team.near1 != team.lineup.get(0)) {
                    if ((ball.ownerLast.inputDevice != ball.ownerLast.ai)
                            && (team.near1.inputDevice == team.near1.ai)) {
                        if (team.near1.role == GOALKEEPER) {
                            // swap goalkeepers
                            Collections.swap(team.lineup, 0, team.near1.index);
                        } else {
                            // transfer input device
                            team.near1.setInputDevice(ball.ownerLast.inputDevice);
                            ball.ownerLast.setInputDevice(ball.ownerLast.ai);
                        }
                    }
                }
            }
        }

        if (ball.owner != null
                && ball.owner != team.lineup.get(0)
                && ball.owner.inputDevice != ball.owner.ai) {
            lastTrained = ball.owner;
        }
    }

    @Override
    void checkConditions() {
        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitTraining();
            return;
        }
    }
}
