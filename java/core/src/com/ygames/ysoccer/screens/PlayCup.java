package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;

public class PlayCup extends GlScreen {

    Cup cup;
    int matches;
    int offset;
    ArrayList<Widget> resultWidgets;

    public PlayCup(GlGame game) {
        super(game);

        cup = (Cup) game.competition;

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        matches = cup.calendarCurrent.size();
        offset = 0;
        if ((matches > 8) && (cup.currentMatch > 4)) {
            offset = Math.min(cup.currentMatch - 4, matches - 8);
        }

        ArrayList<Team> teamsList = (ArrayList<Team>) cup.teams.clone();
        Collections.sort(teamsList, new Team.CompareByStats());

        int dy = 100;
        if (matches < 8) {
            dy = dy + 64 * (8 - matches) / 2;
        }

        // calendar
        resultWidgets = new ArrayList<Widget>();
        for (int m = 0; m < matches; m++) {
            Match match = cup.calendarCurrent.get(m);

            w = new TeamButton(335, dy + 64 * m, cup.teams.get(match.team[Match.HOME]), Font.Align.RIGHT);
            resultWidgets.add(w);
            widgets.add(w);

            w = new VersusLabel(dy + 64 * m, match);
            resultWidgets.add(w);
            widgets.add(w);

            w = new TeamButton(705, dy + 64 * m, cup.teams.get(match.team[Match.AWAY]), Font.Align.LEFT);
            resultWidgets.add(w);
            widgets.add(w);
        }

        Match match = cup.getMatch();

        // home team
        w = new Label();
        w.setGeometry(240, 618, 322, 36);
        w.setText(cup.teams.get(match.team[Match.HOME]).name.toUpperCase(), Font.Align.RIGHT, Assets.font14);
        widgets.add(w);

        // away team
        w = new Label();
        w.setGeometry(720, 618, 322, 36);
        w.setText(cup.teams.get(match.team[Match.AWAY]).name.toUpperCase(), Font.Align.LEFT, Assets.font14);
        widgets.add(w);

        w = new ViewStatisticsButton();
        widgets.add(w);

        Widget exitButton = new ExitButton();
        widgets.add(exitButton);

        if (cup.isEnded()) {

            selectedWidget = exitButton;

        } else {

            if (match.ended) {
                // TODO
            } else {
                Widget playMatchButton = new PlayMatchButton();
                widgets.add(playMatchButton);

                if (cup.bothComputers() || cup.userPrefersResult) {
                    // TODO
                } else {
                    selectedWidget = playMatchButton;
                }
            }
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 840) / 2, 30, 840, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(cup.getMenuTitle().toUpperCase(), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TeamButton extends Button {

        public TeamButton(int x, int y, Team team, Font.Align align) {
            setGeometry(x, y, 240, 26);
            switch (team.controlMode) {
                case COMPUTER:
                    setColors(0x981E1E, 0x000001, 0x000001);
                    break;
                case PLAYER:
                    setColors(0x0000C8, 0x000001, 0x000001);
                    break;
                case COACH:
                    setColors(0x009BDC, 0x000001, 0x000001);
                    break;
            }
            setText(team.name.toUpperCase(), align, Assets.font10);
            setActive(false);
        }
    }

    class VersusLabel extends Label {

        public VersusLabel(int y, Match match) {
            setGeometry((game.settings.GUI_WIDTH - 30) / 2, y, 30, 26);
            // NOTE: max 2 characters
            setText(Assets.strings.get("ABBREVIATIONS.VERSUS"), Font.Align.CENTER, Assets.font10);
            if (match.ended) {
                setText("-");
            }
            setActive(false);
        }
    }

    class PlayMatchButton extends Button {

        public PlayMatchButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 430, 660, 220, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText("", Font.Align.CENTER, Assets.font14);
            if (cup.bothComputers()) {
                setText(Assets.strings.get("VIEW MATCH"));
            } else {
                setText("- " + Assets.strings.get("MATCH") + " -");
            }
        }

        @Override
        public void onFire1Down() {
            // TODO
        }
    }

    class ViewStatisticsButton extends Button {

        public ViewStatisticsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 50, 660, 180, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText(Assets.strings.get("STATS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 250, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
