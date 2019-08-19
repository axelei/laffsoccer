package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;

public class TestMatch extends Competition {

    private Match match;

    public TestMatch() {
        super(Type.TEST_MATCH);
        name = "TEST MATCH";
        match = new Match();
        numberOfTeams = 2;
        time = MatchSettings.Time.RANDOM;
    }

    @Override
    public Match getMatch() {
        return match;
    }

    public Pitch.Type resolvePitchType() {
        return pitchType;
    }
}
