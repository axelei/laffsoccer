package com.ygames.ysoccer.competitions.tournament.knockout;

import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;

public class Leg {

    private Knockout knockout;
    public ArrayList<Match> matches;

    Leg(Knockout knockout) {
        this.knockout = knockout;
        matches = new ArrayList<Match>();
    }

}
