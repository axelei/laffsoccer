package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_CORNER_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateCornerStop extends MatchState {

    private final Vector2 cornerPosition = new Vector2();

    MatchStateCornerStop(MatchFsm fsm) {
        super(fsm);

        displayControlledPlayer = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        if (match.team[HOME].side == -match.ball.ySide) {
            match.stats[HOME].cornersWon += 1;
        } else {
            match.stats[AWAY].cornersWon += 1;
        }

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        cornerPosition.set((Const.TOUCH_LINE - 12) * match.ball.xSide, (Const.GOAL_LINE - 12) * match.ball.ySide);

        // set the player targets relative to corner zone
        // even before moving the ball itself
        match.ball.updateZone(cornerPosition.x, cornerPosition.y);
        match.updateTeamTactics();
        match.team[HOME].lineup.get(0).setTarget(0, match.team[HOME].side * (Const.GOAL_LINE - 8));
        match.team[AWAY].lineup.get(0).setTarget(0, match.team[AWAY].side * (Const.GOAL_LINE - 8));

        match.resetAutomaticInputDevices();

        match.setPlayersState(STATE_REACH_TARGET, null);
    }

    @Override
    void onResume() {
        match.setPointOfInterest(cornerPosition);

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL)
                .setLimited(true, true);
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
            match.ball.setPosition(cornerPosition);
            match.ball.updatePrediction();

            return newAction(NEW_FOREGROUND, STATE_CORNER_KICK);
        }

        return checkCommonConditions();
    }
}
