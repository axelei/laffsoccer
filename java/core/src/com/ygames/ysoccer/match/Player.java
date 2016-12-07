package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Color2;
import com.ygames.ysoccer.framework.Color3;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.math.Emath;
import com.ygames.ysoccer.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Player implements Json.Serializable {

    public enum Role {GOALKEEPER, RIGHT_BACK, LEFT_BACK, DEFENDER, RIGHT_WINGER, LEFT_WINGER, MIDFIELDER, ATTACKER}

    public enum Skill {PASSING, SHOOTING, HEADING, TACKLING, CONTROL, SPEED, FINISHING}

    private static final String[] roleLabels = {
            "ROLES.GOALKEEPER",
            "ROLES.RIGHT_BACK",
            "ROLES.LEFT_BACK",
            "ROLES.DEFENDER",
            "ROLES.RIGHT_WINGER",
            "ROLES.LEFT_WINGER",
            "ROLES.MIDFIELDER",
            "ROLES.ATTACKER"
    };

    private static final String[] skillLabels = {
            "SKILLS.PASSING",
            "SKILLS.SHOOTING",
            "SKILLS.HEADING",
            "SKILLS.TACKLING",
            "SKILLS.CONTROL",
            "SKILLS.SPEED",
            "SKILLS.FINISHING"
    };

    private static final int[] stars = {
            0, 0, 0, 0, 1, 1, 1, 2, 2, 2,
            2, 3, 3, 3, 3, 3, 4, 4, 4, 4,
            4, 4, 5, 5, 5, 5, 5, 5, 5, 6,
            6, 6, 6, 6, 7, 7, 7, 7, 7, 7,
            8, 8, 8, 8, 8, 8, 8, 9, 9, 9,
    };

    // keeper collision types
    private static final int CT_NONE = 0;
    private static final int CT_REBOUND = 1;
    private static final int CT_CATCH = 2;
    private static final int CT_DEFLECT = 3;

    public String name;
    public String shirtName;
    public Team team;
    public String nationality;
    int index;
    public Role role;
    public int number;

    public Skin.Color skinColor;
    public Hair.Color hairColor;
    public String hairStyle;
    public Hair hair;

    public Skills skills;
    public List<Skill> bestSkills = new ArrayList<Skill>();

    int skillKeeper;

    public int value; // 0 to 49

    public InputDevice inputDevice;
    public Ai ai;

    float kickAngle;
    float defendDistance;

    Player facingPlayer;
    float facingAngle;
    Match match;
    PlayerFsm fsm;

    float speed;

    boolean isVisible;

    Data[] data = new Data[Const.REPLAY_SUBFRAMES];

    public float x;
    public float y;
    float z;
    float x0;
    float y0;
    float z0;
    float v;
    float vz;
    public float a;
    float thrustX; // horiz.speed in keeper saves (min=0, max=1)
    float thrustZ; // vert.speed in keeper saves (min=0, max=1)

    float tx; // x position (target)
    float ty; // y position (target)

    float fmx; // 0..7 direction
    float fmy; // 1 = standing, 0 and 2 = running
    float fmySweep;

    float ballDistance;

    // from 0 to BALL_PREDICTION-1: frames required to reach the ball
    // equal to BALL_PREDICTION: ball too far to be reached
    // should be updated every frame
    int frameDistance;

    public Player() {
        skills = new Skills();
        setAi(new Ai(this));
        setInputDevice(ai);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.readFields(this, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("shirtName", shirtName);
        json.writeValue("nationality", nationality);
        json.writeValue("role", role);
        json.writeValue("number", number);
        json.writeValue("skinColor", skinColor);
        json.writeValue("hairColor", hairColor);
        json.writeValue("hairStyle", hairStyle);
        if (role == Role.GOALKEEPER) {
            json.writeValue("value", value);
        } else {
            json.writeValue("skills", skills);
            json.writeValue("bestSkills", bestSkills, Skill[].class, Skill.class);
        }
    }

    void beforeMatch(Match match) {
        for (int i = 0; i < data.length; i++) {
            data[i] = new Data();
        }
        fsm = new PlayerFsm(this);
        isVisible = true;
        this.match = match;
    }

    void setTarget(float tx, float ty) {
        this.tx = tx;
        this.ty = ty;
    }

    public void setState(int id) {
        fsm.setState(id);
    }

    void think() {
        fsm.think();
    }

    public void setInputDevice(InputDevice inputDevice) {
        this.inputDevice = inputDevice;
    }

    void setAi(Ai ai) {
        this.ai = ai;
    }

    void updateAi() {
        ai.update();
    }

    void animationStandRun() {
        fmx = Math.round(((a + 360) % 360) / 45) % 8;
        if (v > 0) {
            fmySweep = (fmySweep + 0.16f * v / 1000) % 4;
            if (fmySweep > 3) {
                fmy = fmySweep - 2;
            } else {
                fmy = fmySweep;
            }
        } else {
            fmy = 1;
        }
    }

    void animationScorer() {
        fmx = Math.round(((a + 360) % 360) / 45) % 8;
        if (v > 0) {
            fmySweep = (fmySweep + 0.16f * v / 1000) % 4;
            if (fmySweep > 3) {
                fmy = 12;
            } else {
                fmy = 11 + fmySweep;
            }
        } else {
            fmy = 1;
        }
    }

    void updateFrameDistance() {
        frameDistance = Const.BALL_PREDICTION;
        for (int f = Const.BALL_PREDICTION - 1; f >= 0; f--) {
            float dist = Emath.dist(x, y, match.ball.prediction[f].x, match.ball.prediction[f].y);
            if (dist < speed * f / GLGame.VIRTUAL_REFRESH_RATE) {
                frameDistance = f;
            }
        }
    }

    public void getPossession() {
        if ((ballDistance <= 8)
                && Emath.dist(x0, y0, match.ball.x0, match.ball.y0) > 8
                && (match.ball.z < (Const.PLAYER_H + Const.BALL_R))) {

            float smoothedBallV = match.ball.v * 0.5f;
            Vector2 ballVec = new Vector2(smoothedBallV, match.ball.a, true);
            Vector2 playerVec = new Vector2(v, a, true);

            Vector2 differenceVec = playerVec.sub(ballVec);

            if (differenceVec.v < 220 + 7 * skills.control) {
                match.ball.setOwner(this);
                match.ball.x = x + (Const.BALL_R - 1) * Emath.cos(a);
                match.ball.y = y + (Const.BALL_R - 1) * Emath.sin(a);
                match.ball.v = v;
                match.ball.a = a;
            } else {
                match.ball.setOwner(this);
                match.ball.setOwner(null);
                match.ball.collisionPlayer(this, 0.5f * differenceVec.v);
            }

            match.ball.vz = match.ball.vz / (2 + skills.control);
        }
    }

    boolean keeperCollision() {
        Ball ball = match.ball;

        int collisionType = CT_NONE;

        if (Math.abs(ball.y0 - y) >= 1 && Math.abs(ball.y - y) < 1) {

            // collision detection

            // keeper frame
            int fmx = Math.round(this.fmx);
            int fmy = Math.abs((int) Math.floor(this.fmy));

            // offset
            int offx = +24 + Math.round(ball.x - x);
            int offy = +34 + Math.round(-ball.z - Const.BALL_R + z);

            // verify if the pixel is inside the frame
            if ((offx < 0) || (offx >= 50)) {
                return false;
            }
            if ((offy < 0) || (offy >= 50)) {
                return false;
            }

            int det_x = Math.round(50 * (fmx % 24) + offx);
            int det_y = Math.round(50 * (fmy % 24) + offy);

            int rgb = Assets.keeperCollisionDetection.getPixel(det_x, det_y) >>> 8;

            switch (rgb) {
                case 0xC0C000:
                    collisionType = CT_REBOUND;
                    break;

                case 0xC00000:
                    collisionType = CT_CATCH;
                    break;

                case 0x0000C0:
                    if (ball.v > 180) {
                        collisionType = CT_DEFLECT;
                    } else {
                        collisionType = CT_CATCH;
                    }
                    break;
            }

            switch (collisionType) {
                case CT_REBOUND:
                    if (ball.v > 180) {
                        Assets.Sounds.deflect.play(0.5f * match.settings.soundVolume / 100f);
                    }
                    ball.v = ball.v / 4;
                    ball.a = (-ball.a) % 360;
                    ball.s = -ball.s;
                    ball.setOwner(this, false);
                    ball.setOwner(null);
                    break;

                case CT_CATCH:
                    if (ball.v > 180) {
                        Assets.Sounds.hold.play(0.5f * match.settings.soundVolume / 100f);
                    }
                    ball.v = 0;
                    ball.vz = 0;
                    ball.s = 0;
                    ball.setOwner(this);
                    ball.setHolder(this);
                    break;

                case CT_DEFLECT:
                    if (ball.v > 180) {
                        Assets.Sounds.deflect.play(0.5f * match.settings.soundVolume / 100f);
                    }
                    // real ball x-y angle (when spinned, it is different from ball.a)
                    float ballAxy = Emath.aTan2(ball.y - ball.y0, ball.x - ball.x0);

                    float ballVx = ball.v * Emath.cos(ballAxy);
                    float ballVy = ball.v * Emath.sin(ballAxy);

                    ballVx = Math.signum(ballVx)
                            * (0.5f * Math.abs(ballVx) + 0.25f * Math.abs(ballVy))
                            + v * Emath.cos(a);
                    ballVy = 0.7f * ballVy;

                    ball.v = (float) Math.sqrt(ballVx * ballVx + ballVy * ballVy);
                    ball.a = Emath.aTan2(ballVy, ballVx);
                    ball.vz = 1.5f * vz;

                    ball.setOwner(this, false);
                    ball.setOwner(null);
                    break;
            }
        }

        return (collisionType == CT_CATCH);
    }

    void holdBall(int offX, int offZ) {
        Ball ball = match.ball;
        if ((ball.holder == this)) {
            ball.x = x + offX;
            ball.y = y;
            ball.z = z + offZ;
            ball.v = v;
            ball.vz = vz;
        }
    }

    public void save(int subframe) {
        data[subframe].x = Math.round(x);
        data[subframe].y = Math.round(y);
        data[subframe].z = Math.round(z);
        data[subframe].fmx = Math.round(fmx);
        data[subframe].fmy = (int) Math.abs(Math.floor(fmy));
        data[subframe].isVisible = isVisible;
    }

    float targetDistance() {
        return Emath.dist(tx, ty, x, y);
    }

    float targetAngle() {
        return Emath.aTan2(ty - y, tx - x);
    }

    void watchBall() {
        a = Math.round((Emath.aTan2(y - match.ball.y, x - match.ball.x) + 180) / 45.0f) * 45.0f;
    }

    public String getRoleLabel() {
        return roleLabels[role.ordinal()];
    }

    // sets and orders skills in function of the role of the player
    public Skill[] getOrderedSkills() {

        if (role == Role.GOALKEEPER) {
            return null;
        }

        Skill[] orderedSkills = new Skill[7];

        // set starting order
        int[] skill = new int[7];
        if ((role == Role.RIGHT_BACK) || (role == Role.LEFT_BACK)) {
            skill[0] = skills.tackling;
            skill[1] = skills.speed;
            skill[2] = skills.passing;
            skill[3] = skills.shooting;
            skill[4] = skills.heading;
            skill[5] = skills.control;
            skill[6] = skills.finishing;
            orderedSkills[0] = Skill.TACKLING;
            orderedSkills[1] = Skill.SPEED;
            orderedSkills[2] = Skill.PASSING;
            orderedSkills[3] = Skill.SHOOTING;
            orderedSkills[4] = Skill.HEADING;
            orderedSkills[5] = Skill.CONTROL;
            orderedSkills[6] = Skill.FINISHING;

        } else if (role == Role.DEFENDER) {
            skill[0] = skills.tackling;
            skill[1] = skills.heading;
            skill[2] = skills.passing;
            skill[3] = skills.speed;
            skill[4] = skills.shooting;
            skill[5] = skills.control;
            skill[6] = skills.finishing;
            orderedSkills[0] = Skill.TACKLING;
            orderedSkills[1] = Skill.HEADING;
            orderedSkills[2] = Skill.PASSING;
            orderedSkills[3] = Skill.SPEED;
            orderedSkills[4] = Skill.SHOOTING;
            orderedSkills[5] = Skill.CONTROL;
            orderedSkills[6] = Skill.FINISHING;

        } else if ((role == Role.RIGHT_WINGER) || (role == Role.LEFT_WINGER)) {
            skill[0] = skills.control;
            skill[1] = skills.speed;
            skill[2] = skills.passing;
            skill[3] = skills.tackling;
            skill[4] = skills.heading;
            skill[5] = skills.finishing;
            skill[6] = skills.shooting;
            orderedSkills[0] = Skill.CONTROL;
            orderedSkills[1] = Skill.SPEED;
            orderedSkills[2] = Skill.PASSING;
            orderedSkills[3] = Skill.TACKLING;
            orderedSkills[4] = Skill.HEADING;
            orderedSkills[5] = Skill.FINISHING;
            orderedSkills[6] = Skill.SHOOTING;

        } else if (role == Role.MIDFIELDER) {
            skill[0] = skills.passing;
            skill[1] = skills.tackling;
            skill[2] = skills.control;
            skill[3] = skills.heading;
            skill[4] = skills.shooting;
            skill[5] = skills.speed;
            skill[6] = skills.finishing;
            orderedSkills[0] = Skill.PASSING;
            orderedSkills[1] = Skill.TACKLING;
            orderedSkills[2] = Skill.CONTROL;
            orderedSkills[3] = Skill.HEADING;
            orderedSkills[4] = Skill.SHOOTING;
            orderedSkills[5] = Skill.SPEED;
            orderedSkills[6] = Skill.FINISHING;

        } else if (role == Role.ATTACKER) {
            skill[0] = skills.heading;
            skill[1] = skills.finishing;
            skill[2] = skills.speed;
            skill[3] = skills.shooting;
            skill[4] = skills.control;
            skill[5] = skills.passing;
            skill[6] = skills.tackling;
            orderedSkills[0] = Skill.HEADING;
            orderedSkills[1] = Skill.FINISHING;
            orderedSkills[2] = Skill.SPEED;
            orderedSkills[3] = Skill.SHOOTING;
            orderedSkills[4] = Skill.CONTROL;
            orderedSkills[5] = Skill.PASSING;
            orderedSkills[6] = Skill.TACKLING;
        }

        boolean ordered = false;
        while (!ordered) {
            ordered = true;
            for (int i = 0; i < 6; i++) {
                if (skill[i] < skill[i + 1]) {
                    int tmp = skill[i];
                    skill[i] = skill[i + 1];
                    skill[i + 1] = tmp;

                    Skill s = orderedSkills[i];
                    orderedSkills[i] = orderedSkills[i + 1];
                    orderedSkills[i + 1] = s;

                    ordered = false;
                }
            }
        }

        return orderedSkills;
    }

    public boolean update(Match match, boolean limit) {

        // physical parameters
        // speeds are in pixel/s
        // TODO: change in function of time and stamina
        speed = 130 + 4 * skills.speed;

        // store old values
        x0 = x;
        y0 = y;
        z0 = z;

        // update position
        x += v / Const.SECOND * Emath.cos(a);
        y += v / Const.SECOND * Emath.sin(a);
        z += vz / Const.SECOND;

        // gravity
        if (z > 0) {
            vz -= Const.GRAVITY;
        }

        // back to the ground
        if (z < 0) {
            z = 0;
            vz = 0;
        }

        if (limit) {
            limitInsideField();
        }

        ballDistance = Emath.dist(x, y, match.ball.x, match.ball.y);

        return ((v > 0) || (vz != 0));
    }

    private void limitInsideField() {
        // left
        x = Math.max(x, -Const.TOUCH_LINE - 50);
        // right
        x = Math.min(x, +Const.TOUCH_LINE + 50);

        if (Math.abs(x) > (Const.POST_X + 10)) {
            // top
            y = Math.max(y, -Const.GOAL_LINE - 50);
            // bottom
            y = Math.min(y, +Const.GOAL_LINE + 50);
        } else {
            // top
            y = Math.max(y, -Const.GOAL_LINE);
            // bottom
            y = Math.min(y, +Const.GOAL_LINE);
        }
    }

    public int getDefenseRating() {
        switch (role) {
            case DEFENDER:
            case RIGHT_BACK:
            case LEFT_BACK:
            case MIDFIELDER:
                return skills.tackling
                        + skills.heading
                        + skills.passing
                        + skills.speed
                        + skills.control;

            default:
                return 0;
        }
    }

    public int getOffenseRating() {
        switch (role) {
            case RIGHT_WINGER:
            case LEFT_WINGER:
            case MIDFIELDER:
            case ATTACKER:
                return skills.heading
                        + skills.finishing
                        + skills.speed
                        + skills.shooting
                        + skills.control;
            default:
                return 0;
        }
    }

    public void copyFrom(Player player) {
        name = player.name;
        shirtName = player.shirtName;
        nationality = player.nationality;
        role = player.role;

        hairColor = player.hairColor;
        hairStyle = player.hairStyle;
        skinColor = player.skinColor;

        skills.passing = player.skills.passing;
        skills.shooting = player.skills.shooting;
        skills.heading = player.skills.heading;
        skills.tackling = player.skills.tackling;
        skills.control = player.skills.control;
        skills.speed = player.skills.speed;
        skills.finishing = player.skills.finishing;
    }

    public int getSkillValue(Skill skill) {
        int value = 0;
        switch (skill) {
            case PASSING:
                value = skills.passing;
                break;

            case SHOOTING:
                value = skills.shooting;
                break;

            case HEADING:
                value = skills.heading;
                break;

            case TACKLING:
                value = skills.tackling;
                break;

            case CONTROL:
                value = skills.control;
                break;

            case SPEED:
                value = skills.speed;
                break;

            case FINISHING:
                value = skills.finishing;
                break;
        }
        return value;
    }

    public void setSkillValue(Skill skill, int value) {
        switch (skill) {
            case PASSING:
                skills.passing = value;
                break;

            case SHOOTING:
                skills.shooting = value;
                break;

            case HEADING:
                skills.heading = value;
                break;

            case TACKLING:
                skills.tackling = value;
                break;

            case CONTROL:
                skills.control = value;
                break;

            case SPEED:
                skills.speed = value;
                break;

            case FINISHING:
                skills.finishing = value;
                break;
        }

        if (bestSkills.contains(skill)) {
            // remove if a not-best skill with higher value exists
            for (int i = Skill.PASSING.ordinal(); i <= Skill.FINISHING.ordinal(); i++) {
                Skill s = Skill.values()[i];
                if (s != skill && !bestSkills.contains(s) && getSkillValue(s) > value) {
                    removeBestSkill(skill);
                }
            }
        } else {
            // add if a best skill with lower value exists
            for (int i = Skill.PASSING.ordinal(); i <= Skill.FINISHING.ordinal(); i++) {
                Skill s = Skill.values()[i];
                if (s != skill && bestSkills.contains(s) && getSkillValue(s) < value) {
                    addBestSkill(skill);
                }
            }
        }
    }

    private boolean addBestSkill(Skill skill) {
        if (bestSkills.contains(skill)) {
            return false;
        }
        // fails if a not-best skills with higher value exists
        for (int i = Skill.PASSING.ordinal(); i <= Skill.FINISHING.ordinal(); i++) {
            Skill s = Skill.values()[i];
            if (s != skill && !bestSkills.contains(s) && getSkillValue(s) > getSkillValue(skill)) {
                return false;
            }
        }
        return bestSkills.add(skill);
    }

    private boolean removeBestSkill(Skill skill) {
        if (!bestSkills.contains(skill)) {
            return false;
        }
        // fails if a best skills with lower value exists
        for (int i = Skill.PASSING.ordinal(); i <= Skill.FINISHING.ordinal(); i++) {
            Skill s = Skill.values()[i];
            if (s != skill && bestSkills.contains(s) && getSkillValue(s) < getSkillValue(skill)) {
                return false;
            }
        }
        return bestSkills.remove(skill);
    }

    public boolean toggleBestSkill(Skill skill) {
        return bestSkills.contains(skill) ? removeBestSkill(skill) : addBestSkill(skill);
    }

    public int getValue() {
        if (role == Role.GOALKEEPER) {
            return value;
        } else {
            return skills.passing
                    + skills.shooting
                    + skills.heading
                    + skills.tackling
                    + skills.control
                    + skills.speed
                    + skills.finishing;
        }
    }

    public int getStars() {
        return stars[getValue()];
    }

    public String getPrice(double maxPlayerPrice) {
        if (getValue() == 49) {
            return Assets.moneyFormat(maxPlayerPrice) + "+";
        } else {
            return Assets.moneyFormat(maxPlayerPrice / (float) Math.pow(1.2, 48 - getValue()));
        }
    }

    public static String getSkillLabel(Skill skill) {
        return skillLabels[skill.ordinal()];
    }

    public static class Skills {
        public int passing;
        public int shooting;
        public int heading;
        public int tackling;
        public int control;
        public int speed;
        public int finishing;
    }

    public int getIndex() {
        return this.team.players.indexOf(this);
    }

    public int getScoringWeight() {
        int value = skills.heading + skills.shooting + skills.finishing;

        if (getIndex() < Const.TEAM_SIZE) {
            value *= 4;
        }

        switch (role) {
            case RIGHT_WINGER:
            case LEFT_WINGER:
                value *= 5;
                break;

            case MIDFIELDER:
                value *= 3;
                break;

            case ATTACKER:
                value *= 10;
                break;
        }
        return value;
    }

    public TextureRegion createFace() {

        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();

        addSkinColors(rgbPairs);
        addHairColors(rgbPairs);

        String filename = "images/player/menu/" + hairStyle + ".PNG";
        return Assets.loadTextureRegion(filename, rgbPairs);
    }

    public void addSkinColors(List<RgbPair> rgbPairs) {
        Color3 sc = Skin.colors[skinColor.ordinal()];
        rgbPairs.add(new RgbPair(0xFF6300, sc.color1));
        rgbPairs.add(new RgbPair(0xB54200, sc.color2));
        rgbPairs.add(new RgbPair(0x631800, sc.color3));
    }

    public void addHairColors(List<RgbPair> rgbPairs) {
        if (hairStyle.equals("SHAVED")) {
            Color2 shavedColor = Hair.shavedColors[skinColor.ordinal()][hairColor.ordinal()];
            if (shavedColor != null) {
                rgbPairs.add(new RgbPair(0x907130, shavedColor.color1));
                rgbPairs.add(new RgbPair(0x715930, shavedColor.color2));
            } else {
                Color3 sc = Skin.colors[skinColor.ordinal()];
                rgbPairs.add(new RgbPair(0x907130, sc.color1));
                rgbPairs.add(new RgbPair(0x715930, sc.color2));
            }
        } else {
            Color3 hc = Hair.colors[hairColor.ordinal()];
            rgbPairs.add(new RgbPair(0x907130, hc.color1));
            rgbPairs.add(new RgbPair(0x715930, hc.color2));
            rgbPairs.add(new RgbPair(0x514030, hc.color3));
        }
    }

    boolean searchFacingPlayer(boolean longRange) {
        return searchFacingPlayer(longRange, false);
    }

    boolean searchFacingPlayer(boolean longRange, boolean inAction) {

        float minDistance = 0.0f;
        float maxDistance = Const.TOUCH_LINE / 2;
        if (longRange) {
            minDistance = Const.TOUCH_LINE / 2;
            maxDistance = Const.TOUCH_LINE;
        }

        float maxAngle;
        if (inAction) {
            maxAngle = 15.5f + skills.passing;
        } else {
            maxAngle = 22.5f;
        }

        float facingDelta = maxDistance * Emath.sin(maxAngle);

        facingPlayer = null;
        facingAngle = 0.0f;

        int len = team.lineup.size();
        for (int i = 0; i < len; i++) {
            Player ply = team.lineup.get(i);
            if (ply == this) {
                continue;
            }

            float plyDistance = Emath.dist(x, y, ply.x, ply.y);
            float plyAngle = ((Emath.aTan2(ply.y + 5 * Emath.sin(ply.a) - y,
                    ply.x + 5 * Emath.cos(ply.a) - x) - a + 540.0f) % 360.0f) - 180.0f;
            float plyDelta = plyDistance * Emath.sin(plyAngle);

            if (Math.abs(plyAngle) < maxAngle && plyDistance > minDistance
                    && plyDistance < maxDistance
                    && Math.abs(plyDelta) < Math.abs(facingDelta)) {
                facingPlayer = ply;
                facingAngle = plyAngle;
                facingDelta = plyDelta;
            }
        }

        return (facingPlayer != null);
    }
}
