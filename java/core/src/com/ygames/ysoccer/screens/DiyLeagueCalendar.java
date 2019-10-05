package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.League;
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

class DiyLeagueCalendar extends GLScreen {

    private League league;
    private int currentMatch;
    private int matchSide;
    private List<Widget> teamButtons;

    DiyLeagueCalendar(GLGame game, League league) {
        super(game);
        this.league = league;
        currentMatch = 0;
        matchSide = HOME;

        background = game.stateBackground;

        Widget w;

        w = new TitleBar("DIY LEAGUE CALENDAR", game.stateColor.body);
        widgets.add(w);

        w = new MatchesLabel();
        widgets.add(w);

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

    private class MatchesLabel extends Button {

        int matches;

        MatchesLabel() {
            matches = league.numberOfTeams * (league.numberOfTeams - 1) / 2;
            setGeometry((game.gui.WIDTH - 180) / 2, 80, 180, 36);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(league.calendar.size() > 0);
            setText("MATCHES:" + " " + league.calendar.size() + " / " + matches);
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
            if (league.calendar.size() > 0) {
                Match match = league.calendar.get(matchSide == HOME ? currentMatch - 1 : currentMatch);
                setText(game.teamList.get(match.teams[HOME]).name);
            } else {
                setText("");
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
            setVisible(league.calendar.size() > 0);
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
            if (league.calendar.size() > 0 && matchSide == HOME) {
                Match match = league.calendar.get(currentMatch - 1);
                setText(game.teamList.get(match.teams[AWAY]).name);
            } else {
                setText("");
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
            setVisible(league.calendar.size() > 0);
            if (!visible) setSelectedWidget(teamButtons.get(0));
        }

        @Override
        public void onFire1Down() {
            Match match = league.calendar.get(matchSide == HOME ? currentMatch - 1 : currentMatch);
            int teamIndex;
            if (matchSide == HOME) {
                matchSide = AWAY;
                currentMatch--;
            } else {
                matchSide = HOME;
                league.calendar.remove(match);
            }
            teamIndex = match.teams[matchSide];
            for (Widget w : teamButtons) {
                TeamButton teamButton = (TeamButton) w;
                if (teamButton.teamIndex == teamIndex) {
                    teamButton.matches--;
                    break;
                }
            }
            refreshAllWidgets();
        }
    }

    private class TeamButton extends Button {

        private Team team;
        int teamIndex;
        private int matches;

        TeamButton(Team team, int teamIndex) {
            this.team = team;
            this.teamIndex = teamIndex;
            setSize(300, 28);
            setText(team.name, CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(team.name + " " + matches);
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

        @Override
        public void onFire1Down() {
            if (matchSide == HOME) {
                Match match = new Match();
                match.teams[HOME] = teamIndex;
                league.calendar.add(match);
                matchSide = AWAY;
            } else {
                Match match = league.calendar.get(currentMatch);
                match.teams[AWAY] = teamIndex;
                matchSide = HOME;
                currentMatch += 1;
            }
            matches++;
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
            setText("PLAY LEAGUE", CENTER, font14);
        }

        @Override
        public void refresh() {
            int diff = currentMatch - league.numberOfTeams * (league.numberOfTeams - 1) / 2;
            if (diff == 0) {
                setColor(0x138B21);
                setActive(true);
            } else {
                setColor(0x666666);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            // add other rounds
            for (int r = 1; r < league.rounds; r++) {
                for (int i = 0; i < (league.numberOfTeams * (league.numberOfTeams - 1) / 2); i++) {
                    Match firstRoundMatch = league.calendar.get(i);
                    Match match = new Match();
                    if (r % 2 == 0) {
                        match.teams[HOME] = firstRoundMatch.teams[HOME];
                        match.teams[AWAY] = firstRoundMatch.teams[AWAY];
                    } else {
                        match.teams[HOME] = firstRoundMatch.teams[AWAY];
                        match.teams[AWAY] = firstRoundMatch.teams[HOME];
                    }
                    league.calendar.add(match);
                }
            }
            league.start(game.teamList);
            game.setCompetition(league);
            game.setScreen(new PlayLeague(game));
        }
    }
}
