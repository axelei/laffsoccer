package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

class MatchStateMain extends MatchState {

    private enum Event {
        KEEPER_STOP, GOAL, CORNER, GOAL_KICK, THROW_IN, NONE
    }

    private Event event;

    MatchStateMain(Match match) {
        super(match);
        id = MatchFsm.STATE_MAIN;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = true;
        match.renderer.displayBallOwner = true;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = true;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = true;
        match.renderer.displayControls = true;

        event = Event.NONE;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();

                // crowd chants
                if (match.clock >= match.nextChant) {
                    if (match.chantSwitch) {
                        match.chantSwitch = false;
                        match.nextChant = match.clock + (6 + match.random.nextInt(6)) * 1000;
                    } else {
                        match.listener.chantSound(match.settings.sfxVolume);
                        match.chantSwitch = true;
                        match.nextChant = match.clock + 8000;
                    }
                }

                match.clock += 1000.0f / GLGame.VIRTUAL_REFRESH_RATE;

                match.updateFrameDistance();

                if (match.ball.owner != null) {
                    match.stats[match.ball.owner.team.index].ballPossession += 1;
                }
            }

            match.updateBall();

            int attackingTeam = match.attackingTeam();
            int defendingTeam = 1 - attackingTeam;

            if (match.ball.holder != null) {
                event = Event.KEEPER_STOP;
                return;
            }

            match.ball.collisionFlagposts();

            if (match.ball.collisionGoal()) {
                match.stats[attackingTeam].centeredShots += 1;
            }

            // goal/corner/goal-kick
            if (match.ball.y * match.ball.ySide >= (Const.GOAL_LINE + Const.BALL_R)) {
                // goal
                if (Emath.isIn(match.ball.x, -Const.POST_X, Const.POST_X)
                        && (match.ball.z <= Const.CROSSBAR_H)) {
                    match.stats[attackingTeam].goals += 1;
                    match.stats[attackingTeam].centeredShots += 1;
                    match.addGoal(attackingTeam);

                    event = Event.GOAL;
                    return;
                } else {
                    // corner/goal-kick
                    if (match.ball.ownerLast.team == match.team[defendingTeam]) {
                        event = Event.CORNER;
                        return;
                    } else {
                        event = Event.GOAL_KICK;
                        return;
                    }
                }
            }

            // Throw-in
            if (Math.abs(match.ball.x) > (Const.TOUCH_LINE + Const.BALL_R)) {
                event = Event.THROW_IN;
                return;
            }

            match.updatePlayers(true);
            match.findNearest();

            for (int t = Match.HOME; t <= Match.AWAY; t++) {
                if (match.team[t].usesAutomaticInputDevice()) {
                    match.team[t].automaticInputDeviceSelection();
                }
            }

            match.updateBallZone();
            match.updateTeamTactics();

            if ((match.subframe % GLGame.VIRTUAL_REFRESH_RATE) == 0) {
                match.ball.updatePrediction();
            }

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        switch (event) {
            case KEEPER_STOP:
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_KEEPER_STOP);
                return;

            case GOAL:
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_GOAL);
                return;

            case CORNER:
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_CORNER_STOP);
                return;

            case GOAL_KICK:
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_GOAL_KICK_STOP);
                return;

            case THROW_IN:
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_THROW_IN_STOP);
                return;

            case NONE:
                ;
        }

        switch (match.period) {

            case UNDEFINED:
                break;

            case FIRST_HALF:
                if ((match.clock > (match.length * 45 / 90)) && match.periodIsTerminable()) {
                    match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HALF_TIME_STOP);
                    return;
                }
                break;

            case SECOND_HALF:
                if ((match.clock > match.length) && match.periodIsTerminable()) {
                    // Select menu.status
                    //
                    // Case MS_FRIENDLY
                    match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_FULL_TIME_STOP);
                    return;

                    // Case MS_COMPETITION
                    // Select competition.typ
                    //
                    // Case CT_LEAGUE
                    // league.set_result(stats[HOME].goal, stats[AWAY].goal)
                    // game_action_queue.push(AT_NEW_FOREGROUND,
                    // GM.MATCH_FULL_TIME_STOP)
                    // Return
                    //
                    // Case CT_CUP
                    // cup.set_result(stats[HOME].goal, stats[AWAY].goal,
                    // RT_AFTER_90_MINS)
                    // If cup.play_extra_time()
                    // game_action_queue.push(AT_NEW_FOREGROUND,
                    // GM.MATCH_EXTRA_TIME_STOP)
                    // Return
                    // Else
                    // game_action_queue.push(AT_NEW_FOREGROUND,
                    // GM.MATCH_FULL_TIME_STOP)
                    // Return
                    // EndIf
                    //
                    // End Select
                    //
                    // End Select
                }
                break;

            case FIRST_EXTRA_TIME:
                if ((match.clock > (match.length * 105 / 90))
                        && match.periodIsTerminable()) {
                    // return match.fsm.stateHalfExtraTimeStop;
                }
                break;

            case SECOND_EXTRA_TIME:
                if ((match.clock > (match.length * 120 / 90))
                        && match.periodIsTerminable()) {
                    // at present an extra time is only possible in cups...
                    // cup.set_result(stats[HOME].goal, stats[AWAY].goal,
                    // RT_AFTER_EXTRA_TIME)
                    // return match.fsm.stateFullExtraTimeStop;
                }
                break;

        }
    }
}
