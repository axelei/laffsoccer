package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.WARP;
import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_EXIT;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;

class MatchStateBenchEnter extends MatchState {

    private float cameraX;
    private float cameraY;

    MatchStateBenchEnter(MatchFsm fsm) {
        super(STATE_BENCH_ENTER, fsm);
    }

    @Override
    void entryActions() {

        fsm.benchStatus.oldTargetX = matchRenderer.vcameraX[match.subframe] - CENTER_X + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        fsm.benchStatus.oldTargetY = matchRenderer.vcameraY[match.subframe] - CENTER_Y + matchRenderer.screenHeight / (2 * matchRenderer.zoom / 100f);

        fsm.benchStatus.selectedPosition = -1;
        fsm.benchStatus.substPosition = -1;

        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = match.team[t].lineup.get(i);
                if (match.team[t].usesAutomaticInputDevice()) {
                    player.setInputDevice(player.ai);
                }
                player.setState(STATE_REACH_TARGET);
            }
        }

        cameraX = matchRenderer.actionCamera.x;
        cameraY = matchRenderer.actionCamera.y;
        matchRenderer.actionCamera.setTarget(fsm.benchStatus.targetX, fsm.benchStatus.targetY);
        matchRenderer.actionCamera.setLimited(false, true);
        matchRenderer.actionCamera.setSpeedMode(WARP);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.updateBall();
            match.ball.inFieldKeep();

            match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            matchRenderer.updateCamera(REACH_TARGET);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {

        float dx = cameraX - matchRenderer.actionCamera.x;
        float dy = cameraY - matchRenderer.actionCamera.y;

        // TODO: replace with test on speed provided by camera
        if (dx <= 1 && dy <= 1) {
            Coach coach = fsm.benchStatus.team.coach;
            coach.status = Coach.Status.STAND;
            fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
            return;
        }

        cameraX = matchRenderer.actionCamera.x;
        cameraY = matchRenderer.actionCamera.y;

        if (fsm.benchStatus.inputDevice.xReleased() || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_EXIT);
            return;
        }
    }
}
