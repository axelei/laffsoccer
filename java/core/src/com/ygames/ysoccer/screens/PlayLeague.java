package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class PlayLeague extends GLScreen {

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

    PlayLeague(GLGame game) {
        super(game);

        league = (League) game.competition;

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(league.getMenuTitle(), game.stateColor.body);
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
            w.setText(team.name, Font.Align.LEFT, Assets.font10);
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

            setSelectedWidget(exitButton);

        } else {

            // home team
            w = new Label();
            w.setGeometry(240, 618, 322, 36);
            w.setText(league.getTeam(HOME).name, Font.Align.RIGHT, Assets.font14);
            widgets.add(w);

            // away team
            w = new Label();
            w.setGeometry(720, 618, 322, 36);
            w.setText(league.getTeam(AWAY).name, Font.Align.LEFT, Assets.font14);
            widgets.add(w);

            Match match = league.getMatch();

            // result (home goals)
            w = new Label();
            w.setGeometry(game.gui.WIDTH / 2 - 60, 618, 40, 36);
            w.setText("", Font.Align.RIGHT, Assets.font14);
            if (match.ended) {
                w.setText(match.result.homeGoals);
            }
            widgets.add(w);

            // versus / -
            w = new Label();
            w.setGeometry(game.gui.WIDTH / 2 - 20, 618, 40, 36);
            w.setText("", Font.Align.CENTER, Assets.font14);
            if (match.ended) {
                w.setText("-");
            } else {
                w.setText(Assets.strings.get("ABBREVIATIONS.VERSUS"));
            }
            widgets.add(w);

            // result (away goals)
            w = new Label();
            w.setGeometry(game.gui.WIDTH / 2 + 20, 618, 40, 36);
            w.setText("", Font.Align.LEFT, Assets.font14);
            if (match.ended) {
                w.setText(match.result.awayGoals);
            }
            widgets.add(w);

            if (match.ended) {
                w = new NextMatchButton();
                widgets.add(w);
                setSelectedWidget(w);
            } else {
                Widget playMatchButton = new PlayViewMatchButton();
                widgets.add(playMatchButton);

                Widget viewResultButton = new ViewResultButton();
                widgets.add(viewResultButton);

                if (league.bothComputers() || league.userPrefersResult) {
                    setSelectedWidget(viewResultButton);
                } else {
                    setSelectedWidget(playMatchButton);
                }
            }
        }
    }

    private class PlayViewMatchButton extends Button {

        PlayViewMatchButton() {
            setGeometry(game.gui.WIDTH / 2 - 430, 660, 220, 36);
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
            league.userPrefersResult = false;

            Team homeTeam = league.getTeam(HOME);
            Team awayTeam = league.getTeam(AWAY);

            if (homeTeam.controlMode != Team.ControlMode.COMPUTER) {
                game.setScreen(new SetTeam(game, null, null, league, homeTeam, awayTeam, HOME));
            } else if (awayTeam.controlMode != Team.ControlMode.COMPUTER) {
                game.setScreen(new SetTeam(game, null, null, league, homeTeam, awayTeam, AWAY));
            } else {
                game.setScreen(new MatchSetup(game, null, null, league, homeTeam, awayTeam));
            }
        }
    }

    private class NextMatchButton extends Button {

        NextMatchButton() {
            setGeometry(game.gui.WIDTH / 2 - 430, 660, 460, 36);
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

    private class ViewResultButton extends Button {

        ViewResultButton() {
            setGeometry(game.gui.WIDTH / 2 - 190, 660, 220, 36);
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

            league.generateResult();

            game.setScreen(new PlayLeague(game));
        }
    }

    private class ViewStatisticsButton extends Button {

        ViewStatisticsButton() {
            setGeometry(game.gui.WIDTH / 2 + 50, 660, 180, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText(Assets.strings.get("STATS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry(game.gui.WIDTH / 2 + 250, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
