package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class Recorder {

    private static final int MAX_RECORDS = 12;

    private final Match match;
    private final ArrayList<short[]> highlights = new ArrayList<>();
    private int current;
    private int recorded;

    Recorder(Match match) {
        this.match = match;
    }

    int getCurrent() {
        return current;
    }

    int getRecorded() {
        return Math.min(recorded, MAX_RECORDS);
    }

    void saveHighlight(SceneRenderer sceneRenderer) {

        int recordSize = (4 + 10 * (match.team[HOME].lineup.size() + match.team[AWAY].lineup.size()) + 2) * 2 * Const.REPLAY_SUBFRAMES;

        short[] record = new short[recordSize];

        int index = 0;

        for (int i = 1; i <= 2 * Const.REPLAY_SUBFRAMES; i++) {

            // ball
            Data ballData = match.ball.data[match.subframe];
            record[index++] = (short) ballData.x;
            record[index++] = (short) ballData.y;
            record[index++] = (short) ballData.z;
            record[index++] = (short) ballData.fmx;

            // players
            for (int t = HOME; t <= AWAY; t++) {
                int len = match.team[t].lineup.size();
                for (int pos = 0; pos < len; pos++) {
                    // using 'players' because order in 'lineup' may change during the match
                    Player player = match.team[t].players.get(pos);
                    Data playerData = player.data[match.subframe];
                    record[index++] = (short) playerData.x;
                    record[index++] = (short) playerData.y;
                    record[index++] = (short) playerData.fmx;
                    record[index++] = (short) playerData.fmy;
                    record[index++] = (short) (playerData.isVisible ? 1 : 0);
                    record[index++] = (short) (playerData.isHumanControlled ? 1 : 0);
                    record[index++] = (short) (playerData.isBestDefender ? 1 : 0);
                    record[index++] = (short) playerData.frameDistance;
                    record[index++] = (short) playerData.playerState;
                    record[index++] = (short) playerData.playerAiState;
                }
            }

            // camera
            record[index++] = (short) sceneRenderer.vCameraX[match.subframe];
            record[index++] = (short) sceneRenderer.vCameraY[match.subframe];

            match.subframe = (match.subframe + GLGame.SUBFRAMES / 2) % Const.REPLAY_SUBFRAMES;
        }

        // if more then MAX_RECORDS the oldest ones are overwritten
        if (recorded < MAX_RECORDS) {
            highlights.add(record);
        } else {
            highlights.set(recorded % MAX_RECORDS, record);
        }

        recorded += 1;
    }

    void loadHighlight(SceneRenderer sceneRenderer) {

        // copy highlights data into objects
        int index = current;

        // if more than MAX_RECORDS actions have been recorded
        // then the oldest have been overwritten
        // and we start from the middle of the array
        if (recorded > MAX_RECORDS) {
            index = (recorded + current) % MAX_RECORDS;
        }

        // if the end of the bank has been reached, then restart
        if (index == MAX_RECORDS) {
            index = 0;
        }

        short[] record = highlights.get(index);

        int offset = 0;

        for (int j = 1; j <= 2 * Const.REPLAY_SUBFRAMES; j++) {

            // ball
            Data ballData = match.ball.data[match.subframe];
            ballData.x = record[offset++];
            ballData.y = record[offset++];
            ballData.z = record[offset++];
            ballData.fmx = record[offset++];

            // players
            for (int t = HOME; t <= AWAY; t++) {
                int len = match.team[t].lineup.size();
                for (int pos = 0; pos < len; pos++) {
                    // using 'players' because order in 'lineup' may change during the match
                    Player player = match.team[t].players.get(pos);
                    Data playerData = player.data[match.subframe];
                    playerData.x = record[offset++];
                    playerData.y = record[offset++];
                    playerData.fmx = record[offset++];
                    playerData.fmy = record[offset++];
                    playerData.isVisible = (record[offset++] == 1);
                    playerData.isHumanControlled = (record[offset++] == 1);
                    playerData.isBestDefender = (record[offset++] == 1);
                    playerData.frameDistance = record[offset++];
                    playerData.playerState = record[offset++];
                    playerData.playerAiState = record[offset++];
                }
            }

            // camera
            sceneRenderer.vCameraX[match.subframe] = record[offset++];
            sceneRenderer.vCameraY[match.subframe] = record[offset++];

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
