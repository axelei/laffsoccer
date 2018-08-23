package com.ygames.ysoccer.competitions.tournament.groups;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;

public class Groups extends Round implements Json.Serializable {

    public ArrayList<Group> groups;
    public int rounds;
    public int pointsForAWin;

    public Groups() {
        super(Type.GROUPS);
        groups = new ArrayList<Group>();
        rounds = 1;
        pointsForAWin = 3;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);

        Group[] groupsArray = json.readValue("groups", Group[].class, jsonData);
        for (Group group : groupsArray) {
            group.setGroups(this);
            groups.add(group);
        }

        rounds = jsonData.getInt("rounds");
        pointsForAWin = jsonData.getInt("pointsForAWin");
    }

    @Override
    public void write(Json json) {
        super.write(json);

        json.writeValue("groups", groups, Group[].class, Group.class);
        json.writeValue("rounds", rounds);
        json.writeValue("pointsForAWin", pointsForAWin);
    }

    public void createGroups(int n) {
        for (int i = 0; i < n; i++) {
            Group group = new Group();
            group.setGroups(this);
            groups.add(group);
        }
    }

    int groupNumberOfTeams() {
        return numberOfTeams / groups.size();
    }

    @Override
    protected void start(ArrayList<Integer> qualifiedTeams) {
        if (seeded) {
            // TODO sort qualifiedTeams
        }

        // start each group
        for (int g = 0; g < groups.size(); g++) {
            ArrayList<Integer> groupTeams = new ArrayList<Integer>();
            for (int t = 0; t < groupNumberOfTeams(); t++) {
                groupTeams.add(qualifiedTeams.get(t * groups.size() + g));
            }
            // TODO groups.get(g).start(groupTeams);
        }
    }

    @Override
    public void restart() {
        // TODO
    }

    @Override
    public void clear() {
        // TODO
    }

    @Override
    public Match getMatch() {
        // TODO
        // return calendar.get(currentMatch);
        return null;
    }

    @Override
    public void nextMatch() {
        // TODO
    }

    @Override
    protected String nextMatchLabel() {
        if (isEnded()) {
            return "NEXT ROUND";
        } else {
            return "NEXT MATCH";
        }
    }

    @Override
    protected boolean nextMatchOnHold() {
        // TODO
        return true;
    }

    @Override
    public boolean isEnded() {
        // TODO
        return false;
    }

    @Override
    public void generateResult() {
        // TODO
    }

    @Override
    protected String getMenuTitle() {
        // TODO
        return name;
    }
}
