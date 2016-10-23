package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.framework.math.Vector3;

public class Ball {

    // motion & graphics
    float x;
    float y;
    float z;
    float x0;
    float y0;
    float z0;
    float v;
    float vz;
    float a;
    float s;
    float f;

    int xSide; // -1=left, 1=right
    int ySide; // -1=up, 1=down

    Vector3[] prediction = new Vector3[Const.BALL_PREDICTION];
    Data[] data = new Data[Const.REPLAY_SUBFRAMES];

    // tactics
    int zoneX;
    int zoneY;
    float mx;
    float my;
    int nextZoneX;
    int nextZoneY;

    Player owner;
    Player ownerLast;
    Player goalOwner;
    Player holder;

    Match match;

    public Ball() {

        for (int frm = 0; frm < Const.BALL_PREDICTION; frm++) {
            prediction[frm] = new Vector3();
        }

        for (int i = 0; i < data.length; i++) {
            data[i] = new Data();
        }

    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        v = 0;
        vz = 0;
        s = 0;
    }

    public float update() {
        if (owner != null) {
            if ((owner.ballDistance > 13)
                    || (z > (Const.PLAYER_H + Const.BALL_R))) {
                setOwner(null);
            }
        }

        // store old values
        x0 = x;
        y0 = y;
        z0 = z;

        // side
        xSide = Emath.sgn(x);
        ySide = Emath.sgn(y);

        float bouncing_speed = updatePhysics();

        // animation
        f = (f + 4 + v / 2500) % 4;

        return bouncing_speed;
    }

    private float updatePhysics() {
        // angle & spin
        a += 4.0 / Const.SECOND * s;
        s *= 1 - 2.0 / Const.SECOND;

        // position & speed
        x += v / Const.SECOND * Emath.cos(a);
        y += v / Const.SECOND * Emath.sin(a);

        if (z < 1) {
            // grass friction
            v -= match.settings.grass.friction / Const.SECOND
                    * Math.sqrt(Math.abs(v));
        } else {
            // air friction
            v *= 1 - 0.3 / Const.SECOND;
        }

        // wind
        if (v > 0 && z > 0) {
            float vx = v * Emath.cos(a);
            float vy = v * Emath.sin(a);

            float windV = 0.025f * (float) Math.log10(1 + z)
                    * match.settings.wind.speed;
            float windA = 45.0f * match.settings.wind.direction;

            float windVx = windV * Emath.cos(windA);
            float windVy = windV * Emath.sin(windA);

            vx += windVx;
            vy += windVy;

            v = Emath.hypo(vx, vy);
            a = Emath.aTan2(vy, vx);
        }

        // back to zero!
        if (v <= 0) {
            v = 0;
            s = 0;
        }

        // z position
        z += vz / Const.SECOND;

        // z speed
        if (z > 0) {
            vz -= Const.GRAVITY;

            // air friction
            vz *= 1 - 0.3 / Const.SECOND;
        }

        // bounce
        float bouncing_speed = 0;
        if (z < 0 && vz < 0) {
            z = 0;
            if (vz > -50) {
                vz = 0;
            } else {
                // bounce
                v *= (1 + vz / 1400.0f);
                vz *= -0.01f * match.settings.grass.bounce;
            }
            bouncing_speed = vz;
        }

        return bouncing_speed;

    }

    public void updatePrediction() {

        // save starting values
        float old_x = x;
        float old_y = y;
        float old_z = z;
        float old_v = v;
        float old_vz = vz;
        float old_a = a;
        float old_s = s;

        for (int frm = 0; frm < Const.BALL_PREDICTION; frm++) {
            prediction[frm].x = Math.round(x);
            prediction[frm].y = Math.round(y);
            prediction[frm].z = Math.round(z);
            for (int subframe = 0; subframe < GLGame.SUBFRAMES; subframe++) {
                updatePhysics();
            }
        }

        // restore starting values
        x = old_x;
        y = old_y;
        z = old_z;
        v = old_v;
        vz = old_vz;
        a = old_a;
        s = old_s;

    }

    public void updateZone(float x, float y, float v, float a) {

        // zone
        zoneX = Math.round(x / Const.BALL_ZONE_DX);
        zoneX = Emath.bound(zoneX, -2, +2);

        zoneY = Math.round(y / Const.BALL_ZONE_DY);
        zoneY = Emath.bound(zoneY, -3, +3);

        // mx / my
        mx = (((x % Const.BALL_ZONE_DX) / Const.BALL_ZONE_DX + 1.5f) % 1) - 0.5f;
        my = (((y % Const.BALL_ZONE_DY) / Const.BALL_ZONE_DY + 1.5f) % 1) - 0.5f;

        // zone_next
        if (v > 0) {
            nextZoneX = zoneX + Math.round(Emath.cos(a));
            nextZoneX = Emath.bound(nextZoneX, -2, +2);

            nextZoneY = zoneY + Math.round(Emath.sin(a));
            nextZoneY = Emath.bound(nextZoneY, -3, +3);
        }

    }

    public void inFieldKeep() {

        // left
        if (x <= Const.FIELD_XMIN) {
            x = Const.FIELD_XMIN;
            v = 0.5f * v;
            a = (180 - a) % 360;
            s = -s;
        }

        // right
        if (x >= Const.FIELD_XMAX) {
            x = Const.FIELD_XMAX;
            v = 0.5f * v;
            a = (180 - a) % 360;
            s = -s;
        }

        // top
        if (y <= Const.FIELD_YMIN) {
            y = Const.FIELD_YMIN;
            v = 0.5f * v;
            a = (-a) % 360;
            s = -s;
        }

        // bottom
        if (y >= Const.FIELD_YMAX) {
            y = Const.FIELD_YMAX;
            v = 0.5f * v;
            a = (-a) % 360;
            s = -s;
        }
    }

    public boolean collisionGoal() {

        if (ySide == 0) {
            return false;
        }

        boolean hit = false;

        // "sette" **
        if ((ySide * y < Const.GOAL_LINE)
                && (Emath.dist(y0, z0, ySide * Const.GOAL_LINE,
                Const.CROSSBAR_H) > 6)
                && (Emath.dist(y, z, ySide * Const.GOAL_LINE, Const.CROSSBAR_H) <= 6)
                && ((Emath.dist(x, y, -Const.POST_X, ySide
                * (Const.GOAL_LINE + 1)) <= 6) || (Emath.dist(x, y,
                Const.POST_X, ySide * (Const.GOAL_LINE + 1)) <= 6))) {

            // real ball x-y angle (when spinned, it is different from ball.a)
            float ballAxy = Emath.aTan2(y - y0, x - x0);

            v = 0.5f * v;
            a = (-ballAxy + 360) % 360;
            s = 0;
            x = x0;
            y = y0;

            hit = true;
        }

        // crossbar
        else if ((Emath.dist(y0, z0, ySide * Const.GOAL_LINE, Const.CROSSBAR_H) > 5)
                && (Emath.dist(y, z, ySide * Const.GOAL_LINE, Const.CROSSBAR_H) <= 5)
                && (-(Const.POST_X + Const.POST_R) < x && x < (Const.POST_X + Const.POST_R))) {

            // cartesian coordinates
            // real ball x-y angle (when spinned, it is different from ball.a)
            float ballAxy = Emath.aTan2(y - y0, x - x0);

            float ballVx = v * Emath.cos(ballAxy);
            float ballVy = v * Emath.sin(ballAxy);

            // collision y-z angle
            float angle = Emath.aTan2(z - Const.CROSSBAR_H, y - ySide
                    * Const.GOAL_LINE);

            // ball y-z speed and angle
            float ballVyz = (float) Math.sqrt(ballVy * ballVy + vz * vz);
            float ballAyz = Emath.aTan2(vz, ballVy);

            // new angle
            ballAyz = (2 * angle - ballAyz + 180);

            // new y-z speeds
            ballVy = 0.5f * ballVyz * Emath.cos(ballAyz);
            vz = 0.5f * ballVyz * Emath.sin(ballAyz);

            // new speed, angle & spin
            v = (float) Math.sqrt(ballVx * ballVx + ballVy * ballVy);
            a = Emath.aTan2(ballVy, ballVx);
            s = 0;
            y = y0;
            z = z0;

            hit = true;

            // posts
        } else if ((Emath.dist(x0, y0, xSide * Const.POST_X, ySide
                * (Const.GOAL_LINE + 1)) > 5)
                && (Emath.dist(x, y, xSide * Const.POST_X, ySide
                * (Const.GOAL_LINE + 1)) <= 5)
                && (z <= (Const.CROSSBAR_H + Const.POST_R))) {

            // real ball x-y angle (when spinned, it is different from ball.a)
            float ballAxy = Emath.aTan2(y - y0, x - x0);

            float angle = Emath.aTan2(y - ySide * (Const.GOAL_LINE + 1), x
                    - xSide * Const.POST_X);

            // new speed, angle & spin
            v = 0.5f * v;
            a = (2 * angle - ballAxy + 180) % 360;
            s = 0;
            x = x0;
            y = y0;

            hit = true;

        }

        // sound
        if (hit) {
            match.listener.postSound(1.0f * match.settings.sfxVolume);
        }
        return hit;

    }

    public void collisionFlagposts() {

        if ((xSide == 0) || (ySide == 0)) {
            return;
        }

        if ((Emath
                .dist(x, y, xSide * Const.TOUCH_LINE, ySide * Const.GOAL_LINE) <= 5)
                && (z <= Const.FLAGPOST_H)) {
            // real ball x-y angle (when spinned, it is different from ball.a)
            float ballAxy = Emath.aTan2(y - y0, x - x0);

            float angle = Emath.aTan2(y - ySide * Const.GOAL_LINE, x
                    - (xSide * Const.TOUCH_LINE));
            v = 0.3f * v;
            a = (2 * angle - ballAxy + 180) % 360;
            s = 0;
            x = x0;
            y = y0;

            match.listener.postSound(0.5f * match.settings.sfxVolume);
        }

    }

    public void collisionNet() {

        boolean sfx = false;

        if (ySide * y > Const.GOAL_LINE) {
            if ((Emath.hypo(1.6f * (y - ySide * Const.GOAL_LINE), z) >= Const.CROSSBAR_H)
                    && (Emath.hypo((1.6f * (y0 - ySide * Const.GOAL_LINE)), z0) < Const.CROSSBAR_H)) {
                if (v > 200) {
                    sfx = true;
                }
                v = v / 10;
                vz = 0;
                a = Emath.aTan2(-Emath.sin(a) / 4, Emath.cos(a));
                s = 0;
            }

            // left/right
            if ((x0 * xSide < Const.POST_X) && (x * xSide >= Const.POST_X)) {
                if (v > 200) {
                    sfx = true;
                }
                v = v / 10.0f;
                vz = 0;
                a = Emath.aTan2(Emath.sin(a), -Emath.cos(a) / 4.0f);
                s = 0;
            }

            if (sfx) {
                match.listener.netSound(0.5f * match.settings.sfxVolume);
            }

        }

    }

    public void collisionNetOut() {

        // back-top
        if ((Emath.dist(y0, z0, ySide * Const.GOAL_LINE, 0) <= Const.CROSSBAR_H)
                && (-Const.POST_X < x && x < Const.POST_X)) {
            float ballVx = v * Emath.cos(a); // cartesian coord.
            float ballVy = v * Emath.sin(a);

            float angle = Emath.aTan2(z, y - ySide * Const.GOAL_LINE); // y-z
            // angle
            float ballVyz = (float) Math.sqrt(ballVy * ballVy + vz * vz); // y-z
            // speed
            float ballAyz = Emath.aTan2(vz, -ballVy); // y-z angle

            ballVyz = ballVyz / 10.0f; // net friction

            ballAyz = (2 * angle - ballAyz + 180) % 360; // new angle
            ballVy = ballVyz * Emath.cos(angle); // new y-z speeds
            vz = ballVyz * Emath.sin(angle);

            v = Emath.hypo(ballVx, ballVy); // back to polar coord.
            a = Emath.aTan2(ballVy, ballVx);
            s = -s;

        }

        // left/right
        if ((x0 * xSide > Const.POST_X) && (x * xSide <= Const.POST_X)
                && (z < Const.CROSSBAR_H + Const.BALL_R)) {
            if (Emath.isIn(ySide * y, (Const.GOAL_LINE + Const.BALL_R),
                    (Const.GOAL_LINE + Const.GOAL_DEPTH + Const.BALL_R))) {
                v = (float) Math.sqrt(v);
                a = Emath.aTan2(Emath.sin(a), -Emath.cos(a) / 4);
            }
        }

    }

    public void collisionJumpers() {

        if ((Emath.dist(x, y, xSide * Const.JUMPER_X, ySide * Const.JUMPER_Y) <= 5)
                && (z <= Const.JUMPER_H)) {
            // real ball x-y angle (when spinned, it is different from ball.a)
            float ballAxy = Emath.aTan2(y - y0, x - x0);

            float angle = Emath.aTan2(y - ySide * Const.JUMPER_Y, x
                    - (xSide * Const.JUMPER_X));
            v = 0.3f * v;
            a = (2 * angle - ballAxy + 180) % 360;
            s = 0;
            x = x0;
            y = y0;
            match.listener.bounceSound(0.5f * match.settings.sfxVolume);
        }

    }

    public void collisionPlayer(Player player, float newV) {
        // real ball x-y angle (when spinned, it is different from ball.a)
        float ballAxy = Emath.aTan2(y - y0, x - x0);

        float angle = Emath.aTan2(y - player.y, x - player.x);
        v = newV;
        a = (2 * angle - ballAxy + 180) % 360;
        s = 0;
        x = x0;
        y = y0;
    }

    public void setOwner(Player newOwner) {
        setOwner(newOwner, true);
    }

    public void setOwner(Player newOwner, boolean updateGoalOwner) {
        owner = newOwner;
        if (newOwner != null) {
            ownerLast = newOwner;
            if (updateGoalOwner) {
                goalOwner = newOwner;
            }
        }
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

}
