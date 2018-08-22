package com.ygames.ysoccer.competitions.tournament.knockout;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.competitions.tournament.Round.ExtraTime.ON;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Match.ResultType.AFTER_PENALTIES;

public class Knockout extends Round implements Json.Serializable {

    public int numberOfLegs;
    public ExtraTime extraTime;
    public Penalties penalties;
    public int currentLeg;
    public ArrayList<Leg> legs;

    public Knockout() {
        super(Type.KNOCKOUT);
        numberOfLegs = 1;
        extraTime = ON;
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

    @Override
    public void start(ArrayList<Integer> qualifiedTeams) {
        currentLeg = 0;

        // if first leg is not preset, create it
        if (legs.size() == 0) {
            newLeg();
            generateCalendar(qualifiedTeams);
        }
    }

    @Override
    public void restart() {
        currentLeg = 0;

        // copy first round, first leg matches
        List<Match> firstLegMatches = new ArrayList<Match>();
        for (Match m : legs.get(0).matches) {
            Match match = new Match();
            match.teams[HOME] = m.teams[HOME];
            match.teams[AWAY] = m.teams[AWAY];
            firstLegMatches.add(match);
        }

        legs.clear();

        // restore first leg matches
        newLeg();
        legs.get(0).matches.addAll(firstLegMatches);
    }

    @Override
    public void clear() {
        currentLeg = 0;
        legs.clear();
    }

    @Override
    public Match getMatch() {
        return getLeg().matches.get(tournament.currentMatch);
    }

    @Override
    public void nextMatch() {
        tournament.currentMatch += 1;
        if (tournament.currentMatch == getLeg().matches.size()) {
            nextLeg();
        }
    }

    private void nextLeg() {
        currentLeg += 1;
        tournament.currentMatch = 0;
        newLeg();
        generateNextLegCalendar();
        if (getLeg().matches.size() == 0) {
            ArrayList<Integer> qualifiedTeams = new ArrayList<Integer>();
            for (Leg leg : legs) {
                qualifiedTeams.addAll(leg.getQualifiedTeams());
            }
            tournament.nextRound(qualifiedTeams);
        }
    }

    @Override
    public boolean isEnded() {
        return currentLeg == legs.size() - 1 && !getLeg().hasReplays();
    }

    private void generateCalendar(ArrayList<Integer> qualifiedTeams) {

        Collections.shuffle(qualifiedTeams);

        for (int i = 0; i < qualifiedTeams.size() / 2; i++) {
            Match match = new Match();
            match.teams[HOME] = qualifiedTeams.get(2 * i);
            match.teams[AWAY] = qualifiedTeams.get(2 * i + 1);
            getLeg().matches.add(match);
        }
    }

    private void generateNextLegCalendar() {
        // second leg
        if ((currentLeg == 1) && (numberOfLegs == 2)) {
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
                if (previousLeg.getQualifiedTeam(oldMatch) == -1) {
                    Match match = new Match();
                    match.teams[HOME] = oldMatch.teams[AWAY];
                    match.teams[AWAY] = oldMatch.teams[HOME];
                    getLeg().matches.add(match);
                }
            }
        }
    }

    public void generateResult() {
        Match match = getMatch();
        Team homeTeam = tournament.getTeam(HOME);
        Team awayTeam = tournament.getTeam(AWAY);

        int homeGoals = Match.generateGoals(homeTeam, awayTeam, false);
        int awayGoals = Match.generateGoals(awayTeam, homeTeam, false);
        match.setResult(homeGoals, awayGoals, Match.ResultType.AFTER_90_MINUTES);

        if (playExtraTime()) {
            homeGoals += Match.generateGoals(homeTeam, awayTeam, true);
            awayGoals += Match.generateGoals(awayTeam, homeTeam, true);
            match.setResult(homeGoals, awayGoals, Match.ResultType.AFTER_EXTRA_TIME);
        }

        tournament.generateScorers(homeTeam, homeGoals);
        tournament.generateScorers(awayTeam, awayGoals);

        if (playPenalties()) {
            do {
                homeGoals = Emath.floor(6 * Math.random());
                awayGoals = Emath.floor(6 * Math.random());
            } while (homeGoals == awayGoals);
            match.setResult(homeGoals, awayGoals, AFTER_PENALTIES);
        }
    }

    // decide if extra time have to be played depending on current result, leg's type and settings
    private boolean playExtraTime() {
        Match match = getMatch();

        // first leg
        if (currentLeg == 0) {

            // two legs round
            if (numberOfLegs == 2) {
                return false;
            }

            // result
            if (match.getResult()[HOME] != match.getResult()[AWAY]) {
                return false;
            }

            // settings
            switch (extraTime) {
                case OFF:
                    return false;

                case ON:
                    return true;

                case IF_REPLAY:
                    return false;
            }
        }

        // second leg
        else if (currentLeg == 1 && numberOfLegs == 2) {

            // aggregate goals
            int aggregate1 = match.getResult()[HOME] + match.oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + match.oldResult[HOME];
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((match.oldResult[AWAY] != match.getResult()[AWAY]) && (tournament.awayGoals == Competition.AwayGoals.AFTER_90_MINUTES)) {
                return false;
            }

            // settings
            switch (extraTime) {
                case OFF:
                    return false;

                case ON:
                    return true;

                case IF_REPLAY:
                    return false;
            }

        }

        // replays
        else {
            // result
            if (match.getResult()[HOME] != match.getResult()[AWAY]) {
                return false;
            }

            // settings
            switch (extraTime) {
                case OFF:
                    return false;

                case ON:
                    return true;

                case IF_REPLAY:
                    return true;
            }
        }

        // should never get here
        return false;
    }

    // decide if penalties have to be played depending on current result, leg's type and settings
    private boolean playPenalties() {
        Match match = getMatch();

        // first leg
        if (currentLeg == 0) {

            // two legs round
            if (numberOfLegs == 2) {
                return false;
            }

            // result
            if (match.getResult()[HOME] != match.getResult()[AWAY]) {
                return false;
            }

            // settings
            switch (penalties) {
                case OFF:
                    return false;

                case ON:
                    return true;

                case IF_REPLAY:
                    return false;
            }
        }

        // second leg
        else if ((currentLeg == 1) && (numberOfLegs == 2)) {

            // aggregate goals
            int aggregate1 = match.getResult()[HOME] + match.oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + match.oldResult[HOME];
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((match.oldResult[AWAY] != match.getResult()[AWAY]) && (tournament.awayGoals != Competition.AwayGoals.OFF)) {
                return false;
            }

            // settings
            switch (penalties) {
                case OFF:
                    return false;

                case ON:
                    return true;

                case IF_REPLAY:
                    return false;
            }
        }

        // replays
        else {
            // result
            if (match.getResult()[HOME] != match.getResult()[AWAY]) {
                return false;
            }

            // settings
            switch (penalties) {
                case OFF:
                    return false;
                case ON:
                    // this should never happen
                    throw new GdxRuntimeException("Invalid state in cup");
                case IF_REPLAY:
                    return true;
            }
        }

        // should never get here
        return false;
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
