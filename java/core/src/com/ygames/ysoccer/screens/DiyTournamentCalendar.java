package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.competitions.tournament.groups.Group;
import com.ygames.ysoccer.competitions.tournament.groups.Groups;
import com.ygames.ysoccer.competitions.tournament.knockout.Knockout;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.framework.Font.Align.LEFT;
import static com.ygames.ysoccer.framework.Font.Align.RIGHT;
import static com.ygames.ysoccer.gui.Widget.widgetComparatorByText;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class DiyTournamentCalendar extends GLScreen {

    private Tournament tournament;
    private Groups groups;
    private Knockout knockout;
    private int currentGroup;
    private int currentMatch;
    private int matchSide;
    private List<Integer> groupsTeams;
    private List<Widget> teamButtons;
    private ArrayList<Match> matches;

    private enum Mode {GROUPS_DISTRIBUTION, GROUP_MATCHES, KNOCKOUT_CALENDAR}

    private Mode mode;

    DiyTournamentCalendar(GLGame game, Tournament tournament) {
        super(game);
        this.tournament = tournament;
        groupsTeams = new ArrayList<>();
        matches = new ArrayList<>();

        background = game.stateBackground;
        Widget w;

        w = new TitleBar("DIY TOURNAMENT CALENDAR", game.stateColor.body);
        widgets.add(w);

        w = new StatusLabel();
        widgets.add(w);

        switch (tournament.getRound().type) {
            case GROUPS:
                groups = (Groups) tournament.getRound();
                mode = Mode.GROUPS_DISTRIBUTION;
                break;

            case KNOCKOUT:
                knockout = (Knockout) tournament.getRound();
                mode = Mode.KNOCKOUT_CALENDAR;
                break;
        }

        teamButtons = new ArrayList<>();

        int teamIndex = 0;
        for (Team team : game.teamList) {
            w = new TeamButton(team, teamIndex);
            teamButtons.add(w);
            widgets.add(w);
            teamIndex++;
        }

        if (teamButtons.size() > 0) {
            Collections.sort(teamButtons, widgetComparatorByText);
            Widget.arrange(game.gui.WIDTH, 392, 29, 20, teamButtons);
            setSelectedWidget(teamButtons.get(0));
        }

        w = new HomeTeamLabel();
        widgets.add(w);

        w = new VersusLabel();
        widgets.add(w);

        w = new AwayTeamLabel();
        widgets.add(w);

        w = new BackButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);
        if (getSelectedWidget() == null) {
            setSelectedWidget(w);
        }

        w = new PlayButton();
        widgets.add(w);
    }

    private class StatusLabel extends Button {

        StatusLabel() {
            setGeometry((game.gui.WIDTH - 180) / 2, 80, 180, 36);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUPS_DISTRIBUTION:
                    int groupIndex = groupsTeams.size() / groups.groupNumberOfTeams();
                    setText("CREATE GROUP " + (char) (65 + groupIndex));
                    break;

                case GROUP_MATCHES:
                    if (currentGroup == groups.groups.size()) {
                        setVisible(false);
                    } else {
                        int matches = groups.groupNumberOfTeams() * (groups.groupNumberOfTeams() - 1) / 2;
                        int match = groups.groups.get(currentGroup).calendar.size();
                        setText("GROUP " + (char) (65 + currentGroup) +
                                " MATCHES:" + " " + match + " / " + matches);
                    }
                    break;

                case KNOCKOUT_CALENDAR:
                    setText("MATCHES:" + " " + currentMatch + " / " + knockout.numberOfTeams / 2);
                    break;
            }
        }
    }

    private class HomeTeamLabel extends Button {

        HomeTeamLabel() {
            setGeometry(240, 618, 322, 36);
            setText("", RIGHT, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUP_MATCHES:
                    if (currentGroup < groups.groups.size() && groups.groups.get(currentGroup).calendar.size() > 0) {
                        Match match = groups.groups.get(currentGroup).calendar.get(matchSide == HOME ? currentMatch - 1 : currentMatch);
                        setText(game.teamList.get(match.teams[HOME]).name);
                    } else {
                        setText("");
                    }
                    break;

                case KNOCKOUT_CALENDAR:
                    if (matches.size() > 0) {
                        Match match = matches.get(matchSide == HOME ? currentMatch - 1 : currentMatch);
                        setText(game.teamList.get(match.teams[HOME]).name);
                    } else {
                        setText("");
                    }
                    break;
            }
        }
    }

    private class VersusLabel extends Button {

        VersusLabel() {
            setGeometry(game.gui.WIDTH / 2 - 20, 618, 40, 36);
            setText(gettext("ABBREVIATIONS.VERSUS"), CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUP_MATCHES:
                    setVisible(currentGroup < groups.groups.size() && groups.groups.get(currentGroup).calendar.size() > 0);
                    break;

                case KNOCKOUT_CALENDAR:
                    setVisible(matches.size() > 0);
                    break;
            }
        }
    }

    private class AwayTeamLabel extends Button {

        AwayTeamLabel() {
            setGeometry(720, 618, 322, 36);
            setText("", LEFT, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUP_MATCHES:
                    if (currentGroup < groups.groups.size() && groups.groups.get(currentGroup).calendar.size() > 0 && matchSide == HOME) {
                        Match match = groups.groups.get(currentGroup).calendar.get(currentMatch - 1);
                        setText(game.teamList.get(match.teams[AWAY]).name);
                    } else {
                        setText("");
                    }
                    break;

                case KNOCKOUT_CALENDAR:
                    if (matches.size() > 0 && matchSide == HOME) {
                        Match match = matches.get(currentMatch - 1);
                        setText(game.teamList.get(match.teams[AWAY]).name);
                    } else {
                        setText("");
                    }
                    break;
            }
        }
    }

    private class BackButton extends Button {

        BackButton() {
            setGeometry((game.gui.WIDTH - 180) / 2 - 360 - 20, 660, 360, 36);
            setColor(0x9A6C9C);
            setText("BACK", CENTER, font14);
        }

        @Override
        public void refresh() {
            boolean b;
            switch (mode) {
                case GROUPS_DISTRIBUTION:
                    b = groupsTeams.size() > 0;
                    setVisible(b);
                    setActive(b);
                    break;

                case GROUP_MATCHES:
                    b = currentGroup < groups.groups.size() && groups.groups.get(currentGroup).calendar.size() > 0;
                    setVisible(b);
                    setActive(b);
                    break;

                case KNOCKOUT_CALENDAR:
                    b = matches.size() > 0;
                    setVisible(b);
                    setActive(b);
                    break;
            }
        }

        @Override
        public void onFire1Down() {
            Match match;
            int team;
            switch (mode) {
                case GROUPS_DISTRIBUTION:
                    Integer teamIndex = groupsTeams.get(groupsTeams.size() - 1);
                    groupsTeams.remove(teamIndex);
                    for (Widget w : teamButtons) {
                        TeamButton teamButton = (TeamButton) w;
                        if (teamButton.teamIndex == teamIndex) {
                            teamButton.done = false;
                            break;
                        }
                    }
                    break;

                case GROUP_MATCHES:
                    Group group = groups.groups.get(currentGroup);
                    match = group.calendar.get(matchSide == HOME ? currentMatch - 1 : currentMatch);

                    if (matchSide == HOME) {
                        matchSide = AWAY;
                        currentMatch--;
                    } else {
                        matchSide = HOME;
                        group.calendar.remove(match);
                    }
                    team = match.teams[matchSide];
                    for (Widget w : teamButtons) {
                        TeamButton teamButton = (TeamButton) w;
                        if (teamButton.teamIndex == team) {
                            teamButton.teamMatches--;
                            break;
                        }
                    }
                    break;

                case KNOCKOUT_CALENDAR:
                    match = matches.get(matchSide == HOME ? currentMatch - 1 : currentMatch);
                    if (matchSide == HOME) {
                        matchSide = AWAY;
                        currentMatch--;
                    } else {
                        matchSide = HOME;
                        matches.remove(match);
                    }
                    team = match.teams[matchSide];
                    for (Widget w : teamButtons) {
                        TeamButton teamButton = (TeamButton) w;
                        if (teamButton.teamIndex == team) {
                            teamButton.done = false;
                            break;
                        }
                    }
                    break;
            }
            refreshAllWidgets();
        }
    }

    private class TeamButton extends Button {

        private Team team;
        int teamIndex;
        private int groupIndex;
        private int teamMatches;
        boolean done;

        TeamButton(Team team, int teamIndex) {
            this.team = team;
            this.teamIndex = teamIndex;
            setSize(300, 28);
            setText(team.name, CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUPS_DISTRIBUTION:
                    if (done) {
                        setText(team.name + " (" + (char) (65 + groupIndex) + ")");
                        setActive(false);
                    } else {
                        setText(team.name);
                        setActive(true);
                    }
                    break;

                case GROUP_MATCHES:
                    setText(team.name + " " + teamMatches);
                    setVisible(groupIndex == currentGroup);
                    setActive(groupIndex == currentGroup);
                    break;
            }

            if (done) {
                setColor(0x666666);
            } else {
                switch (team.controlMode) {
                    case UNDEFINED:
                        setColor(0x98691E);
                        break;

                    case COMPUTER:
                        setColor(0x981E1E);
                        break;

                    case PLAYER:
                        setColor(0x0000C8);
                        break;

                    case COACH:
                        setColor(0x009BDC);
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
                    teamMatches++;

                    // advance to next group
                    if (currentMatch == groups.groupNumberOfTeams() * (groups.groupNumberOfTeams() - 1) / 2) {
                        currentGroup++;
                        currentMatch = 0;
                    }
                    break;

                case KNOCKOUT_CALENDAR:
                    if (matchSide == HOME) {
                        Match match = new Match();
                        match.teams[HOME] = teamIndex;
                        matches.add(match);
                        matchSide = AWAY;
                    } else {
                        Match match = matches.get(currentMatch);
                        match.teams[AWAY] = teamIndex;
                        matchSide = HOME;
                        currentMatch += 1;
                    }
                    done = true;
                    break;
            }
            refreshAllWidgets();
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColor(0xC8000E);
            setText(gettext("ABORT"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    private class PlayButton extends Button {

        PlayButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 660, 360, 36);
            setText("PLAY TOURNAMENT", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (mode) {
                case GROUPS_DISTRIBUTION:
                    setColor(0x666666);
                    setActive(false);
                    break;

                case GROUP_MATCHES:
                    if (currentGroup == groups.groups.size() && currentMatch == 0) {
                        setColor(0x138B21);
                        setActive(true);
                    } else {
                        setColor(0x666666);
                        setActive(false);
                    }
                    break;

                case KNOCKOUT_CALENDAR:
                    int teams = 0;
                    for (Match match : matches) {
                        if (match.teams[HOME] != -1) teams++;
                        if (match.teams[AWAY] != -1) teams++;
                    }
                    if (teams == knockout.numberOfTeams) {
                        setColor(0x138B21);
                        setActive(true);
                    } else {
                        setColor(0x666666);
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
                    }
                    tournament.start(game.teamList);
                    game.setCompetition(tournament);
                    game.setScreen(new PlayTournament(game));
                    break;

                case KNOCKOUT_CALENDAR:
                    knockout.newLeg();
                    knockout.legs.get(0).matches = matches;

                    tournament.start(game.teamList);
                    game.setCompetition(tournament);
                    game.setScreen(new PlayTournament(game));
                    break;
            }
        }
    }
}
