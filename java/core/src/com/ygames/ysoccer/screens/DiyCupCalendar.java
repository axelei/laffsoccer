package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.Round;
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

class DiyCupCalendar extends GLScreen {

    private Cup cup;
    private ArrayList<Match> matches;
    private int currentMatch;
    private int matchSide;
    private List<Widget> teamButtons;

    DiyCupCalendar(GLGame game, Cup cup) {
        super(game);
        this.cup = cup;
        currentMatch = 0;
        matchSide = HOME;
        matches = new ArrayList<>();

        background = game.stateBackground;

        Widget w;

        w = new TitleBar("DIY CUP CALENDAR", game.stateColor.body);
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

        MatchesLabel() {
            setGeometry((game.gui.WIDTH - 180) / 2, 80, 180, 36);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText("MATCHES:" + " " + currentMatch + " / " + cup.numberOfTeams / 2);
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
            if (matches.size() > 0) {
                Match match = matches.get(matchSide == HOME ? currentMatch - 1 : currentMatch);
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
            setVisible(matches.size() > 0);
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
            if (matches.size() > 0 && matchSide == HOME) {
                Match match = matches.get(currentMatch - 1);
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
            setVisible(matches.size() > 0);
            if (!visible) setSelectedWidget(teamButtons.get(0));
        }

        @Override
        public void onFire1Down() {
            Match match = matches.get(matchSide == HOME ? currentMatch - 1 : currentMatch);
            int teamIndex;
            if (matchSide == HOME) {
                matchSide = AWAY;
                currentMatch--;
            } else {
                matchSide = HOME;
                matches.remove(match);
            }
            teamIndex = match.teams[matchSide];
            for (Widget w : teamButtons) {
                TeamButton teamButton = (TeamButton) w;
                if (teamButton.teamIndex == teamIndex) {
                    teamButton.done = false;
                    break;
                }
            }
            refreshAllWidgets();
        }
    }

    private class TeamButton extends Button {

        private Team team;
        int teamIndex;
        boolean done;

        TeamButton(Team team, int teamIndex) {
            this.team = team;
            this.teamIndex = teamIndex;
            setSize(300, 28);
            setText(team.name, CENTER, font14);
        }

        @Override
        public void refresh() {
            if (done) {
                setColor(0x666666);
                setActive(false);
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
                setActive(true);
            }
        }

        @Override
        public void onFire1Down() {
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
            setText("PLAY CUP", CENTER, font14);
        }

        @Override
        public void refresh() {
            int teams = 0;
            for (Match match : matches) {
                if (match.teams[HOME] != -1) teams++;
                if (match.teams[AWAY] != -1) teams++;
            }
            if (teams == cup.numberOfTeams) {
                setColor(0x138B21);
                setActive(true);
            } else {
                setColor(0x666666);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            Round round = cup.rounds.get(0);
            round.newLeg();
            round.legs.get(0).matches = matches;

            cup.start(game.teamList);
            game.setCompetition(cup);
            game.setScreen(new PlayCup(game));
        }
    }
}
