package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.framework.EMath.rotate;
import static com.ygames.ysoccer.match.Const.BALL_PREDICTION;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.BENCH_X;
import static com.ygames.ysoccer.match.Const.BENCH_Y_DOWN;
import static com.ygames.ysoccer.match.Const.BENCH_Y_UP;
import static com.ygames.ysoccer.match.Const.GOAL_AREA_H;
import static com.ygames.ysoccer.match.Const.GOAL_AREA_W;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Const.isInsideDirectShotArea;
import static com.ygames.ysoccer.match.Match.PenaltyState.SCORED;
import static com.ygames.ysoccer.match.Match.PenaltyState.TO_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_SITTING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_GOAL_MATE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_GOAL_SCORER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OWN_GOAL_SCORER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PHOTO;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SENT_OFF;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SUBSTITUTED;

public class Match extends Scene implements Json.Serializable {

    public enum ResultType {AFTER_90_MINUTES, AFTER_EXTRA_TIME, AFTER_PENALTIES}

    public interface MatchListener {
        void quitMatch(boolean matchCompleted);
    }

    public static final int HOME = 0;
    public static final int AWAY = 1;
    public final int[] teams = {-1, -1};
    public MatchStats[] stats = {new MatchStats(), new MatchStats()};
    public Scorers scorers;
    Referee referee;
    public int[] resultAfter90;
    public int[] resultAfterExtraTime;
    public int[] resultAfterPenalties;

    Ball ball;
    float lastGoalCollisionTime;

    public final Team[] team;
    int benchSide; // 1 = home upside, -1 = home downside

    public MatchListener listener;
    public Competition competition;

    enum Period {UNDEFINED, FIRST_HALF, SECOND_HALF, FIRST_EXTRA_TIME, SECOND_EXTRA_TIME, PENALTIES}

    public float clock;
    int length;
    Period period;
    int coinToss;
    int kickOffTeam;
    int penaltyKickingTeam;

    Tackle tackle;
    Foul foul;
    List<Goal> goals;

    enum PenaltyState {TO_KICK, SCORED, MISSED}

    final ArrayList<Penalty>[] penalties = new ArrayList[2];
    Penalty penalty;
    Player penaltyScorer;

    class Penalty {
        PenaltyState state;
        final Player kicker;
        final Player keeper;
        final int side;

        Penalty(Player kicker, Player keeper, int side) {
            this.state = TO_KICK;
            this.kicker = kicker;
            this.keeper = keeper;
            this.side = side;
        }

        void setState(PenaltyState state) {
            this.state = state;
        }
    }

    boolean chantSwitch;
    float nextChant;

    Recorder recorder;

    public Match() {
        team = new Team[2];
    }

    public MatchFsm getFsm() {
        return (MatchFsm) fsm;
    }

    public MatchSettings getSettings() {
        return (MatchSettings) settings;
    }

    public void setTeam(int side, Team team) {
        this.team[side] = team;
        this.team[side].index = side;
    }

    public void init(GLGame game, MatchSettings matchSettings, Competition competition) {
        this.game = game;
        this.settings = matchSettings;
        this.competition = competition;

        scorers = new Scorers();
        referee = new Referee();
        ball = new Ball(matchSettings);

        for (int t = HOME; t <= AWAY; t++) {
            team[t].index = t;
            team[t].beforeMatch(this);
        }

        fsm = new MatchFsm(this);

        team[HOME].setSide(1 - 2 * Assets.random.nextInt(2)); // -1 = up, 1 = down
        team[AWAY].setSide(-team[HOME].side);

        benchSide = 1 - 2 * Assets.random.nextInt(2);

        period = Period.FIRST_HALF;
        length = matchSettings.matchLength * 60 * 1000;
        coinToss = Assets.random.nextInt(2); // 0 = home begins, 1 = away begins
        kickOffTeam = coinToss;

        goals = new ArrayList<>();
        penalties[HOME] = new ArrayList<>();
        penalties[AWAY] = new ArrayList<>();

        recorder = new Recorder(this);
        pointOfInterest = new Vector2();
    }

    void createPenalty(Player player, Player keeper, int side) {
        this.penalty = new Penalty(player, keeper, side);
    }

    void addPenalties(int n) {
        for (int t = HOME; t <= AWAY; t++) {
            int newSize = penalties[t].size() + n;
            while (penalties[t].size() < newSize) {
                Player kicker = team[t].lineupAtPosition(team[t].kickerIndex);
                Player keeper = team[1 - t].lineupAtPosition(0);
                if (!kicker.checkState(STATE_SUBSTITUTED) && !kicker.checkState(STATE_SENT_OFF)) {
                    penalties[t].add(new Penalty(kicker, keeper, -1));
                }
                team[t].kickerIndex = rotate(team[t].kickerIndex, 0, team[t].lineup.size() - 1, -1);
            }
        }
    }

    void nextPenalty() {
        this.penalty = null;
        for (Penalty penalty : penalties[penaltyKickingTeam]) {
            if (penalty.state == TO_KICK) {
                this.penalty = penalty;
                return;
            }
        }
    }

    int penaltyGoals(int t) {
        int goals = 0;
        for (Penalty penalty : penalties[t]) {
            if (penalty.state == SCORED) goals++;
        }
        return goals;
    }

    int penaltiesScore(int t) {
        int s = 0;
        for (Penalty penalty : penalties[t]) {
            if (penalty.state == SCORED) {
                s++;
            }
        }
        return s;
    }

    int penaltiesPotentialScore(int t) {
        int s = 0;
        for (Penalty penalty : penalties[t]) {
            if (penalty.state == SCORED || penalty.state == TO_KICK) {
                s++;
            }
        }
        return s;
    }

    int penaltiesLeft(int t) {
        int s = 0;
        for (Penalty penalty : penalties[t]) {
            if (penalty.state == TO_KICK) {
                s++;
            }
        }
        return s;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.readFields(this, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeValue("teams", teams);
        if (resultAfter90 != null) {
            json.writeValue("resultAfter90", resultAfter90);
        }
        if (resultAfterExtraTime != null) {
            json.writeValue("resultAfterExtraTime", resultAfterExtraTime);
        }
        if (resultAfterPenalties != null) {
            json.writeValue("resultAfterPenalties", resultAfterPenalties);
        }
    }

    public int[] getResult() {
        return resultAfterExtraTime != null ? resultAfterExtraTime : resultAfter90;
    }

    public void setResult(int homeGoals, int awayGoals, ResultType resultType) {
        switch (resultType) {
            case AFTER_90_MINUTES:
                resultAfter90 = new int[]{homeGoals, awayGoals};
                break;
            case AFTER_EXTRA_TIME:
                resultAfterExtraTime = new int[]{homeGoals, awayGoals};
                break;
            case AFTER_PENALTIES:
                resultAfterPenalties = new int[]{homeGoals, awayGoals};
                break;
        }
    }

    public boolean isEnded() {
        return getResult() != null;
    }

    public static int generateGoals(Team teamFor, Team teamAgainst, boolean extraTimeResult) {

        double factor = (teamFor.offenseRating() - teamAgainst.defenseRating() + 300) / 60.0;

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
            return (int) Math.floor(goals / 3f);
        } else {
            return goals;
        }
    }

    void updateAi() {
        for (int t = HOME; t <= AWAY; t++) {
            if (team[t] != null) {
                team[t].updateLineupAi();
            }
        }
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
        if (penaltyScorer != null && player != null && player != penaltyScorer) {
            penaltyScorer = null;
        }
        ball.owner = player;
        if (player != null) {
            ball.ownerLast = player;
            if (updateGoalOwner) {
                ball.goalOwner = player;
            }
        }
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

    void updateCoaches() {
        team[HOME].coach.update();
        team[AWAY].coach.update();
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
            if (i < TEAM_SIZE) {
                player.setState(STATE_OUTSIDE);

                player.x = Const.TOUCH_LINE + 80;
                player.y = 10 * team.side + 20;
                player.z = 0;

                player.tx = player.x;
                player.ty = player.y;
            } else {
                player.x = BENCH_X;
                if ((1 - 2 * team.index) == benchSide) {
                    player.y = BENCH_Y_UP + 14 * (i - TEAM_SIZE) + 46;
                } else {
                    player.y = BENCH_Y_DOWN + 14 * (i - TEAM_SIZE) + 46;
                }
                player.setState(STATE_BENCH_SITTING);
            }
        }

        team.coach.x = BENCH_X;
        if ((1 - 2 * team.index) == team.match.benchSide) {
            team.coach.y = BENCH_Y_UP + 32;
        } else {
            team.coach.y = BENCH_Y_DOWN + 32;
        }
    }

    void enterPlayers(int timer, int enterDelay) {
        if ((timer % enterDelay) == 0 && (timer / enterDelay <= TEAM_SIZE)) {
            for (int t = HOME; t <= AWAY; t++) {
                for (int i = 0; i < TEAM_SIZE; i++) {
                    if (i < timer / enterDelay) {
                        Player player = team[t].lineup.get(i);
                        if (player.getState().checkId(STATE_OUTSIDE)) {
                            player.setState(STATE_REACH_TARGET);
                            player.tx = Const.TOUCH_LINE - 300 + 7 * i;
                            player.ty = 60 * team[t].side - ((i % 2 == 0) ? 20 : 0);
                        }
                    }
                }
            }
        }
    }

    boolean enterPlayersFinished(int timer, int enterDelay) {
        return timer / enterDelay > TEAM_SIZE;
    }

    void playersPhoto() {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                if (player.getState().checkId(STATE_IDLE)) {
                    player.setState(STATE_PHOTO);
                }
            }
        }
    }

    void setStartingPositions() {
        int t = kickOffTeam;
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                player.tx = Pitch.startingPositions[k][i][0] * -team[t].side;
                player.ty = Pitch.startingPositions[k][i][1] * -team[t].side;
            }
            t = 1 - t;
        }
    }

    public void setPlayersState(PlayerFsm.Id stateId, Player excluded) {
        for (int t = HOME; t <= AWAY; t++) {
            team[t].setPlayersState(stateId, excluded);
        }
    }

    void setLineupState(PlayerFsm.Id stateId) {
        for (int t = HOME; t <= AWAY; t++) {
            team[t].setLineupState(stateId);
        }
    }

    void setStatesForGoal(Goal goal) {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                PlayerState playerState = player.getState();
                if (playerState.checkId(STATE_STAND_RUN) || playerState.checkId(STATE_KEEPER_POSITIONING)) {
                    player.tx = player.x;
                    player.ty = player.y;
                    if ((t == goal.player.team.index) && (player == goal.player)) {
                        player.setState(STATE_GOAL_SCORER);
                    } else if ((t == goal.player.team.index) && (EMath.dist(player.x, player.y, goal.player.x, goal.player.y) < 150 * Assets.random.nextInt(4))) {
                        player.setState(STATE_GOAL_MATE);
                    } else {
                        player.setState(STATE_REACH_TARGET);
                    }
                }
            }
        }
    }

    void setStatesForOwnGoal(Goal goal) {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = team[t].lineup.get(i);
                PlayerState playerState = player.getState();
                if (playerState.checkId(STATE_STAND_RUN) || playerState.checkId(STATE_KEEPER_POSITIONING)) {
                    player.tx = player.x;
                    player.ty = player.y;
                    if (player == goal.player) {
                        player.setState(STATE_OWN_GOAL_SCORER);
                    } else {
                        player.setState(STATE_REACH_TARGET);
                    }
                }
            }
        }
    }

    void setPlayersTarget(float tx, float ty) {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                team[t].lineup.get(i).setTarget(tx, ty);
            }
        }
    }

    void setLineupTarget(float tx, float ty) {
        for (int t = HOME; t <= AWAY; t++) {
            team[t].setLineupTarget(tx, ty);
        }
    }

    void resetAutomaticInputDevices() {
        for (int t = HOME; t <= AWAY; t++) {
            team[t].assignAutomaticInputDevices(null);
        }
    }

    void findNearest() {
        team[HOME].findNearest();
        team[AWAY].findNearest();
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
        team[HOME].updateFrameDistance();
        team[AWAY].updateFrameDistance();
    }

    void updateBallZone() {
        ball.updateZone(ball.x, ball.y);
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
        if (ball.goalOwner == penaltyScorer) {
            competition.addGoal(ball.goalOwner);
            goal = new Goal(ball.goalOwner, getMinute(), Goal.Type.PENALTY);
        } else if (team[attackingTeam] == ball.goalOwner.team) {
            competition.addGoal(ball.goalOwner);
            goal = new Goal(ball.goalOwner, getMinute(), Goal.Type.NORMAL);
        } else {
            goal = new Goal(ball.goalOwner, getMinute(), Goal.Type.OWN_GOAL);
        }

        goals.add(goal);

        scorers.build(goals);
    }

    boolean periodIsTerminable() {
        // ball near the penalty area
        if ((Math.abs(ball.zoneY) == 3)) {
            return false;
        }
        // ball going toward the goals
        if ((Math.abs(ball.y) > (GOAL_LINE / 4f))
                && (Math.abs(ball.y) > Math.abs(ball.y0))) {
            return false;
        }
        return true;
    }

    void swapTeamSides() {
        team[HOME].setSide(-team[HOME].side);
        team[AWAY].setSide(-team[AWAY].side);
    }

    @Override
    void quit() {
        Assets.Sounds.chant.stop();
        Assets.Sounds.crowd.stop();
        Assets.Sounds.end.stop();
        Assets.Sounds.homeGoal.stop();
        Assets.Sounds.intro.stop();
        Assets.Commentary.stop();

        listener.quitMatch(getFsm().matchCompleted);
    }

    // returns an integer from 0 to 9
    int getRank() {
        int matchRank = (int) ((team[HOME].getRank() + 2 * team[AWAY].getRank()) / 3);
        return (competition.type == Competition.Type.FRIENDLY) ? matchRank : (matchRank + 1);
    }

    void newTackle(Player player, Player opponent, float strength, float angleDiff) {
        tackle = new Tackle();
        tackle.time = clock;
        tackle.position = new Vector2(player.x, player.y);
        tackle.player = player;
        tackle.opponent = opponent;
        tackle.strength = strength;
        tackle.angleDiff = angleDiff;
    }

    void newFoul(float x, float y, float hardness, float unfairness) {
        foul = new Foul();
        foul.time = tackle.time;
        foul.position = new Vector2(x, y);
        foul.player = tackle.player;
        foul.opponent = tackle.opponent;
        float r = Assets.random.nextFloat();
        foul.entailsYellowCard = r < EMath.pow(hardness * unfairness, 2);
        foul.entailsRedCard = r < EMath.pow(hardness * unfairness, 5);
    }

    class Foul {
        float time;
        public Vector2 position;
        public Player player;
        public Player opponent;
        public boolean entailsYellowCard;
        public boolean entailsRedCard;

        public boolean isPenalty() {
            return (Math.abs(position.x) < Const.PENALTY_AREA_W / 2f)
                    &&
                    EMath.isIn(
                            position.y,
                            player.team.side * (Const.GOAL_LINE - Const.PENALTY_AREA_H),
                            player.team.side * Const.GOAL_LINE
                    );
        }

        boolean isDirectShot() {
            return isInsideDirectShotArea(position.x, position.y, player.team.side);
        }

        boolean isNearOwnGoal() {
            return Math.abs(ball.x) < (GOAL_AREA_W / 2f + 50)
                    && EMath.isIn(ball.y,
                    -player.team.side * (GOAL_LINE - GOAL_AREA_H - 50),
                    -player.team.side * GOAL_LINE
            );
        }
    }

    class Tackle {
        float time;
        Vector2 position;
        public Player player;
        public Player opponent;
        float strength;
        float angleDiff;
    }

    public Referee getReferee() {
        return referee;
    }
}
