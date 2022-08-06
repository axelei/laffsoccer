package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_CORNER_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FREE_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FULL_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FULL_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_GOAL;
import static com.ygames.ysoccer.match.MatchFsm.STATE_GOAL_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HALF_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HALF_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_KEEPER_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PENALTIES_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PENALTY_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_RED_CARD;
import static com.ygames.ysoccer.match.MatchFsm.STATE_THROW_IN_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_YELLOW_CARD;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateMain extends MatchState {

    private enum Event {
        KEEPER_STOP, GOAL, CORNER, GOAL_KICK, THROW_IN, FREE_KICK, PENALTY_KICK, YELLOW_CARD, RED_CARD, NONE
    }

    private Event event;

    MatchStateMain(MatchFsm fsm) {
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

        event = Event.NONE;
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
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();

                // crowd chants
                if (match.clock >= match.nextChant) {
                    if (match.chantSwitch) {
                        match.chantSwitch = false;
                        match.nextChant = match.clock + (6 + Assets.random.nextInt(6)) * 1000;
                    } else {
                        Assets.Sounds.chant.play(match.getSettings().crowdChants ? Assets.Sounds.volume / 100f : 0);
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

            match.ball.collisionFlagPosts();

            if (match.ball.collisionGoal()) {
                float elapsed = match.clock - match.lastGoalCollisionTime;
                if (elapsed > 100) {
                    match.stats[attackingTeam].overallShots += 1;
                    match.stats[attackingTeam].centeredShots += 1;
                    match.lastGoalCollisionTime = match.clock;
                }
            }

            // goal/corner/goal-kick
            if (match.ball.y * match.ball.ySide >= (Const.GOAL_LINE + Const.BALL_R)) {
                // goal
                if (EMath.isIn(match.ball.x, -Const.POST_X, Const.POST_X)
                        && (match.ball.z <= Const.CROSSBAR_H)) {
                    match.stats[attackingTeam].goals += 1;
                    match.stats[attackingTeam].overallShots += 1;
                    match.stats[attackingTeam].centeredShots += 1;
                    match.addGoal(attackingTeam);

                    event = Event.GOAL;
                    return;
                } else {
                    // corner/goal-kick
                    if (match.ball.ownerLast.team == match.team[defendingTeam]) {
                        event = Event.CORNER;
                        getFsm().cornerKickTeam = match.team[1 - match.ball.ownerLast.team.index];
                        return;
                    } else {
                        if (EMath.isIn(match.ball.x, -Const.GOAL_AREA_W / 2f, Const.GOAL_AREA_W / 2f)) {
                            match.stats[attackingTeam].overallShots += 1;
                        }
                        event = Event.GOAL_KICK;
                        getFsm().goalKickTeam = match.team[1 - match.ball.ownerLast.team.index];
                        return;
                    }
                }
            }

            // throw-ins
            if (Math.abs(match.ball.x) > (Const.TOUCH_LINE + Const.BALL_R)) {
                event = Event.THROW_IN;
                getFsm().throwInTeam = match.team[1 - match.ball.ownerLast.team.index];
                return;
            }

            // colliding tackles and fouls
            if (match.tackle == null) {

                for (int t = HOME; t <= AWAY; t++) {

                    // for each tackling player
                    for (int i = 0; i < TEAM_SIZE; i++) {
                        Player player = match.team[t].lineup.get(i);
                        if (player != null && player.checkState(STATE_TACKLE) && player.v > 50) {

                            // search near opponents
                            Team opponentTeam = match.team[1 - player.team.index];
                            Player opponent = opponentTeam.searchPlayerTackledBy(player);

                            if (opponent != null && !opponent.checkState(STATE_DOWN)) {
                                float strength = (4f + player.v / 260f) / 5f;
                                float angleDiff = EMath.angleDiff(player.a, opponent.a);
                                match.newTackle(player, opponent, strength, angleDiff);
                                Gdx.app.debug(player.shirtName, "tackles on " + opponent.shirtName + " at speed: " + player.v + " (strength = " + strength + ") and angle: " + angleDiff);
                            }
                        }
                    }
                }
            } else {
                if (EMath.dist(match.tackle.player.x, match.tackle.player.y, match.tackle.opponent.x, match.tackle.opponent.y) >= 8) {

                    // tackle is finished, eventually generate a foul
                    Player player = match.tackle.player;
                    Player opponent = match.tackle.opponent;
                    float angleDiff = match.tackle.angleDiff;

                    float hardness;

                    // back/side
                    if (angleDiff < 112.5f) {
                        hardness = match.tackle.strength * (0.7f + 0.01f * player.skills.tackling - 0.01f * opponent.skills.control);
                    }

                    // front
                    else {
                        hardness = match.tackle.strength * (0.9f + 0.01f * player.skills.tackling - 0.01f * opponent.skills.control);
                    }

                    float unfairness;

                    // back tackle
                    if (angleDiff < 67.5f) {
                        unfairness = (player.ballDistance < opponent.ballDistance) ? 0.8f : 0.9f;
                    }

                    // side tackle
                    else if (angleDiff < 112.5f) {
                        unfairness = (player.ballDistance < opponent.ballDistance) ? 0.2f : 0.8f;
                    }

                    // front tackle
                    else {
                        unfairness = (player.ballDistance < opponent.ballDistance) ? 0.3f : 0.9f;
                    }

                    Gdx.app.debug(player.shirtName, "tackles on " + opponent.shirtName + " finished, hardness: " + hardness + ", unfairness: " + unfairness);

                    if (Assets.random.nextFloat() < hardness) {
                        opponent.setState(STATE_DOWN);

                        if (Assets.random.nextFloat() < unfairness) {
                            match.newFoul(match.tackle.opponent.x, match.tackle.opponent.y, hardness, unfairness);
                            Gdx.app.debug(player.shirtName, "tackle on " + opponent.shirtName + " is a foul at: " + match.tackle.opponent.x + ", " + match.tackle.opponent.y
                                    + " direct shot: " + (match.foul.isDirectShot() ? "yes" : "no") + " yellow: " + match.foul.entailsYellowCard + " red: " + match.foul.entailsRedCard);
                        } else {
                            Gdx.app.debug(player.shirtName, "tackles on " + opponent.shirtName + " is probably not a foul");
                        }
                    } else {
                        Gdx.app.debug(opponent.shirtName, "avoids the tackle from " + player.shirtName);
                    }
                    match.tackle = null;
                }
            }

            if (match.foul != null) {
                if (match.foul.entailsRedCard) {
                    match.referee.addRedCard(match.foul.player);
                    match.stats[match.foul.player.team.index].redCards++;

                    event = Event.RED_CARD;
                } else if (match.foul.entailsYellowCard) {
                    match.referee.addYellowCard(match.foul.player);
                    match.stats[match.foul.player.team.index].yellowCards++;
                    if (match.referee.isSentOff(match.foul.player)) {
                        match.stats[match.foul.player.team.index].redCards++;
                    }

                    event = Event.YELLOW_CARD;
                } else if (match.foul.isPenalty()) {
                    event = Event.PENALTY_KICK;
                } else {
                    event = Event.FREE_KICK;
                }
                match.stats[match.foul.player.team.index].foulsConceded += 1;
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

            if ((match.subframe % GLGame.SUBFRAMES) == 0) {
                match.ball.updatePrediction();
            }

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        switch (event) {
            case KEEPER_STOP:
                return newAction(NEW_FOREGROUND, STATE_KEEPER_STOP);

            case GOAL:
                return newAction(NEW_FOREGROUND, STATE_GOAL);

            case CORNER:
                return newAction(NEW_FOREGROUND, STATE_CORNER_STOP);

            case GOAL_KICK:
                return newAction(NEW_FOREGROUND, STATE_GOAL_KICK_STOP);

            case THROW_IN:
                return newAction(NEW_FOREGROUND, STATE_THROW_IN_STOP);

            case RED_CARD:
                return newAction(NEW_FOREGROUND, STATE_RED_CARD);

            case YELLOW_CARD:
                return newAction(NEW_FOREGROUND, STATE_YELLOW_CARD);

            case FREE_KICK:
                return newAction(NEW_FOREGROUND, STATE_FREE_KICK_STOP);

            case PENALTY_KICK:
                return newAction(NEW_FOREGROUND, STATE_PENALTY_KICK_STOP);
        }

        switch (match.period) {

            case UNDEFINED:
                break;

            case FIRST_HALF:
                if ((match.clock > (match.length * 45f / 90f)) && match.periodIsTerminable()) {
                    return newAction(NEW_FOREGROUND, STATE_HALF_TIME_STOP);
                }
                break;

            case SECOND_HALF:
                if ((match.clock > match.length) && match.periodIsTerminable()) {

                    match.setResult(match.stats[HOME].goals, match.stats[AWAY].goals, Match.ResultType.AFTER_90_MINUTES);

                    if (match.competition.playExtraTime()) {
                        return newAction(NEW_FOREGROUND, STATE_EXTRA_TIME_STOP);
                    } else if (match.competition.playPenalties()) {
                        return newAction(NEW_FOREGROUND, STATE_PENALTIES_STOP);
                    } else {
                        return newAction(NEW_FOREGROUND, STATE_FULL_TIME_STOP);
                    }
                }
                break;

            case FIRST_EXTRA_TIME:
                if ((match.clock > (match.length * 105f / 90f)) && match.periodIsTerminable()) {
                    return newAction(NEW_FOREGROUND, STATE_HALF_EXTRA_TIME_STOP);
                }
                break;

            case SECOND_EXTRA_TIME:
                if ((match.clock > (match.length * 120f / 90f)) && match.periodIsTerminable()) {

                    match.setResult(match.stats[HOME].goals, match.stats[AWAY].goals, Match.ResultType.AFTER_EXTRA_TIME);

                    if (match.competition.playPenalties()) {
                        return newAction(NEW_FOREGROUND, STATE_PENALTIES_STOP);
                    } else {
                        return newAction(NEW_FOREGROUND, STATE_FULL_EXTRA_TIME_STOP);
                    }
                }
                break;
        }

        return checkCommonConditions();
    }
}
