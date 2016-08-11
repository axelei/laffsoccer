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
}
