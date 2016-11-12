package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class Recorder {

    private static final int MAX_RECORDS = 6;
    private static final int RECORD_SIZE = (4 + 2 * Const.TEAM_SIZE * 5 + 2) * 2 * Const.REPLAY_SUBFRAMES;

    private Match match;
    private short[] highlights = new short[MAX_RECORDS * RECORD_SIZE];
    private int current;
    private int recorded;

    Recorder(Match match) {
        this.match = match;
    }

    void saveHighlight() {

        // if more then MAX_RECORDS the oldest ones are overwritten
        int index = (recorded % MAX_RECORDS) * RECORD_SIZE;

        for (int i = 1; i <= 2 * Const.REPLAY_SUBFRAMES; i++) {

            // ball
            Data ballData = match.ball.data[match.subframe];
            highlights[index++] = (short) ballData.x;
            highlights[index++] = (short) ballData.y;
            highlights[index++] = (short) ballData.z;
            highlights[index++] = (short) ballData.fmx;

            // players
            for (int t = Match.HOME; t <= Match.AWAY; t++) {
                for (int j = 0; j < Const.TEAM_SIZE; j++) {
                    Player player = match.team[t].players.get(j);
                    Data playerData = player.data[match.subframe];
                    highlights[index++] = (short) playerData.x;
                    highlights[index++] = (short) playerData.y;
                    highlights[index++] = (short) playerData.fmx;
                    highlights[index++] = (short) playerData.fmy;
                    highlights[index++] = (short) (playerData.isVisible ? 1 : 0);
                }
            }

            // camera
            highlights[index++] = (short) match.renderer.vcameraX[match.subframe];
            highlights[index++] = (short) match.renderer.vcameraY[match.subframe];

            match.subframe = (match.subframe + GLGame.SUBFRAMES / 2) % Const.REPLAY_SUBFRAMES;
        }

        recorded += 1;
    }

    void loadHighlight() {

        // copy highlights data into objects
        int offset = current * RECORD_SIZE;

        // if more than MAX_RECORDS actions have been recorded
        // then the oldest have been overwritten
        // and we start from the middle of the array
        if (recorded > MAX_RECORDS) {
            offset = ((recorded + current) % MAX_RECORDS) * RECORD_SIZE;
        }

        // if the end of the bank has been reached, then restart
        if (offset == MAX_RECORDS * RECORD_SIZE) {
            offset = 0;
        }

        for (int j = 1; j <= 2 * Const.REPLAY_SUBFRAMES; j++) {

            // ball
            Data ballData = match.ball.data[match.subframe];
            ballData.x = highlights[offset++];
            ballData.y = highlights[offset++];
            ballData.z = highlights[offset++];
            ballData.fmx = highlights[offset++];

            // players
            for (int t = Match.HOME; t <= Match.AWAY; t++) {
                for (int i = 0; i < Const.TEAM_SIZE; i++) {
                    Player player = match.team[t].players.get(i);
                    Data playerData = player.data[match.subframe];
                    playerData.x = highlights[offset++];
                    playerData.y = highlights[offset++];
                    playerData.fmx = highlights[offset++];
                    playerData.fmy = highlights[offset++];
                    playerData.isVisible = (highlights[offset++] == 1);
                }
            }

            // camera
            match.renderer.vcameraX[match.subframe] = highlights[offset++];
            match.renderer.vcameraY[match.subframe] = highlights[offset++];

            match.subframe = (match.subframe + GLGame.SUBFRAMES / 2) % Const.REPLAY_SUBFRAMES;
        }
    }

    void nextHighlight() {
        current += 1;
    }

    boolean hasEnded() {
        return (current == Math.min(recorded, MAX_RECORDS));
    }

    void restart() {
        current = 0;
    }

    boolean hasHighlights() {
        return recorded > 0;
    }
}
