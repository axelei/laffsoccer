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
import com.ygames.ysoccer.math.Emath;

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
        int dy = 86 + 10 * (24 - league.numberOfTeams);
        for (String header : headers) {
            w = new Label();
            w.setGeometry(dx, dy, 62, 21);
            w.setText(Assets.strings.get(header), Font.Align.CENTER, Assets.font10);
            widgets.add(w);
            dx += 60;
        }

        ArrayList<Team> teamsList = (ArrayList<Team>) league.teams.clone();

        Collections.sort(teamsList, new Team.CompareByStats());

        // table
        int tm = 0;
        dx = 590;
        for (Team team : teamsList) {
            w = new Button();
            w.setGeometry(230, dy + 20 + 21 * tm, 36, 23);
            w.setText(tm + 1, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            w = new Button();
            w.setGeometry(270, dy + 20 + 21 * tm, 322, 23);
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
            w.setGeometry(dx, dy + 20 + 21 * tm, 62, 23);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.won + team.drawn + team.lost, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // won
            w = new Button();
            w.setGeometry(dx + 60, dy + 20 + 21 * tm, 62, 23);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.won, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // drawn
            w = new Button();
            w.setGeometry(dx + 2 * 60, dy + 20 + 21 * tm, 62, 23);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.drawn, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // lost
            w = new Button();
            w.setGeometry(dx + 3 * 60, dy + 20 + 21 * tm, 62, 23);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.lost, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // goals for
            w = new Button();
            w.setGeometry(dx + 4 * 60, dy + 20 + 21 * tm, 62, 23);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.goalsFor, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // goals against
            w = new Button();
            w.setGeometry(dx + 5 * 60, dy + 20 + 21 * tm, 62, 23);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.goalsAgainst, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // points
            w = new Button();
            w.setGeometry(dx + 6 * 60, dy + 20 + 21 * tm, 62, 23);
            w.setColors(0x808080, 0x010001, 0x010001);
            w.setText(team.points, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            tm = tm + 1;
        }

        w = new ViewStatisticsButton();
        widgets.add(w);

        Widget exitButton = new ExitButton();
        widgets.add(exitButton);

        if (league.isEnded()) {

            selectedWidget = exitButton;

        } else {

            match = league.getMatch();

            // team A
            w = new Label();
            w.setGeometry(240, 618, 322, 36);
            w.setText(league.teams.get(match.team[0]).name.toUpperCase(), Font.Align.RIGHT, Assets.font14);
            widgets.add(w);

            // team B
            w = new Label();
            w.setGeometry(720, 618, 322, 36);
            w.setText(league.teams.get(match.team[1]).name.toUpperCase(), Font.Align.LEFT, Assets.font14);
            widgets.add(w);

            // result (home goals)
            w = new Label();
            w.setGeometry(game.settings.GUI_WIDTH / 2 - 60, 618, 40, 36);
            w.setText("", Font.Align.RIGHT, Assets.font14);
            if (match.ended) {
                w.setText(match.stats[0].goals);
            }
            widgets.add(w);

            // versus / -
            w = new Label();
            w.setGeometry(game.settings.GUI_WIDTH / 2 - 20, 618, 40, 36);
            w.setText("", Font.Align.CENTER, Assets.font14);
            if (match.ended) {
                w.setText("-");
            } else {
                w.setText(Assets.strings.get("VERSUS (short)"));
            }
            widgets.add(w);

            // result (away goals)
            w = new Label();
            w.setGeometry(game.settings.GUI_WIDTH / 2 + 20, 618, 40, 36);
            w.setText("", Font.Align.LEFT, Assets.font14);
            if (match.ended) {
                w.setText(match.stats[1].goals);
            }
            widgets.add(w);

            if (match.ended) {
                Widget nextMatchButton = new NextMatchButton();
                widgets.add(nextMatchButton);
                selectedWidget = nextMatchButton;
            } else {
                Widget playMatchButton = new PlayMatchButton();
                widgets.add(playMatchButton);

                Widget viewResultButton = new ViewResultButton();
                widgets.add(viewResultButton);

                if (league.bothComputers() || league.userPrefersResult) {
                    selectedWidget = viewResultButton;
                } else {
                    selectedWidget = playMatchButton;
                }
            }
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

    class PlayMatchButton extends Button {

        public PlayMatchButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 430, 660, 220, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText("", Font.Align.CENTER, Assets.font14);
            if (league.bothComputers()) {
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

    class NextMatchButton extends Button {

        public NextMatchButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 430, 660, 460, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText(Assets.strings.get("NEXT MATCH"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            nextMatch();
        }

        @Override
        public void onFire1Hold() {
            nextMatch();
        }

        private void nextMatch() {
            league.nextMatch();
            game.setScreen(new PlayLeague(game));
        }
    }

    class ViewResultButton extends Button {

        public ViewResultButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 190, 660, 220, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText("", Font.Align.CENTER, Assets.font14);
            if (league.bothComputers()) {
                setText(Assets.strings.get("VIEW RESULT"));
            } else {
                setText("- " + Assets.strings.get("RESULT") + " -");
            }
        }

        @Override
        public void onFire1Down() {
            viewResult();
        }

        @Override
        public void onFire1Hold() {
            if (league.bothComputers()) {
                viewResult();
            }
        }

        private void viewResult() {
            if (!league.bothComputers()) {
                league.userPrefersResult = true;
            }

            // TODO: generate result
            int goalA = 6 - Emath.floor(Math.log10(1000000 * Math.random()));
            int goalB = 6 - Emath.floor(Math.log10(1000000 * Math.random()));

            league.setResult(goalA, goalB);

            league.teams.get(league.getMatch().team[Match.HOME]).generateScorers(goalA);
            league.teams.get(league.getMatch().team[Match.AWAY]).generateScorers(goalB);

            game.setScreen(new PlayLeague(game));
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
