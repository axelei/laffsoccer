package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.framework.math.Vector2;
import com.ysoccer.android.ysdemo.Assets;

public class Player {

    // keeper collision types
    private static final int CT_NONE = 0;
    private static final int CT_REBOUND = 1;
    private static final int CT_CATCH = 2;
    private static final int CT_DEFLECT = 3;

    public static final int GOALKEEPER = 0;
    public static final int RIGHT_BACK = 1;
    public static final int LEFT_BACK = 2;
    public static final int DEFENDER = 3;
    public static final int RIGHT_WINGER = 4;
    public static final int LEFT_WINGER = 5;
    public static final int MIDFIELDER = 6;
    public static final int ATTACKER = 7;

    String name;
    String surname;
    Team team;
    String nationality;
    int index;
    public int role;
    int number;

    int hairType;
    int hairColor;
    int skinColor;

    int skillPassing;
    int skillShooting;
    int skillHeading;
    int skillTackling;
    int skillControl;
    int skillSpeed;
    int skillFinishing;
    int skillKeeper;

    int price; // 0 to 49

    int goals;

    public InputDevice inputDevice;
    Ai ai;

    float kickAngle;
    float defendDistance;

    Player facingPlayer;
    float facingAngle;
    Match match;
    PlayerFsm fsm;

    // menu
    String skills; // string containing ordered skills
    Texture face;
    Texture faceShadow;

    // match
    float speed;

    int hair;

    PlayerSprite sprite;
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
    // shoud be updated every frame
    int frameDistance;

    public Player() {

        for (int i = 0; i < data.length; i++) {
            data[i] = new Data();
        }

        fsm = new PlayerFsm(this);

        isVisible = true;
    }

    void setTarget(float tx, float ty) {
        this.tx = tx;
        this.ty = ty;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setState(int id) {
        fsm.setState(id);
    }

    public void think() {
        fsm.think();
    }

    public void updateAi() {
        ai.readInput();
    }

    public void animationStandRun() {
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
            if (Emath.dist(x, y, match.ball.prediction[f].x, match.ball.prediction[f].y)
                    < speed * f / GLGame.VIRTUAL_REFRESH_RATE) {
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

            if (differenceVec.v < 220 + 7 * skillControl) {
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

            match.ball.vz = match.ball.vz / (2 + skillControl);
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

            int rgb = Assets.keeperCollisionDetection.getPixel(det_x, det_y) & 0xFFFFFF;

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
                        match.listener.deflectSound(0.5f * match.settings.sfxVolume);
                    }
                    ball.v = ball.v / 4;
                    ball.a = (-ball.a) % 360;
                    ball.s = -ball.s;
                    ball.setOwner(this, false);
                    ball.setOwner(null);
                    break;

                case CT_CATCH:
                    if (ball.v > 180) {
                        match.listener.holdSound(0.5f * match.settings.sfxVolume);
                    }
                    ball.v = 0;
                    ball.vz = 0;
                    ball.s = 0;
                    ball.setOwner(this);
                    ball.setHolder(this);
                    break;

                case CT_DEFLECT:
                    if (ball.v > 180) {
                        match.listener.deflectSound(0.5f * match.settings.sfxVolume);
                    }
                    // real ball x-y angle (when spinned, it is different from
                    // ball.a)
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

    // sets and orders skills in function of the role of the player
    void orderSkills() {

        skills = "";

        if (role == Player.GOALKEEPER) {
            return;
        }

        // set starting order
        int[] skill = new int[7];

        if ((role == Player.RIGHT_BACK) || (role == Player.LEFT_BACK)) {
            skill[0] = skillTackling;
            skill[1] = skillSpeed;
            skill[2] = skillPassing;
            skill[3] = skillShooting;
            skill[4] = skillHeading;
            skill[5] = skillControl;
            skill[6] = skillFinishing;
            skills = "TSPVHCF";

        } else if (role == Player.DEFENDER) {
            skill[0] = skillTackling;
            skill[1] = skillHeading;
            skill[2] = skillPassing;
            skill[3] = skillSpeed;
            skill[4] = skillShooting;
            skill[5] = skillControl;
            skill[6] = skillFinishing;
            skills = "THPSVCF";

        } else if ((role == Player.RIGHT_WINGER)
                || (role == Player.LEFT_WINGER)) {
            skill[0] = skillControl;
            skill[1] = skillSpeed;
            skill[2] = skillPassing;
            skill[3] = skillTackling;
            skill[4] = skillHeading;
            skill[5] = skillFinishing;
            skill[6] = skillShooting;
            skills = "CSPTHFV";

        } else if (role == Player.MIDFIELDER) {
            skill[0] = skillPassing;
            skill[1] = skillTackling;
            skill[2] = skillControl;
            skill[3] = skillHeading;
            skill[4] = skillShooting;
            skill[5] = skillSpeed;
            skill[6] = skillFinishing;
            skills = "PTCHVSF";

        } else if (role == Player.ATTACKER) {
            skill[0] = skillHeading;
            skill[1] = skillFinishing;
            skill[2] = skillSpeed;
            skill[3] = skillShooting;
            skill[4] = skillControl;
            skill[5] = skillPassing;
            skill[6] = skillTackling;
            skills = "HFSVCPT";
        }

        boolean ordered = false;
        while (!ordered) {
            ordered = true;
            for (int i = 0; i < 6; i++) {
                if (skill[i] < skill[i + 1]) {
                    int tmp = skill[i];
                    skill[i] = skill[i + 1];
                    skill[i + 1] = tmp;

                    skills = skills.substring(0, i)
                            + skills.substring(i + 1, i + 2)
                            + skills.substring(i, i + 1)
                            + skills.substring(i + 2);

                    ordered = false;
                }
            }
        }
    }

    public boolean update(Match match, boolean limit) {

        // physical parameters
        // speeds are in pixel/s
        // TODO: change in function of time and stamina
        speed = 130 + 4 * skillSpeed;

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

        if (limit == true) {
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

    public float targetDistance() {
        return Emath.dist(tx, ty, x, y);
    }

    public float targetAngle() {
        return Emath.aTan2(ty - y, tx - x);
    }

    public void watchBall() {
        a = Math.round((Emath.aTan2(y - match.ball.y, x - match.ball.x) + 180) / 45.0f) * 45.0f;
    }

    public boolean searchFacingPlayer(boolean longRange) {
        return searchFacingPlayer(longRange, false);
    }

    public boolean searchFacingPlayer(boolean longRange, boolean inAction) {

        float minDistance = 0.0f;
        float maxDistance = Const.TOUCH_LINE / 2;
        if (longRange) {
            minDistance = Const.TOUCH_LINE / 2;
            maxDistance = Const.TOUCH_LINE;
        }

        float maxAngle;
        if (inAction) {
            maxAngle = 15.5f + skillPassing;
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
