package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.competitions.TableRow;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class PlayLeague extends GLScreen {

    private League league;

    PlayLeague(GLGame game) {
        super(game);

        league = (League) game.competition;

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(league.getMenuTitle(), game.stateColor.body);
        widgets.add(w);

        // table headers
        int dx = 570;
        int dy = 86 + 10 * (24 - league.numberOfTeams);
        String[] headers = {
                "TABLE HEADER.PLAYED MATCHES",
                "TABLE HEADER.WON MATCHES",
                "TABLE HEADER.DRAWN MATCHES",
                "TABLE HEADER.LOST MATCHES",
                "TABLE HEADER.GOALS FOR",
                "TABLE HEADER.GOALS AGAINST",
                "TABLE HEADER.POINTS"
        };
        for (String header : headers) {
            w = new Label();
            w.setGeometry(dx, dy, 72, 21);
            w.setText(Assets.strings.get(header), Font.Align.CENTER, Assets.font10);
            widgets.add(w);
            dx += 70;
        }

        // table body
        int tm = 0;
        dx = 570;
        for (TableRow row : league.table) {
            w = new Button();
            w.setGeometry(210, dy + 20 + 21 * tm, 36, 23);
            w.setText(tm + 1, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            Team team = league.teams.get(row.team);
            w = new Button();
            w.setGeometry(250, dy + 20 + 21 * tm, 322, 23);
            switch (team.controlMode) {
                case COMPUTER:
                    w.setColors(0x981E1E, 0x1E1E1E, 0x1E1E1E);
                    break;

                case PLAYER:
                    w.setColors(0x0000C8, 0x1E1E1E, 0x1E1E1E);
                    break;

                case COACH:
                    w.setColors(0x009BDC, 0x1E1E1E, 0x1E1E1E);
                    break;
            }
            w.setText(team.name, Font.Align.LEFT, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // played
            w = new Button();
            w.setGeometry(dx, dy + 20 + 21 * tm, 72, 23);
            w.setColors(0x808080, 0x1E1E1E, 0x1E1E1E);
            w.setText(row.won + row.drawn + row.lost, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // won
            w = new Button();
            w.setGeometry(dx + 70, dy + 20 + 21 * tm, 72, 23);
            w.setColors(0x808080, 0x1E1E1E, 0x1E1E1E);
            w.setText(row.won, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // drawn
            w = new Button();
            w.setGeometry(dx + 2 * 70, dy + 20 + 21 * tm, 72, 23);
            w.setColors(0x808080, 0x1E1E1E, 0x1E1E1E);
            w.setText(row.drawn, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // lost
            w = new Button();
            w.setGeometry(dx + 3 * 70, dy + 20 + 21 * tm, 72, 23);
            w.setColors(0x808080, 0x1E1E1E, 0x1E1E1E);
            w.setText(row.lost, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // goals for
            w = new Button();
            w.setGeometry(dx + 4 * 70, dy + 20 + 21 * tm, 72, 23);
            w.setColors(0x808080, 0x1E1E1E, 0x1E1E1E);
            w.setText(row.goalsFor, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // goals against
            w = new Button();
            w.setGeometry(dx + 5 * 70, dy + 20 + 21 * tm, 72, 23);
            w.setColors(0x808080, 0x1E1E1E, 0x1E1E1E);
            w.setText(row.goalsAgainst, Font.Align.CENTER, Assets.font10);
            w.setActive(false);
            widgets.add(w);

            // points
            w = new Button();
            w.setGeometry(dx + 6 * 70, dy + 20 + 21 * tm, 72, 23);
            w.setColors(0x808080, 0x1E1E1E, 0x1E1E1E);
            w.setText(row.points, Font.Align.CENTER, Assets.font10);
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
            if (match.isEnded()) {
                w.setText(match.getResult()[HOME]);
            }
            widgets.add(w);

            // versus / -
            w = new Label();
            w.setGeometry(game.gui.WIDTH / 2 - 20, 618, 40, 36);
            w.setText("", Font.Align.CENTER, Assets.font14);
            if (match.isEnded()) {
                w.setText("-");
            } else {
                w.setText(Assets.strings.get("ABBREVIATIONS.VERSUS"));
            }
            widgets.add(w);

            // result (away goals)
            w = new Label();
            w.setGeometry(game.gui.WIDTH / 2 + 20, 618, 40, 36);
            w.setText("", Font.Align.LEFT, Assets.font14);
            if (match.isEnded()) {
                w.setText(match.getResult()[AWAY]);
            }
            widgets.add(w);

            if (match.isEnded()) {
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

            Match match = league.getMatch();
            match.setTeam(HOME, homeTeam);
            match.setTeam(AWAY, awayTeam);

            // reset input devices
            game.inputDevices.setAvailability(true);
            homeTeam.setInputDevice(null);
            homeTeam.releaseNonAiInputDevices();
            awayTeam.setInputDevice(null);
            awayTeam.releaseNonAiInputDevices();

            // choose the menu to set
            if (homeTeam.controlMode != Team.ControlMode.COMPUTER) {
                if (lastFireInputDevice != null) {
                    homeTeam.setInputDevice(lastFireInputDevice);
                }
                navigation.competition = league;
                navigation.team = homeTeam;
                game.setScreen(new SetTeam(game));
            } else if (awayTeam.controlMode != Team.ControlMode.COMPUTER) {
                if (lastFireInputDevice != null) {
                    awayTeam.setInputDevice(lastFireInputDevice);
                }
                navigation.competition = league;
                navigation.team = awayTeam;
                game.setScreen(new SetTeam(game));
            } else {
                navigation.competition = league;
                game.setScreen(new MatchSetup(game));
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
