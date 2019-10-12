package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.MatchFsm.STATE_GOAL_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoalKickStop extends MatchState {

    private final Vector2 goalKickPosition = new Vector2();

    MatchStateGoalKickStop(MatchFsm fsm) {
        super(fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        match.resetAutomaticInputDevices();
        match.setPlayersState(STATE_REACH_TARGET, null);

        Team goalKickTeam = match.team[1 - match.ball.ownerLast.team.index];
        Player goalKickKeeper = goalKickTeam.lineup.get(0);
        goalKickKeeper.tx = match.ball.x / 4;
        goalKickKeeper.ty = goalKickTeam.side * (Const.GOAL_LINE - 8);

        Team opponentTeam = match.team[1 - goalKickTeam.index];
        Player opponentKeeper = opponentTeam.lineup.get(0);
        opponentKeeper.tx = 0;
        opponentKeeper.ty = opponentTeam.side * (Const.GOAL_LINE - 8);

        goalKickTeam.updateTactics(true);
        opponentTeam.updateTactics(true);

        goalKickPosition.set(
                (Const.GOAL_AREA_W / 2f) * match.ball.xSide,
                (Const.GOAL_LINE - Const.GOAL_AREA_H) * match.ball.ySide
        );
    }

    @Override
    void onResume() {
        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL)
                .setLimited(true, true);

        match.setPointOfInterest(goalKickPosition);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();
            match.ball.collisionGoal();
            match.ball.collisionJumpers();
            match.ball.collisionNetOut();

            match.updatePlayers(true);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if ((match.ball.v < 5) && (match.ball.vz < 5)) {
            match.ball.setPosition(goalKickPosition.x, goalKickPosition.y, 0);
            match.ball.updatePrediction();

            return newAction(NEW_FOREGROUND, STATE_GOAL_KICK);
        }

        return checkCommonConditions();
    }
}
