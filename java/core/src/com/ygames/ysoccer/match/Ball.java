package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.framework.GLGame.LogType.BALL;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.CROSSBAR_H;
import static com.ygames.ysoccer.match.Const.FLAGPOST_H;
import static com.ygames.ysoccer.match.Const.GOAL_DEPTH;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.JUMPER_H;
import static com.ygames.ysoccer.match.Const.JUMPER_X;
import static com.ygames.ysoccer.match.Const.JUMPER_Y;
import static com.ygames.ysoccer.match.Const.POST_R;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;

class Ball {

    // motion & graphics
    float x;
    float y;
    float z;
    private float zMax;
    float x0;
    float y0;
    private float z0;
    float v;
    float vMax;
    float vz;
    private float vzMax;
    private float bouncing_speed;
    float a;
    float s;
    private float f;

    int xSide; // -1=left, 1=right
    int ySide; // -1=up, 1=down

    final Vector3[] predictionL = new Vector3[Const.BALL_PREDICTION];
    final Vector3[] prediction = new Vector3[Const.BALL_PREDICTION];
    final Vector3[] predictionR = new Vector3[Const.BALL_PREDICTION];
    final Data[] data = new Data[Const.REPLAY_SUBFRAMES];

    // tactics
    int zoneX;
    int zoneY;
    float mx;
    float my;

    Player owner;
    Player ownerLast;
    Player goalOwner;
    Player holder;

    private final SceneSettings sceneSettings;

    Ball(SceneSettings sceneSettings) {
        this.sceneSettings = sceneSettings;

        for (int frm = 0; frm < Const.BALL_PREDICTION; frm++) {
            predictionL[frm] = new Vector3();
            prediction[frm] = new Vector3();
            predictionR[frm] = new Vector3();
        }

        for (int i = 0; i < data.length; i++) {
            data[i] = new Data();
        }
    }

    void setX(float x) {
        this.x = x;
        xSide = EMath.sgn(x);
    }

    void setY(float y) {
        this.y = y;
        ySide = EMath.sgn(y);
    }

    void setZ(float z) {
        this.z = z;
    }

    public void setPosition(float x, float y, float z) {
        setX(x);
        setY(y);
        setZ(z);
        v = 0;
        vz = 0;
        s = 0;
    }

    public void setPosition(Vector2 position) {
        this.setPosition(position.x, position.y, 0);
    }

    public void update() {
        // store old values
        x0 = x;
        y0 = y;
        z0 = z;

        updatePhysics(true);

        if (bouncing_speed > 0) {
            Assets.Sounds.bounce.play(Math.min(bouncing_speed / 250, 1) * Assets.Sounds.volume / 100f);
            bouncing_speed = 0;
        }

        // animation
        f = (f + 4 + v / 2500) % 4;
    }

    private void updatePhysics(boolean saveBouncingSpeed) {
        // angle & spin
        a += Const.SPIN_FACTOR / Const.SECOND * s;
        s *= 1 - Const.SPIN_DAMPENING / Const.SECOND;

        // position & speed
        setX(x + v / Const.SECOND * EMath.cos(a));
        setY(y + v / Const.SECOND * EMath.sin(a));

        if (z < 1) {
            // grass friction
            v -= sceneSettings.grass.friction / Const.SECOND * Math.sqrt(Math.abs(v));
        } else {
            // air friction
            v *= 1 - Const.AIR_FRICTION / Const.SECOND;
        }

        // wind
        if (v > 0 && z > 0) {
            float vx = v * EMath.cos(a);
            float vy = v * EMath.sin(a);

            float windV = 0.025f * (float) Math.log10(1 + z) * sceneSettings.wind.speed;
            float windA = 45.0f * sceneSettings.wind.direction;

            float windVx = windV * EMath.cos(windA);
            float windVy = windV * EMath.sin(windA);

            vx += windVx;
            vy += windVy;

            v = EMath.hypo(vx, vy);
            a = EMath.aTan2(vy, vx);
        }

        // back to zero!
        if (v <= 0) {
            v = 0;
            s = 0;
        }

        if (v > vMax) {
            vMax = v;
        }

        // z position
        setZ(z + vz / Const.SECOND);

        // z speed
        if (z > 0) {
            vz -= Const.GRAVITY;

            // air friction
            vz *= 1 - Const.AIR_FRICTION / Const.SECOND;
        }

        // bounce
        if (z < 0 && vz < 0) {
            setZ(0);
            if (vz > -40) {
                vz = 0;
            } else {
                // bounce
                v *= (1 + vz / 1400.0f);
                vz *= -Const.BOUNCE * sceneSettings.grass.bounce;
                if (saveBouncingSpeed) {
                    bouncing_speed = vz;
                }
            }
        }

        if (z > zMax) {
            zMax = z;
        }

        if (vz > vzMax) {
            vzMax = vz;
        }
    }

    public void updatePrediction() {

        PhysicsSet.saveValues(this);
        a -= 45f;

        for (int frm = 0; frm < Const.BALL_PREDICTION; frm++) {
            predictionL[frm].x = Math.round(x);
            predictionL[frm].y = Math.round(y);
            predictionL[frm].z = Math.round(z);
            for (int subframe = 0; subframe < GLGame.SUBFRAMES; subframe++) {
                updatePhysics(false);
            }
        }

        PhysicsSet.restoreValues(this);

        for (int frm = 0; frm < Const.BALL_PREDICTION; frm++) {
            prediction[frm].x = Math.round(x);
            prediction[frm].y = Math.round(y);
            prediction[frm].z = Math.round(z);
            for (int subframe = 0; subframe < GLGame.SUBFRAMES; subframe++) {
                updatePhysics(false);
            }
        }

        PhysicsSet.restoreValues(this);
        a += 45f;

        for (int frm = 0; frm < Const.BALL_PREDICTION; frm++) {
            predictionR[frm].x = Math.round(x);
            predictionR[frm].y = Math.round(y);
            predictionR[frm].z = Math.round(z);
            for (int subframe = 0; subframe < GLGame.SUBFRAMES; subframe++) {
                updatePhysics(false);
            }
        }

        PhysicsSet.restoreValues(this);
    }

    void updateZone(float x, float y) {

        // zone
        zoneX = Math.round(x / Const.BALL_ZONE_DX);
        zoneX = EMath.bound(zoneX, -2, +2);

        zoneY = Math.round(y / Const.BALL_ZONE_DY);
        zoneY = EMath.bound(zoneY, -3, +3);

        // mx / my
        mx = (((x % Const.BALL_ZONE_DX) / Const.BALL_ZONE_DX + 1.5f) % 1) - 0.5f;
        my = (((y % Const.BALL_ZONE_DY) / Const.BALL_ZONE_DY + 1.5f) % 1) - 0.5f;
    }

    public void inFieldKeep() {

        // left
        if (x <= Const.FIELD_XMIN) {
            setX(Const.FIELD_XMIN);
            v = 0.5f * v;
            a = (180 - a) % 360;
            s = -s;
        }

        // right
        if (x >= Const.FIELD_XMAX) {
            setX(Const.FIELD_XMAX);
            v = 0.5f * v;
            a = (180 - a) % 360;
            s = -s;
        }

        // top
        if (y <= Const.FIELD_YMIN) {
            setY(Const.FIELD_YMIN);
            v = 0.5f * v;
            a = (-a) % 360;
            s = -s;
        }

        // bottom
        if (y >= Const.FIELD_YMAX) {
            setY(Const.FIELD_YMAX);
            v = 0.5f * v;
            a = (-a) % 360;
            s = -s;
        }
    }

    boolean isInsidePenaltyArea(int ySide) {
        return Const.isInsidePenaltyArea(x, y, ySide);
    }

    boolean collisionGoal() {

        if (ySide == 0) {
            return false;
        }

        boolean hit = false;

        // top corners
        if ((ySide * y < GOAL_LINE)
                && (EMath.dist(y0, z0, ySide * GOAL_LINE, CROSSBAR_H) > 6)
                && (EMath.dist(y, z, ySide * GOAL_LINE, CROSSBAR_H) <= 6)
                && ((EMath.dist(x, y, -POST_X, ySide * (GOAL_LINE + 1)) <= 6)
                || (EMath.dist(x, y, POST_X, ySide * (GOAL_LINE + 1)) <= 6))) {

            // real ball x-y angle (when spinning, it is different from ball.a)
            float ballAxy = EMath.aTan2(y - y0, x - x0);

            v = 0.5f * v;
            a = (-ballAxy + 360) % 360;
            s = 0;
            setX(x0);
            setY(y0);
            setZ(z0);

            hit = true;
            bouncing_speed = v;

            GLGame.debug(BALL, this, "Goal top corner collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        // crossbar
        else if ((EMath.dist(y0, z0, ySide * GOAL_LINE, CROSSBAR_H) > 5)
                && (EMath.dist(y, z, ySide * GOAL_LINE, CROSSBAR_H) <= 5)
                && (-(POST_X + POST_R) < x && x < (POST_X + POST_R))) {

            // cartesian coordinates
            // real ball x-y angle (when spinning, it is different from ball.a)
            float ballAxy = EMath.aTan2(y - y0, x - x0);

            float ballVx = v * EMath.cos(ballAxy);
            float ballVy = v * EMath.sin(ballAxy);

            // collision y-z angle
            float angle = EMath.aTan2(z - CROSSBAR_H, y - ySide * GOAL_LINE);

            // ball y-z speed and angle
            float ballVyz = (float) Math.sqrt(ballVy * ballVy + vz * vz);
            float ballAyz = EMath.aTan2(vz, ballVy);

            // new angle
            ballAyz = (2 * angle - ballAyz + 180);

            // new y-z speeds
            ballVy = 0.5f * ballVyz * EMath.cos(ballAyz);
            vz = 0.5f * ballVyz * EMath.sin(ballAyz);

            // new speed, angle & spin
            v = (float) Math.sqrt(ballVx * ballVx + ballVy * ballVy);
            a = EMath.aTan2(ballVy, ballVx);
            s = 0;
            setX(x0);
            setY(y0);
            setZ(z0);

            hit = true;
            bouncing_speed = 0.5f * ballVyz;

            GLGame.debug(BALL, this, "Goal crossbar collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        // posts
        else if ((EMath.dist(x0, y0, xSide * POST_X, ySide * (GOAL_LINE + 1)) > 5)
                && (EMath.dist(x, y, xSide * POST_X, ySide * (GOAL_LINE + 1)) <= 5)
                && (z <= (CROSSBAR_H + POST_R))) {

            // real ball x-y angle (when spinning, it is different from ball.a)
            float ballAxy = EMath.aTan2(y - y0, x - x0);

            float angle = EMath.aTan2(y - ySide * GOAL_LINE, x - xSide * POST_X);

            // new speed, angle & spin
            v = 0.5f * v;
            a = (2 * angle - ballAxy + 180) % 360;
            s = 0;
            setX(x0);
            setY(y0);
            setZ(z0);

            hit = true;
            bouncing_speed = v;

            GLGame.debug(BALL, this, "Goal post collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        // sound
        if (hit) {
            float volume = Math.min(EMath.floor(bouncing_speed / 10) / 10f, 1);
            Assets.Sounds.post.play(volume * Assets.Sounds.volume / 100f);
            bouncing_speed = 0;
        }

        return hit;
    }

    void collisionFlagPosts() {

        if ((EMath.dist(Math.abs(x), Math.abs(y), TOUCH_LINE, GOAL_LINE) <= BALL_R + 1)
                && (EMath.dist(Math.abs(x0), Math.abs(y0), TOUCH_LINE, GOAL_LINE) > BALL_R + 1)
                && (z <= FLAGPOST_H)) {

            // real ball x-y angle (when spinning, it is different from ball.a)
            float ballAxy = EMath.aTan2(y - y0, x - x0);

            float angle = EMath.aTan2(y - Math.signum(y) * GOAL_LINE, x - Math.signum(x) * TOUCH_LINE);
            v = 0.3f * v;
            a = (2 * angle - ballAxy + 180) % 360;
            s = 0;

            setX(x0);
            setY(y0);
            setZ(z0);

            float volume = Math.min(EMath.floor(v / 10) / 20f, 1);
            Assets.Sounds.post.play(volume * Assets.Sounds.volume / 100f);

            GLGame.debug(BALL, this, "Flag post collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }
    }

    void collisionNet() {

        boolean xTest = Math.abs(x0) < POST_X - BALL_R / 2f;
        if (!xTest) return;
        boolean yTest = Math.abs(y0) > GOAL_LINE && Math.abs(y0) < GOAL_LINE + GOAL_DEPTH - BALL_R / 2f;
        if (!yTest) return;
        boolean zTest = z0 < (CROSSBAR_H - BALL_R / 2f - (Math.abs(y0) - GOAL_LINE) / 3f);
        if (!zTest) return;

        boolean collision = false;

        // back
        if (Math.abs(y) >= GOAL_LINE + GOAL_DEPTH - BALL_R / 2f) {

            float ballVx = v * EMath.cos(a);
            float ballVy = v * EMath.sin(a);

            ballVx = 0.5f * ballVx;
            ballVy = -0.1f * ballVy;

            v = EMath.hypo(ballVx, ballVy);
            a = EMath.aTan2(ballVy, ballVx);

            collision = true;

            GLGame.debug(BALL, this, "Net internal back collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        // sides
        if (Math.abs(x) >= POST_X - BALL_R / 2f) {

            float ballVx = v * EMath.cos(a);
            float ballVy = v * EMath.sin(a);

            ballVx = -0.1f * ballVx;
            ballVy = 0.5f * ballVy;

            v = EMath.hypo(ballVx, ballVy);
            a = EMath.aTan2(ballVy, ballVx);

            collision = true;

            GLGame.debug(BALL, this, "Net internal side collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        // top
        if (z >= (CROSSBAR_H - BALL_R / 2f - (Math.abs(y) - GOAL_LINE) / 3f)) {

            float ballVx = v * EMath.cos(a);
            float ballVy = v * EMath.sin(a);

            float ballVyz = 0.25f * EMath.sqrt(ballVy * ballVy + vz * vz);
            float ballAyz = EMath.aTan2(0, y - Math.signum(y) * GOAL_LINE);

            ballVy = ballVyz * EMath.cos(ballAyz);
            ballVy = Math.signum(ballVy) * Math.abs(ballVy);

            vz = -0.25f * vz;
            v = EMath.hypo(ballVx, ballVy);
            a = EMath.aTan2(ballVy, ballVx);

            collision = true;

            GLGame.debug(BALL, this, "Net internal top collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        if (collision) {
            setX(x0);
            setY(y0);
            setZ(z0);
        }
    }

    void collisionNetOut() {

        boolean xTest = Math.abs(x) <= POST_X + BALL_R / 2f;
        if (!xTest) return;
        boolean yTest = EMath.isIn(Math.abs(y), GOAL_LINE, GOAL_LINE + GOAL_DEPTH + BALL_R / 2f);
        if (!yTest) return;
        boolean zTest = z <= (CROSSBAR_H + BALL_R - (Math.abs(y) - GOAL_LINE) / 3f);
        if (!zTest) return;

        boolean collision = false;

        // top
        if (z0 > (CROSSBAR_H + BALL_R - (Math.abs(y0) - GOAL_LINE) / 3f)) {

            float ballVx = v * EMath.cos(a);
            float ballVy = v * EMath.sin(a);

            float ballVyz = 0.25f * EMath.sqrt(ballVy * ballVy + vz * vz);
            float ballAyz = EMath.aTan2(0, y - Math.signum(y) * GOAL_LINE);

            ballVy = ballVyz * EMath.cos(ballAyz);
            ballVy = Math.signum(ballVy) * Math.max(30, Math.abs(ballVy));

            vz = -0.25f * vz;
            v = EMath.hypo(ballVx, ballVy);
            a = EMath.aTan2(ballVy, ballVx);

            collision = true;

            GLGame.debug(BALL, this, "Net external top collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        // back
        if (Math.abs(y0) > GOAL_LINE + GOAL_DEPTH + BALL_R / 2f) {

            float ballVx = v * EMath.cos(a);
            float ballVy = v * EMath.sin(a);

            ballVx = 0.5f * ballVx;
            ballVy = -0.15f * ballVy;

            v = EMath.hypo(ballVx, ballVy);
            a = EMath.aTan2(ballVy, ballVx);

            collision = true;

            GLGame.debug(BALL, this, "Net external back collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        // sides
        if (Math.abs(x0) > POST_X + BALL_R / 2f) {

            float ballVx = v * EMath.cos(a);
            float ballVy = v * EMath.sin(a);

            ballVx = -0.15f * ballVx;
            ballVy = 0.5f * ballVy;

            v = EMath.hypo(ballVx, ballVy);
            a = EMath.aTan2(ballVy, ballVx);

            collision = true;

            GLGame.debug(BALL, this, "Net external side collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }

        if (collision) {
            setX(x0);
            setY(y0);
            setZ(z0);
        }
    }

    void collisionJumpers() {

        if ((EMath.dist(Math.abs(x), Math.abs(y), JUMPER_X, JUMPER_Y) <= BALL_R + 1)
                && (EMath.dist(Math.abs(x0), Math.abs(y0), JUMPER_X, JUMPER_Y) > BALL_R + 1)
                && (z < JUMPER_H)) {

            // real ball x-y angle (when spinning, it is different from ball.a)
            float ballAxy = EMath.aTan2(y - y0, x - x0);

            float angle = EMath.aTan2(y - Math.signum(y) * JUMPER_Y, x - Math.signum(x) * JUMPER_X);
            v = 0.3f * v;
            a = (2 * angle - ballAxy + 180) % 360;
            s = 0;

            setX(x0);
            setY(y0);
            setZ(z0);

            float volume = Math.min(EMath.floor(v / 10) / 20f, 1);
            Assets.Sounds.post.play(volume * Assets.Sounds.volume / 100f);

            GLGame.debug(BALL, this, "Jumper collision, v: " + v + ", a: " + a + ", vz: " + vz);
        }
    }

    void collisionPlayer(Player player) {
        // real ball x-y angle (when spinning, it is different from ball.a)
        float ballAxy = EMath.aTan2(y - y0, x - x0);
        float angle = EMath.aTan2(y - player.y, x - player.x);
        v = 0.25f * v;
        a = (2 * angle - ballAxy + 180) % 360;
        s = 0;

        setX(x0);
        setY(y0);
        setZ(z0);
    }

    public void setHolder(Player player) {
        holder = player;
    }

    public void save(int subframe) {
        data[subframe].x = Math.round(x);
        data[subframe].y = Math.round(y);
        data[subframe].z = Math.round(z);
        data[subframe].fmx = (int) Math.floor(f);
    }

    boolean isInsideDirectShotArea(int ySide) {
        return Const.isInsideDirectShotArea(x, y, ySide);
    }

    static class PhysicsSet {
        static float x;
        static float y;
        static float z;
        static float v;
        static float vz;
        static float a;
        static float s;

        static void saveValues(Ball ball) {
            x = ball.x;
            y = ball.y;
            z = ball.z;
            v = ball.v;
            vz = ball.vz;
            a = ball.a;
            s = ball.s;
        }

        static void restoreValues(Ball ball) {
            ball.setX(x);
            ball.setY(y);
            ball.setZ(z);
            ball.v = v;
            ball.vz = vz;
            ball.a = a;
            ball.s = s;
        }
    }
}
