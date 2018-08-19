package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Groups extends Round implements Json.Serializable {

    public Groups() {
    }

    public Groups(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
    }

    @Override
    public void write(Json json) {
        super.write(json);
    }

    boolean isPreset() {
        // TODO
        return false;
    }
}
