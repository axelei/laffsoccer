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
        Round[] roundsArray = json.readValue("rounds", Round[].class, jsonData);
        if (roundsArray != null) {
            for (Round round : roundsArray) {
                round.setTournament(this);
                rounds.add(round);
            }
        }
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("rounds", rounds, Round[].class, Round.class);
    }

    @Override
    public Match getMatch() {
        return null;
    }

    @Override
    public boolean isEnded() {
        return currentRound == rounds.size() - 1; // TODO && currentRound.isEnded()
    }

    public boolean hasTwoLegsRound() {
        // TODO
        return true;
    }
}