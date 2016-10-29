package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GlGame;

import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchCore {

    private GlGame game;

    private MatchFsm fsm;

    final Ball ball;
    public final Team team[];
    public int benchSide; // 1 = home upside, -1 = home downside

    public MatchRenderer renderer;
    public MatchSettings settings;

    final List<Goal> goals;

    public int subframe;

    public MatchCore(GlGame game, Team[] team, MatchSettings matchSettings) {
        this.game = game;
        this.team = team;
        this.settings = matchSettings;

        fsm = new MatchFsm(this);

        ball = new Ball(this);

        for (int t = HOME; t <= AWAY; t++) {
            team[t].index = t;
            team[t].beforeMatch(this);
        }

        team[HOME].side = 1 - 2 * Assets.random.nextInt(2); // -1 = up, 1 = down
        team[AWAY].side = -team[HOME].side;

        benchSide = 1 - 2 * Assets.random.nextInt(2);

        goals = new ArrayList<Goal>();
    }

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    public void nextSubframe() {
        subframe = (subframe + 1) % Const.REPLAY_SUBFRAMES;
    }

    boolean updatePlayers(boolean limit) {
        boolean move = false;
        if (team[HOME].updatePlayers(limit)) {
            move = true;
        }
        if (team[AWAY].updatePlayers(limit)) {
            move = true;
        }
        return move;
    }

    void save() {
        ball.save(subframe);
        team[HOME].save(subframe);
        team[AWAY].save(subframe);
    }

    void resetData() {
        updatePlayers(false);
        for (int f = 0; f < Const.REPLAY_SUBFRAMES; f++) {
            ball.save(f);
            team[HOME].save(f);
            team[AWAY].save(f);
        }
    }

    void setIntroPositions() {
        setIntroPositions(team[HOME]);
        setIntroPositions(team[AWAY]);
    }

    private void setIntroPositions(Team team) {
        int len = team.lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = team.lineup.get(i);
            if (i < Const.TEAM_SIZE) {
                player.fsm.setState(PlayerFsm.STATE_OUTSIDE);

                player.x = Const.TOUCH_LINE + 80;
                player.y = 10 * team.side + 20;
                player.z = 0;

                player.tx = player.x;
                player.ty = player.y;
            } else {
                player.x = Const.BENCH_X;
                if ((1 - 2 * team.index) == benchSide) {
                    player.y = Const.BENCH_Y_UP + 14 * (i - Const.TEAM_SIZE) + 46;
                } else {
                    player.y = Const.BENCH_Y_DOWN + 14 * (i - Const.TEAM_SIZE) + 46;
                }
                player.fsm.setState(PlayerFsm.STATE_BENCH_SITTING);
            }
        }
    }

    void enterPlayers(int timer, int enterDelay) {
        if ((timer % enterDelay) == 0 && (timer / enterDelay <= Const.TEAM_SIZE)) {
            for (int t = HOME; t <= AWAY; t++) {
                for (int i = 0; i < Const.TEAM_SIZE; i++) {
                    if (i < timer / enterDelay) {
                        Player player = team[t].lineup.get(i);
                        if (player.fsm.getState().checkId(PlayerFsm.STATE_OUTSIDE)) {
                            player.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
                            player.tx = Const.TOUCH_LINE - 300 + 7 * i;
                            player.ty = (60 + 2 * i) * team[t].side - ((i % 2 == 0) ? 20 : 0);
                        }
                    }
                }
            }
        }
    }

    void playersPhoto() {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < Const.TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                if (player.fsm.getState().checkId(PlayerFsm.STATE_IDLE)) {
                    player.fsm.setState(PlayerFsm.STATE_PHOTO);
                }
            }
        }
    }

    public void start() {
        fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_INTRO);
        fsm.pushAction(MatchFsm.ActionType.FADE_IN);
    }
}
