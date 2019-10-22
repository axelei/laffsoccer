package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

public class Const {

    static final int SECOND = GLGame.SUBFRAMES_PER_SECOND;
    static final int BALL_R = 4;
    static float GRAVITY = 332.8f / SECOND;
    static float AIR_FRICTION = 0.28f;
    static float SPIN_FACTOR = 12.0f;
    static float SPIN_DAMPENING = 7.0f;
    static float BOUNCE = 0.9f;
    static float PLAYER_RUN_ANIMATION = 0.18f;
    static float PASSING_THRESHOLD = 0.1f;
    static float PASSING_SPEED_FACTOR = 0.3f;
    static float SHOOTING_ANGLE_TOLERANCE = 22.5f;

    static final int REPLAY_DURATION = 8; // seconds
    static final int REPLAY_FRAMES = REPLAY_DURATION * GLGame.VIRTUAL_REFRESH_RATE;
    static final int REPLAY_SUBFRAMES = REPLAY_DURATION * SECOND;
    static final int BALL_PREDICTION = 2 * SECOND / GLGame.SUBFRAMES;

    static int[][] goalsProbability = new int[][]{
            {1000, 0, 0, 0, 0, 0, 0},
            {870, 100, 25, 4, 1, 0, 0},
            {730, 210, 50, 7, 2, 1, 0},
            {510, 320, 140, 20, 6, 4, 0},
            {390, 370, 180, 40, 10, 7, 3},
            {220, 410, 190, 150, 15, 10, 5},
            {130, 390, 240, 200, 18, 15, 7},
            {40, 300, 380, 230, 25, 15, 10},
            {20, 220, 240, 220, 120, 100, 80},
            {10, 150, 190, 190, 170, 150, 140},
            {0, 100, 150, 200, 200, 200, 150}
    };

    public static String[] associations = new String[]{
            "AFG", "AIA", "ALB", "ALG", "AND", "ANG", "ARG", "ARM",
            "ARU", "ASA", "ATG", "AUS", "AUT", "AZE", "BAH", "BAN",
            "BDI", "BEL", "BEN", "BER", "BFA", "BHR", "BHU", "BIH",
            "BLR", "BLZ", "BOL", "BOT", "BRA", "BRB", "BRU", "BUL",
            "CAM", "CAN", "CAY", "CGO", "CHA", "CHI", "CHN", "CIV",
            "CMR", "COD", "COK", "COL", "COM", "CPV", "CRC", "CRO",
            "CTA", "CUB", "CUS", "CUW", "CYP", "CZE", "DEN", "DJI",
            "DMA", "DOM", "ECU", "EGY", "ENG", "EQG", "ERI", "ESP",
            "EST", "ETH", "FIJ", "FIN", "FRA", "FRO", "GAB", "GAM",
            "GEO", "GER", "GHA", "GNB", "GRE", "GRN", "GUA", "GUI",
            "GUM", "GUY", "HAI", "HKG", "HON", "HUN", "IDN", "IND",
            "IRL", "IRN", "IRQ", "ISL", "ISR", "ITA", "JAM", "JOR",
            "JPN", "KAZ", "KEN", "KGZ", "KOR", "KSA", "KUW", "LAO",
            "LBR", "LBY", "LCA", "LES", "LIB", "LIE", "LTU", "LUX",
            "LVA", "MAC", "MAD", "MAR", "MAS", "MDA", "MDV", "MEX",
            "MGL", "MKD", "MLI", "MLT", "MNE", "MOZ", "MRI", "MSR",
            "MTN", "MWI", "MYA", "NAM", "NCA", "NCL", "NED", "NEP",
            "NGA", "NIG", "NIR", "NOR", "NZL", "OMA", "PAK", "PAN",
            "PAR", "PER", "PHI", "PLE", "PNG", "POL", "POR", "PRK",
            "PUR", "QAT", "ROU", "RSA", "RUS", "RWA", "SAM", "SCO",
            "SDN", "SEN", "SEY", "SIN", "SKN", "SLE", "SLV", "SMR",
            "SOL", "SOM", "SRB", "SRI", "SSD", "STP", "SUI", "SUR",
            "SVK", "SVN", "SWE", "SWZ", "SYR", "TAH", "TAN", "TCA",
            "TGA", "THA", "TJK", "TKM", "TLS", "TOG", "TPE", "TRI",
            "TUN", "TUR", "UAE", "UGA", "UKR", "URU", "USA", "UZB",
            "VAN", "VEN", "VGB", "VIE", "VIN", "VIR", "WAL", "YEM",
            "ZAM", "ZIM"
    };

    // teams
    public static final int TEAM_SIZE = 11;
    public static final int BASE_TEAM = 16;
    public static final int FULL_TEAM = 26;

    static final int GOAL_LINE = 640;
    static final int TOUCH_LINE = 510;
    static final int GOAL_AREA_W = 252;
    static final int GOAL_AREA_H = 58;
    static final int PENALTY_AREA_W = 572;
    static final int PENALTY_AREA_H = 174;
    static final int PENALTY_SPOT_Y = 524;
    static final float PENALTY_DISTANCE = GOAL_LINE - PENALTY_SPOT_Y;
    static final float YARD = PENALTY_DISTANCE / 12f;
    static final float FREE_KICK_DISTANCE = 10f * YARD;
    static final float DIRECT_SHOT_DISTANCE = 310;

    // goals
    static final int GOAL_BTM_X = -73;
    static final int GOAL_BTM_Y = 603;

    // benches
    static final int BENCH_X = -544;
    static final int BENCH_Y_UP = -198;
    static final int BENCH_Y_DOWN = 38;

    static final int PLAYER_H = 22;
    static final int PLAYER_W = 12;
    static final int FIELD_XMIN = -565;
    static final int FIELD_XMAX = +572;
    static final int FIELD_YMIN = -700;
    static final int FIELD_YMAX = +698;

    static final int FLAGPOST_H = 21;

    // size of the pitch
    static final int PITCH_W = 1700;
    static final int PITCH_H = 1800;

    static final int CENTER_X = 847;
    static final int CENTER_Y = 919;

    // jumpers
    static final int JUMPER_X = 92;
    static final int JUMPER_Y = 684;
    static final int JUMPER_H = 40;

    // goals
    static final int GOAL_DEPTH = 19;
    static final int POST_X = 71;
    static final int POST_R = 2; // posts and crossbar radius
    static final int CROSSBAR_H = 33;

    // tactics
    public static final int TACT_DX = 68;
    public static final int TACT_DY = 40;
    static final int BALL_ZONES = 35;

    // width and height of each ball zone
    static final int BALL_ZONE_DX = 206;
    static final int BALL_ZONE_DY = 184;


    static boolean isInsidePenaltyArea(float x, float y, int ySide) {
        return Math.abs(x) < (PENALTY_AREA_W / 2)
                && EMath.isIn(y,
                ySide * (GOAL_LINE - PENALTY_AREA_H),
                ySide * GOAL_LINE
        );
    }

    static boolean isInsideGoalArea(float x, float y, int ySide) {
        return Math.abs(x) < (GOAL_AREA_W / 2)
                && EMath.isIn(y,
                ySide * (GOAL_LINE - GOAL_AREA_H),
                ySide * GOAL_LINE
        );
    }

    static boolean isInsideDirectShotArea(float x, float y, int ySide) {
        return ySide * y < GOAL_LINE
                && (EMath.dist(x, ySide * y, -POST_X, GOAL_LINE) < DIRECT_SHOT_DISTANCE
                || EMath.dist(x, ySide * y, POST_X, GOAL_LINE) < DIRECT_SHOT_DISTANCE);
    }

    static boolean seesTheGoal(float x, float y, float a) {
        int ySide = EMath.sgn(y);

        if (EMath.angleDiff(a, ySide * 90) > 90) return false;

        float x0 = -POST_X + (2 * POST_R + 2 * BALL_R);
        float y0 = ySide * GOAL_LINE;
        float m0 = EMath.tan(a - ySide * SHOOTING_ANGLE_TOLERANCE);
        float s0 = EMath.sgn(EMath.cos(a - ySide * SHOOTING_ANGLE_TOLERANCE));
        float b0 = y0 - m0 * x0;
        float x1 = +POST_X - (2 * POST_R + 2 * BALL_R);
        float y1 = ySide * GOAL_LINE;
        float m1 = EMath.tan(a + ySide * SHOOTING_ANGLE_TOLERANCE);
        float s1 = EMath.sgn(EMath.cos(a + ySide * SHOOTING_ANGLE_TOLERANCE));
        float b1 = y1 - m1 * x1;
        return s0 * ySide * y < s0 * ySide * (m0 * x + b0) && s1 * ySide * y > s1 * ySide * (m1 * x + b1);
    }

    static boolean isInsideGoal(float x, float y) {
        return Math.abs(x) < POST_X
                && Math.abs(y) > GOAL_LINE
                && Math.abs(y) < GOAL_LINE + GOAL_DEPTH;
    }
}
