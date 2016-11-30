package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;

public class Friendly extends Competition {

    private Match match;

    public Friendly() {
        match = new Match();
        numberOfTeams = 2;
        time = MatchSettings.Time.RANDOM;
    }

    public Type getType() {
        return Type.FRIENDLY;
    }

    @Override
    public Match getMatch() {
        return match;
    }

    public Pitch.Type resolvePitchType() {
        return pitchType;
    }
}
