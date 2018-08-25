package com.ygames.ysoccer.competitions.tournament.knockout;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.framework.Assets;
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

    @Override
    protected String nextMatchLabel() {
        String label = "NEXT MATCH";
        if (isLegEnded()) {
            if (isEnded()) {
                label = "NEXT ROUND";
            } else {
                switch (currentLeg) {
                    case 0:
                        if (numberOfLegs == 2) {
                            label = "CUP.2ND LEG ROUND";
                        } else {
                            label = "CUP.PLAY REPLAYS";
                        }
                        break;
                    default:
                        label = "CUP.PLAY REPLAYS";
                        break;
                }
            }
        }
        return label;
    }

    @Override
    protected boolean nextMatchOnHold() {
        return !isLegEnded();
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

            // seeding
            Collections.sort(qualifiedTeams, tournament.teamComparatorByPlayersValue);

            tournament.nextRound(qualifiedTeams);
        }
    }

    @Override
    public boolean isEnded() {
        return currentLeg == legs.size() - 1 && !getLeg().hasReplays();
    }

    private boolean isLegEnded() {
        return tournament.currentMatch == getLeg().matches.size() - 1;
    }

    private void generateCalendar(ArrayList<Integer> qualifiedTeams) {

        if (!seeded) {
            Collections.shuffle(qualifiedTeams);
        }

        // create random partitioned mapping
        List<Integer> groupsIndexes = new ArrayList<Integer>();
        for (int t = 0; t < qualifiedTeams.size() / 2; t++) {
            groupsIndexes.add(t);
        }
        List<Integer> teamsMapping = new ArrayList<Integer>();
        for (int t = 0; t < 2; t++) {
            Collections.shuffle(groupsIndexes);
            for (int g = 0; g < qualifiedTeams.size() / 2; g++) {
                teamsMapping.add(t * qualifiedTeams.size() / 2 + groupsIndexes.get(g));
            }
        }

        for (int i = 0; i < qualifiedTeams.size() / 2; i++) {
            Match match = new Match();
            int shuffle = Emath.rand(0, 1);
            match.teams[HOME] = qualifiedTeams.get(teamsMapping.get(i + shuffle * (qualifiedTeams.size() / 2)));
            match.teams[AWAY] = qualifiedTeams.get(teamsMapping.get(i + (1 - shuffle) * (qualifiedTeams.size() / 2)));
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
            int[] oldResult = legs.get(currentLeg - 1).findResult(match.teams);
            int aggregate1 = match.getResult()[HOME] + oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + oldResult[HOME];
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((oldResult[AWAY] != match.getResult()[AWAY]) && (tournament.awayGoals == Competition.AwayGoals.AFTER_90_MINUTES)) {
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
            int[] oldResult = legs.get(0).findResult(match.teams);
            int aggregate1 = match.getResult()[HOME] + oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + oldResult[HOME];
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((oldResult[AWAY] != match.getResult()[AWAY]) && (tournament.awayGoals != Competition.AwayGoals.OFF)) {
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

        int qualified = getLeg().getQualifiedTeam(match);

        // first leg
        if (currentLeg == 0) {
            if (qualified != -1) {
                if (match.resultAfterPenalties != null) {
                    s = tournament.teams.get(qualified).name
                            + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                            + Math.max(match.resultAfterPenalties[HOME], match.resultAfterPenalties[AWAY])
                            + "-"
                            + Math.min(match.resultAfterPenalties[HOME], match.resultAfterPenalties[AWAY])
                            + " " + Assets.strings.get("MATCH STATUS.ON PENALTIES");
                    if (match.resultAfterExtraTime != null) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                        if ((match.getResult()[HOME] != match.resultAfter90[HOME])
                                || (match.getResult()[AWAY] != match.resultAfter90[AWAY])) {
                            s += " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                                    + " " + match.resultAfter90[HOME] + "-" + match.resultAfter90[AWAY];
                        }
                    }
                } else if (match.resultAfterExtraTime != null) {
                    s = Assets.strings.get("AFTER EXTRA TIME")
                            + " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                            + " " + match.resultAfter90[HOME] + "-" + match.resultAfter90[AWAY];
                }
            }
        }

        // second leg
        else if ((currentLeg == 1) && (numberOfLegs == 2)) {
            if (qualified != -1) {
                // penalties
                if (match.resultAfterPenalties != null) {
                    s = tournament.teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                            + Math.max(match.resultAfterPenalties[HOME], match.resultAfterPenalties[AWAY])
                            + "-"
                            + Math.min(match.resultAfterPenalties[HOME], match.resultAfterPenalties[AWAY])
                            + " " + Assets.strings.get("MATCH STATUS.ON PENALTIES");
                    if (match.resultAfterExtraTime != null) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                        if ((match.getResult()[HOME] != match.resultAfter90[HOME])
                                || (match.getResult()[AWAY] != match.resultAfter90[AWAY])) {
                            s += " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                                    + " " + match.resultAfter90[HOME] + "-" + match.resultAfter90[AWAY];
                        }
                    }
                } else {
                    int[] oldResult = legs.get(currentLeg - 1).findResult(match.teams);
                    int agg_score_a = match.getResult()[HOME] + oldResult[AWAY];
                    int agg_score_b = match.getResult()[AWAY] + oldResult[HOME];

                    // away goals
                    if (agg_score_a == agg_score_b) {
                        s += agg_score_a + "-" + agg_score_b + " " + Assets.strings.get("MATCH STATUS.ON AGGREGATE") + " "
                                + tournament.teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " " + Assets.strings.get("MATCH STATUS.ON AWAY GOALS");
                    }
                    //on aggregate
                    else {
                        s = tournament.teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                                + Math.max(agg_score_a, agg_score_b)
                                + "-"
                                + Math.min(agg_score_a, agg_score_b)
                                + " " + Assets.strings.get("MATCH STATUS.ON AGGREGATE");
                    }
                    if (match.resultAfterExtraTime != null) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                    }
                }
            } else {
                int[] oldResult = legs.get(currentLeg - 1).findResult(match.teams);
                s = Assets.strings.get("MATCH STATUS.1ST LEG") + " " + oldResult[AWAY] + "-" + oldResult[HOME];
            }
        }

        // replays
        else {
            if (qualified != -1) {
                if (match.resultAfterPenalties != null) {
                    s = tournament.teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                            + Math.max(match.resultAfterPenalties[HOME], match.resultAfterPenalties[AWAY])
                            + "-"
                            + Math.min(match.resultAfterPenalties[HOME], match.resultAfterPenalties[AWAY])
                            + " " + Assets.strings.get("MATCH STATUS.ON PENALTIES");
                    if (match.resultAfterExtraTime != null) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                    }
                } else if (match.resultAfterExtraTime != null) {
                    s = Assets.strings.get("AFTER EXTRA TIME") + " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                            + " " + match.resultAfter90[HOME] + "-" + match.resultAfter90[AWAY];
                }
            }
        }

        return s;
    }

    @Override
    protected String getMenuTitle() {
        String title = Assets.gettext(name);
        int matches = getLeg().matches.size();
        switch (numberOfLegs) {
            case 1:
                switch (currentLeg) {
                    case 0:
                        break;
                    case 1:
                        if (matches == 1) {
                            title += " " + Assets.strings.get("MATCH STATUS.REPLAY");
                        } else {
                            title += " " + Assets.strings.get("MATCH STATUS.REPLAYS");
                        }
                        break;
                    case 2:
                        if (matches == 1) {
                            title += " " + Assets.strings.get("MATCH STATUS.2ND REPLAY");
                        } else {
                            title += " " + Assets.strings.get("MATCH STATUS.2ND REPLAYS");
                        }
                        break;
                    case 3:
                        if (matches == 1) {
                            title += " " + Assets.strings.get("MATCH STATUS.3RD REPLAY");
                        } else {
                            title += " " + Assets.strings.get("MATCH STATUS.3RD REPLAYS");
                        }
                        break;
                    default:
                        if (matches == 1) {
                            title += " " + Assets.strings.get("MATCH STATUS.REPLAY");
                        } else {
                            title += " " + Assets.strings.get("MATCH STATUS.REPLAYS");
                        }
                }
                break;
            case 2:
                switch (currentLeg) {
                    case 0:
                        title += " " + Assets.strings.get("MATCH STATUS.1ST LEG");
                        break;
                    case 1:
                        title += " " + Assets.strings.get("MATCH STATUS.2ND LEG");
                        break;
                    default:
                        if (matches == 1) {
                            title += " " + Assets.strings.get("MATCH STATUS.REPLAY");
                        } else {
                            title += " " + Assets.strings.get("MATCH STATUS.REPLAYS");
                        }
                }
        }
        return title;
    }

    private void newLeg() {
        Leg leg = new Leg(this);
        legs.add(leg);
    }
}
