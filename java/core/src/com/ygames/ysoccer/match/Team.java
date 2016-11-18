package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Team {

    public enum Type {CLUB, NATIONAL}

    public enum ControlMode {UNDEFINED, COMPUTER, PLAYER, COACH}

    public MatchCore match;

    public String name;
    public Type type;
    public String path;
    public String country;
    public String confederation;
    public String league;
    public String city;
    public String stadium;
    public Coach coach;

    public String tactics;

    public int kitIndex;
    public List<Kit> kits;
    public static final int MIN_KITS = 3;
    public static final int MAX_KITS = 5;

    public int index; // 0=HOME, 1=AWAY

    public List<Player> players;
    public List<Player> lineup;

    public ControlMode controlMode;
    public InputDevice inputDevice;
    int side; // -1=upside, 1=downside

    Player near1; // nearest to the ball
    Player bestDefender;

    public int won;
    public int drawn;
    public int lost;

    public int goalsFor;
    public int goalsAgainst;
    public int points;

    public Team() {
        controlMode = ControlMode.UNDEFINED;
    }

    public void setInputDevice(InputDevice inputDevice) {
        this.inputDevice = inputDevice;
    }

    public Player newPlayer() {
        if (players.size() == Const.FULL_TEAM) {
            return null;
        }

        Player player = new Player();
        player.name = "";
        player.shirtName = "";
        player.nationality = country;
        player.role = Player.Role.GOALKEEPER;

        // number
        for (int i = 1; i <= Const.FULL_TEAM; i++) {
            boolean used = false;
            for (Player ply : players) {
                if (Integer.parseInt(ply.number) == i) {
                    used = true;
                }
            }
            if (!used) {
                player.number = "" + i;
                break;
            }
        }
        player.hairColor = "BLACK";
        player.hairStyle = "SMOOTH_A";
        player.skinColor = Skin.Color.PINK;
        player.skills = new Player.Skills();
        players.add(player);
        return player;
    }

    void beforeMatch(MatchCore match) {
        this.match = match;
        lineup = new ArrayList<Player>();
        int lineupSize = Math.min(players.size(), Const.TEAM_SIZE + match.settings.benchSize);
        for (int i = 0; i < lineupSize; i++) {
            Player player = players.get(i);
            player.beforeMatch(match, this);
            player.index = i;
            lineup.add(player);
        }
    }

    void save(int subframe) {
        int len = lineup.size();
        for (int i = 0; i < len; i++) {
            lineup.get(i).save(subframe);
        }
    }

    void findNearest() {
        // step 1: search using frame_distance,
        // which takes into account both the speed of the player and the speed
        // and direction of the ball
        near1 = null;
        for (int i = 0; i < Const.TEAM_SIZE; i++) {
            Player player = lineup.get(i);

            // discard those players which cannot reach the ball in less than
            // BALL_PREDICTION frames
            if (player.frameDistance == Const.BALL_PREDICTION)
                continue;

            if ((near1 == null) || (player.frameDistance < near1.frameDistance)) {
                near1 = player;
            }
        }

        // step 2: if not found, repeat using pixel distance
        if (near1 == null) {
            near1 = lineup.get(0);
            for (int i = 1; i < Const.TEAM_SIZE; i++) {
                Player player = lineup.get(i);

                if (player.ballDistance < near1.ballDistance) {
                    near1 = player;
                }
            }
        }
    }

    private void findBestDefender() {
        bestDefender = null;

        if ((match.ball.owner != null) && (match.ball.owner.team != this)) {
            float attackerGoalDistance = Emath.dist(match.ball.owner.x, match.ball.owner.y, 0, -Const.GOAL_LINE * match.ball.owner.team.side);

            float bestDistance = 2 * Const.GOAL_LINE;
            for (int i = 1; i < Const.TEAM_SIZE; i++) {
                Player player = lineup.get(i);
                player.defendDistance = Emath.dist(player.x, player.y, match.ball.owner.x, match.ball.owner.y);

                float playerGoalDistance = Emath.dist(player.x, player.y, 0, -Const.GOAL_LINE * match.ball.owner.team.side);
                if ((playerGoalDistance < 0.95f * attackerGoalDistance)
                        && (player.defendDistance < bestDistance)) {
                    bestDefender = player;
                    bestDistance = player.defendDistance;
                }
            }
        }
    }

    void updateTactics(boolean relativeToCenter) {

        int ball_zone = 17 - side * match.ball.zoneX - 5 * side * match.ball.zoneY;

        if (relativeToCenter) {
            ball_zone = 17;
        }

        for (int i = 1; i < Const.TEAM_SIZE; i++) {

            Player player = lineup.get(i);

            int tx = Assets.tactics[getTacticsIndex()].target[i][ball_zone][0];
            int ty = Assets.tactics[getTacticsIndex()].target[i][ball_zone][1];

            player.tx = (1 - Math.abs(match.ball.mx)) * tx + Math.abs(match.ball.mx) * tx;
            player.ty = (1 - Math.abs(match.ball.my)) * ty + Math.abs(match.ball.my) * ty;

            player.tx = -side * player.tx;
            player.ty = -side * (player.ty - 4);
        }
    }

    void updateFrameDistance() {
        for (int i = 0; i < Const.TEAM_SIZE; i++) {
            lineup.get(i).updateFrameDistance();
        }
    }

    boolean updatePlayers(boolean limit) {
        findNearest();

        findBestDefender();

        boolean move = updateLineup(limit);

        return move;
    }

    private boolean updateLineup(boolean limit) {
        boolean move = false;

        int len = lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = lineup.get(i);
            if (player.update(match, limit)) {
                move = true;
            }
            player.think();
        }

        return move;
    }

    void updateLineupAi() {
        int len = lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = lineup.get(i);
            if (player.inputDevice == player.ai) {
                player.updateAi();
            }
        }
    }

    public int nonAiInputDevicesCount() {
        int n = 0;
        for (Player player : players) {
            if (player.inputDevice != player.ai) n++;
        }
        return n;
    }

    public boolean deletePlayer(Player player) {
        if (players.size() > Const.BASE_TEAM) {
            return players.remove(player);
        }
        return false;
    }

    public Kit newKit() {
        if (kits.size() == MAX_KITS) {
            return null;
        }
        Kit kit = new Kit();
        kits.add(kit);
        return kit;
    }

    public boolean deleteKit() {
        if (kits.size() > MIN_KITS) {
            return kits.remove(kits.get(kits.size() - 1));
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        Team t = (Team) obj;
        return path.equals(t.path);
    }

    public static class CompareByStats implements Comparator<Team> {

        @Override
        public int compare(Team o1, Team o2) {
            // by points
            if (o1.points != o2.points) {
                return o2.points - o1.points;
            }

            // by goals diff
            int diff1 = o1.goalsFor - o1.goalsAgainst;
            int diff2 = o2.goalsFor - o2.goalsAgainst;
            if (diff1 != diff2) {
                return diff2 - diff1;
            }

            // by scored goals
            if (o1.goalsFor != o2.goalsFor) {
                return o2.goalsFor - o1.goalsFor;
            }

            // by names
            return o1.name.compareTo(o2.name);
        }
    }

    public void releaseNonAiInputDevices() {
        for (Player player : players) {
            if (player.inputDevice != player.ai) {
                player.inputDevice.available = true;
                player.inputDevice = player.ai;
            }
        }
    }

    boolean usesAutomaticInputDevice() {
        return (controlMode == ControlMode.PLAYER) && (inputDevice != null);
    }

    void setPlayersState(int stateId, Player excluded) {
        for (int i = 0; i < Const.TEAM_SIZE; i++) {
            Player player = lineup.get(i);
            if (player != excluded) {
                player.fsm.setState(stateId);
            }
        }
    }

    void assignAutomaticInputDevices(Player receiver) {
        if (usesAutomaticInputDevice()) {
            for (int i = 0; i < Const.TEAM_SIZE; i++) {
                Player player = lineup.get(i);
                if (player == receiver) {
                    player.inputDevice = player.team.inputDevice;
                } else {
                    player.inputDevice = player.ai;
                }
            }
        }
    }

    void automaticInputDeviceSelection() {

        // search controlled player
        Player controlled = null;
        int len = lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = lineup.get(i);
            if (player.inputDevice != player.ai) {
                controlled = player;
            }
        }

        if (controlled == null) {

            // assign input device
            if (near1.fsm.getState().checkId(PlayerFsm.STATE_STAND_RUN)) {
                near1.inputDevice = inputDevice;
            }

        } else if (match.ball.owner == null) {

            // move input_device to nearest
            if ((controlled != near1)
                    && (controlled.frameDistance == Const.BALL_PREDICTION)) {

                if (controlled.fsm.getState().checkId(PlayerFsm.STATE_STAND_RUN)
                        && near1.fsm.getState().checkId(PlayerFsm.STATE_STAND_RUN)) {
                    near1.inputDevice = inputDevice;
                    controlled.inputDevice = controlled.ai;
                }
            }

        } else if (match.ball.owner.team.index == index) {

            // move input_device to ball owner
            if ((controlled != match.ball.owner)
                    && controlled.fsm.getState().checkId(PlayerFsm.STATE_STAND_RUN)
                    && near1.fsm.getState().checkId(PlayerFsm.STATE_STAND_RUN)) {
                match.ball.owner.inputDevice = inputDevice;
                controlled.inputDevice = controlled.ai;
            }

        } else {

            if ((bestDefender != null)
                    && (bestDefender != controlled)
                    && (bestDefender.defendDistance < 0.95f * controlled.defendDistance)
                    && controlled.fsm.getState().checkId(PlayerFsm.STATE_STAND_RUN)
                    && bestDefender.fsm.getState().checkId(PlayerFsm.STATE_STAND_RUN)) {
                bestDefender.inputDevice = inputDevice;
                controlled.inputDevice = controlled.ai;
            }
        }
    }

    InputDevice fire1Down() {
        if (usesAutomaticInputDevice()) {
            if (inputDevice.fire1Down()) {
                return inputDevice;
            }
        } else {
            for (Player player : lineup) {
                if ((player.inputDevice != player.ai) && player.inputDevice.fire1Down()) {
                    return player.inputDevice;
                }
            }
        }
        return null;
    }

    InputDevice fire1Up() {
        if (usesAutomaticInputDevice()) {
            if (inputDevice.fire1Up()) {
                return inputDevice;
            }
        } else {
            for (Player player : lineup) {
                if ((player.inputDevice != player.ai) && player.inputDevice.fire1Up()) {
                    return player.inputDevice;
                }
            }
        }
        return null;
    }

    public void updateStats(int goalsFor, int goalsAgainst, int pointsForAWin) {
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        if (goalsFor > goalsAgainst) {
            won += 1;
            points += pointsForAWin;
        } else if (goalsFor == goalsAgainst) {
            drawn += 1;
            points += 1;
        } else {
            lost += 1;
        }
    }

    public void generateScorers(int goals) {
        int teamFinishing = 0;
        for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
            Player ply = playerAtPosition(pos);

            teamFinishing += ply.skills.heading + ply.skills.shooting + ply.skills.finishing;

            switch (ply.role) {
                case RIGHT_WINGER:
                case LEFT_WINGER:
                    teamFinishing += 10;
                    break;

                case MIDFIELDER:
                    teamFinishing += 5;
                    break;

                case ATTACKER:
                    teamFinishing += 30;
                    break;
            }
        }

        for (int g = 1; g <= goals; g++) {
            int target = 1 + Emath.floor(teamFinishing * Math.random());
            int sum = teamFinishing;
            for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
                Player ply = playerAtPosition(pos);

                sum = sum - ply.skills.heading - ply.skills.shooting - ply.skills.finishing;

                switch (ply.role) {
                    case RIGHT_WINGER:
                    case LEFT_WINGER:
                        sum -= 10;
                        break;

                    case MIDFIELDER:
                        sum -= 5;
                        break;

                    case ATTACKER:
                        sum -= 30;
                        break;
                }

                if (sum < target) {
                    ply.goals += 1;
                    break;
                }
            }
        }
    }

    public Player playerAtPosition(int pos) {
        return playerAtPosition(pos, null);
    }

    public Player playerAtPosition(int pos, Tactics tcs) {
        if (tcs == null) {
            tcs = Assets.tactics[getTacticsIndex()];
        }
        if (pos < players.size()) {
            int ply = (pos < Const.TEAM_SIZE) ? Tactics.order[tcs.basedOn][pos] : pos;
            return players.get(ply);
        } else {
            return null;
        }
    }

    public int playerIndexAtPosition(int pos) {
        if (pos < players.size()) {
            int baseTactics = Assets.tactics[getTacticsIndex()].basedOn;
            return (pos < Const.TEAM_SIZE) ? Tactics.order[baseTactics][pos] : pos;
        } else {
            return -1;
        }
    }

    public int defenseRating() {
        int defense = 0;
        for (int p = 0; p < 11; p++) {
            defense += playerAtPosition(p).getDefenseRating();
        }
        return defense;
    }

    public int offenseRating() {
        int offense = 0;
        for (int p = 0; p < 11; p++) {
            offense += playerAtPosition(p).getOffenseRating();
        }
        return offense;
    }

    // TODO: replace with custom serialization
    public int getTacticsIndex() {
        for (int i = 0; i < Tactics.codes.length; i++) {
            if (Tactics.codes[i].equals(tactics)) {
                return i;
            }
        }
        return -1;
    }

    public static void kitAutoSelection(Team homeTeam, Team awayTeam) {
        for (int i = 0; i < homeTeam.kits.size(); i++) {
            for (int j = 0; j < awayTeam.kits.size(); j++) {
                Kit homeKit = homeTeam.kits.get(i);
                Kit awayKit = awayTeam.kits.get(j);
                float difference = Kit.colorDifference(homeKit, awayKit);
                if (difference > 45) {
                    homeTeam.kitIndex = i;
                    awayTeam.kitIndex = j;
                    return;
                }
            }
        }
    }
}
