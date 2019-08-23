package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_CORNER_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_FREE_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_FULL_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_FULL_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_GOAL;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_GOAL_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HALF_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HALF_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_KEEPER_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_MAIN;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTY_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_THROW_IN_STOP;

class MatchStateMain extends MatchState {

    private enum Event {
        KEEPER_STOP, GOAL, CORNER, GOAL_KICK, THROW_IN, FREE_KICK, PENALTY_KICK, NONE
    }

    private Event event;

    MatchStateMain(MatchFsm fsm) {
        super(STATE_MAIN, fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

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
                        match.nextChant = match.clock + (6 + Assets.random.nextInt(6)) * 1000;
                    } else {
                        Assets.Sounds.chant.play(match.settings.crowdChants ? Assets.Sounds.volume / 100f : 0);
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

            if (match.foul != null) {
                if (match.foul.isPenalty()) {
                    event = Event.PENALTY_KICK;
                } else {
                    event = Event.FREE_KICK;
                }
                match.stats[match.foul.player.team.index].foulsConceded += 1;
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
                        fsm.cornerKickTeam = match.team[1 - match.ball.ownerLast.team.index];
                        return;
                    } else {
                        event = Event.GOAL_KICK;
                        fsm.goalKickTeam = match.team[1 - match.ball.ownerLast.team.index];
                        return;
                    }
                }
            }

            // Throw-in
            if (Math.abs(match.ball.x) > (Const.TOUCH_LINE + Const.BALL_R)) {
                event = Event.THROW_IN;
                fsm.throwInTeam = match.team[1 - match.ball.ownerLast.team.index];
                return;
            }

            match.updatePlayers(true);
            match.findNearest();

            for (int t = HOME; t <= AWAY; t++) {
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

            matchRenderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        switch (event) {
            case KEEPER_STOP:
                fsm.pushAction(NEW_FOREGROUND, STATE_KEEPER_STOP);
                return;

            case GOAL:
                fsm.pushAction(NEW_FOREGROUND, STATE_GOAL);
                return;

            case CORNER:
                fsm.pushAction(NEW_FOREGROUND, STATE_CORNER_STOP);
                return;

            case GOAL_KICK:
                fsm.pushAction(NEW_FOREGROUND, STATE_GOAL_KICK_STOP);
                return;

            case THROW_IN:
                fsm.pushAction(NEW_FOREGROUND, STATE_THROW_IN_STOP);
                return;

            case FREE_KICK:
                fsm.pushAction(NEW_FOREGROUND, STATE_FREE_KICK_STOP);
                return;

            case PENALTY_KICK:
                fsm.pushAction(NEW_FOREGROUND, STATE_PENALTY_KICK_STOP);
                return;
        }

        switch (match.period) {

            case UNDEFINED:
                break;

            case FIRST_HALF:
                if ((match.clock > (match.length * 45 / 90)) && match.periodIsTerminable()) {
                    fsm.pushAction(NEW_FOREGROUND, STATE_HALF_TIME_STOP);
                    return;
                }
                break;

            case SECOND_HALF:
                if ((match.clock > match.length) && match.periodIsTerminable()) {

                    match.setResult(match.stats[HOME].goals, match.stats[AWAY].goals, Match.ResultType.AFTER_90_MINUTES);

                    switch (match.competition.type) {
                        case FRIENDLY:
                        case TEST_MATCH:
                        case LEAGUE:
                            fsm.pushAction(NEW_FOREGROUND, STATE_FULL_TIME_STOP);
                            return;

                        case CUP:
                            Cup cup = (Cup) match.competition;
                            if (cup.playExtraTime()) {
                                fsm.pushAction(NEW_FOREGROUND, STATE_EXTRA_TIME_STOP);
                            } else {
                                fsm.pushAction(NEW_FOREGROUND, STATE_FULL_TIME_STOP);
                            }
                            return;

                        case TOURNAMENT:
                            Tournament tournament = (Tournament) match.competition;
                            if (tournament.getRound().playExtraTime()) {
                                fsm.pushAction(NEW_FOREGROUND, STATE_EXTRA_TIME_STOP);
                            } else {
                                fsm.pushAction(NEW_FOREGROUND, STATE_FULL_TIME_STOP);
                            }
                            return;
                    }
                }
                break;

            case FIRST_EXTRA_TIME:
                if ((match.clock > (match.length * 105 / 90)) && match.periodIsTerminable()) {
                    fsm.pushAction(NEW_FOREGROUND, STATE_HALF_EXTRA_TIME_STOP);
                    return;
                }
                break;

            case SECOND_EXTRA_TIME:
                if ((match.clock > (match.length * 120 / 90)) && match.periodIsTerminable()) {

                    match.setResult(match.stats[HOME].goals, match.stats[AWAY].goals, Match.ResultType.AFTER_EXTRA_TIME);

                    fsm.pushAction(NEW_FOREGROUND, STATE_FULL_EXTRA_TIME_STOP);
                    return;
                }
                break;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(HOLD_FOREGROUND, STATE_PAUSE);
            return;
        }
    }
}
