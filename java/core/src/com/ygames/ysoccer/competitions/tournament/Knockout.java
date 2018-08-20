package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Knockout extends Round implements Json.Serializable {

    public int numberOfLegs;
    public ExtraTime extraTime;
    public Penalties penalties;

    public Knockout() {
        super();
        numberOfLegs = 1;
        extraTime = ExtraTime.ON;
        penalties = Penalties.ON;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        numberOfLegs = jsonData.getInt("numberOfLegs");
        extraTime = json.readValue("extraTime", ExtraTime.class, jsonData);
        penalties = json.readValue("penalties", Penalties.class, jsonData);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("numberOfLegs", numberOfLegs);
        json.writeValue("extraTime", extraTime);
        json.writeValue("penalties", penalties);
    }

    boolean isPreset() {
        // TODO
        return false;
    }
}
