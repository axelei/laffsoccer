package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;
import java.util.Collections;

public class Round implements Json.Serializable {

    public enum ExtraTime {OFF, ON, IF_REPLAY}

    public enum Penalties {OFF, ON, IF_REPLAY}

    Cup cup;
    public String name;
    public int numberOfLegs;
    public ExtraTime extraTime;
    public Penalties penalties;
    public ArrayList<Leg> legs;

    Round() {
        numberOfLegs = 1;
        extraTime = ExtraTime.ON;
        penalties = Penalties.ON;
        legs = new ArrayList<Leg>();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        numberOfLegs = jsonData.getInt("numberOfLegs");
        extraTime = json.readValue("extraTime", ExtraTime.class, jsonData);
        penalties = json.readValue("penalties", Penalties.class, jsonData);

        Match[][] legsArray = json.readValue("legs", Match[][].class, jsonData);
        if (legsArray != null) {
            for (Match[] matchesArray : legsArray) {
                Leg leg = new Leg(this);
                Collections.addAll(leg.matches, matchesArray);
                legs.add(leg);
            }
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("numberOfLegs", numberOfLegs);
        json.writeValue("extraTime", extraTime);
        json.writeValue("penalties", penalties);
        json.writeArrayStart("legs");
        for (Leg leg : legs) {
            json.writeArrayStart();
            for (Match match : leg.matches) {
                json.writeValue(match, Match.class);
            }
            json.writeArrayEnd();
        }
        json.writeArrayEnd();
    }

    public void setCup(Cup cup) {
        this.cup = cup;
    }

    public String getLegsLabel() {
        switch (numberOfLegs) {
            case 1:
                return "ONE LEG";

            case 2:
                return "TWO LEGS";

            default:
                throw new GdxRuntimeException("Wrong numberOfLegs value");
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

    public int getIndex() {
        return cup.rounds.indexOf(this);
    }

    public void newLeg() {
        Leg leg = new Leg(this);
        legs.add(leg);
    }
}
