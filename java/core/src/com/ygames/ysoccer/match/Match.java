package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.List;

public class Match implements Json.Serializable {

    public interface MatchListener {
        void quitMatch();
    }

    public static final int HOME = 0;
    public static final int AWAY = 1;
    public int teams[] = {-1, -1};
    public MatchStats[] stats = {new MatchStats(), new MatchStats()};
    public int[] result;
    public boolean includesExtraTime;
    public int[] resultAfter90;
    public int[] resultAfterPenalties;
    public int[] oldResult;
    public int qualified = -1;
    public String status = "";
    public boolean ended;

    public GLGame game;

    public MatchFsm fsm;

    Ball ball;
    public Team team[];
    public int benchSide; // 1 = home upside, -1 = home downside

    public MatchListener listener;
    public MatchSettings settings;
    public Competition competition;

    enum Period {UNDEFINED, FIRST_HALF, SECOND_HALF, FIRST_EXTRA_TIME, SECOND_EXTRA_TIME}

    float clock;
    int length;
    Period period;
    int coinToss;
    int kickOffTeam;
    float throwInX;
    float throwInY;

    List<Goal> goals;

    public int subframe;
    boolean chantSwitch;
    float nextChant;

    Recorder recorder;

    public Match() {
    }

    public void init(GLGame game, Team[] team, MatchSettings matchSettings, Competition competition) {
        this.game = game;
        this.team = team;
        this.settings = matchSettings;
        this.competition = competition;

        ball = new Ball(this);

        for (int t = HOME; t <= AWAY; t++) {
            team[t].index = t;
            team[t].beforeMatch(this);
        }

        fsm = new MatchFsm(this);

        team[HOME].side = 1 - 2 * Assets.random.nextInt(2); // -1 = up, 1 = down
        team[AWAY].side = -team[HOME].side;

        benchSide = 1 - 2 * Assets.random.nextInt(2);

        period = Period.FIRST_HALF;
        length = matchSettings.matchLength * 60 * 1000;
        coinToss = Assets.random.nextInt(2); // 0 = home begins, 1 = away begins
        kickOffTeam = coinToss;

        goals = new ArrayList<Goal>();

        recorder = new Recorder(this);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.readFields(this, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeValue("teams", teams);
        if (result != null) {
            json.writeValue("result", result);
        }
        if (resultAfter90 != null) {
            json.writeValue("resultAfter90", resultAfter90);
        }
        if (resultAfterPenalties != null) {
            json.writeValue("resultAfterPenalties", resultAfterPenalties);
        }
        json.writeValue("ended", ended);
    }

    public void setResult(int homeGoals, int awayGoals) {
        result = new int[]{homeGoals, awayGoals};
    }

    public void setResultAfter90(int homeGoals, int awayGoals) {
        resultAfter90 = new int[]{homeGoals, awayGoals};
    }

    public void setResultAfterPenalties(int homeGoals, int awayGoals) {
        resultAfterPenalties = new int[]{homeGoals, awayGoals};
    }

    public static int generateScore(Team teamA, Team teamB, boolean extraTimeResult) {

        double factor = (teamA.offenseRating() - teamB.defenseRating() + 300) / 60.0;

        int a, b;
        int[] goalsProbability = new int[7];
        for (int goals = 0; goals < 7; goals++) {
            a = Const.goalsProbability[(int) Math.floor(factor)][goals];
            b = Const.goalsProbability[(int) Math.ceil(factor)][goals];
            goalsProbability[goals] = (int) Math.round(a + (b - a) * (factor - Math.floor(factor)));
        }

        goalsProbability[6] = 1000;
        for (int goals = 0; goals <= 5; goals++) {
            goalsProbability[6] = goalsProbability[6] - goalsProbability[goals];
        }

        int r = (int) Math.ceil(1000 * Math.random());
        int sum = 0;
        int goals = -1;
        while (sum < r) {
            goals += 1;
            sum += goalsProbability[goals];
        }

        if (extraTimeResult) {
            return (int) Math.floor(goals / 3);
        } else {
            return goals;
        }
    }

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    void updateAi() {
        for (int t = HOME; t <= AWAY; t++) {
            if (team[t] != null) {
                team[t].updateLineupAi();
            }
        }
    }

    public void nextSubframe() {
        subframe = (subframe + 1) % Const.REPLAY_SUBFRAMES;
    }

    void updateBall() {
        float bouncing_speed = ball.update();
        if (bouncing_speed > 0) {
            // TODO
            // listener.bounceSound(Math.min(2 * bouncing_speed / Const.SECOND, 1) * settings.sfxVolume);
        }
        ball.inFieldKeep();
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

    boolean enterPlayersFinished(int timer, int enterDelay) {
        return timer / enterDelay > Const.TEAM_SIZE;
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

    void setStartingPositions() {
        int t = kickOffTeam;
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < Const.TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                player.tx = Pitch.startingPositions[k][i][0] * -team[t].side;
                player.ty = Pitch.startingPositions[k][i][1] * -team[t].side;
            }
            t = 1 - t;
        }
    }

    public void setPlayersState(int stateId, Player excluded) {
        for (int t = HOME; t <= AWAY; t++) {
            team[t].setPlayersState(stateId, excluded);
        }
    }

    void setStatesForGoal(Goal goal) {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < Const.TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                PlayerState playerState = player.fsm.getState();
                if (playerState.checkId(PlayerFsm.STATE_STAND_RUN) || playerState.checkId(PlayerFsm.STATE_KEEPER_POSITIONING)) {
                    player.tx = player.x;
                    player.ty = player.y;
                    if ((t == goal.player.team.index) && (player == goal.player)) {
                        player.fsm.setState(PlayerFsm.STATE_GOAL_SCORER);
                    } else if ((t == goal.player.team.index) && (Emath.dist(player.x, player.y, goal.player.x, goal.player.y) < 150 * Assets.random.nextInt(4))) {
                        player.fsm.setState(PlayerFsm.STATE_GOAL_MATE);
                    } else {
                        player.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
                    }
                }
            }
        }
    }

    void setStatesForOwnGoal(Goal goal) {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < Const.TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                PlayerState playerState = player.fsm.getState();
                if (playerState.checkId(PlayerFsm.STATE_STAND_RUN) || playerState.checkId(PlayerFsm.STATE_KEEPER_POSITIONING)) {
                    player.tx = player.x;
                    player.ty = player.y;
                    if (player == goal.player) {
                        player.setState(PlayerFsm.STATE_OWN_GOAL_SCORER);
                    } else {
                        player.setState(PlayerFsm.STATE_REACH_TARGET);
                    }
                }
            }
        }
    }

    void setPlayersTarget(float tx, float ty) {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < Const.TEAM_SIZE; i++) {
                team[t].lineup.get(i).setTarget(tx, ty);
            }
        }
    }

    void resetAutomaticInputDevices() {
        for (int t = HOME; t <= AWAY; t++) {
            team[t].assignAutomaticInputDevices(null);
        }
    }

    public void start() {
        fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_INTRO);
        fsm.pushAction(MatchFsm.ActionType.FADE_IN);
    }

    void findNearest() {
        team[HOME].findNearest();
        team[AWAY].findNearest();
    }

    void updateFrameDistance() {
        team[HOME].updateFrameDistance();
        team[AWAY].updateFrameDistance();
    }

    void updateBallZone() {
        ball.updateZone(ball.x, ball.y, ball.v, ball.a);
    }

    void updateTeamTactics() {
        team[HOME].updateTactics(false);
        team[AWAY].updateTactics(false);
    }

    int attackingTeam() {
        return (team[HOME].side == -ball.ySide) ? HOME : AWAY;
    }

    int getMinute() {

        // virtual minutes : 90 = clock : length
        int minute = (int) (clock * 90 / length);

        switch (period) {
            case FIRST_HALF:
                minute = Math.min(minute, 45);
                break;

            case SECOND_HALF:
                minute = Math.min(minute, 90);
                break;

            case FIRST_EXTRA_TIME:
                minute = Math.min(minute, 105);
                break;

            case SECOND_EXTRA_TIME:
                minute = Math.min(minute, 120);
                break;
        }

        return minute;
    }

    void addGoal(int attackingTeam) {
        Goal goal;
        if (team[attackingTeam] == ball.goalOwner.team) {
            ball.goalOwner.goals += 1;
            goal = new Goal(ball.goalOwner, getMinute(), Goal.Type.NORMAL);
        } else {
            goal = new Goal(ball.goalOwner, getMinute(), Goal.Type.OWN_GOAL);
        }

        goals.add(goal);

        //TODO buildScorerLists();
    }

    boolean periodIsTerminable() {
        // ball near the penalty area
        if ((Math.abs(ball.zoneX) <= 1) && (Math.abs(ball.zoneY) == 3)) {
            return false;
        }
        // ball going toward the goals
        if (Math.abs(ball.y) > Math.abs(ball.y0)) {
            return false;
        }
        return true;
    }

    void swapTeamSides() {
        team[HOME].side = -team[HOME].side;
        team[AWAY].side = -team[AWAY].side;
    }

    void quit() {
        listener.quitMatch();
    }
}
