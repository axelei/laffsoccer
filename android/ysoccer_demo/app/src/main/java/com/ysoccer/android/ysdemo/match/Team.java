package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.ysdemo.Assets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Team {

    public enum ControlMode {UNDEFINED, COMPUTER, PLAYER}

    public Match match;

    String confederation;
    boolean isNational; // false => club team, true => national team
    String country;
    public int number;
    public String ext;
    public String nameKey;
    public String name;
    int tactics;
    int division;
    String coachName;
    String city;
    String stadium;

    List<Player> players;
    List<Player> lineup;
    int subsCount; // substitutions made

    public int kitIndex; // 0=first, 1=second, 2=third kit, 3=change1, 4=change2
    public List<Kit> kits;

    public int index; // 0=HOME, 1=AWAY
    ControlMode controlMode; // 1=computer, 2=player/coach, 3=coach
    InputDevice inputDevice;
    public int side; // -1=upside, 1=downside

    Player near1; // nearest to the ball
    Player bestDefender;

    Texture clnf; // club logo / national flag
    Texture clnf_sh; // shadow

    public Team() {
        players = new ArrayList<Player>();
        lineup = new ArrayList<Player>();
        kits = new ArrayList<Kit>();
    }

    public Team(String nameKey, String ext, int number) {
        this();
        this.nameKey = nameKey;
        this.ext = ext;
        this.number = number;
        this.controlMode = ControlMode.UNDEFINED;
    }

    public String getName() {
        return name;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlMode controlMode) {
        this.controlMode = controlMode;
    }

    private void _init() {
        confederation = "";
        isNational = false;
        country = "";
        name = "";
        tactics = 0;
        division = 0;
        coachName = "";
        city = "";
        stadium = "";

        // input_device = null;

        players.clear();
        lineup.clear();
        subsCount = 0;

        kitIndex = 0;
        kits.clear();

        index = 0;
        side = 0;

        // clnf = null;
        // clnf_sh = null;

        // won = 0;
        // drawn = 0;
        // lost = 0;

        // goals_for = 0;
        // goals_against = 0;
        // points = 0;

    }

    public void loadFromFile(GLGame game) {

        String fileName = "data/team_wld.yst";
        InputStream in = null;
        try {
            in = game.getFileIO().readAsset(fileName);

            _init();

            DataInputStream is = new DataInputStream(in);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";

            try {

                line = br.readLine();

                while (line != null) {

                    if (line.length() > 0 && line.charAt(0) == '#') {

                        if (Integer.parseInt(line.substring(10)) == number) {

                            // country
                            line = br.readLine();

                            country = line.substring(10).trim();

                            isNational = false;
                            if (country == "UEF")
                                isNational = true; // UEFA
                            if (country == "CNC")
                                isNational = true; // CONCACAF
                            if (country == "CNM")
                                isNational = true; // CONMEBOL
                            if (country == "CAF")
                                isNational = true; // CAF
                            if (country == "AFC")
                                isNational = true; // AFC
                            if (country == "OFC")
                                isNational = true; // OFC

                            // name
                            line = br.readLine();
                            nameKey = line.substring(10).trim();
                            name = game.translate(nameKey);

                            // tactics
                            line = br.readLine();
                            tactics = Integer.parseInt(line.substring(10));

                            // division
                            line = br.readLine();
                            division = Integer.parseInt(line.substring(10));

                            // coach name
                            line = br.readLine();
                            coachName = line.substring(10).trim();

                            // coach type
                            line = br.readLine();

                            // city
                            line = br.readLine();
                            city = line.substring(10).trim();

                            // stadium
                            line = br.readLine();
                            stadium = line.substring(10).trim();

                            // stadiumid
                            line = br.readLine();

                            // void line
                            line = br.readLine();

                            // kits headings
                            line = br.readLine();

                            // main kit
                            for (int k = 0; k < 5; k++) {
                                line = br.readLine();
                                if (line.substring(10).trim().length() > 0) {
                                    int style = Integer.parseInt(line
                                            .substring(10, 17).trim());
                                    int shirt1 = Integer.parseInt(line
                                            .substring(17, 24).trim());
                                    int shirt2 = Integer.parseInt(line
                                            .substring(24, 31).trim());
                                    int shorts = Integer.parseInt(line
                                            .substring(31, 38).trim());
                                    int socks = Integer.parseInt(line
                                            .substring(38).trim());

                                    Kit kit = new Kit(style, shirt1, shirt2,
                                            shorts, socks);
                                    kits.add(kit);
                                }
                            }

                            // goalie kits
                            for (int k = 0; k < 2; k++) {
                                line = br.readLine();
                            }

                            // void line
                            line = br.readLine();

                            // player headings
                            line = br.readLine();

                            // read players
                            line = br.readLine();
                            while (line.length() > 0) {

                                Player player = newPlayer();

                                player.index = Integer.parseInt(line.substring(
                                        0, 2).trim()) - 1;
                                player.number = Integer.parseInt(line
                                        .substring(10, 13).trim());
                                player.name = line.substring(18, 32).trim();
                                player.surname = line.substring(32, 46).trim();
                                player.nationality = line.substring(46, 49);
                                player.role = Integer.parseInt(line.substring(
                                        52, 54).trim());

                                player.hairType = Hair
                                        .type(Integer.parseInt(line.substring(
                                                58, 61).trim()));
                                player.hairColor = Integer.parseInt(line
                                        .substring(64, 66).trim());
                                player.skinColor = Integer.parseInt(line
                                        .substring(70, 72).trim());
                                player.price = Integer.parseInt(line.substring(
                                        76, 78).trim());

                                // skills
                                player.skillPassing = Integer.parseInt(line
                                        .substring(82, 83));
                                player.skillShooting = Integer.parseInt(line
                                        .substring(86, 87));
                                player.skillHeading = Integer.parseInt(line
                                        .substring(90, 91));
                                player.skillTackling = Integer.parseInt(line
                                        .substring(94, 95));
                                player.skillControl = Integer.parseInt(line
                                        .substring(98, 99));
                                player.skillSpeed = Integer.parseInt(line
                                        .substring(102, 103));
                                player.skillFinishing = Integer.parseInt(line
                                        .substring(106, 107));

                                if (player.role == Player.GOALKEEPER) {
                                    player.skillKeeper = Math
                                            .round(player.price / 7);
                                }

                                // order string with skills
                                player.orderSkills();

                                line = br.readLine();
                            }
                        }
                    }

                    line = br.readLine();
                }

            } catch (IOException e) {
                throw new RuntimeException("All read: '" + fileName + "'", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load team '" + fileName + "'",
                    e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }
    }

    private Player newPlayer() {
        if (players.size() == Const.FULL_TEAM) {
            return null;
        }
        Player player = new Player();
        players.add(player);
        player.team = this;
        return player;
    }

    void setMatch(Match match) {
        this.match = match;
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
            float attackerGoalDistance = Emath.dist(match.ball.owner.x,
                    match.ball.owner.y, 0, -Const.GOAL_LINE
                            * match.ball.owner.team.side);

            float bestDistance = 2 * Const.GOAL_LINE;
            for (int i = 1; i < Const.TEAM_SIZE; i++) {
                Player player = lineup.get(i);
                player.defendDistance = Emath.dist(player.x, player.y,
                        match.ball.owner.x, match.ball.owner.y);

                float playerGoalDistance = Emath.dist(player.x, player.y, 0,
                        -Const.GOAL_LINE * match.ball.owner.team.side);
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

            int tx = Assets.tactics[tactics].target[i][ball_zone][0];
            int ty = Assets.tactics[tactics].target[i][ball_zone][1];

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

    boolean usesAutomaticInputDevice() {
        return (controlMode == ControlMode.PLAYER) && (inputDevice != null);
    }

    void setIntroPositions() {
        int len = lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = lineup.get(i);
            if (i < Const.TEAM_SIZE) {
                player.fsm.setState(PlayerFsm.STATE_OUTSIDE);

                player.x = Const.TOUCH_LINE + 80;
                player.y = 10 * side + 20;
                player.z = 0;

                player.tx = player.x;
                player.ty = player.y;
            } else {
                player.x = Const.BENCH_X;
                if ((1 - 2 * index) == match.benchSide) {
                    player.y = Const.BENCH_Y_UP + 14 * (i - Const.TEAM_SIZE)
                            + 46;
                } else {
                    player.y = Const.BENCH_Y_DOWN + 14 * (i - Const.TEAM_SIZE)
                            + 46;
                }
                player.fsm.setState(PlayerFsm.STATE_BENCH_SITTING);
            }
        }
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

    public InputDevice fire1Down() {
        if (usesAutomaticInputDevice()) {
            if (inputDevice.fire1Down()) {
                return inputDevice;
            }
        } else {
            int len = lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = lineup.get(i);
                if ((player.inputDevice != player.ai) && player.inputDevice.fire1Down()) {
                    return player.inputDevice;
                }
            }
        }
        return null;
    }

    public InputDevice fire1Up() {
        if (usesAutomaticInputDevice()) {
            if (inputDevice.fire1Up()) {
                return inputDevice;
            }
        } else {
            int len = lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = lineup.get(i);
                if ((player.inputDevice != player.ai) && player.inputDevice.fire1Up()) {
                    return player.inputDevice;
                }
            }
        }
        return null;
    }

    public InputDevice fire2Down() {
        if (usesAutomaticInputDevice()) {
            if (inputDevice.fire2Down()) {
                return inputDevice;
            }
        } else {
            int len = lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = lineup.get(i);
                if ((player.inputDevice != player.ai) && player.inputDevice.fire2Down()) {
                    return player.inputDevice;
                }
            }
        }
        return null;
    }

    public InputDevice fire2Up() {
        if (usesAutomaticInputDevice()) {
            if (inputDevice.fire2Up()) {
                return inputDevice;
            }
        } else {
            int len = lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = lineup.get(i);
                if ((player.inputDevice != player.ai) && player.inputDevice.fire2Up()) {
                    return player.inputDevice;
                }
            }
        }
        return null;
    }

    public float getRank() {
        //absolute ranking from 0 to 10
        float r = 0;
        for (int i = 0; i < Const.TEAM_SIZE; i++) {
            Player player = lineup.get(i);
            r += player.price / 5.0f;
        }
        return r / Const.TEAM_SIZE;
    }

    public static void kitAutoselection(Team homeTeam, Team awayTeam) {
        for (int i = 0; i < homeTeam.kits.size(); i++) {
            for (int j = 0; j < awayTeam.kits.size(); j++) {
                Kit homeKit = homeTeam.kits.get(i);
                Kit awayKit = awayTeam.kits.get(j);
                float difference = Kit.colorDifference(homeKit, awayKit);
                if (difference > 40) {
                    homeTeam.kitIndex = i;
                    awayTeam.kitIndex = j;
                    return;
                }
            }
        }
    }
}
