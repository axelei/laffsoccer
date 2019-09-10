package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.framework.TeamList;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Player.Role.ATTACKER;
import static com.ygames.ysoccer.match.Player.Role.DEFENDER;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;
import static com.ygames.ysoccer.match.Player.Role.LEFT_BACK;
import static com.ygames.ysoccer.match.Player.Role.LEFT_WINGER;
import static com.ygames.ysoccer.match.Player.Role.MIDFIELDER;
import static com.ygames.ysoccer.match.Player.Role.RIGHT_BACK;
import static com.ygames.ysoccer.match.Player.Role.RIGHT_WINGER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

public class Team implements Json.Serializable {

    public enum Type {CLUB, NATIONAL, CUSTOM}

    public enum ControlMode {UNDEFINED, COMPUTER, PLAYER, COACH}

    private final int[] controlModeColors = {0x666666, 0x981E1E, 0x123FC8, 0x009BDC};

    public Match match;
    public Training training;

    public String name;
    public Type type;
    public String path;
    public String country;
    public String confederation;
    public String league;
    public String city;
    public String stadium;
    public Coach coach;

    public int tactics;

    public int kitIndex;
    public List<Kit> kits;
    public static final int MIN_KITS = 3;
    public static final int MAX_KITS = 5;

    public int index; // 0=HOME, 1=AWAY

    public List<Player> players;
    public List<Player> lineup;
    public int substitutionsCount;
    int kickerIndex;

    private static Map<Player.Role, Player.Role[]> substitutionRules;

    static {
        Map<Player.Role, Player.Role[]> aMap = new HashMap<Player.Role, Player.Role[]>();
        aMap.put(GOALKEEPER, new Player.Role[]{GOALKEEPER, GOALKEEPER});
        aMap.put(RIGHT_BACK, new Player.Role[]{LEFT_BACK, DEFENDER});
        aMap.put(LEFT_BACK, new Player.Role[]{RIGHT_BACK, DEFENDER});
        aMap.put(DEFENDER, new Player.Role[]{RIGHT_BACK, LEFT_BACK});
        aMap.put(RIGHT_WINGER, new Player.Role[]{LEFT_WINGER, MIDFIELDER});
        aMap.put(LEFT_WINGER, new Player.Role[]{RIGHT_WINGER, MIDFIELDER});
        aMap.put(MIDFIELDER, new Player.Role[]{RIGHT_WINGER, LEFT_WINGER});
        aMap.put(ATTACKER, new Player.Role[]{RIGHT_WINGER, LEFT_WINGER});
        substitutionRules = Collections.unmodifiableMap(aMap);
    }

    public ControlMode controlMode;
    public InputDevice inputDevice;
    int side; // -1=upside, 1=downside

    Player near1; // nearest to the ball
    Player bestDefender;

    public TextureRegion image;
    public boolean imageIsDefaultLogo;

    public Team() {
        controlMode = ControlMode.UNDEFINED;
        kits = new ArrayList<Kit>();
        players = new ArrayList<Player>();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        path = jsonData.getString("path", null);
        controlMode = json.readValue("controlMode", ControlMode.class, ControlMode.UNDEFINED, jsonData);
        type = json.readValue("type", Team.Type.class, jsonData);
        country = jsonData.getString("country");
        confederation = jsonData.getString("confederation", null);
        league = jsonData.getString("league", null);
        city = jsonData.getString("city");
        stadium = jsonData.getString("stadium");

        coach = json.readValue("coach", Coach.class, jsonData);
        coach.team = this;

        String tacticsCode = jsonData.getString("tactics");
        tactics = 0;
        for (int i = 0; i < Tactics.codes.length; i++) {
            if (Tactics.codes[i].equals(tacticsCode)) {
                tactics = i;
            }
        }

        Kit[] kitsArray = json.readValue("kits", Kit[].class, jsonData);
        Collections.addAll(kits, kitsArray);

        Player[] playersArray = json.readValue("players", Player[].class, jsonData);
        for (Player player : playersArray) {
            player.team = this;
            players.add(player);
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        if (path != null) {
            json.writeValue("path", path);
        }
        if (controlMode != ControlMode.UNDEFINED) {
            json.writeValue("controlMode", controlMode);
        }
        json.writeValue("type", type);
        json.writeValue("country", country);
        if (confederation != null) {
            json.writeValue("confederation", confederation);
        }
        if (league != null) {
            json.writeValue("league", league);
        }
        json.writeValue("city", city);
        json.writeValue("stadium", stadium);
        json.writeValue("coach", coach);
        json.writeValue("tactics", Tactics.codes[tactics]);
        json.writeValue("kits", kits, Kit[].class, Kit.class);
        json.writeValue("players", players, Player[].class, Player.class);
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
        player.role = GOALKEEPER;
        rotatePlayerNumber(player, 1);
        player.skinColor = Skin.Color.PINK;
        player.hairColor = Hair.Color.BLACK;
        player.hairStyle = "SMOOTH_A";
        player.skills = new Player.Skills();
        players.add(player);
        return player;
    }

    void beforeMatch(Match match) {
        this.match = match;
        lineup = new ArrayList<>();
        int lineupSize = Math.min(players.size(), TEAM_SIZE + match.settings.benchSize);
        for (int i = 0; i < lineupSize; i++) {
            Player player = players.get(i);
            player.beforeMatch(match);
            player.index = i;
            lineup.add(player);
        }
        substitutionsCount = 0;
        kickerIndex = TEAM_SIZE - 1;
    }

    void beforeTraining(Training training) {
        this.training = training;
        lineup = new ArrayList<>();
        int lineupSize = players.size();
        for (int i = 0; i < lineupSize; i++) {
            Player player = players.get(i);
            player.beforeTraining(training);
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
        for (int i = 0; i < TEAM_SIZE; i++) {
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
            for (int i = 1; i < TEAM_SIZE; i++) {
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
            for (int i = 1; i < TEAM_SIZE; i++) {
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

        for (int i = 1; i < TEAM_SIZE; i++) {

            Player player = lineup.get(i);

            int tx = Assets.tactics[tactics].target[i][ball_zone][0];
            int ty = Assets.tactics[tactics].target[i][ball_zone][1];

            player.tx = (1 - Math.abs(match.ball.mx)) * tx + Math.abs(match.ball.mx) * tx;
            player.ty = (1 - Math.abs(match.ball.my)) * ty + Math.abs(match.ball.my) * ty;

            player.tx = -side * player.tx;
            player.ty = -side * (player.ty - 4);
        }
    }

    void keepTargetDistanceFrom(Vector2 position) {
        for (int i = 1; i < TEAM_SIZE; i++) {
            Player player = lineup.get(i);
            Vector2 vec = new Vector2(player.tx, player.ty);
            vec.sub(position);
            if (vec.len() < Const.FREE_KICK_DISTANCE) {
                vec.setLength(Const.FREE_KICK_DISTANCE);
                vec.add(position);
                player.setTarget(vec.x, vec.y);
            }
        }
    }

    void updateFrameDistance() {
        for (int i = 0; i < TEAM_SIZE; i++) {
            lineup.get(i).updateFrameDistance();
        }
    }

    boolean updatePlayers(boolean limit) {
        findNearest();

        findBestDefender();

        boolean move = updateLineup(limit);

        return move;
    }

    boolean updateLineup(boolean limit) {
        boolean move = false;

        int len = lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = lineup.get(i);
            if (player.update(limit)) {
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

    public Kit getKit() {
        return kits.get(kitIndex);
    }

    public boolean deleteKit() {
        if (kits.size() > MIN_KITS) {
            return kits.remove(kits.get(kits.size() - 1));
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Team t = (Team) obj;
        return Assets.teamsRootFolder.child(path).equals(Assets.teamsRootFolder.child(t.path));
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

    void setPlayersState(PlayerFsm.Id stateId, Player excluded) {
        for (int i = 0; i < TEAM_SIZE; i++) {
            Player player = lineup.get(i);
            if (player != excluded) {
                player.setState(stateId);
            }
        }
    }

    void assignAutomaticInputDevices(Player receiver) {
        if (usesAutomaticInputDevice()) {
            for (int i = 0; i < TEAM_SIZE; i++) {
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
            if (near1.getState().checkId(STATE_STAND_RUN)) {
                near1.inputDevice = inputDevice;
            }

        } else if (match.ball.owner == null) {

            // move input_device to nearest
            if ((controlled != near1)
                    && (controlled.frameDistance == Const.BALL_PREDICTION)) {

                if (controlled.getState().checkId(STATE_STAND_RUN)
                        && near1.getState().checkId(STATE_STAND_RUN)) {
                    near1.inputDevice = inputDevice;
                    controlled.inputDevice = controlled.ai;
                }
            }

        } else if (match.ball.owner.team.index == index) {

            // move input_device to ball owner
            if ((controlled != match.ball.owner)
                    && controlled.getState().checkId(STATE_STAND_RUN)
                    && near1.getState().checkId(STATE_STAND_RUN)) {
                match.ball.owner.inputDevice = inputDevice;
                controlled.inputDevice = controlled.ai;
            }

        } else {

            if ((bestDefender != null)
                    && (bestDefender != controlled)
                    && (bestDefender.defendDistance < 0.95f * controlled.defendDistance)
                    && controlled.getState().checkId(STATE_STAND_RUN)
                    && bestDefender.getState().checkId(STATE_STAND_RUN)) {
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

    InputDevice fire2Down() {
        if (usesAutomaticInputDevice()) {
            if (inputDevice.fire2Down()) {
                return inputDevice;
            }
        } else {
            for (Player player : lineup) {
                if ((player.inputDevice != player.ai) && player.inputDevice.fire2Down()) {
                    return player.inputDevice;
                }
            }
        }
        return null;
    }

    public Player playerAtPosition(int pos) {
        return playerAtPosition(pos, null);
    }

    public Player playerAtPosition(int pos, Tactics tcs) {
        if (tcs == null) {
            tcs = Assets.tactics[tactics];
        }
        if (pos < players.size()) {
            int ply = (pos < TEAM_SIZE) ? Tactics.order[tcs.basedOn][pos] : pos;
            return players.get(ply);
        } else {
            return null;
        }
    }

    public Player lineupAtPosition(int pos) {
        return lineupAtPosition(pos, null);
    }

    public Player lineupAtPosition(int pos, Tactics tcs) {
        if (tcs == null) {
            tcs = Assets.tactics[tactics];
        }
        if (pos < lineup.size()) {
            int ply = (pos < TEAM_SIZE) ? Tactics.order[tcs.basedOn][pos] : pos;
            return lineup.get(ply);
        } else {
            return null;
        }
    }

    Player lastOfLineup() {
        for (int pos = TEAM_SIZE - 1; pos > 0; pos--) {
            Player ply = lineupAtPosition(pos);
            if (!ply.checkState(STATE_OUTSIDE)) {
                return ply;
            }
        }
        return null;
    }

    public int playerIndexAtPosition(int pos) {
        return playerIndexAtPosition(pos, null);
    }

    public int playerIndexAtPosition(int pos, Tactics tcs) {
        if (tcs == null) {
            tcs = Assets.tactics[tactics];
        }
        if (pos < players.size()) {
            return (pos < TEAM_SIZE) ? Tactics.order[tcs.basedOn][pos] : pos;
        } else {
            return -1;
        }
    }

    int nearestBenchPlayerByRole(Player.Role role) {

        int level = -1;

        // in the first pass, search for the same role
        Player.Role target = role;

        while (true) {
            for (int pos = 0; pos < TEAM_SIZE; pos++) {
                if (lineupAtPosition(pos).role == target) {
                    return pos;
                }
            }

            level = level + 1;

            // no match found
            if (level == 2) {
                return 0;
            }

            target = substitutionRules.get(role)[level];
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

    float getRank() {
        // absolute ranking from 0 to 10
        float r = 0;
        for (int i = 0; i < TEAM_SIZE; i++) {
            Player player = lineup.get(i);
            r += player.getValue() / 5.0f;
        }
        return r / TEAM_SIZE;
    }

    public void rotatePlayerNumber(Player player, int direction) {
        boolean used;
        do {
            player.number = Emath.rotate(player.number, 1, 99, direction);
            used = false;
            for (Player ply : players) {
                if (ply != player && ply.number == player.number) {
                    used = true;
                }
            }
        } while (used);
    }

    public void persist() {
        if (path == null || path.equals("")) {
            Gdx.app.error("Team", "Cannot save in empty path");
            return;
        }
        FileHandle fh = Assets.teamsRootFolder.child(path);
        String tmp = path;
        path = null;
        fh.writeString(Assets.json.prettyPrint(this), false, "UTF-8");
        path = tmp;
    }

    public static TeamList loadTeamList(List<String> paths) {
        TeamList teamList = new TeamList();
        for (String path : paths) {
            FileHandle teamFile = Assets.teamsRootFolder.child(path);
            if (teamFile.exists()) {
                Team team = Assets.json.fromJson(Team.class, teamFile.readString("UTF-8"));
                team.path = path;
                team.controlMode = Team.ControlMode.COMPUTER;
                teamList.add(team);
            }
        }
        return teamList;
    }

    public void loadImage() {
        if (image != null) return;

        switch (type) {
            case CLUB:
            case CUSTOM:
                String logoPath = path.replaceFirst("/team.", "/logo.").replaceFirst(".json", ".png");
                FileHandle customLogo = Assets.teamsRootFolder.child(logoPath);
                if (customLogo.exists()) {
                    Texture texture = new Texture(customLogo);
                    image = new TextureRegion(texture);
                    image.flip(false, true);
                } else {
                    image = kits.get(0).loadLogo();
                    imageIsDefaultLogo = true;
                }
                break;

            case NATIONAL:
                // custom flag
                String flagPath = path.replaceFirst("/team.", "/flag.").replaceFirst(".json", ".png");
                FileHandle file = Assets.teamsRootFolder.child(flagPath);
                if (!file.exists()) {
                    // default flag
                    file = Gdx.files.internal("images/flags/medium/" + name.toLowerCase().replace(" ", "_").replace(".", "") + ".png");
                }
                if (file.exists()) {
                    Texture texture = new Texture(file);
                    image = new TextureRegion(texture);
                    image.flip(false, true);
                }
                break;
        }
    }

    public TextureRegion loadKit(int index) {
        // custom kit - all indexes
        String kitPath = path.replaceFirst("/team.", "/kit.").replaceFirst(".json", ".png");
        FileHandle file = Assets.teamsRootFolder.child(kitPath);
        if (file.exists()) {
            List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
            kits.get(index).addKitColors(rgbPairs);
            return Assets.loadTextureRegion(file.path(), rgbPairs);
        }

        // custom kit - single index
        String[] names = {"home", "away", "third", "change1", "change2"};
        kitPath = path.replaceFirst("/team.", "/kit_" + names[index] + ".").replaceFirst(".json", ".png");
        file = Assets.teamsRootFolder.child(kitPath);
        if (file.exists()) {
            List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
            kits.get(index).addKitColors(rgbPairs);
            return Assets.loadTextureRegion(file.path(), rgbPairs);
        }

        // standard kit
        return kits.get(index).loadImage();
    }

    public int controlModeColor() {
        return controlModeColors[controlMode.ordinal()];
    }

    Player searchPlayerNearTo(Player other, float maxDistance) {
        for (int j = 0; j < TEAM_SIZE; j++) {
            Player ply = lineup.get(j);
            if (Emath.dist(ply.x, ply.y, other.x, other.y) < maxDistance) {
                return ply;
            }
        }
        return null;
    }
}
