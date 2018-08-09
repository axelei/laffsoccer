package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.match.Match;

public class Tournament extends Competition implements Json.Serializable {

    public AwayGoals awayGoals;

    public Tournament() {
        super(Type.TOURNAMENT);
        awayGoals = AwayGoals.OFF;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
    }

    @Override
    public void write(Json json) {
        super.write(json);
    }

    @Override
    public Match getMatch() {
        return null;
    }

    public boolean hasTwoLegsRound() {
        // TODO
        return true;
    }
}
