package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Team;

public class Friendly extends Competition {

    private final Match match;

    public Friendly() {
        super(Type.FRIENDLY);
        name = Assets.strings.get("FRIENDLY");
        match = new Match();
        numberOfTeams = 2;
        weather = Competition.Weather.BY_PITCH_TYPE;
    }

    @Override
    public Match getMatch() {
        return match;
    }

    @Override
    public MatchSettings.Time getTime() {
        return MatchSettings.randomTime();
    }
}
