package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class Cup extends Competition implements Json.Serializable {

    public ArrayList<Round> rounds;
    public int currentLeg;

    public enum AwayGoals {
        OFF,
        AFTER_90_MINUTES,
        AFTER_EXTRA_TIME
    }

    public AwayGoals awayGoals;

    public Cup() {
        super(Type.CUP);
        rounds = new ArrayList<Round>();
        awayGoals = AwayGoals.OFF;
        currentLeg = 0;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        Round[] roundsArray = json.readValue("rounds", Round[].class, jsonData);
        if (roundsArray != null) {
            for (Round round : roundsArray) {
                round.setCup(this);
                rounds.add(round);
            }
        }
        currentLeg = jsonData.getInt("currentLeg", 0);
        if (hasTwoLegsRound()) {
            awayGoals = json.readValue("awayGoals", AwayGoals.class, AwayGoals.AFTER_90_MINUTES, jsonData);
        }
    }

    @Override
    public void write(Json json) {
        super.write(json);

        json.writeValue("rounds", rounds, Round[].class, Round.class);
        json.writeValue("currentLeg", currentLeg);
        if (hasTwoLegsRound()) {
            json.writeValue("awayGoals", awayGoals);
        }
    }

    @Override
    public void start(ArrayList<Team> teams) {
        super.start(teams);

        // if first leg is not preset, create it
        if (getRound().legs.size() == 0) {
            getRound().newLeg();
            generateMatches();
        }
        updateMonth();
    }

    @Override
    public void restart() {
        super.restart();
        currentRound = 0;
        currentMatch = 0;
        currentLeg = 0;

        // copy first round, first leg matches
        List<Match> firstLegMatches = new ArrayList<Match>();
        for (Match m : rounds.get(0).legs.get(0).matches) {
            Match match = new Match();
            match.teams[HOME] = m.teams[HOME];
            match.teams[AWAY] = m.teams[AWAY];
            firstLegMatches.add(match);
        }

        // clear all legs
        for (int r = 0; r < rounds.size(); r++) {
            Round round = rounds.get(r);
            round.legs.clear();
            // restore first leg matches
            if (r == 0) {
                round.newLeg();
                round.legs.get(0).matches.addAll(firstLegMatches);
            }
        }
    }

    public Round getRound() {
        return rounds.get(currentRound);
    }

    public Leg getLeg() {
        return getRound().legs.get(currentLeg);
    }

    @Override
    public Match getMatch() {
        return getLeg().matches.get(currentMatch);
    }

    public ArrayList<Match> getMatches() {
        return getLeg().matches;
    }

    public Team getTeam(int t) {
        return teams.get(getMatch().teams[t]);
    }

    public boolean isLegEnded() {
        return currentMatch == getLeg().matches.size() - 1;
    }

    public boolean isRoundEnded() {
        return currentLeg == getRound().legs.size() - 1 && !getLeg().hasReplays();
    }

    @Override
    public boolean isEnded() {
        return currentRound == rounds.size() - 1 && getLeg().getQualifiedTeam(getMatch()) != -1;
    }

    public void nextMatch() {
        currentMatch += 1;
        if (currentMatch == getLeg().matches.size()) {
            nextLeg();
        }
    }

    private void nextLeg() {
        currentLeg += 1;
        currentMatch = 0;
        getRound().newLeg();
        generateMatches();
        if (getLeg().matches.size() == 0) {
            nextRound();
        }
        updateMonth();
    }

    private void nextRound() {
        currentRound += 1;
        currentLeg = 0;
        currentMatch = 0;
        getRound().newLeg();
        generateMatches();
    }

    private void updateMonth() {
        if (weather == Weather.BY_SEASON) {
            int seasonLength = ((seasonEnd.ordinal() - seasonStart.ordinal() + 12) % 12);
            currentMonth = Month.values()[(seasonStart.ordinal() + seasonLength * currentRound / rounds.size()) % 12];
        }
    }

    private void generateMatches() {

        // first leg
        if (currentLeg == 0) {
            ArrayList<Integer> qualifiedTeams = new ArrayList<Integer>();
            if (currentRound == 0) {
                for (int i = 0; i < numberOfTeams; i++) {
                    qualifiedTeams.add(i);
                }
            } else {
                for (Leg leg : rounds.get(currentRound - 1).legs) {
                    qualifiedTeams.addAll(leg.getQualifiedTeams());
                }
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
        else if ((currentLeg == 1) && (getRound().numberOfLegs == 2)) {
            for (Match oldMatch : getRound().legs.get(0).matches) {
                Match match = new Match();
                match.teams[HOME] = oldMatch.teams[AWAY];
                match.teams[AWAY] = oldMatch.teams[HOME];
                match.oldResult = oldMatch.getResult();
                getLeg().matches.add(match);
            }
        }

        // replays
        else {
            Leg previousLeg = getRound().legs.get(currentLeg - 1);
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
        Team homeTeam = getTeam(HOME);
        Team awayTeam = getTeam(AWAY);

        int homeGoals = Match.generateGoals(homeTeam, awayTeam, false);
        int awayGoals = Match.generateGoals(awayTeam, homeTeam, false);
        setResult(homeGoals, awayGoals, Match.ResultType.AFTER_90_MINUTES);

        if (playExtraTime()) {
            homeGoals += Match.generateGoals(homeTeam, awayTeam, true);
            awayGoals += Match.generateGoals(awayTeam, homeTeam, true);
            setResult(homeGoals, awayGoals, Match.ResultType.AFTER_EXTRA_TIME);
        }

        generateScorers(homeTeam, homeGoals);
        generateScorers(awayTeam, awayGoals);

        if (playPenalties()) {
            do {
                homeGoals = Emath.floor(6 * Math.random());
                awayGoals = Emath.floor(6 * Math.random());
            } while (homeGoals == awayGoals);
            setResult(homeGoals, awayGoals, Match.ResultType.AFTER_PENALTIES);
        }
    }

    public void setResult(int homeGoals, int awayGoals, Match.ResultType resultType) {
        Match match = getMatch();
        switch (resultType) {
            case AFTER_90_MINUTES:
                match.setResultAfter90(homeGoals, awayGoals);
                break;
            case AFTER_EXTRA_TIME:
                match.setResultAfterExtraTime(homeGoals, awayGoals);
                break;
            case AFTER_PENALTIES:
                match.setResultAfterPenalties(homeGoals, awayGoals);
                break;
        }
    }

    // decide if extra time have to be played depending on current result, leg's type and settings
    public boolean playExtraTime() {
        Match match = getMatch();
        Round round = rounds.get(currentRound);

        // first leg
        if (currentLeg == 0) {

            // two legs round
            if (round.numberOfLegs == 2) {
                return false;
            }

            // result
            if (match.getResult()[HOME] != match.getResult()[AWAY]) {
                return false;
            }

            // settings
            switch (round.extraTime) {
                case OFF:
                    return false;

                case ON:
                    return true;

                case IF_REPLAY:
                    return false;
            }
        }

        // second leg
        else if (currentLeg == 1 && round.numberOfLegs == 2) {

            // aggregate goals
            int aggregate1 = match.getResult()[HOME] + match.oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + match.oldResult[HOME];
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((match.oldResult[AWAY] != match.getResult()[AWAY]) && (awayGoals == AwayGoals.AFTER_90_MINUTES)) {
                return false;
            }

            // settings
            switch (round.extraTime) {
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
            switch (round.extraTime) {
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
        Round round = rounds.get(currentRound);

        // first leg
        if (currentLeg == 0) {

            // two legs round
            if (round.numberOfLegs == 2) {
                return false;
            }

            // result
            if (match.getResult()[HOME] != match.getResult()[AWAY]) {
                return false;
            }

            // settings
            switch (round.penalties) {
                case OFF:
                    return false;

                case ON:
                    return true;

                case IF_REPLAY:
                    return false;
            }
        }

        // second leg
        else if ((currentLeg == 1) && (round.numberOfLegs == 2)) {

            // aggregate goals
            int aggregate1 = match.getResult()[HOME] + match.oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + match.oldResult[HOME];
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((match.oldResult[AWAY] != match.getResult()[AWAY]) && (awayGoals != AwayGoals.OFF)) {
                return false;
            }

            // settings
            switch (round.penalties) {
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
            switch (round.penalties) {
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
                    s = teams.get(qualified).name
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
        else if ((currentLeg == 1) && (rounds.get(currentRound).numberOfLegs == 2)) {
            if (qualified != -1) {
                // penalties
                if (match.resultAfterPenalties != null) {
                    s = teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
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
                    int agg_score_a = match.getResult()[HOME] + match.oldResult[AWAY];
                    int agg_score_b = match.getResult()[AWAY] + match.oldResult[HOME];

                    // away goals
                    if (agg_score_a == agg_score_b) {
                        s += agg_score_a + "-" + agg_score_b + " " + Assets.strings.get("MATCH STATUS.ON AGGREGATE") + " "
                                + teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " " + Assets.strings.get("MATCH STATUS.ON AWAY GOALS");
                    }
                    //on aggregate
                    else {
                        s = teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
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
                s = Assets.strings.get("MATCH STATUS.1ST LEG") + " " + match.oldResult[HOME] + "-" + match.oldResult[AWAY];
            }
        }

        // replays
        else {
            if (qualified != -1) {
                if (match.resultAfterPenalties != null) {
                    s = teams.get(qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
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
    public String getMenuTitle() {

        String title = name + " " + Assets.strings.get(getRoundName(currentRound));
        int matches = getLeg().matches.size();
        switch (rounds.get(currentRound).numberOfLegs) {
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

    public void addRound() {
        if (rounds.size() < 6) {
            Round round = new Round();
            round.setCup(this);
            rounds.add(round);
        }
        numberOfTeams = getRoundTeams(0);
    }

    public void removeRound() {
        if (rounds.size() > 1) {
            rounds.remove(rounds.size() - 1);
        }
        numberOfTeams = getRoundTeams(0);
    }

    public String getRoundName(int round) {
        if (round == rounds.size() - 1) {
            return "FINAL";
        } else if (round == rounds.size() - 2) {
            return "SEMI-FINAL";
        } else if (round == rounds.size() - 3) {
            return "QUARTER-FINAL";
        } else if (round == 0) {
            return "FIRST ROUND";
        } else if (round == 1) {
            return "SECOND ROUND";
        } else {
            return "THIRD ROUND";
        }
    }

    public int getRoundTeams(int round) {
        return (int) Math.pow(2, (rounds.size() - round));
    }

    public String getAwayGoalsLabel() {
        switch (awayGoals) {
            case OFF:
                return "OFF";
            case AFTER_90_MINUTES:
                return "AFTER 90 MINS";
            case AFTER_EXTRA_TIME:
                return "AFTER EXTRA TIME";
            default:
                throw new GdxRuntimeException("Unknown AwayGoals value");
        }
    }

    public boolean hasTwoLegsRound() {
        for (Round round : rounds) {
            if (round.numberOfLegs == 2) {
                return true;
            }
        }
        return false;
    }
}
