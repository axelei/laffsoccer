package com.ygames.ysoccer.competitions.tournament.knockout;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;
import java.util.Collections;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

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
            generateMatches();
        }
    }

    private void generateMatches() {

        // first leg
        if (currentLeg == 0) {
            ArrayList<Integer> qualifiedTeams = new ArrayList<Integer>();
            if (tournament.currentRound == 0) {
                for (int i = 0; i < numberOfTeams; i++) {
                    qualifiedTeams.add(i);
                }
            } else {
                // TODO
//                for (Leg leg : rounds.get(currentRound - 1).legs) {
//                    qualifiedTeams.addAll(leg.getQualifiedTeams());
//                }
            }

            Collections.shuffle(qualifiedTeams);

            for (int i = 0; i < qualifiedTeams.size() / 2; i++) {
                Match match = new Match();
                match.teams[HOME] = qualifiedTeams.get(2 * i);
                match.teams[AWAY] = qualifiedTeams.get(2 * i + 1);
                getLeg().matches.add(match);
            }
        }

        // second leg
        else if ((currentLeg == 1) && (numberOfLegs == 2)) {
            for (Match oldMatch : legs.get(0).matches) {
                Match match = new Match();
                match.teams[HOME] = oldMatch.teams[AWAY];
                match.teams[AWAY] = oldMatch.teams[HOME];
                match.oldResult = oldMatch.getResult();
                getLeg().matches.add(match);
            }
        }

        // replays
        else {
            Leg previousLeg = legs.get(currentLeg - 1);
            for (Match oldMatch : previousLeg.matches) {
                // TODO
//                if (previousLeg.getQualifiedTeam(oldMatch) == -1) {
//                    Match match = new Match();
//                    match.teams[HOME] = oldMatch.teams[AWAY];
//                    match.teams[AWAY] = oldMatch.teams[HOME];
//                    getLeg().matches.add(match);
//                }
            }
        }
    }

    public String getMatchStatus(Match match) {
        String s = "";
        // TODO
        return s;
    }

    private void newLeg() {
        Leg leg = new Leg(this);
        legs.add(leg);
    }
}
