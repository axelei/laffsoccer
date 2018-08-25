package com.ygames.ysoccer.competitions;

public class TableRow {

    public int team;

    public int won;
    public int drawn;
    public int lost;

    public int goalsFor;
    public int goalsAgainst;
    public int points;

    // needed by json deserializer
    TableRow() {
    }

    public TableRow(int team) {
        this.team = team;
    }

    public void update(int goalsFor, int goalsAgainst, int pointsForAWin) {
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        if (goalsFor > goalsAgainst) {
            won += 1;
            points += pointsForAWin;
        } else if (goalsFor == goalsAgainst) {
            drawn += 1;
            points += 1;
        } else {
            lost += 1;
        }
    }

    public void reset() {
        won = 0;
        drawn = 0;
        lost = 0;

        goalsFor = 0;
        goalsAgainst = 0;
        points = 0;
    }
}
