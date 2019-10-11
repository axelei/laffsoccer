package com.ygames.ysoccer.match;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.Settings;

public class MatchSettings extends SceneSettings {

    int matchLength;

    public int substitutions;
    public int benchSize;
    boolean autoReplays;
    public boolean radar;
    boolean crowdChants;

    public MatchSettings(Competition competition, Settings gameSettings) {
        super(gameSettings);

        if (competition.type == Competition.Type.FRIENDLY || competition.category == Competition.Category.DIY_COMPETITION) {
            matchLength = gameSettings.matchLength;
        } else {
            matchLength = Settings.matchLengths[0];
        }
        this.time = competition.getTime();
        this.pitchType = competition.getPitchType();
        this.weatherMaxStrength = gameSettings.weatherMaxStrength;
        for (int i = EMath.rand(0, 2 + 4 * weatherMaxStrength); i >= 0; i--) {
            rotateWeather();
        }
        substitutions = competition.substitutions;
        benchSize = competition.benchSize;
        autoReplays = gameSettings.autoReplays;
        radar = gameSettings.radar;
        crowdChants = true;
        commentary = gameSettings.commentary;
    }
}
