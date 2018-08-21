package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
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
                break;

            case KNOCKOUT:
                knockout = (Knockout) tournament.getRound();
                matches = knockout.getMatches();

                offset = 0;
                if ((matches.size() > 8) && (tournament.currentMatch > 4)) {
                    offset = Math.min(tournament.currentMatch - 4, matches.size() - 8);
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
