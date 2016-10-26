package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Collections;

public class Cup extends Competition {

    public ArrayList<Round> rounds;
    int currentLeg;
    ArrayList<Integer> qualifiedTeams;
    public ArrayList<Match> calendarCurrent;

    public enum ResultType {AFTER_90_MINS, AFTER_EXTRA_TIME, AFTER_PENALTIES}

    public enum AwayGoals {
        OFF,
        AFTER_90_MINS,
        AFTER_EXTRA_TIME
    }

    public AwayGoals awayGoals;

    public Cup() {
        rounds = new ArrayList<Round>();
        awayGoals = AwayGoals.OFF;
        currentLeg = 0;
        calendarCurrent = null;
        qualifiedTeams = new ArrayList<Integer>();
    }

    public void init() {
        numberOfTeams = getRoundTeams(0);
        absolutePath = Assets.teamsFolder.child(path).path();
    }

    public void start(ArrayList<Team> teams) {
        super.start(teams);
        for (int i = 0; i < teams.size(); i++) {
            qualifiedTeams.add(i);
        }
        calendarGenerate();
    }

    @Override
    public void restart() {
        currentRound = 0;
        currentMatch = 0;
        currentLeg = 0;
        qualifiedTeams.clear();
        for (int i = 0; i < teams.size(); i++) {
            qualifiedTeams.add(i);
        }
        calendarGenerate();
    }

    public Match getMatch() {
        return calendarCurrent.get(currentMatch);
    }

    @Override
    public boolean isEnded() {
        return currentRound == rounds.size() - 1 && getMatch().qualified != -1;
    }

    public void nextMatch() {
        currentMatch += 1;
        if (currentMatch == calendarCurrent.size()) {
            nextLeg();
        }
    }

    public void nextLeg() {
        currentLeg += 1;
        currentMatch = 0;
        calendarGenerate();
        if (calendarCurrent.size() == 0) {
            nextRound();
        }
    }

    public void nextRound() {
        currentRound += 1;
        currentLeg = 0;
        currentMatch = 0;
        calendarGenerate();
    }

    void calendarGenerate() {

        // first leg
        if (currentLeg == 0) {
            Collections.shuffle(qualifiedTeams);
            calendarCurrent = new ArrayList<Match>();
            for (int i = 0; i < qualifiedTeams.size() / 2; i++) {
                Match match = new Match();
                match.team[Match.HOME] = qualifiedTeams.get(2 * i);
                match.team[Match.AWAY] = qualifiedTeams.get(2 * i + 1);
                match.result = null;
                match.oldResult = null;
                calendarCurrent.add(match);
            }
            qualifiedTeams.clear();
        }

        // second leg
        else if ((currentLeg == 1) && (rounds.get(currentRound).legs == 2)) {
            for (int i = 0; i < calendarCurrent.size(); i++) {
                Match match = calendarCurrent.get(i);

                // swap teams
                int tmp = match.team[Match.HOME];
                match.team[Match.HOME] = match.team[Match.AWAY];
                match.team[Match.AWAY] = tmp;

                match.ended = false;
                match.oldResult = match.result;
                match.result = null;
                match.includesExtraTime = false;
                match.resultAfter90 = null;
                match.status = Assets.strings.get("MATCH STATUS.1ST LEG") +
                        " " + match.oldResult.awayGoals +
                        "-" + match.oldResult.homeGoals;
            }
        }

        // replays
        else {
            for (int i = 0; i < calendarCurrent.size(); i++) {
                Match match = calendarCurrent.get(i);
                if (match.qualified == -1) {
                    // swap teams
                    int tmp = match.team[Match.HOME];
                    match.team[Match.HOME] = match.team[Match.AWAY];
                    match.team[Match.AWAY] = tmp;

                    match.ended = false;
                    match.result = null;
                    match.includesExtraTime = false;
                    match.resultAfter90 = null;
                    match.oldResult = null;
                    match.status = "";
                } else {
                    calendarCurrent.remove(match);
                }
            }
        }

        // update month
        int seasonLength = ((seasonEnd - seasonStart + 12) % 12);
        currentMonth = (seasonStart + seasonLength * currentRound / rounds.size()) % 12;
    }

    public void generateResult() {
        Match match = getMatch();
        Team homeTeam = teams.get(match.team[Match.HOME]);
        Team awayTeam = teams.get(match.team[Match.AWAY]);

        int goalA = Match.generateScore(homeTeam, awayTeam, false);
        int goalB = Match.generateScore(awayTeam, homeTeam, false);
        setResult(goalA, goalB, Cup.ResultType.AFTER_90_MINS);

        if (playExtraTime()) {
            goalA += Match.generateScore(homeTeam, awayTeam, true);
            goalB += Match.generateScore(awayTeam, homeTeam, true);
            setResult(goalA, goalB, Cup.ResultType.AFTER_EXTRA_TIME);
        }

        homeTeam.generateScorers(goalA);
        awayTeam.generateScorers(goalB);

        if (playPenalties()) {
            do {
                goalA = Emath.floor(6 * Math.random());
                goalB = Emath.floor(6 * Math.random());
            } while (goalA == goalB);
            setResult(goalA, goalB, Cup.ResultType.AFTER_PENALTIES);
        }
    }

    public void setResult(int homeGoals, int awayGoals, ResultType resultType) {
        Match match = getMatch();
        if (resultType == ResultType.AFTER_PENALTIES) {
            match.resultAfterPenalties = new Match.Result(homeGoals, awayGoals);
        } else {
            match.result = new Match.Result(homeGoals, awayGoals);
            if (resultType == ResultType.AFTER_EXTRA_TIME) {
                match.includesExtraTime = true;
            } else {
                match.resultAfter90 = new Match.Result(homeGoals, awayGoals);
            }
        }
        match.qualified = getQualified(match);
        match.status = getMatchStatus(match);
        if (match.qualified != -1) {
            qualifiedTeams.add(match.qualified);
        }
        match.ended = true;
    }

    // decide if extra time have to be played depending on current result, leg's type and settings
    public boolean playExtraTime() {
        Match match = getMatch();
        Round round = rounds.get(currentRound);

        // first leg
        if (currentLeg == 0) {

            // two legs round
            if (round.legs == 2) {
                return false;
            }

            // result
            if (match.result.homeGoals != match.result.awayGoals) {
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
        else if (currentLeg == 1 && round.legs == 2) {

            // aggregate goals
            int aggregate1 = match.result.homeGoals + match.oldResult.awayGoals;
            int aggregate2 = match.result.awayGoals + match.oldResult.homeGoals;
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((match.oldResult.awayGoals != match.result.awayGoals) && (awayGoals == AwayGoals.AFTER_90_MINS)) {
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
            if (match.result.homeGoals != match.result.awayGoals) {
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
            if (round.legs == 2) {
                return false;
            }

            // result
            if (match.result.homeGoals != match.result.awayGoals) {
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
        else if ((currentLeg == 1) && (round.legs == 2)) {

            // aggregate goals
            int aggregate1 = match.result.homeGoals + match.oldResult.awayGoals;
            int aggregate2 = match.result.awayGoals + match.oldResult.homeGoals;
            if (aggregate1 != aggregate2) {
                return false;
            }

            // away goals
            if ((match.oldResult.awayGoals != match.result.awayGoals) && (awayGoals != AwayGoals.OFF)) {
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
            if (match.result.homeGoals != match.result.awayGoals) {
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

    private int getQualified(Match match) {
        if (match.resultAfterPenalties != null) {
            if (match.resultAfterPenalties.homeGoals > match.resultAfterPenalties.awayGoals) {
                return match.team[Match.HOME];
            } else if (match.resultAfterPenalties.homeGoals < match.resultAfterPenalties.awayGoals) {
                return match.team[Match.AWAY];
            } else {
                throw new GdxRuntimeException("Invalid state in cup");
            }
        }

        Round round = rounds.get(currentRound);

        // first leg
        if (currentLeg == 0) {
            switch (round.legs) {
                case 1:
                    if (match.result.homeGoals > match.result.awayGoals) {
                        return match.team[Match.HOME];
                    } else if (match.result.homeGoals < match.result.awayGoals) {
                        return match.team[Match.AWAY];
                    } else {
                        return -1;
                    }
                case 2:
                    return -1;
            }
        }

        // second leg
        else if ((currentLeg == 1) && (round.legs == 2)) {
            int aggregate1 = match.result.homeGoals + match.oldResult.awayGoals;
            int aggregate2 = match.result.awayGoals + match.oldResult.homeGoals;
            if (aggregate1 > aggregate2) {
                return match.team[Match.HOME];
            } else if (aggregate1 < aggregate2) {
                return match.team[Match.AWAY];
            } else {
                if ((awayGoals == AwayGoals.AFTER_90_MINS) ||
                        (awayGoals == AwayGoals.AFTER_EXTRA_TIME && match.includesExtraTime)) {
                    if (match.oldResult.awayGoals > match.result.awayGoals) {
                        return match.team[Match.HOME];
                    } else if (match.oldResult.awayGoals < match.result.awayGoals) {
                        return match.team[Match.AWAY];
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
        }

        // replays
        else {
            if (match.result.homeGoals > match.result.awayGoals) {
                return match.team[Match.HOME];
            } else if (match.result.homeGoals < match.result.awayGoals) {
                return match.team[Match.AWAY];
            } else {
                return -1;
            }
        }

        // should never get here
        return -1;
    }

    private String getMatchStatus(Match match) {
        String s = "";

        // first leg
        if (currentLeg == 0) {
            if (match.qualified != -1) {
                if (match.resultAfterPenalties != null) {
                    s = teams.get(match.qualified).name
                            + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                            + Math.max(match.resultAfterPenalties.homeGoals, match.resultAfterPenalties.awayGoals)
                            + "-"
                            + Math.min(match.resultAfterPenalties.homeGoals, match.resultAfterPenalties.awayGoals)
                            + " " + Assets.strings.get("MATCH STATUS.ON PENALTIES");
                    if (match.includesExtraTime) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                        if ((match.result.homeGoals != match.resultAfter90.homeGoals)
                                || (match.result.awayGoals != match.resultAfter90.awayGoals)) {
                            s += " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                                    + " " + match.resultAfter90.homeGoals + "-" + match.resultAfter90.awayGoals;
                        }
                    }
                } else if (match.includesExtraTime) {
                    s = Assets.strings.get("AFTER EXTRA TIME")
                            + " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                            + " " + match.resultAfter90.homeGoals + "-" + match.resultAfter90.awayGoals;
                }
            }
        }

        // second leg
        else if ((currentLeg == 1) && (rounds.get(currentRound).legs == 2)) {
            if (match.qualified != -1) {
                // penalties
                if (match.resultAfterPenalties != null) {
                    s = teams.get(match.qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                            + Math.max(match.resultAfterPenalties.homeGoals, match.resultAfterPenalties.awayGoals)
                            + "-"
                            + Math.min(match.resultAfterPenalties.homeGoals, match.resultAfterPenalties.awayGoals)
                            + " " + Assets.strings.get("MATCH STATUS.ON PENALTIES");
                    if (match.includesExtraTime) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                        if ((match.result.homeGoals != match.resultAfter90.homeGoals)
                                || (match.result.awayGoals != match.resultAfter90.awayGoals)) {
                            s += " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                                    + " " + match.resultAfter90.homeGoals + "-" + match.resultAfter90.awayGoals;
                        }
                    }
                } else {
                    int agg_score_a = match.result.homeGoals + match.oldResult.awayGoals;
                    int agg_score_b = match.result.awayGoals + match.oldResult.homeGoals;

                    // away goals
                    if (agg_score_a == agg_score_b) {
                        s += agg_score_a + "-" + agg_score_b + " " + Assets.strings.get("MATCH STATUS.ON AGGREGATE") + " "
                                + teams.get(match.qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " " + Assets.strings.get("MATCH STATUS.ON AWAY GOALS");
                    }
                    //on aggregate
                    else {
                        s = teams.get(match.qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                                + Math.max(agg_score_a, agg_score_b)
                                + "-"
                                + Math.min(agg_score_a, agg_score_b)
                                + " " + Assets.strings.get("MATCH STATUS.ON AGGREGATE");
                    }
                    if (match.includesExtraTime) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                    }
                }
            } else {
                s = Assets.strings.get("MATCH STATUS.1ST LEG") + " " + match.oldResult.homeGoals + "-" + match.oldResult.awayGoals;
            }

        }

        // replays
        else {
            if (match.qualified != -1) {
                if (match.resultAfterPenalties != null) {
                    s = teams.get(match.qualified).name + " " + Assets.strings.get("MATCH STATUS.WIN") + " "
                            + Math.max(match.resultAfterPenalties.homeGoals, match.resultAfterPenalties.awayGoals)
                            + "-"
                            + Math.min(match.resultAfterPenalties.homeGoals, match.resultAfterPenalties.awayGoals)
                            + " " + Assets.strings.get("MATCH STATUS.ON PENALTIES");
                    if (match.includesExtraTime) {
                        s += " " + Assets.strings.get("AFTER EXTRA TIME");
                    }
                } else if (match.includesExtraTime) {
                    s = Assets.strings.get("AFTER EXTRA TIME") + " " + Assets.strings.get("MATCH STATUS.90 MINUTES")
                            + " " + match.resultAfter90.homeGoals + "-" + match.resultAfter90.awayGoals;
                }
            }
        }

        return s;
    }

    @Override
    public String getMenuTitle() {

        String title = name + " " + Assets.strings.get(getRoundName(currentRound));
        int matches = calendarCurrent.size();
        switch (rounds.get(currentRound).legs) {
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

    public Type getType() {
        return Type.CUP;
    }

    public void addRound() {
        if (rounds.size() < 6) {
            rounds.add(new Round());
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
            case AFTER_90_MINS:
                return "AFTER 90 MINS";
            case AFTER_EXTRA_TIME:
                return "AFTER EXTRA TIME";
            default:
                throw new GdxRuntimeException("Unknown AwayGoals value");
        }
    }

    public boolean hasTwoLegsRound() {
        for (Round round : rounds) {
            if (round.legs == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean bothComputers() {
        Match match = getMatch();
        return teams.get(match.team[Match.HOME]).controlMode == Team.ControlMode.COMPUTER
                && teams.get(match.team[Match.AWAY]).controlMode == Team.ControlMode.COMPUTER;
    }
}
