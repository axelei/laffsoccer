package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.TableRow;
import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.competitions.tournament.groups.Group;
import com.ygames.ysoccer.competitions.tournament.groups.Groups;
import com.ygames.ysoccer.competitions.tournament.knockout.Knockout;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static java.lang.Math.min;

class PlayTournament extends GLScreen {

    private Tournament tournament;
    private Knockout knockout;
    private ArrayList<Match> matches;
    private int offset;
    private ArrayList<Widget> resultWidgets;
    private Font font10green;

    PlayTournament(GLGame game) {
        super(game);

        tournament = (Tournament) game.competition;

        background = game.stateBackground;

        font10green = new Font(10, new RgbPair(0xFCFCFC, 0x21E337));
        font10green.load();

        Widget w;

        w = new TitleBar(tournament.getMenuTitle(), game.stateColor.body);
        widgets.add(w);

        switch (tournament.getRound().type) {
            case GROUPS:
                Groups groups = (Groups) tournament.getRound();
                int tableHeight = 21 * (groups.groupNumberOfTeams() + 1) + 2;
                int visibleGroups = min(groups.groups.size(), 480 / tableHeight);
                for (int g = offset; g < offset + visibleGroups; g++) {
                    Group group = groups.groups.get(g);

                    // table headers
                    int dx = 250;
                    int dy = 94 + (tableHeight + 24) * (g - offset) + 10 * (24 - visibleGroups * (groups.groupNumberOfTeams()+2));
                    w = new Label();
                    w.setGeometry(dx, dy, 322, 21);
                    w.setText(Assets.strings.get("GROUP") + " " + ((char) (65 + g)), Font.Align.CENTER, Assets.font10);
                    widgets.add(w);
                    dx += 320;
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
                    for (TableRow row : group.table) {
                        w = new Button();
                        w.setGeometry(210, dy + 20 + 21 * tm, 36, 23);
                        w.setText(tm + 1, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        Team team = tournament.teams.get(row.team);
                        w = new Button();
                        w.setGeometry(250, dy + 20 + 21 * tm, 322, 23);
                        switch (team.controlMode) {
                            case COMPUTER:
                                w.setColors(0x981E1E, 0x000000, 0x000000);
                                break;

                            case PLAYER:
                                w.setColors(0x0000C8, 0x000000, 0x000000);
                                break;

                            case COACH:
                                w.setColors(0x009BDC, 0x000000, 0x000000);
                                break;
                        }
                        w.setText(team.name, Font.Align.LEFT, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        // played
                        w = new Button();
                        w.setGeometry(dx, dy + 20 + 21 * tm, 72, 23);
                        w.setColors(0x808080, 0x000000, 0x000000);
                        w.setText(row.won + row.drawn + row.lost, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        // won
                        w = new Button();
                        w.setGeometry(dx + 70, dy + 20 + 21 * tm, 72, 23);
                        w.setColors(0x808080, 0x000000, 0x000000);
                        w.setText(row.won, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        // drawn
                        w = new Button();
                        w.setGeometry(dx + 2 * 70, dy + 20 + 21 * tm, 72, 23);
                        w.setColors(0x808080, 0x000000, 0x000000);
                        w.setText(row.drawn, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        // lost
                        w = new Button();
                        w.setGeometry(dx + 3 * 70, dy + 20 + 21 * tm, 72, 23);
                        w.setColors(0x808080, 0x000000, 0x000000);
                        w.setText(row.lost, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        // goals for
                        w = new Button();
                        w.setGeometry(dx + 4 * 70, dy + 20 + 21 * tm, 72, 23);
                        w.setColors(0x808080, 0x000000, 0x000000);
                        w.setText(row.goalsFor, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        // goals against
                        w = new Button();
                        w.setGeometry(dx + 5 * 70, dy + 20 + 21 * tm, 72, 23);
                        w.setColors(0x808080, 0x000000, 0x000000);
                        w.setText(row.goalsAgainst, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        // points
                        w = new Button();
                        w.setGeometry(dx + 6 * 70, dy + 20 + 21 * tm, 72, 23);
                        w.setColors(0x808080, 0x000000, 0x000000);
                        w.setText(row.points, Font.Align.CENTER, Assets.font10);
                        w.setActive(false);
                        widgets.add(w);

                        tm = tm + 1;
                    }
                }
                break;

            case KNOCKOUT:
                knockout = (Knockout) tournament.getRound();
                matches = knockout.getMatches();

                offset = 0;
                if ((matches.size() > 8) && (tournament.currentMatch > 4)) {
                    offset = min(tournament.currentMatch - 4, matches.size() - 8);
                }

                int dy = 100;
                if (matches.size() < 8) {
                    dy = dy + 64 * (8 - matches.size()) / 2;
                }

                // calendar
                resultWidgets = new ArrayList<Widget>();
                for (int m = 0; m < matches.size(); m++) {
                    Match match = matches.get(m);
                    int qualified = knockout.getLeg().getQualifiedTeam(match);

                    w = new TeamButton(335, dy + 64 * m, tournament.teams.get(match.teams[HOME]), Font.Align.RIGHT, qualified == match.teams[HOME]);
                    resultWidgets.add(w);
                    widgets.add(w);

                    // result (home goals)
                    w = new Label();
                    w.setGeometry(640 - 45, dy + 64 * m, 30, 26);
                    w.setText("", Font.Align.RIGHT, Assets.font10);
                    if (match.getResult() != null) {
                        w.setText(match.getResult()[HOME]);
                    }
                    resultWidgets.add(w);
                    widgets.add(w);

                    w = new VersusLabel(dy + 64 * m, match);
                    resultWidgets.add(w);
                    widgets.add(w);

                    // result (away goals)
                    w = new Label();
                    w.setGeometry(640 + 15, dy + 64 * m, 30, 26);
                    w.setText("", Font.Align.LEFT, Assets.font10);
                    if (match.isEnded()) {
                        w.setText(match.getResult()[AWAY]);
                    }
                    resultWidgets.add(w);
                    widgets.add(w);

                    w = new TeamButton(705, dy + 64 * m, tournament.teams.get(match.teams[AWAY]), Font.Align.LEFT, qualified == match.teams[AWAY]);
                    resultWidgets.add(w);
                    widgets.add(w);

                    // status
                    w = new Label();
                    w.setGeometry(game.gui.WIDTH / 2 - 360, dy + 26 + 64 * m, 720, 26);
                    w.setText(knockout.getMatchStatus(match), Font.Align.CENTER, font10green);
                    resultWidgets.add(w);
                    widgets.add(w);
                }
                updateResultWidgets();

                if (!tournament.isEnded()) {

                    if (matches.size() > 8) {
                        w = new ScrollButton(115, -1);
                        widgets.add(w);

                        w = new ScrollButton(564, +1);
                        widgets.add(w);
                    }
                }
                break;
        }

        // home team
        w = new Label();
        w.setGeometry(240, 618, 322, 36);
        w.setText(tournament.getTeam(HOME).name, Font.Align.RIGHT, Assets.font14);
        widgets.add(w);

        Match match = tournament.getMatch();

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

        // away team
        w = new Label();
        w.setGeometry(720, 618, 322, 36);
        w.setText(tournament.getTeam(AWAY).name, Font.Align.LEFT, Assets.font14);
        widgets.add(w);

        w = new ViewStatisticsButton();
        widgets.add(w);

        Widget exitButton = new ExitButton();
        widgets.add(exitButton);

        if (tournament.isEnded()) {

            setSelectedWidget(exitButton);

        } else {

            if (match.isEnded()) {
                w = new NextMatchButton();
                widgets.add(w);
                setSelectedWidget(w);
            } else {
                Widget playMatchButton = new PlayViewMatchButton();
                widgets.add(playMatchButton);

                Widget viewResultButton = new ViewResultButton();
                widgets.add(viewResultButton);

                if (tournament.bothComputers() || tournament.userPrefersResult) {
                    setSelectedWidget(viewResultButton);
                } else {
                    setSelectedWidget(playMatchButton);
                }
            }
        }
    }

    private class TeamButton extends Button {

        TeamButton(int x, int y, Team team, Font.Align align, boolean qualified) {
            setGeometry(x, y, 240, 26);
            int bodyColor = 0;
            switch (team.controlMode) {
                case COMPUTER:
                    bodyColor = 0x981E1E;
                    break;

                case PLAYER:
                    bodyColor = 0x0000C8;
                    break;

                case COACH:
                    bodyColor = 0x009BDC;
                    break;
            }
            int borderColor = qualified ? 0x21E337 : 0x1A1A1A;
            setColors(bodyColor, borderColor, borderColor);
            setText(team.name, align, Assets.font10);
            setActive(false);
        }
    }

    private class VersusLabel extends Label {

        VersusLabel(int y, Match match) {
            setGeometry((game.gui.WIDTH - 30) / 2, y, 30, 26);
            // NOTE: max 2 characters
            setText(Assets.strings.get("ABBREVIATIONS.VERSUS"), Font.Align.CENTER, Assets.font10);
            if (match.isEnded()) {
                setText("-");
            }
            setActive(false);
        }
    }

    private class ScrollButton extends Button {

        int direction;

        ScrollButton(int y, int direction) {
            this.direction = direction;
            setGeometry(228, y, 20, 36);
            textureRegion = Assets.scroll[direction == 1 ? 1 : 0];
            setAddShadow(true);
        }

        @Override
        public void onFire1Down() {
            scroll(direction);
        }

        @Override
        public void onFire1Hold() {
            scroll(direction);
        }

        private void scroll(int direction) {
            offset = Emath.slide(offset, 0, matches.size() - 8, direction);
            updateResultWidgets();
        }
    }

    private class PlayViewMatchButton extends Button {

        PlayViewMatchButton() {
            setGeometry(game.gui.WIDTH / 2 - 430, 660, 220, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText("", Font.Align.CENTER, Assets.font14);
            if (tournament.bothComputers()) {
                setText(Assets.strings.get("VIEW MATCH"));
            } else {
                setText("- " + Assets.strings.get("MATCH") + " -");
            }
        }

        @Override
        public void onFire1Down() {
            tournament.userPrefersResult = false;

            Team homeTeam = tournament.getTeam(HOME);
            Team awayTeam = tournament.getTeam(AWAY);

            Match match = tournament.getMatch();
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
                navigation.competition = tournament;
                navigation.team = homeTeam;
                game.setScreen(new SetTeam(game));
            } else if (awayTeam.controlMode != Team.ControlMode.COMPUTER) {
                if (lastFireInputDevice != null) {
                    awayTeam.setInputDevice(lastFireInputDevice);
                }
                navigation.competition = tournament;
                navigation.team = awayTeam;
                game.setScreen(new SetTeam(game));
            } else {
                navigation.competition = tournament;
                game.setScreen(new MatchSetup(game));
            }
        }
    }

    private class NextMatchButton extends Button {

        NextMatchButton() {
            setGeometry(game.gui.WIDTH / 2 - 430, 660, 460, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText(Assets.strings.get(tournament.nextMatchLabel()), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            nextMatch();
        }

        @Override
        public void onFire1Hold() {
            if (tournament.nextMatchOnHold()) {
                nextMatch();
            }
        }

        private void nextMatch() {
            tournament.nextMatch();
            game.setScreen(new PlayTournament(game));
        }
    }

    private class ViewResultButton extends Button {

        ViewResultButton() {
            setGeometry(game.gui.WIDTH / 2 - 190, 660, 220, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText("", Font.Align.CENTER, Assets.font14);
            if (tournament.bothComputers()) {
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
            if (tournament.bothComputers()) {
                viewResult();
            }
        }

        private void viewResult() {
            if (!tournament.bothComputers()) {
                tournament.userPrefersResult = true;
            }

            tournament.generateResult();

            game.setScreen(new PlayTournament(game));
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

    private void updateResultWidgets() {
        if (matches.size() > 8) {
            int m = 0;
            for (Widget w : resultWidgets) {
                if ((m >= 6 * offset) && (m < 6 * (offset + 8))) {
                    w.y = 120 + 64 * (m / 6 - offset) + ((m % 6) == 5 ? 26 : 0);
                    w.setVisible(true);
                } else {
                    w.setVisible(false);
                }
                m = m + 1;
            }
        }
    }
}
