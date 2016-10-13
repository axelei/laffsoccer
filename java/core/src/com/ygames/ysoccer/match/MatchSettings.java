package com.ygames.ysoccer.match;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.math.Emath;

public class MatchSettings {

    public int time;
    public int pitchType;

    public MatchSettings(Competition competition) {
        this.time = competition.time;
        this.pitchType = competition.pitchType;
    }

    public void rotateTime(int direction) {
        time = Emath.rotate(time, Time.DAY, Time.RANDOM, direction);
    }

    public void rotatePitchType(int direction) {
        pitchType = Emath.rotate(pitchType, Pitch.FROZEN, Pitch.RANDOM, direction);
        // TODO: weatherEffect = Weather.WIND;
        // TODO: weatherStrength = Weather.Strength.NONE;
        // TODO: sky = Sky.CLEAR;
    }
}
