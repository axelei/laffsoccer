package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.CROSSBAR_H;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;
import static com.ygames.ysoccer.match.TrainingFsm.Id.STATE_FREE;

class TrainingStateFree extends TrainingState {

    private Player lastTrained;

    TrainingStateFree(TrainingFsm fsm) {
        super(STATE_FREE, fsm);

        displayControlledPlayer = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        training.setIntroPositions();
        training.resetData();

        lastTrained = team.lineup.get(0);

        sceneRenderer.actionCamera.setLimited(true, true);
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

            if (ball.y * ball.ySide >= (GOAL_LINE + BALL_R)) {
                ball.collisionNet();

                if (!Emath.isIn(ball.x, -POST_X, POST_X) && (ball.z <= CROSSBAR_H)) {
                    ball.collisionJumpers();
                    ball.collisionNetOut();
                }
                if (ball.v == 0) {
                    resetBall();
                }
            }

            if ((Math.abs(ball.x) > TOUCH_LINE) && (ball.v == 0)) {
                resetBall();
            }

            for (Player player : team.lineup) {
                if (player.inputDevice == player.ai && player != team.lineup.get(0)) {
                    player.watchPosition(ball.x, ball.y);
                }
            }

            if ((training.subframe % GLGame.VIRTUAL_REFRESH_RATE) == 0) {
                training.ball.updatePrediction();
            }

            training.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        // control swap / keeper swap
        if ((ball.owner == null) && (ball.ownerLast != null)) {
            if (team.near1.ballDistance < ball.ownerLast.ballDistance && team.near1.ballDistance < 20) {
                if (team.near1 != team.lineup.get(0)) {
                    if ((ball.ownerLast.inputDevice != ball.ownerLast.ai)
                            && (team.near1.inputDevice == team.near1.ai)) {
                        if (team.near1.role == GOALKEEPER) {
                            // TODO enable after pointOfInterest is moved to Scene
                            /*
                            // swap goalkeepers
                            team.lineup.get(team.near1.index).setState(STATE_KEEPER_POSITIONING);
                            training.resetPosition(team.lineup.get(0));
                            team.lineup.get(0).setState(STATE_REACH_TARGET);
                            Collections.swap(team.lineup, 0, team.near1.index);
                            */
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

        if (training.team.fire2Down() != null) {
            resetBall();
        }
    }

    @Override
    void checkConditions() {
        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitTraining();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            return;
        }
    }

    private void resetBall() {
        ball.x = lastTrained.x + (BALL_R - 1) * Emath.cos(lastTrained.a);
        ball.y = lastTrained.y + (BALL_R - 1) * Emath.sin(lastTrained.a);
        ball.z = 0;
        ball.vMax = 0;
    }
}
