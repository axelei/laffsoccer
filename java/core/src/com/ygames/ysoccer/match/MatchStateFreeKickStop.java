package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FREE_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFreeKickStop extends MatchState {

    private boolean allPlayersReachingTarget;
    private final ArrayList<Player> playersReachingTarget;

    MatchStateFreeKickStop(MatchFsm fsm) {
        super(fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        playersReachingTarget = new ArrayList<>();
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        if (match.settings.commentary) {
            int size = Assets.Commentary.foul.size();
            if (size > 0) {
                Assets.Commentary.foul.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
            }
        }

        // set the player targets relative to foul zone
        // even before moving the ball itself
        ball.updateZone(match.foul.position.x, match.foul.position.y);
        match.updateTeamTactics();
        match.foul.player.team.keepTargetDistanceFrom(match.foul.position);
        if (match.foul.isDirectShot()) {
            match.foul.player.team.setFreeKickBarrier();
        }
        match.team[HOME].lineup.get(0).setTarget(0, match.team[HOME].side * (Const.GOAL_LINE - 8));
        match.team[AWAY].lineup.get(0).setTarget(0, match.team[AWAY].side * (Const.GOAL_LINE - 8));

        match.resetAutomaticInputDevices();

        allPlayersReachingTarget = false;
        playersReachingTarget.clear();
    }

    @Override
    void onResume() {
        match.setPointOfInterest(match.foul.position);

        sceneRenderer.actionCamera.setMode(STILL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();

                allPlayersReachingTarget = true;
                for (int t = HOME; t <= AWAY; t++) {
                    for (int i = 0; i < TEAM_SIZE; i++) {
                        Player player = match.team[t].lineup.get(i);

                        // wait for tackle and down states to finish
                        if (player.checkState(STATE_TACKLE) || player.checkState(STATE_DOWN)) {
                            allPlayersReachingTarget = false;
                        } else if (!playersReachingTarget.contains(player)) {
                            player.setState(STATE_REACH_TARGET);
                            playersReachingTarget.add(player);
                        }
                    }
                }
            }

            match.updateBall();
            ball.inFieldKeep();
            ball.collisionFlagPosts();
            ball.collisionGoal();
            ball.collisionJumpers();
            ball.collisionNet();
            ball.collisionNetOut();

            match.updatePlayers(true);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (allPlayersReachingTarget) {
            ball.setPosition(match.foul.position.x, match.foul.position.y, 0);
            ball.updatePrediction();

            return newAction(NEW_FOREGROUND, STATE_FREE_KICK);
        }

        return checkCommonConditions();
    }
}
