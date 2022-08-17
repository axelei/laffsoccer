package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.tournament.knockout.Knockout;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;

public class Tournament extends Competition implements Json.Serializable {

    public ArrayList<Round> rounds;
    public AwayGoals awayGoals;
    public ComparatorByPlayersValue teamComparatorByPlayersValue;

    public Tournament() {
        super(Type.TOURNAMENT);
        rounds = new ArrayList<>();
        awayGoals = AwayGoals.OFF;
        teamComparatorByPlayersValue = new ComparatorByPlayersValue();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);

        Round[] roundsArray = json.readValue("rounds", Round[].class, jsonData);
        if (roundsArray != null) {
            for (Round round : roundsArray) {
                round.setTournament(this);
                rounds.add(round);
            }
        }
        if (hasTwoLegsRound()) {
            awayGoals = json.readValue("awayGoals", AwayGoals.class, AwayGoals.AFTER_90_MINUTES, jsonData);
        }
    }

    @Override
    public void write(Json json) {
        super.write(json);

        json.writeValue("rounds", rounds, Round[].class, Round.class);
        if (hasTwoLegsRound()) {
            json.writeValue("awayGoals", awayGoals);
        }
    }

    @Override
    public void start(ArrayList<Team> teams) {
        super.start(teams);

        ArrayList<Integer> qualifiedTeams = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++) {
            qualifiedTeams.add(i);
        }

        if (!getRound().isPreset()) {
            // seeding
            Collections.sort(qualifiedTeams, teamComparatorByPlayersValue);
        }

        getRound().start(qualifiedTeams);
        updateMonth();
    }

    @Override
    public void restart() {
        super.restart();
        currentRound = 0;
        currentMatch = 0;

        rounds.get(0).restart();
        for (int i = 1; i < rounds.size(); i++) {
            rounds.get(i).clear();
        }
        updateMonth();
    }

    public Round getRound() {
        return rounds.get(currentRound);
    }

    public void addRound(Round round) {
        round.setTournament(this);
        rounds.add(round);
        numberOfTeams = rounds.get(0).numberOfTeams;
        updateRoundNames();
    }

    public void nextMatch() {
        getRound().nextMatch();
    }

    public String nextMatchLabel() {
        return getRound().nextMatchLabel();
    }

    public void nextRound(ArrayList<Integer> qualifiedTeams) {
        currentRound += 1;
        currentMatch = 0;
        updateMonth();

        getRound().start(qualifiedTeams);
    }

    private void updateMonth() {
        if (weather == Weather.BY_SEASON) {
            int seasonLength = ((seasonEnd.ordinal() - seasonStart.ordinal() + 12) % 12);
            currentMonth = Month.values()[(seasonStart.ordinal() + seasonLength * currentRound / rounds.size()) % 12];
        }
    }

    private void updateRoundNames() {
        for (int i = 0; i < rounds.size(); i++) {
            switch (rounds.get(i).type) {
                case KNOCKOUT:
                    rounds.get(i).name = getKnockoutLabel(i);
                    break;

                case GROUPS:
                    rounds.get(i).name = getGroupsLabel(i);
                    break;
            }
        }
    }

    @Override
    public String getMenuTitle() {
        return name + " " + getRound().getMenuTitle();
    }

    private String getKnockoutLabel(int round) {
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

    private String getGroupsLabel(int round) {
        if (round == rounds.size() - 1) {
            return "FINAL ROUND";
        } else if (round == 0) {
            return "FIRST ROUND";
        } else if (round == 1) {
            return "SECOND ROUND";
        } else if (round == 2) {
            return "THIRD ROUND";
        } else if (round == 3) {
            return "FOURTH ROUND";
        } else {
            return "FIFTH ROUND";
        }
    }

    @Override
    public Match getMatch() {
        return getRound().getMatch();
    }

    public void generateResult() {
        getRound().generateResult();
    }

    public boolean playExtraTime() {
        return getRound().playExtraTime();
    }

    public boolean playPenalties() {
        return getRound().playPenalties();
    }

    @Override
    public boolean isEnded() {
        return currentRound == rounds.size() - 1 && getRound().isEnded();
    }

    public boolean nextMatchOnHold() {
        return getRound().nextMatchOnHold();
    }

    public boolean hasTwoLegsRound() {
        for (Round round : rounds) {
            if (round.type == Round.Type.KNOCKOUT) {
                if (((Knockout) round).numberOfLegs == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public int numberOfNextRoundTeams() {
        if (currentRound == rounds.size() - 1) {
            return 1;
        } else {
            return rounds.get(currentRound + 1).numberOfTeams;
        }
    }

    @Override
    public void matchCompleted() {
        super.matchCompleted();
        getRound().matchCompleted();
    }

    @Override
    public void matchInterrupted() {
        getRound().matchInterrupted();
    }

    @Override
    public Team getMatchWinner() {
        return getRound().getMatchWinner();
    }

    @Override
    public Team getFinalWinner() {
        return getRound().getFinalWinner();
    }

    @Override
    public Team getFinalRunnerUp() {
        return getRound().getFinalRunnerUp();
    }

    @Override
    protected boolean oneToSuspension(int yellows) {
        return (yellows % 2 == 1);
    }
}
