package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Const.BALL_PREDICTION;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.Id.STATE_FREE;

public class Training extends Scene {

    public interface TrainingListener {
        void quitTraining();
    }

    Ball ball;
    public Team[] team;

    public TrainingListener listener;

    public Training(Team trainingTeam) {
        this.team = new Team[2];

        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            team[t] = new Team();
        }
        team[HOME].coach = trainingTeam.coach;

        // split into two teams
        int keepers = 0;
        int players = 0;
        for (int i = 0; i < trainingTeam.players.size(); i++) {
            Player ply = trainingTeam.players.get(i);
            if (ply.role == GOALKEEPER) {
                ply.team = team[keepers % 2];
                team[keepers % 2].players.add(ply);
                keepers++;
            } else {
                ply.team = team[players % 2];
                team[players % 2].players.add(ply);
                players++;
            }
        }
    }

    public void init(GLGame game, SceneSettings sceneSettings) {
        this.game = game;
        this.settings = sceneSettings;

        ball = new Ball(sceneSettings);

        for (int t = HOME; t <= AWAY; t++) {
            team[t].index = t;
            team[t].beforeTraining(this);
            team[t].setSide(-1 + 2 * t);
        }

        fsm = new TrainingFsm(this);
    }

    public TrainingFsm getFsm() {
        return (TrainingFsm) fsm;
    }

    void updateBall() {
        float bouncing_speed = ball.update();
        if (bouncing_speed > 0) {
            Assets.Sounds.bounce.play(Math.min(2 * bouncing_speed / Const.SECOND, 1) * Assets.Sounds.volume / 100f);
        }
        ball.inFieldKeep();
    }

    void updatePlayers(boolean limit) {
        team[HOME].updateLineup(limit);
        team[AWAY].updateLineup(limit);
    }

    void updatePlayersSide() {
        for (int t = HOME; t <= AWAY; t++) {
            for (Player player : team[t].lineup) {
                player.side = (player.role == GOALKEEPER ? 1 : -1) * Emath.sgn(player.y);
            }
        }
    }

    void resetData() {
        updatePlayers(true);
        for (int f = 0; f < Const.REPLAY_SUBFRAMES; f++) {
            ball.save(f);
            team[HOME].save(f);
            team[AWAY].save(f);
        }
    }

    @Override
    public void start() {
        fsm.pushAction(NEW_FOREGROUND, STATE_FREE);
        fsm.pushAction(FADE_IN);
    }

    void findNearest() {
        team[HOME].findNearest(team[HOME].lineup.size());
        team[AWAY].findNearest(team[AWAY].lineup.size());
    }

    Player getNearestOfAll() {
        Player player0 = team[HOME].near1;
        Player player1 = team[AWAY].near1;

        int distance0 = Emath.min(player0.frameDistanceL, player0.frameDistance, player0.frameDistanceR);
        int distance1 = Emath.min(player1.frameDistanceL, player1.frameDistance, player1.frameDistanceR);

        if (distance0 == BALL_PREDICTION && distance1 == BALL_PREDICTION) return null;

        return distance0 < distance1 ? player0 : player1;
    }

    void updateFrameDistance() {
        team[HOME].updateFrameDistance(team[HOME].lineup.size());
        team[AWAY].updateFrameDistance(team[AWAY].lineup.size());
    }

    @Override
    void quit() {
        Assets.Sounds.chant.stop();

        listener.quitTraining();
    }
}
