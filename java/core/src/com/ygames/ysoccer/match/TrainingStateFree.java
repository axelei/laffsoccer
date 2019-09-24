package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.BENCH_X;
import static com.ygames.ysoccer.match.Const.BENCH_Y_UP;
import static com.ygames.ysoccer.match.Const.CROSSBAR_H;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.TrainingFsm.Id.STATE_FREE;

class TrainingStateFree extends TrainingState {

    private Player lastTrained;
    private Player[] keepers;

    TrainingStateFree(TrainingFsm fsm) {
        super(STATE_FREE, fsm);

        displayControlledPlayer = true;

        keepers = new Player[]{null, null};
    }

    @Override
    void entryActions() {
        super.entryActions();

        setIntroPositions();
        training.resetData();

        lastTrained = team[HOME].lineup.get(0);

        sceneRenderer.actionCamera.setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (training.subframe % GLGame.SUBFRAMES == 0) {
                training.updateFrameDistance();
            }

            training.updatePlayers(true);
            training.updatePlayersSide();
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

            for (int t = Match.HOME; t <= AWAY; t++) {
                for (Player player : team[t].lineup) {
                    if (player.inputDevice == player.ai) {
                        if (player.checkState(STATE_STAND_RUN)) {
                            player.watchPosition(ball.x, ball.y);
                        }
                        if (player.checkState(STATE_KEEPER_KICK_ANGLE)) {
                            player.setState(STATE_KEEPER_POSITIONING);
                            ball.a = player.angleToPoint(lastTrained.x, lastTrained.y);
                            ball.v = 240;
                        }
                    }
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
            Player nearestOfAll = training.getNearestOfAll();
            if (nearestOfAll != null
                    && nearestOfAll.ballDistance < ball.ownerLast.ballDistance
                    && nearestOfAll.ballDistance < 20) {
                if ((ball.ownerLast.inputDevice != ball.ownerLast.ai)
                        && (nearestOfAll.inputDevice == nearestOfAll.ai)) {
                    if (nearestOfAll.role == GOALKEEPER
                            && nearestOfAll != keepers[nearestOfAll.team.index]) {
                        // TODO enable after pointOfInterest is moved to Scene
//                        // swap goalkeepers
//                        resetTargetPosition(keepers[nearestOfAll.team.index]);
//                        keepers[nearestOfAll.team.index].setState(STATE_REACH_TARGET);
//                        nearestOfAll.setState(STATE_KEEPER_POSITIONING);
                    } else {
                        // transfer input device
                        nearestOfAll.setInputDevice(ball.ownerLast.inputDevice);
                        ball.ownerLast.setInputDevice(ball.ownerLast.ai);
                    }
                }
            }
        }

        if (ball.owner != null
                && ball.owner != keepers[HOME]
                && ball.owner != keepers[AWAY]
                && ball.owner.inputDevice != ball.owner.ai) {
            lastTrained = ball.owner;
        }

        if (lastTrained.inputDevice.fire2Down()) {
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

    private void setIntroPositions() {

        ball.setPosition(0, 0, 0);

        setIntroPositions(team[HOME]);
        setIntroPositions(team[AWAY]);

        team[HOME].coach.x = BENCH_X;
        team[HOME].coach.y = BENCH_Y_UP + 32;
    }

    private void setIntroPositions(Team team) {

        int len = team.lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = team.lineup.get(i);
            player.setState(STATE_OUTSIDE);

            Vector2 target = getDefaultTarget(player);
            player.x = target.x;
            player.y = target.y;
            player.z = 0;

            player.tx = target.x;
            player.ty = target.y;

            // set states
            if (player.role == GOALKEEPER && keepers[team.index] == null) {
                keepers[team.index] = player;
                player.setState(STATE_KEEPER_POSITIONING);
            } else {
                player.setState(STATE_STAND_RUN);
            }
        }
    }

    private Vector2 getDefaultTarget(Player player) {
        return new Vector2(
                -280 + 16 * (-player.team.lineup.size() + 2 * player.index) + 6 * Emath.cos(70 * (player.number)),
                player.team.side * (150 + 5 * (player.index % 2)) + 8 * Emath.sin(70 * (player.number))
        );
    }

    private void resetTargetPosition(Player player) {
        Vector2 target = getDefaultTarget(player);
        player.tx = target.x;
        player.ty = target.y;
    }
}
