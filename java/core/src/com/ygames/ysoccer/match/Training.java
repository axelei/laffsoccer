package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Const.BENCH_X;
import static com.ygames.ysoccer.match.Const.BENCH_Y_UP;
import static com.ygames.ysoccer.match.Const.PENALTY_AREA_H;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.STATE_FREE;

public class Training {

    public interface TrainingListener {
        void quitTraining();
    }

    public GLGame game;

    public TrainingFsm fsm;

    Ball ball;
    public Team team;

    public TrainingListener listener;
    public MatchSettings settings;

    public int subframe;

    public Training(Team team) {
        this.team = team;
    }

    public void init(GLGame game, MatchSettings matchSettings) {
        this.game = game;
        this.settings = matchSettings;

        ball = new Ball(matchSettings);

        team.beforeTraining(this);

        fsm = new TrainingFsm(this);

        team.side = -1;
    }

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    public void nextSubframe() {
        subframe = (subframe + 1) % Const.REPLAY_SUBFRAMES;
    }

    void updateBall() {
        float bouncing_speed = ball.update();
        if (bouncing_speed > 0) {
            Assets.Sounds.bounce.play(Math.min(2 * bouncing_speed / Const.SECOND, 1) * Assets.Sounds.volume / 100f);
        }
        ball.inFieldKeep();
    }

    boolean updatePlayers() {
        return team.updateLineup(true);
    }

    void setIntroPositions() {

        ball.setPosition(0, team.side * PENALTY_AREA_H, 0);

        int len = team.lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = team.lineup.get(i);
            player.setState(STATE_OUTSIDE);

            player.x = 18 * (-team.lineup.size() + 2 * i) + 8 * Emath.cos(70 * (player.number));
            player.y = team.side * (15 + 5 * (i % 2)) + 8 * Emath.sin(70 * (player.number));
            player.z = 0;

            player.tx = player.x;
            player.ty = player.y;

            // set states
            if (i == 0 && player.role == GOALKEEPER) {
                player.setState(STATE_KEEPER_POSITIONING);
            } else {
                player.setState(STATE_STAND_RUN);
            }
        }

        team.coach.x = BENCH_X;
        team.coach.y = BENCH_Y_UP + 32;
    }

    public void start() {
        fsm.pushAction(NEW_FOREGROUND, STATE_FREE);
        fsm.pushAction(FADE_IN);
    }

    void findNearest() {
        team.findNearest();
    }

    void updateFrameDistance() {
        team.updateFrameDistance();
    }

    void quit() {
        Assets.Sounds.chant.stop();

        listener.quitTraining();
    }
}
