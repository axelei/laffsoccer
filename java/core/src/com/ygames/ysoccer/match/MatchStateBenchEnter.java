package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateBenchEnter extends MatchState {

    private float cameraX;
    private float cameraY;

    MatchStateBenchEnter(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_BENCH_ENTER;
    }

    @Override
    void entryActions() {

        fsm.benchStatus.oldTargetX = matchRenderer.vcameraX[match.subframe] - CENTER_X + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        fsm.benchStatus.oldTargetY = matchRenderer.vcameraY[match.subframe] - CENTER_Y + matchRenderer.screenHeight / (2 * matchRenderer.zoom / 100f);

        fsm.benchStatus.selectedPos = -1;
        fsm.benchStatus.forSubs = -1;

        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = match.team[t].lineup.get(i);
                if (match.team[t].usesAutomaticInputDevice()) {
                    player.setInputDevice(player.ai);
                }
                player.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
            }
        }

        cameraX = matchRenderer.actionCamera.x;
        cameraY = matchRenderer.actionCamera.y;
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

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, fsm.benchStatus.targetX, false);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_WARP, fsm.benchStatus.targetY);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {

        float dx = cameraX - matchRenderer.actionCamera.x;
        float dy = cameraY - matchRenderer.actionCamera.y;

        if (dx <= 1 && dy <= 1) {
            // TODO
//            coach[bench_status.team.index].status = CS_STAND
//            coach[bench_status.team.index].x = BENCH_X + 8
//            game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_SUBSTITUTIONS)
//            Return
        }

        cameraX = matchRenderer.actionCamera.x;
        cameraY = matchRenderer.actionCamera.y;

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            // TODO
//            game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_EXIT)
//            return;
        }

    }
}
