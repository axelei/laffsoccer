package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

class MatchStateKeeperStop extends MatchState {

    private Player keeper;
    private Team keeperTeam;
    private Team opponentTeam;

    MatchStateKeeperStop(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_KEEPER_STOP;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = true;
        matchRenderer.displayBallOwner = true;
        matchRenderer.displayGoalScorer = false;
        matchRenderer.displayTime = true;
        matchRenderer.displayWindVane = true;
        matchRenderer.displayScore = false;
        matchRenderer.displayStatistics = false;
        matchRenderer.displayRadar = true;

        keeper = match.ball.holder;
        keeperTeam = match.team[keeper.team.index];
        opponentTeam = match.team[1 - keeper.team.index];

        match.stats[opponentTeam.index].overallShots += 1;
        match.stats[opponentTeam.index].centeredShots += 1;

        keeperTeam.assignAutomaticInputDevices(keeper);
        opponentTeam.assignAutomaticInputDevices(null);

        keeperTeam.setPlayersState(PlayerFsm.STATE_REACH_TARGET, keeper);
        opponentTeam.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);

        keeperTeam.updateTactics(true);
        opponentTeam.updateTactics(true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;

        while (timeLeft > GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.holder == null) {
            keeperTeam.setPlayersState(PlayerFsm.STATE_STAND_RUN, keeper);
            opponentTeam.setPlayersState(PlayerFsm.STATE_STAND_RUN, null);
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_MAIN);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            match.fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }
    }
}
