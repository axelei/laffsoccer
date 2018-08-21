package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class Round {

    public enum Type {GROUPS, KNOCKOUT}

    public enum ExtraTime {OFF, ON, IF_REPLAY}

    public enum Penalties {OFF, ON, IF_REPLAY}

    public Tournament tournament;
    public String name;
    public final Type type;
    public int numberOfTeams;
    public boolean seeded;

    public Round(Type type) {
        this.type = type;
    }

    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        numberOfTeams = jsonData.getInt("numberOfTeams");
        seeded = jsonData.getBoolean("seeded");
    }

    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("numberOfTeams", numberOfTeams);
        json.writeValue("seeded", seeded);
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public abstract boolean isPreset();

    public static String getExtraTimeLabel(ExtraTime extraTime) {
        switch (extraTime) {
            case OFF:
                return "EXTRA TIME.OFF";
            case ON:
                return "EXTRA TIME.ON";
            case IF_REPLAY:
                return "EXTRA TIME.IF REPLAY";
            default:
                throw new GdxRuntimeException("Wrong ExtraTime value");
        }
    }

    public static String getPenaltiesLabel(Penalties penalties) {
        switch (penalties) {
            case OFF:
                return "PENALTIES.OFF";
            case ON:
                return "PENALTIES.ON";
            case IF_REPLAY:
                return "PENALTIES.IF REPLAY";
            default:
                throw new GdxRuntimeException("Wrong Penalties value");
        }
    }
}
