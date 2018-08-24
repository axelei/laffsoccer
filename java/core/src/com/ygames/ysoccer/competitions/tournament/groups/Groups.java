package com.ygames.ysoccer.competitions.tournament.groups;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Groups extends Round implements Json.Serializable {

    public ArrayList<Group> groups;
    private int currentGroup;
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

        currentGroup = jsonData.getInt("currentGroup");
        rounds = jsonData.getInt("rounds");
        pointsForAWin = jsonData.getInt("pointsForAWin");
    }

    @Override
    public void write(Json json) {
        super.write(json);

        json.writeValue("groups", groups, Group[].class, Group.class);
        json.writeValue("currentGroup", currentGroup);
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
        if (!seeded) {
            Collections.shuffle(qualifiedTeams);
        }

        // create random partitioned mapping
        List<Integer> groupsIndexes = new ArrayList<Integer>();
        for (int t = 0; t < groups.size(); t++) {
            groupsIndexes.add(t);
        }
        List<Integer> teamsMapping = new ArrayList<Integer>();
        for (int t = 0; t < groupNumberOfTeams(); t++) {
            Collections.shuffle(groupsIndexes);
            for (int g = 0; g < groups.size(); g++) {
                teamsMapping.add(t * groups.size() + groupsIndexes.get(g));
            }
        }

        // start each group
        for (int g = 0; g < groups.size(); g++) {
            ArrayList<Integer> groupTeams = new ArrayList<Integer>();
            for (int t = 0; t < groupNumberOfTeams(); t++) {
                groupTeams.add(qualifiedTeams.get(teamsMapping.get(t * groups.size() + g)));
            }
            groups.get(g).start(groupTeams);
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

    private Group getGroup() {
        return groups.get(currentGroup);
    }

    @Override
    public Match getMatch() {
        return getGroup().getMatch();
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
