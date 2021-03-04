package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_END_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFinalCelebration extends MatchState {

    enum Step {POSITIONING, CELEBRATING, QUITTING}

    private int side;
    private Step step;
    private Team winner;
    private Team runnerUp;
    private int celebrationEndingTime;

    MatchStateFinalCelebration(MatchFsm fsm) {
        super(fsm);

        displayWindVane = true;

        checkReplayKey = false;
        checkPauseKey = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        winner = match.competition.getFinalWinner();
        side = (winner == match.team[HOME]) ? -1 : 1;
        runnerUp = match.competition.getFinalRunnerUp();
        positionLineups();
        match.setPointOfInterest(0, side * GOAL_LINE);

        sceneRenderer.actionCamera
                .setMode(ActionCamera.Mode.REACH_TARGET)
                .setTarget(0, side * GOAL_LINE / 2f)
                .setSpeed(NORMAL)
                .setLimited(false, false);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            boolean move = match.updatePlayers(false);

            switch (step) {
                case POSITIONING:
                    if (readyToCelebrate()) {
                        winner.setLineupState(STATE_FINAL_CELEBRATION);
                        sceneRenderer.actionCamera.setTarget(0, side * GOAL_LINE);
                        step = Step.CELEBRATING;
                        Assets.Sounds.celebration.play(Assets.Sounds.volume / 100f);
                    }
                    break;

                case CELEBRATING:
                    if (!move) {
                        celebrationEndingTime = timer;
                        step = Step.QUITTING;
                    }
                    break;
            }


            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }


    @Override
    SceneFsm.Action[] checkConditions() {
        if (step == Step.QUITTING && (timer - celebrationEndingTime > SECOND)) {
            return newAction(NEW_FOREGROUND, STATE_END_POSITIONS);
        }

        return checkCommonConditions();
    }

    private void positionLineups() {

        List<Player> winners = new ArrayList<>(winner.lineup);

        step = Step.POSITIONING;
        PlayerCompareByX playerComparator = new PlayerCompareByX();
        Collections.sort(winners, playerComparator);

        float tx = -8 * winners.size();
        float ty = side * GOAL_LINE / 2f;
        for (Player player : winners) {
            player.isActive = true;
            player.setState(STATE_REACH_TARGET);
            player.setTarget(tx, ty);
            tx += 16;
        }

        runnerUp.setLineupTarget(Const.TOUCH_LINE + 80, 0);
        runnerUp.setLineupState(STATE_OUTSIDE);
    }

    private boolean readyToCelebrate() {
        for (Player player : winner.lineup) {
            if (player.checkState(STATE_REACH_TARGET)) {
                return false;
            }
        }
        return true;
    }

    static class PlayerCompareByX implements Comparator<Player> {

        @Override
        public int compare(Player player1, Player player2) {
            return EMath.sgn(player1.x - player2.x);
        }
    }
}
