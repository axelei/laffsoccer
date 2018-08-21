package com.ygames.ysoccer.competitions.tournament.knockout;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;
import java.util.Collections;

public class Knockout extends Round implements Json.Serializable {

    public int numberOfLegs;
    public ExtraTime extraTime;
    public Penalties penalties;
    public int currentLeg;
    public ArrayList<Leg> legs;

    public Knockout() {
        super(Type.KNOCKOUT);
        numberOfLegs = 1;
        extraTime = ExtraTime.ON;
        penalties = Penalties.ON;
        currentLeg = 0;
        legs = new ArrayList<Leg>();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        numberOfLegs = jsonData.getInt("numberOfLegs");
        extraTime = json.readValue("extraTime", ExtraTime.class, jsonData);
        penalties = json.readValue("penalties", Penalties.class, jsonData);
        currentLeg = jsonData.getInt("currentLeg", 0);

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
        super.write(json);
        json.writeValue("numberOfLegs", numberOfLegs);
        json.writeValue("extraTime", extraTime);
        json.writeValue("penalties", penalties);
        json.writeValue("currentLeg", currentLeg);

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

    public ArrayList<Match> getMatches() {
        return getLeg().matches;
    }

    public Leg getLeg() {
        return legs.get(currentLeg);
    }

    protected void start() {
        // if first leg is not preset, create it
        if (legs.size() == 0) {
            newLeg();
            // TODO generateMatches();
        }
    }

    private void newLeg() {
        Leg leg = new Leg(this);
        legs.add(leg);
    }
}
