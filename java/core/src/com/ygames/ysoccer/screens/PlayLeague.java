package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.League;
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

public class PlayLeague extends GlScreen {

    private String[] headers = {
            "TABLE HEADER.PLAYED MATCHES",
            "TABLE HEADER.WON MATCHES",
            "TABLE HEADER.DRAWN MATCHES",
            "TABLE HEADER.LOST MATCHES",
            "TABLE HEADER.GOALS FOR",
            "TABLE HEADER.GOALS AGAINST",
            "TABLE HEADER.POINTS"
    };

    private League league;
    private Match match;

    public PlayLeague(GlGame game) {
        super(game);

        league = (League) game.competition;

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        // table headers
        int dx = 590;
        int dy = 100 + 11 * (24 - league.numberOfTeams);
        for (String header : headers) {
            w = new Label();
            w.setGeometry(dx, dy, 62, 21);
            w.setText(Assets.strings.get(header), Font.Align.CENTER, Assets.font10);
            widgets.add(w);
            dx += 60;
        }

        ArrayList<Team> teams = (ArrayList<Team>) league.teams.clone();

        Collections.sort(teams, new Team.CompareByStats());

        // table
        int tm = 0;
        dx = 590;
        for (Team team : teams) {
            w = new Button();
            w.setGeometry(230, dy + 20 + 22 * tm, 36, 24);
            w.setText(tm + 1, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            w = new Button();
            w.setGeometry(270, dy + 20 + 22 * tm, 322, 24);
            switch (team.controlMode) {
                case COMPUTER:
                    w.setColors(0x981E1E, 0x000001, 0x000001);
                    break;
                case PLAYER:
                    w.setColors(0x0000C8, 0x000001, 0x000001);
                    break;
                case COACH:
                    w.setColors(0x009BDC, 0x000001, 0x000001);
                    break;
            }
            w.setText(team.name.toUpperCase(), Font.Align.LEFT, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // played
            w = new Button();
            w.setGeometry(dx, dy + 20 + 22 * tm, 62, 24);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.won + team.drawn + team.lost, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // won
            w = new Button();
            w.setGeometry(dx + 60, dy + 20 + 22 * tm, 62, 24);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.won, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // drawn
            w = new Button();
            w.setGeometry(dx + 2 * 60, dy + 20 + 22 * tm, 62, 24);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.drawn, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // lost
            w = new Button();
            w.setGeometry(dx + 3 * 60, dy + 20 + 22 * tm, 62, 24);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.lost, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // goals for
            w = new Button();
            w.setGeometry(dx + 4 * 60, dy + 20 + 22 * tm, 62, 24);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.goalsFor, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // goals against
            w = new Button();
            w.setGeometry(dx + 5 * 60, dy + 20 + 22 * tm, 62, 24);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.goalsAgainst, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // points
            w = new Button();
            w.setGeometry(dx + 6 * 60, dy + 20 + 22 * tm, 62, 24);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.points, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            tm = tm + 1;
        }

        if (!league.ended) {
            match = league.getMatch();

            // team A
            w = new Label();
            w.setGeometry(240, 660, 322, 36);
            w.setText(match.team[0].name.toUpperCase(), Font.Align.RIGHT, Assets.font14);
            widgets.add(w);

            // team B
            w = new Label();
            w.setGeometry(720, 660, 322, 36);
            w.setText(match.team[1].name.toUpperCase(), Font.Align.LEFT, Assets.font14);
            widgets.add(w);
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 520) / 2, 30, 520, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(league.getMenuTitle().toUpperCase(), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
