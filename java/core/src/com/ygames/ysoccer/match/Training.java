package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.BALL_PREDICTION;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

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
            team[t].setSide(1 - 2 * t);
        }

        fsm = new TrainingFsm(this);
        pointOfInterest = new Vector2();
    }

    public TrainingFsm getFsm() {
        return (TrainingFsm) fsm;
    }

    void updateBall() {
        ball.update();
        updateBallOwner();
        ball.inFieldKeep();
    }

    private void updateBallOwner() {
        if (ball.owner != null) {
            if ((ball.owner.ballDistance > 11) || (ball.z > (Const.PLAYER_H + BALL_R))) {
                setBallOwner(null);
            }
        }
    }

    public void setBallOwner(Player player, boolean updateGoalOwner) {
        ball.owner = player;
        if (player != null) {
            ball.ownerLast = player;
            if (updateGoalOwner) {
                ball.goalOwner = player;
            }
        }
    }

    void updatePlayers(boolean limit) {
        team[HOME].updateLineup(limit);
        team[AWAY].updateLineup(limit);
    }

    void updateStates() {
        for (int t = HOME; t <= AWAY; t++) {
            for (Player player : team[t].lineup) {
                if (player.checkState(STATE_IDLE)) {
                    player.setState(STATE_STAND_RUN);
                }
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

    void findNearest() {
        team[HOME].findNearest(team[HOME].lineup.size());
        team[AWAY].findNearest(team[AWAY].lineup.size());
    }

    Player getNearestOfAll() {
        Player player0 = team[HOME].near1;
        Player player1 = team[AWAY].near1;

        int distance0 = EMath.min(player0.frameDistanceL, player0.frameDistance, player0.frameDistanceR);
        int distance1 = EMath.min(player1.frameDistanceL, player1.frameDistance, player1.frameDistanceR);

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
