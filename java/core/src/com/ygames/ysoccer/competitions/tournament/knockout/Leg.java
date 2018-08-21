package com.ygames.ysoccer.competitions.tournament.knockout;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class Leg {

    private Knockout knockout;
    public ArrayList<Match> matches;

    Leg(Knockout knockout) {
        this.knockout = knockout;
        matches = new ArrayList<Match>();
    }

    private int getIndex() {
        return knockout.legs.indexOf(this);
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
            if (knockout.numberOfLegs == 1) {
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
        else if ((getIndex() == 1) && (knockout.numberOfLegs == 2)) {

            int aggregate1 = match.getResult()[HOME] + match.oldResult[AWAY];
            int aggregate2 = match.getResult()[AWAY] + match.oldResult[HOME];
            if (aggregate1 > aggregate2) {
                return match.teams[HOME];
            } else if (aggregate1 < aggregate2) {
                return match.teams[AWAY];
            } else {
                if ((knockout.tournament.awayGoals == Competition.AwayGoals.AFTER_90_MINUTES) ||
                        (knockout.tournament.awayGoals == Competition.AwayGoals.AFTER_EXTRA_TIME && match.resultAfterExtraTime != null)) {
                    if (match.oldResult[AWAY] > match.getResult()[AWAY]) {
                        return match.teams[HOME];
                    } else if (match.oldResult[AWAY] < match.getResult()[AWAY]) {
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

}
