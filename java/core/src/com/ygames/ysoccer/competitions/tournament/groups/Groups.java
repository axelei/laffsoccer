package com.ygames.ysoccer.competitions.tournament.groups;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.match.Match;

public class Groups extends Round implements Json.Serializable {

    public int rounds;
    public int pointsForAWin;

    public Groups() {
        super(Type.GROUPS);
        rounds = 1;
        pointsForAWin = 3;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        rounds = jsonData.getInt("rounds");
        pointsForAWin = jsonData.getInt("pointsForAWin");
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("rounds", rounds);
        json.writeValue("pointsForAWin", pointsForAWin);
    }

    protected void start() {
        // TODO
    }

    @Override
    public Match getMatch() {
        // TODO
        // return calendar.get(currentMatch);
        return null;
    }

    public void generateResult() {
        // TODO
    }

}
