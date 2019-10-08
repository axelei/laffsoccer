package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateIntro extends MatchState {

    private final int enterDelay = GLGame.VIRTUAL_REFRESH_RATE / 16;

    MatchStateIntro(MatchFsm fsm) {
        super(fsm);

        displayRosters = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.clock = 0;
        getFsm().matchCompleted = false;
        match.setIntroPositions();
        match.resetData();

        Assets.Sounds.introId = Assets.Sounds.intro.play(Assets.Sounds.volume / 100f);
        Assets.Sounds.crowdId = Assets.Sounds.crowd.play(Assets.Sounds.volume / 100f);
        Assets.Sounds.crowd.setLooping(Assets.Sounds.crowdId, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        match.enterPlayers(timer - 1, enterDelay);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.updatePlayers(false);
            match.playersPhoto();

            match.nextSubframe();

            sceneRenderer.save();

            if (timer < GLGame.VIRTUAL_REFRESH_RATE) {
                sceneRenderer.actionCamera.update(STILL);
            } else {
                sceneRenderer.actionCamera.update(FOLLOW_BALL);
            }

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (match.enterPlayersFinished(timer, enterDelay)) {
            if ((match.team[HOME].fire1Down() != null)
                    || (match.team[AWAY].fire1Down() != null)
                    || (timer >= 5 * GLGame.VIRTUAL_REFRESH_RATE)) {
                return newAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
            }
        }

        return checkCommonConditions();
    }
}
