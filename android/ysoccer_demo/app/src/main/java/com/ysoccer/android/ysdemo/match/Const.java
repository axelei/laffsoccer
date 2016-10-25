package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class Const {

    static final boolean DEBUG = true;

    static final int SECOND = GLGame.SUBFRAMES_PER_SECOND;
    static final int BALL_R = 4;
    static final float GRAVITY = 350.0f / SECOND;

    static final int REPLAY_DURATION = 8; // seconds
    static final int REPLAY_FRAMES = REPLAY_DURATION * GLGame.VIRTUAL_REFRESH_RATE;
    static final int REPLAY_SUBFRAMES = REPLAY_DURATION * SECOND;
    static final int BALL_PREDICTION = 2 * SECOND / GLGame.SUBFRAMES;
    static final int PREDICTION_UPDATE_SUBFRAMES = 40;

    // teams
    public static final int TEAM_SIZE = 11;
    static final int BASE_TEAM = 16;
    static final int FULL_TEAM = 32;

    static final int GOAL_LINE = 640;
    static final int TOUCH_LINE = 510;
    static final int GOAL_AREA_W = 252;
    static final int GOAL_AREA_H = 58;
    static final int PENALTY_AREA_W = 572;
    static final int PENALTY_AREA_H = 174;
    static final int PENALTY_SPOT_Y = 524;

    // goals
    static final int GOAL_BTM_X = -72;
    static final int GOAL_BTM_Y = 604;

    // benches
    static final int BENCH_X = -544;
    static final int BENCH_Y_UP = -198;
    static final int BENCH_Y_DOWN = 38;

    static final int PLAYER_H = 22;
    static final int FIELD_XMIN = -565;
    static final int FIELD_XMAX = +572;
    static final int FIELD_YMIN = -700;
    static final int FIELD_YMAX = +698;

    static final int FLAGPOST_H = 21;

    // jumpers
    static final int JUMPER_X = 92;
    static final int JUMPER_Y = 684;
    static final int JUMPER_H = 42;

    // goals
    static final int GOAL_DEPTH = 22;
    static final int POST_X = 71;
    static final int POST_R = 2; // posts and crossbar radius
    static final int CROSSBAR_H = 33;

    // size of the pitch
    static final int PITCH_W = 1700;
    static final int PITCH_H = 1800;

    static final int CENTER_X = 846;
    static final int CENTER_Y = 918;

    // tactics
    static final int TACT_DX = 68;
    static final int TACT_DY = 40;
    static final int BALL_ZONES = 35;

    // width and height of each ball zone
    static final int BALL_ZONE_DX = 206;
    static final int BALL_ZONE_DY = 184;

}
