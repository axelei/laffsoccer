package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Groups extends Round implements Json.Serializable {

    public int pointsForAWin;

    public Groups() {
        super();
        this.pointsForAWin = 3;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        pointsForAWin = jsonData.getInt("pointsForAWin");
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("pointsForAWin", pointsForAWin);
    }

    boolean isPreset() {
        // TODO
        return false;
    }
}
