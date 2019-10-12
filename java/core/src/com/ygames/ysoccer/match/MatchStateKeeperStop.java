package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateKeeperStop extends MatchState {

    private Player keeper;
    private Team keeperTeam;
    private Team opponentTeam;

    MatchStateKeeperStop(MatchFsm fsm) {
        super(fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        checkBenchCall = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        keeper = match.ball.holder;
        keeperTeam = match.team[keeper.team.index];
        opponentTeam = match.team[1 - keeper.team.index];

        match.stats[opponentTeam.index].overallShots += 1;
        match.stats[opponentTeam.index].centeredShots += 1;

        keeperTeam.assignAutomaticInputDevices(keeper);
        opponentTeam.assignAutomaticInputDevices(null);

        keeperTeam.setPlayersState(STATE_REACH_TARGET, keeper);
        opponentTeam.setPlayersState(STATE_REACH_TARGET, null);

        keeperTeam.updateTactics(true);
        opponentTeam.updateTactics(true);

        match.setPointOfInterest(keeper.x, keeper.y);
    }

    @Override
    void onResume() {
        super.onResume();

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;

        while (timeLeft > GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.ball.updatePrediction();
                match.updateFrameDistance();
                match.updateAi();
            }

            match.updateBall();
            match.updatePlayers(true);
            match.findNearest();

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (keeper.checkState(STATE_STAND_RUN) || keeper.checkState(STATE_KEEPER_POSITIONING)) {
            keeperTeam.setPlayersState(STATE_STAND_RUN, keeper);
            opponentTeam.setPlayersState(STATE_STAND_RUN, null);
            return newAction(NEW_FOREGROUND, STATE_MAIN);
        }

        return checkCommonConditions();
    }
}
