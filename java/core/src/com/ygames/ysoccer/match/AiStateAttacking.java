package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector3;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.framework.GLGame.LogType.AI_ATTACKING;
import static com.ygames.ysoccer.match.AiFsm.Id.STATE_ATTACKING;
import static com.ygames.ysoccer.match.Const.DIRECT_SHOT_DISTANCE;
import static com.ygames.ysoccer.match.Const.GOAL_AREA_H;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class AiStateAttacking extends AiState {

    AiStateAttacking(AiFsm fsm) {
        super(STATE_ATTACKING, fsm);
    }

    static class Parameters {
        static int URGENT_TARGET_UPDATE_INTERVAL = 10;
        static int TARGET_UPDATE_INTERVAL = 30;
        static int CONTROLS_UPDATE_INTERVAL = 5;
        static int MATE_SEARCH_INTERVAL = 8;
        static float PASSING_PROBABILITY = 0.3f;

        static int PLAYER_DETECTION_RADIUS = 300;
        static int OUTSIDE_DETECTION_RADIUS = 60;

        static float MATE_FACTOR = 1f;
        static float OPPONENT_FACTOR = 1.5f;
        static float OWN_GOAL_FACTOR = 3.5f;
        static float GOAL_FACTOR = 2.5f;
        static float GOAL_FACTOR_INSIDE_PENALTY_AREA = 3.5f;
    }

    private float controlsAngle;
    private float targetAngle;

    @Override
    void entryActions() {
        super.entryActions();

        controlsAngle = player.a;
        ai.x0 = Math.round(EMath.cos(controlsAngle));
        ai.y0 = Math.round(EMath.sin(controlsAngle));

        targetAngle = controlsAngle;

        GLGame.debug(AI_ATTACKING, player.numberName(), "Enters attacking state, player.a: " + player.a + ", controlsAngle: " + controlsAngle + ", targetAngle: " + targetAngle);
    }

    @Override
    void doActions() {
        super.doActions();

        GLGame.debug(AI_ATTACKING, player.numberName(), "[TIMER: " + timer + "] controlsAngle: " + controlsAngle + ", player.a: " + player.a);

        if (isUrgentTargetUpdateTime()) {
            targetAngle += getUrgentAngleCorrection();
            GLGame.debug(AI_ATTACKING, player.numberName(), "Urgent target update time, targetAngle: " + targetAngle);
        }

        if (isTargetUpdateTime()) {
            targetAngle += getAngleCorrection();
            GLGame.debug(AI_ATTACKING, player.numberName(), "Target update, targetAngle: " + targetAngle);
        }

        if (isMateSearchingTime()) {
            player.searchPassingMate();
            GLGame.debug(AI_ATTACKING, player.numberName(), "Mate searching, passingMate: " + (player.passingMate == null ? "null" : player.passingMate.numberName()) + " with passingMateAngleCorrection: " + player.passingMateAngleCorrection + ", updating targetAngle: " + targetAngle + ", timer: " + timer);
        }

        if (isTurningTime()) {
            float signedAngleDiff = EMath.signedAngleDiff(targetAngle, player.a);
            if (Math.abs(signedAngleDiff) > 22.5f) {
                controlsAngle += 45f * Math.signum(signedAngleDiff);
                GLGame.debug(AI_ATTACKING, player.numberName(), "Turning, signedAngleDiff: " + signedAngleDiff + ", correction: " + (45f * Math.signum(signedAngleDiff)) + ", new controlsAngle: " + controlsAngle);
            } else {
                GLGame.debug(AI_ATTACKING, player.numberName(), "No turning needed");
            }
        }

        ai.x0 = Math.round(EMath.cos(controlsAngle));
        ai.y0 = Math.round(EMath.sin(controlsAngle));
    }

    private boolean isUrgentTargetUpdateTime() {
        return (timer - 1) % (Parameters.URGENT_TARGET_UPDATE_INTERVAL) == 0;
    }

    private boolean isTargetUpdateTime() {
        return (timer - 1) % Parameters.TARGET_UPDATE_INTERVAL == 0;
    }

    private boolean isMateSearchingTime() {
        return (timer - 1) % Parameters.MATE_SEARCH_INTERVAL == 0;
    }

    private boolean isTurningTime() {
        return (timer - 1) % Parameters.CONTROLS_UPDATE_INTERVAL == 0;
    }

    @Override
    State checkConditions() {

        // passing
        if (isMateSearchingTime()) {
            if (player.passingMate != null && Assets.random.nextFloat() < Parameters.PASSING_PROBABILITY) {
                return fsm.statePassing;
            }
        }

        // TODO:
        // if (is clear ahead){
        //     return fsm.autoPassing;
        // }

        if (player.ball.isInsideDirectShotArea(-player.team.side) && player.seesTheGoal()) {
            float visualWidth = goalVisualWidth(player.x, player.y);
            float probabilityByVisualWidth = probabilityByVisualWidth(visualWidth, 180);

            float distance = EMath.dist(player.x, player.y, POST_X * EMath.sgn(player.x), GOAL_LINE * EMath.sgn(player.y));
            float probabilityByDistance = probabilityByDistance(distance, 0.1f, DIRECT_SHOT_DISTANCE);

            float probability = (probabilityByVisualWidth + probabilityByDistance * probabilityByDistance) / 2;
            GLGame.debug(AI_ATTACKING, player.numberName(), "Inside direct shot area, visualWidth: " + visualWidth
                    + ", probabilityByVisualWidth: " + probabilityByVisualWidth
                    + ", distance: " + distance
                    + ", probabilityByDistance: " + probabilityByDistance
                    + ", probability: " + probability);
            if (Assets.random.nextFloat() < probability) {
                return fsm.stateKicking;
            }
        }

        // player has changed its state
        PlayerState playerState = player.getState();
        if (playerState != null
                && !playerState.checkId(STATE_STAND_RUN)) {
            return fsm.stateIdle;
        }

        // lost the ball
        if (player.ball.owner != player) {
            return fsm.stateSeeking;
        }

        return null;
    }

    private float getUrgentAngleCorrection() {

        GLGame.debug(AI_ATTACKING, player.numberName(), "player.x: " + player.x + ", player.y: " + player.y + ", controlsAngle: " + controlsAngle);

        // Vector3(left, center, right)
        Vector3 totalWeights = new Vector3(1, 1, 1);

        // avoid exiting
        Vector3 inFieldMap = getInFieldMap();
        GLGame.debug(AI_ATTACKING, player.numberName(), "In field map: " + inFieldMap);

        // sum all weights
        totalWeights.scl(inFieldMap);

        GLGame.debug(AI_ATTACKING, player.numberName(), "TotalWeights: " + totalWeights);

        // whenever players picks the ball near goal or touch lines
        // he needs to turn more than normal 45 degrees
        if (totalWeights.isZero()) {
            return emergencyAngle();
        }

        // if very near to goal area turn immediately to see the goal
        if (Math.abs(player.x) < (POST_X + 10)
                && EMath.isIn(player.y, -player.side * (GOAL_LINE - GOAL_AREA_H - 10), -player.side * GOAL_LINE)
                && !player.seesTheGoal()) {
            float signedAngleDiff = player.goalSignedAngleDiff();
            if (signedAngleDiff < -Const.SHOOTING_ANGLE_TOLERANCE) {
                GLGame.debug(AI_ATTACKING, player.numberName(), "Forcing left turn to see the goal");
                totalWeights.scl(2, 0, 0);
            }
            if (signedAngleDiff > Const.SHOOTING_ANGLE_TOLERANCE) {
                GLGame.debug(AI_ATTACKING, player.numberName(), "Forcing right turn to see the goal");
                totalWeights.scl(0, 0, 2);
            }
        }

        // finally decide
        if (totalWeights.x > Math.max(totalWeights.y, totalWeights.z)) {
            GLGame.debug(AI_ATTACKING, player.numberName(), "Turning left: -45");
            return -45;
        } else if (totalWeights.y >= totalWeights.z) {
            GLGame.debug(AI_ATTACKING, player.numberName(), "No turning: 0");
            return 0;
        } else {
            GLGame.debug(AI_ATTACKING, player.numberName(), "Turning right: +45");
            return 45;
        }
    }

    private float getAngleCorrection() {

        // Vector3(left, center, right)
        Vector3 totalWeights = new Vector3();

        // 1. minimize mates frame distance
        Vector3 mateWeights = getMateWeights();
        GLGame.debug(AI_ATTACKING, player.numberName(), "Mates weights: " + mateWeights);
        totalWeights.add(mateWeights.scl(Parameters.MATE_FACTOR));

        // 2. maximize opponent frame distance
        Vector3 opponentWeights = getOpponentWeights();
        GLGame.debug(AI_ATTACKING, player.numberName(), "Opponents weights: " + opponentWeights);
        totalWeights.add(opponentWeights.scl(Parameters.OPPONENT_FACTOR));

        // 3. push toward/away goals
        Vector3 goalWeights;
        float GOAL_FACTOR;
        if (player.ball.isInsidePenaltyArea(player.team.side)) {
            goalWeights = getOwnGoalWeights();
            GOAL_FACTOR = Parameters.OWN_GOAL_FACTOR;
        } else {
            goalWeights = getGoalWeights();
            if (player.ball.isInsidePenaltyArea(-player.side)) {
                GOAL_FACTOR = Parameters.GOAL_FACTOR_INSIDE_PENALTY_AREA;
            } else {
                GOAL_FACTOR = Parameters.GOAL_FACTOR;
            }
        }
        totalWeights.add(goalWeights.scl(GOAL_FACTOR));
        GLGame.debug(AI_ATTACKING, player.numberName(), "Goal weights: " + goalWeights);

        GLGame.debug(AI_ATTACKING, player.numberName(), "TotalWeights: " + totalWeights);

        // finally decide
        if (totalWeights.x > Math.max(totalWeights.y, totalWeights.z)) {
            GLGame.debug(AI_ATTACKING, player.numberName(), "Turning left: -45");
            return -45;
        } else if (totalWeights.y >= totalWeights.z) {
            GLGame.debug(AI_ATTACKING, player.numberName(), "No turning: 0");
            return 0;
        } else {
            GLGame.debug(AI_ATTACKING, player.numberName(), "Turning right: +45");
            return 45;
        }
    }

    private Vector3 getMateWeights() {
        Vector3 weights = new Vector3();
        int count = 0;
        for (int i = 0; i < TEAM_SIZE; i++) {
            Player ply = player.team.lineup.get(i);
            if (ply == player) continue;

            float dist = player.distanceFrom(ply);
            if (dist > Parameters.PLAYER_DETECTION_RADIUS) continue;

            float minFrameDistance = EMath.min(ply.frameDistanceL, ply.frameDistance, ply.frameDistanceR);

            // mate weight decay with distance
            float w = weightByDistance(dist);
            if (ply.frameDistanceL == minFrameDistance) {
                weights.add(w, 0, 0);
            }
            if (ply.frameDistance == minFrameDistance) {
                weights.add(0, w, 0);
            }
            if (ply.frameDistanceR == minFrameDistance) {
                weights.add(0, 0, w);
            }

            count++;
        }
        return count == 0 ? weights.setZero() : weights.scl(1f / count);
    }

    private Vector3 getOpponentWeights() {
        Vector3 weights = new Vector3();
        Team opponentTeam = player.team.match.team[1 - player.team.index];
        int count = 0;
        for (int i = 0; i < TEAM_SIZE; i++) {
            Player ply = opponentTeam.lineup.get(i);

            float dist = player.distanceFrom(ply);
            if (dist > Parameters.PLAYER_DETECTION_RADIUS) continue;

            float maxFrameDistance = EMath.max(ply.frameDistanceL, ply.frameDistance, ply.frameDistanceR);

            // opponent weight decay with distance
            float w = weightByDistance(dist);
            if (ply.frameDistanceL == maxFrameDistance) {
                weights.add(w, 0, 0);
            }
            if (ply.frameDistance == maxFrameDistance) {
                weights.add(0, w, 0);
            }
            if (ply.frameDistanceR == maxFrameDistance) {
                weights.add(0, 0, w);
            }

            count++;
        }
        return count == 0 ? weights.setZero() : weights.scl(1f / count);
    }

    private Vector3 getOwnGoalWeights() {
        Vector3 weights = new Vector3();
        float ownGoalToPlayerAngle = EMath.aTan2(player.y - GOAL_LINE * player.team.side, player.x);
        weights.set(
                1 - EMath.angleDiff(ownGoalToPlayerAngle, controlsAngle - 45) / 360,
                1 - EMath.angleDiff(ownGoalToPlayerAngle, controlsAngle) / 360,
                1 - EMath.angleDiff(ownGoalToPlayerAngle, controlsAngle + 45) / 360
        );
        GLGame.debug(AI_ATTACKING, player.numberName(), "player.x: " + player.x + ", player.y: " + player.y + ", controlsAngle: " + controlsAngle + ", ownGoalToPlayerAngle: " + ownGoalToPlayerAngle);
        return weights;
    }

    private Vector3 getGoalWeights() {
        Vector3 weights = new Vector3();
        float playerToGoalAngle = EMath.aTan2(-(GOAL_LINE - GOAL_AREA_H) * player.team.side - player.y, -player.x);
        weights.set(
                1 - EMath.angleDiff(playerToGoalAngle, controlsAngle - 45) / 360,
                1 - EMath.angleDiff(playerToGoalAngle, controlsAngle) / 360,
                1 - EMath.angleDiff(playerToGoalAngle, controlsAngle + 45) / 360
        );
        GLGame.debug(AI_ATTACKING, player.numberName(), "player.x: " + player.x + ", player.y: " + player.y + ", controlsAngle: " + controlsAngle + ", playerToGoalAngle: " + playerToGoalAngle);
        return weights;
    }

    private Vector3 getInFieldMap() {

        boolean includeGoal = Const.isInsideGoalArea(player.x, player.y, -player.side);

        float leftX = player.x + Parameters.OUTSIDE_DETECTION_RADIUS * EMath.cos(controlsAngle - 45);
        float leftY = player.y + Parameters.OUTSIDE_DETECTION_RADIUS * EMath.sin(controlsAngle - 45);
        boolean left = insideField(leftX, leftY, includeGoal);

        float centerX = player.x + Parameters.OUTSIDE_DETECTION_RADIUS * EMath.cos(controlsAngle);
        float centerY = player.y + Parameters.OUTSIDE_DETECTION_RADIUS * EMath.sin(controlsAngle);
        boolean center = insideField(centerX, centerY, includeGoal);

        float rightX = player.x + Parameters.OUTSIDE_DETECTION_RADIUS * EMath.cos(controlsAngle + 45);
        float rightY = player.y + Parameters.OUTSIDE_DETECTION_RADIUS * EMath.sin(controlsAngle + 45);
        boolean right = insideField(rightX, rightY, includeGoal);

        Vector3 map = new Vector3(
                left ? 1 : 0,
                (center && left && right) ? 1 : 0,
                right ? 1 : 0
        );

        // if choosing between left and right in own goal area, remove the nearest to the goal
        if (map.len2() == 2 && Const.isInsideGoalArea(player.x, player.y, player.side)) {
            if (EMath.dist(0, player.side * GOAL_LINE, leftX, leftY) < EMath.dist(0, player.side * GOAL_LINE, rightX, rightY)) {
                map.x = 0;
            } else {
                map.z = 0;
            }
        }

        return map;
    }

    private boolean insideField(float x, float y, boolean includeGoal) {
        if (Math.abs(x) < TOUCH_LINE && Math.abs(y) < GOAL_LINE) {
            return true;
        }

        return includeGoal && (Math.abs(x) < POST_X && Math.abs(y) < (GOAL_LINE + Parameters.OUTSIDE_DETECTION_RADIUS));
    }

    private float weightByDistance(float dist) {
        return EMath.pow(dist / Parameters.PLAYER_DETECTION_RADIUS, 2) - 2 * dist / Parameters.PLAYER_DETECTION_RADIUS + 1;
    }

    private float emergencyAngle() {
        float playerToCenterAngle = player.angleToPoint(0, 0);
        float angle = 90 * Math.signum(EMath.signedAngleDiff(controlsAngle, playerToCenterAngle));

        GLGame.debug(AI_ATTACKING, player.numberName(), "Emergency turn by: " + angle);
        return angle;
    }

    private float probabilityByVisualWidth(float value, float maxValue) {
        return -value * value / (maxValue * maxValue) + 2f * value / maxValue;
    }

    private float probabilityByDistance(float value, float minProb, float maxValue) {
        return (minProb - 1) * value * value / (maxValue * maxValue) + 1;
    }

    private float goalVisualWidth(float x, float y) {
        int side = EMath.sgn(y);
        float angle1 = EMath.angle(x, y, -POST_X, side * GOAL_LINE);
        float angle2 = EMath.angle(x, y, +POST_X, side * GOAL_LINE);
        return EMath.angleDiff(angle1, angle2);
    }
}
