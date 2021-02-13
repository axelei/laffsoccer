package com.ygames.ysoccer.competitions.tournament.groups;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.TableRow;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Match.ResultType.AFTER_90_MINUTES;
import static com.ygames.ysoccer.match.Team.ControlMode.COMPUTER;

public class Groups extends Round implements Json.Serializable {

    public int rounds;
    public int pointsForAWin;
    public int currentGroup;
    public ArrayList<Group> groups;

    Comparator<TableRow> tableRowComparator;

    public Groups() {
        super(Type.GROUPS);
        groups = new ArrayList<>();
        rounds = 1;
        pointsForAWin = 3;
        tableRowComparator = new TableRowComparator();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);

        rounds = jsonData.getInt("rounds");
        pointsForAWin = jsonData.getInt("pointsForAWin");
        currentGroup = jsonData.getInt("currentGroup", 0);

        Group[] groupsArray = json.readValue("groups", Group[].class, jsonData);
        for (Group group : groupsArray) {
            group.setGroups(this);
            groups.add(group);
        }
    }

    @Override
    public void write(Json json) {
        super.write(json);

        json.writeValue("rounds", rounds);
        json.writeValue("pointsForAWin", pointsForAWin);
        json.writeValue("currentGroup", currentGroup);
        json.writeValue("groups", groups, Group[].class, Group.class);
    }

    public void createGroups(int n) {
        for (int i = 0; i < n; i++) {
            Group group = new Group();
            group.setGroups(this);
            groups.add(group);
        }
    }

    public int groupNumberOfTeams() {
        return numberOfTeams / groups.size();
    }

    @Override
    protected void start(ArrayList<Integer> qualifiedTeams) {
        if (isPreset()) {
            for (Group group : groups) {
                group.start(null);
            }
        } else {
            if (!seeded) {
                Collections.shuffle(qualifiedTeams);
            }

            // create random partitioned mapping
            List<Integer> groupsIndexes = new ArrayList<>();
            for (int t = 0; t < groups.size(); t++) {
                groupsIndexes.add(t);
            }
            List<Integer> teamsMapping = new ArrayList<>();
            for (int t = 0; t < groupNumberOfTeams(); t++) {
                Collections.shuffle(groupsIndexes);
                for (int g = 0; g < groups.size(); g++) {
                    teamsMapping.add(t * groups.size() + groupsIndexes.get(g));
                }
            }

            // start each group
            for (int g = 0; g < groups.size(); g++) {
                ArrayList<Integer> groupTeams = new ArrayList<>();
                for (int t = 0; t < groupNumberOfTeams(); t++) {
                    groupTeams.add(qualifiedTeams.get(teamsMapping.get(t * groups.size() + g)));
                }
                groups.get(g).start(groupTeams);
            }
        }
    }

    @Override
    public void restart() {
        currentGroup = 0;
        for (Group group : groups) {
            group.restart();
        }
    }

    @Override
    public void clear() {
        currentGroup = 0;
        for (Group group : groups) {
            group.clear();
        }
    }

    private Group getGroup() {
        return groups.get(currentGroup);
    }

    @Override
    public Match getMatch() {
        return getGroup().getMatch();
    }

    public int numberOfTopTeams() {
        return tournament.numberOfNextRoundTeams() / groups.size();
    }

    public int numberOfRunnersUp() {
        return tournament.numberOfNextRoundTeams() % groups.size();
    }

    @Override
    public void nextMatch() {
        if (isEnded()) {
            ArrayList<Integer> qualifiedTeams = new ArrayList<>();

            // top teams
            int topTeams = numberOfTopTeams();
            for (int team = 0; team < topTeams; team++) {
                for (Group group : groups) {
                    qualifiedTeams.add(group.table.get(team).team);
                }
            }

            // runners up
            int runnersUp = numberOfRunnersUp();
            ArrayList<TableRow> runnersUpTable = new ArrayList<>();
            for (Group group : groups) {
                runnersUpTable.add(group.table.get(topTeams));
            }
            Collections.sort(runnersUpTable, tableRowComparator);
            for (int team = 0; team < runnersUp; team++) {
                qualifiedTeams.add(runnersUpTable.get(team).team);
            }

            tournament.nextRound(qualifiedTeams);
        } else {
            currentGroup = currentGroup + 1;

            if (currentGroup == groups.size()) {
                currentGroup = 0;
                for (Group group : groups) {
                    group.nextMatch();
                }
            }
        }
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
        return !isEnded();
    }

    @Override
    public boolean isEnded() {
        return (currentGroup == groups.size() - 1) && getGroup().isEnded();
    }

    @Override
    public boolean isPreset() {
        return groups.get(0).calendar.size() > 0;
    }

    @Override
    public void generateResult() {
        getGroup().generateResult();
    }

    @Override
    protected boolean playExtraTime() {
        return false;
    }

    @Override
    protected boolean playPenalties() {
        return false;
    }

    @Override
    protected String getMenuTitle() {
        return name;
    }

    private class TableRowComparator implements Comparator<TableRow> {

        @Override
        public int compare(TableRow o1, TableRow o2) {
            // by points
            if (o1.points != o2.points) {
                return o2.points - o1.points;
            }

            // by goals diff
            int diff1 = o1.goalsFor - o1.goalsAgainst;
            int diff2 = o2.goalsFor - o2.goalsAgainst;
            if (diff1 != diff2) {
                return diff2 - diff1;
            }

            // by scored goals
            if (o1.goalsFor != o2.goalsFor) {
                return o2.goalsFor - o1.goalsFor;
            }

            // by names
            return tournament.teams.get(o1.team).name.compareTo(tournament.teams.get(o2.team).name);
        }
    }

    @Override
    protected void matchCompleted() {
        getGroup().addMatchToTable(getMatch());
    }

    @Override
    protected void matchInterrupted() {
        Match match = getMatch();
        if (match.team[HOME].controlMode == COMPUTER && match.team[AWAY].controlMode != COMPUTER) {
            int goals = 4 + Assets.random.nextInt(2);
            if (match.resultAfter90 != null) {
                goals += match.resultAfter90[AWAY];
                match.resultAfter90[HOME] += goals;
            } else {
                match.setResult(goals, 0, AFTER_90_MINUTES);
            }
            tournament.generateScorers(match.team[HOME], goals);
            matchCompleted();
        } else if (match.team[HOME].controlMode != COMPUTER && match.team[AWAY].controlMode == COMPUTER) {
            int goals = 4 + Assets.random.nextInt(2);
            if (match.resultAfter90 != null) {
                goals += match.resultAfter90[AWAY];
                match.resultAfter90[HOME] += goals;
            } else {
                match.setResult(0, goals, AFTER_90_MINUTES);
            }
            tournament.generateScorers(match.team[AWAY], goals);
            matchCompleted();
        } else {
            match.resultAfter90 = null;
        }
    }

    @Override
    public Team getMatchWinner() {
        int[] result = getMatch().getResult();
        if (result[HOME] > result[AWAY]) {
            return tournament.teams.get(getMatch().teams[HOME]);
        } else if (result[HOME] < result[AWAY]) {
            return tournament.teams.get(getMatch().teams[AWAY]);
        }
        return null;
    }
}
