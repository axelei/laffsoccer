package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.competitions.tournament.groups.Group;
import com.ygames.ysoccer.competitions.tournament.groups.Groups;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class DiyTournamentCalendar extends GLScreen {

    private Tournament tournament;
    private Groups groups;
    private int currentGroup;
    private int currentMatch;
    private int matchSide;
    private List<Integer> groupsTeams;
    private List<Widget> teamButtons;

    private enum Mode {GROUPS_DISTRIBUTION, GROUP_MATCHES, KNOCKOUT_CALENDAR}

    private Mode mode;

    DiyTournamentCalendar(GLGame game, Tournament tournament) {
        super(game);
        this.tournament = tournament;
        groupsTeams = new ArrayList<Integer>();

        background = game.stateBackground;
        Widget w;

        w = new TitleBar("DIY TOURNAMENT CALENDAR", game.stateColor.body);
        widgets.add(w);

        switch (tournament.getRound().type) {
            case GROUPS:
                groups = (Groups) tournament.getRound();
                mode = Mode.GROUPS_DISTRIBUTION;
                break;
        }

        teamButtons = new ArrayList<Widget>();

        int teamIndex = 0;
        for (Team team : game.teamList) {
            w = new TeamButton(team, teamIndex);
            teamButtons.add(w);
            widgets.add(w);
            teamIndex++;
        }

        if (teamButtons.size() > 0) {
            Collections.sort(teamButtons, Widget.widgetComparatorByText);
            Widget.arrange(game.gui.WIDTH, 392, 29, teamButtons);
            setSelectedWidget(teamButtons.get(0));
        }

        w = new PlayButton();
        widgets.add(w);
    }

    private class TeamButton extends Button {

        private Team team;
        int teamIndex;
        private int groupIndex;
        private int matches;
        boolean done;

        TeamButton(Team team, int teamIndex) {
            this.team = team;
            this.teamIndex = teamIndex;
            setSize(300, 28);
            setText(team.name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUPS_DISTRIBUTION:
                    if (done) {
                        setText(team.name + " (" + (char) (65 + groupIndex) + ")");
                        setActive(false);
                    }
                    break;

                case GROUP_MATCHES:
                    setText(team.name + " " + matches);
                    setVisible(groupIndex == currentGroup);
                    setActive(groupIndex == currentGroup);
                    break;
            }

            if (done) {
                setColors(0x666666);
            } else {
                switch (team.controlMode) {
                    case UNDEFINED:
                        setColors(0x98691E);
                        break;

                    case COMPUTER:
                        setColors(0x981E1E);
                        break;

                    case PLAYER:
                        setColors(0x0000C8);
                        break;

                    case COACH:
                        setColors(0x009BDC);
                        break;
                }
            }
        }

        @Override
        public void onFire1Down() {
            switch (mode) {
                case GROUPS_DISTRIBUTION:
                    groupIndex = groupsTeams.size() / groups.groupNumberOfTeams();
                    groupsTeams.add(teamIndex);
                    done = true;

                    // advance to GROUP_MATCHES mode
                    if (groupsTeams.size() == game.teamList.size()) {
                        for (Widget w : teamButtons) {
                            ((TeamButton) w).done = false;
                        }
                        mode = Mode.GROUP_MATCHES;
                    }
                    break;

                case GROUP_MATCHES:
                    if (matchSide == HOME) {
                        Match match = new Match();
                        match.teams[HOME] = teamIndex;
                        groups.groups.get(groupIndex).calendar.add(match);
                        matchSide = AWAY;
                    } else {
                        Match match = groups.groups.get(groupIndex).calendar.get(currentMatch);
                        match.teams[AWAY] = teamIndex;
                        matchSide = HOME;
                        currentMatch += 1;
                    }
                    matches++;

                    // advance to next group
                    if (currentMatch == groups.groupNumberOfTeams() * (groups.groupNumberOfTeams() - 1) / 2) {
                        currentGroup++;
                        currentMatch = 0;
                    }
            }
            refreshAllWidgets();
        }
    }

    private class PlayButton extends Button {

        PlayButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 660, 360, 36);
            setText("PLAY TOURNAMENT", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUP_MATCHES:
                    if (currentGroup == groups.groups.size() && currentMatch == 0) {
                        setColors(0x138B21);
                        setActive(true);
                    } else {
                        setColors(0x666666);
                        setActive(false);
                    }
                    break;
            }
        }

        @Override
        public void onFire1Down() {
            switch (mode) {
                case GROUP_MATCHES:
                    for (int g = 0; g < groups.groups.size(); g++) {
                        Group group = groups.groups.get(g);
                        // add other rounds
                        for (int r = 1; r < groups.rounds; r++) {
                            for (int i = 0; i < (groups.groupNumberOfTeams() * (groups.groupNumberOfTeams() - 1) / 2); i++) {
                                Match firstRoundMatch = group.calendar.get(i);
                                Match match = new Match();
                                if (r % 2 == 0) {
                                    match.teams[HOME] = firstRoundMatch.teams[HOME];
                                    match.teams[AWAY] = firstRoundMatch.teams[AWAY];
                                } else {
                                    match.teams[HOME] = firstRoundMatch.teams[AWAY];
                                    match.teams[AWAY] = firstRoundMatch.teams[HOME];
                                }
                                group.calendar.add(match);
                            }
                        }
                        // populate table
                        ArrayList<Integer> teams = new ArrayList<Integer>();
                        for (int t = 0; t < groups.groupNumberOfTeams(); t++) {
                            teams.add(groupsTeams.get(g * groups.groupNumberOfTeams() + t));
                        }
                        group.populateTable(teams);
                    }
                    tournament.start(game.teamList);
                    game.setCompetition(tournament);
                    game.setScreen(new PlayTournament(game));
            }
        }
    }
}
