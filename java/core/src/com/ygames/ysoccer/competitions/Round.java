package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Round implements Json.Serializable {

    public enum ExtraTime {OFF, ON, IF_REPLAY}

    public enum Penalties {OFF, ON, IF_REPLAY}

    public int legs;
    public ExtraTime extraTime;
    public Penalties penalties;

    Round() {
        legs = 1;
        extraTime = ExtraTime.ON;
        penalties = Penalties.OFF;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.readFields(this, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeValue("legs", legs);
        json.writeValue("extraTime", extraTime);
        json.writeValue("penalties", penalties);
    }

    public String getLegsLabel() {
        switch (legs) {
            case 1:
                return "ONE LEG";

            case 2:
                return "TWO LEGS";

            default:
                throw new GdxRuntimeException("Wrong legs value");
        }
    }

    private static String[] extraTimeLabels = {
            "EXTRA TIME.OFF",
            "EXTRA TIME.ON",
            "EXTRA TIME.IF REPLAY"
    };

    public String getExtraTimeLabel() {
        return extraTimeLabels[extraTime.ordinal()];
    }

    private static String[] penaltiesLabels = {
            "PENALTIES.OFF",
            "PENALTIES.ON",
            "PENALTIES.IF REPLAY"
    };

    public String getPenaltiesLabel() {
        return penaltiesLabels[penalties.ordinal()];
    }
}
