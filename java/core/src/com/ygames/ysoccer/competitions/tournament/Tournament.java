package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;

public class Tournament extends Competition implements Json.Serializable {

    public ArrayList<Round> rounds;
    public AwayGoals awayGoals;

    public Tournament() {
        super(Type.TOURNAMENT);
        rounds = new ArrayList<Round>();
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
