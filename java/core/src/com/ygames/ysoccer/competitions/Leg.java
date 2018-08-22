package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class Leg {

    private Round round;
    public ArrayList<Match> matches;

    Leg(Round round) {
        this.round = round;
        matches = new ArrayList<Match>();
    }

    private int getIndex() {
        return round.legs.indexOf(this);
    }

    public int getQualifiedTeam(Match match) {

        if (match.getResult() == null) {
            return -1;
        }

        if (match.resultAfterPenalties != null) {
            if (match.resultAfterPenalties[HOME] > match.resultAfterPenalties[AWAY]) {
                return match.teams[HOME];
            } else if (match.resultAfterPenalties[HOME] < match.resultAfterPenalties[AWAY]) {
                return match.teams[AWAY];
            } else {
                throw new GdxRuntimeException("Invalid state in cup");
            }
        }

        // first leg
        if (getIndex() == 0) {
            if (round.numberOfLegs == 1) {
                if (match.getResult()[HOME] > match.getResult()[AWAY]) {
                    return match.teams[HOME];
                } else if (match.getResult()[HOME] < match.getResult()[AWAY]) {
                    return match.teams[AWAY];
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        // second leg
        else if ((getIndex() == 1) && (round.numberOfLegs == 2)) {

            int[] oldResult = round.legs.get(getIndex() - 1).findResult(match.teams);
            int aggregate1 = match.getResult()[HOME] + oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + oldResult[HOME];
            if (aggregate1 > aggregate2) {
                return match.teams[HOME];
            } else if (aggregate1 < aggregate2) {
                return match.teams[AWAY];
            } else {
                if ((round.cup.awayGoals == Competition.AwayGoals.AFTER_90_MINUTES) ||
                        (round.cup.awayGoals == Competition.AwayGoals.AFTER_EXTRA_TIME && match.resultAfterExtraTime != null)) {
                    if (oldResult[AWAY] > match.getResult()[AWAY]) {
                        return match.teams[HOME];
                    } else if (oldResult[AWAY] < match.getResult()[AWAY]) {
                        return match.teams[AWAY];
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
            if (match.getResult()[HOME] > match.getResult()[AWAY]) {
                return match.teams[HOME];
            } else if (match.getResult()[HOME] < match.getResult()[AWAY]) {
                return match.teams[AWAY];
            } else {
                return -1;
            }
        }
    }

    ArrayList<Integer> getQualifiedTeams() {
        ArrayList<Integer> qualifiedTeams = new ArrayList<Integer>();
        for (Match match : matches) {
            int qualifiedTeam = getQualifiedTeam(match);
            if (qualifiedTeam != -1) {
                qualifiedTeams.add(qualifiedTeam);
            }
        }
        return qualifiedTeams;
    }

    boolean hasReplays() {
        return getQualifiedTeams().size() < matches.size();
    }

    protected int[] findResult(int[] teams) {
        for (Match match : matches) {
            if (match.teams[HOME] == teams[AWAY] && match.teams[AWAY] == teams[HOME]) {
                return match.getResult();
            }
        }
        throw new RuntimeException("Cannot find result");
    }
}
