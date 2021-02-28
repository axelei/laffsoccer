package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Match.ResultType.AFTER_90_MINUTES;
import static com.ygames.ysoccer.match.Match.ResultType.AFTER_PENALTIES;
import static com.ygames.ysoccer.match.Team.ControlMode.COMPUTER;

public class Cup extends Competition implements Json.Serializable {

    public ArrayList<Round> rounds;
    public int currentLeg;

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

        updateMonth();
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
        Match match = getMatch();
        Team homeTeam = getTeam(HOME);
        Team awayTeam = getTeam(AWAY);

        int homeGoals = Match.generateGoals(homeTeam, awayTeam, false);
        int awayGoals = Match.generateGoals(awayTeam, homeTeam, false);
        match.setResult(homeGoals, awayGoals, Match.ResultType.AFTER_90_MINUTES);

        if (playExtraTime()) {
            homeGoals += Match.generateGoals(homeTeam, awayTeam, true);
            awayGoals += Match.generateGoals(awayTeam, homeTeam, true);
            match.setResult(homeGoals, awayGoals, Match.ResultType.AFTER_EXTRA_TIME);
        }

        generateScorers(homeTeam, homeGoals);
        generateScorers(awayTeam, awayGoals);

        if (playPenalties()) {
            do {
                homeGoals = EMath.floor(6 * Math.random());
                awayGoals = EMath.floor(6 * Math.random());
            } while (homeGoals == awayGoals);
            match.setResult(homeGoals, awayGoals, AFTER_PENALTIES);
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
            int[] oldResult = getRound().legs.get(currentLeg - 1).findResult(match.teams);
            int aggregate1 = match.getResult()[HOME] + oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + oldResult[HOME];

            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((oldResult[AWAY] != match.getResult()[AWAY]) && (awayGoals == AwayGoals.AFTER_90_MINUTES)) {
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
    public boolean playPenalties() {
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
            int[] oldResult = getRound().legs.get(currentLeg - 1).findResult(match.teams);
            int aggregate1 = match.getResult()[HOME] + oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + oldResult[HOME];
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((oldResult[AWAY] != match.getResult()[AWAY]) && (awayGoals != AwayGoals.OFF)) {
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
                    int[] oldResult = getRound().legs.get(currentLeg - 1).findResult(match.teams);
                    int agg_score_a = match.getResult()[HOME] + oldResult[AWAY];
                    int agg_score_b = match.getResult()[AWAY] + oldResult[HOME];

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
                int[] oldResult = getRound().legs.get(currentLeg - 1).findResult(match.teams);
                s = Assets.strings.get("MATCH STATUS.1ST LEG") + " " + oldResult[AWAY] + "-" + oldResult[HOME];
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

        String title = name + " " + Assets.gettext(getRound().name);
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
        updateRoundNames();
    }

    public void removeRound() {
        if (rounds.size() > 1) {
            rounds.remove(rounds.size() - 1);
        }
        numberOfTeams = getRoundTeams(0);
        updateRoundNames();
    }

    private void updateRoundNames() {
        for (int i = 0; i < rounds.size(); i++) {
            rounds.get(i).name = getRoundLabel(i);
        }
    }

    private String getRoundLabel(int round) {
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

    public boolean hasTwoLegsRound() {
        for (Round round : rounds) {
            if (round.numberOfLegs == 2) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void matchInterrupted() {
        Match match = getMatch();
        if (match.team[HOME].controlMode == COMPUTER && match.team[AWAY].controlMode != COMPUTER) {
            int goals = 4 + Assets.random.nextInt(2);
            if (match.resultAfterPenalties != null) {
                goals += match.resultAfterPenalties[AWAY];
                match.resultAfterPenalties[HOME] += goals;
            } else if (match.resultAfterExtraTime != null) {
                goals += match.resultAfterExtraTime[AWAY];
                match.resultAfterExtraTime[HOME] += goals;
                generateScorers(match.team[HOME], goals);
            } else if (match.resultAfter90 != null) {
                goals += match.resultAfter90[AWAY];
                match.resultAfter90[HOME] += goals;
                generateScorers(match.team[HOME], goals);
            } else {
                match.setResult(goals, 0, AFTER_90_MINUTES);
                generateScorers(match.team[HOME], goals);
            }
            matchCompleted();
        } else if (match.team[HOME].controlMode != COMPUTER && match.team[AWAY].controlMode == COMPUTER) {
            int goals = 4 + Assets.random.nextInt(2);
            if (match.resultAfterPenalties != null) {
                goals += match.resultAfterPenalties[HOME];
                match.resultAfterPenalties[AWAY] += goals;
            } else if (match.resultAfterExtraTime != null) {
                goals += match.resultAfterExtraTime[HOME];
                match.resultAfterExtraTime[AWAY] += goals;
                generateScorers(match.team[HOME], goals);
            } else if (match.resultAfter90 != null) {
                goals += match.resultAfter90[HOME];
                match.resultAfter90[AWAY] += goals;
                generateScorers(match.team[HOME], goals);
            } else {
                match.setResult(0, 6, AFTER_90_MINUTES);
                generateScorers(match.team[HOME], goals);
            }
            matchCompleted();
        } else {
            match.resultAfter90 = null;
            match.resultAfterExtraTime = null;
            match.resultAfterPenalties = null;
        }
    }

    @Override
    public Team getMatchWinner() {
        int qualified = getLeg().getQualifiedTeam(getMatch());
        if (qualified != -1) {
            return teams.get(qualified);
        }
        return null;
    }

    @Override
    public Team getFinalWinner() {
        if (isEnded()) {
            return teams.get(getLeg().getQualifiedTeam(getMatch()));
        }
        return null;
    }

    @Override
    public Team getFinalRunnerUp() {
        if (isEnded()) {
            int winner = getLeg().getQualifiedTeam(getMatch());
            if (winner == getMatch().teams[HOME]) {
                return teams.get(getMatch().teams[AWAY]);
            } else {
                return teams.get(getMatch().teams[HOME]);
            }
        }
        return null;
    }
}
