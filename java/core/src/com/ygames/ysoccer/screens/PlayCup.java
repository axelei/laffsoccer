package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Cup;
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
import com.ygames.ysoccer.framework.EMath;

import java.util.ArrayList;

import static com.ygames.ysoccer.framework.Assets.font10;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.framework.Font.Align.LEFT;
import static com.ygames.ysoccer.framework.Font.Align.RIGHT;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Team.ControlMode.COMPUTER;

class PlayCup extends GLScreen {

    Cup cup;
    private ArrayList<Match> matches;
    private int offset;
    private ArrayList<Widget> resultWidgets;

    PlayCup(GLGame game) {
        super(game);

        cup = (Cup) game.competition;

        background = game.stateBackground;

        Font font10green = new Font(10, 13, 17, 12,16, new RgbPair(0xFCFCFC, 0x21E337));
        font10green.load();

        Widget w;

        w = new TitleBar(cup.getMenuTitle(), game.stateColor.body);
        widgets.add(w);

        matches = cup.getMatches();
        offset = 0;
        if ((matches.size() > 8) && (cup.currentMatch > 4)) {
            offset = Math.min(cup.currentMatch - 4, matches.size() - 8);
        }

        int dy = 100;
        if (matches.size() < 8) {
            dy = dy + 64 * (8 - matches.size()) / 2;
        }

        // calendar
        resultWidgets = new ArrayList<>();
        for (int m = 0; m < matches.size(); m++) {
            Match match = matches.get(m);
            int qualified = cup.getLeg().getQualifiedTeam(match);
            int borderColor = 0x1E1E1E;
            if (qualified == match.teams[HOME]) borderColor = 0x26D73A;
            if (qualified == match.teams[AWAY]) borderColor = 0xD4333E;

            w = new TeamButton(335, dy + 64 * m, cup.teams.get(match.teams[HOME]), RIGHT, borderColor);
            resultWidgets.add(w);
            widgets.add(w);

            // result (home goals)
            w = new Label();
            w.setGeometry(640 - 45, dy + 64 * m, 30, 26);
            w.setText("", RIGHT, font10);
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
            w.setText("", LEFT, font10);
            if (match.isEnded()) {
                w.setText(match.getResult()[AWAY]);
            }
            resultWidgets.add(w);
            widgets.add(w);

            borderColor = 0x1E1E1E;
            if (qualified == match.teams[AWAY]) borderColor = 0x26D73A;
            if (qualified == match.teams[HOME]) borderColor = 0xD4333E;
            w = new TeamButton(705, dy + 64 * m, cup.teams.get(match.teams[AWAY]), LEFT, borderColor);
            resultWidgets.add(w);
            widgets.add(w);

            // status
            w = new Label();
            w.setGeometry(game.gui.WIDTH / 2 - 360, dy + 26 + 64 * m, 720, 26);
            w.setText(cup.getMatchStatus(match), CENTER, font10green);
            resultWidgets.add(w);
            widgets.add(w);
        }
        updateResultWidgets();

        // home team
        w = new Label();
        w.setGeometry(240, 618, 322, 36);
        w.setText(cup.getTeam(HOME).name, RIGHT, font14);
        widgets.add(w);

        Match match = cup.getMatch();

        // result (home goals)
        w = new Label();
        w.setGeometry(game.gui.WIDTH / 2 - 60, 618, 40, 36);
        w.setText("", RIGHT, font14);
        if (match.isEnded()) {
            w.setText(match.getResult()[HOME]);
        }
        widgets.add(w);

        // versus / -
        w = new Label();
        w.setGeometry(game.gui.WIDTH / 2 - 20, 618, 40, 36);
        w.setText("", CENTER, font14);
        if (match.isEnded()) {
            w.setText("-");
        } else {
            w.setText(gettext("ABBREVIATIONS.VERSUS"));
        }
        widgets.add(w);

        // result (away goals)
        w = new Label();
        w.setGeometry(game.gui.WIDTH / 2 + 20, 618, 40, 36);
        w.setText("", LEFT, font14);
        if (match.isEnded()) {
            w.setText(match.getResult()[AWAY]);
        }
        widgets.add(w);

        // away team
        w = new Label();
        w.setGeometry(720, 618, 322, 36);
        w.setText(cup.getTeam(AWAY).name, LEFT, font14);
        widgets.add(w);

        w = new ViewStatisticsButton();
        widgets.add(w);

        Widget exitButton = new ExitButton();
        widgets.add(exitButton);

        if (cup.isEnded()) {

            setSelectedWidget(exitButton);

        } else {

            if (matches.size() > 8) {
                w = new ScrollButton(115, -1);
                widgets.add(w);

                w = new ScrollButton(564, +1);
                widgets.add(w);
            }

            if (match.isEnded()) {
                w = new NextMatchButton();
                widgets.add(w);
                setSelectedWidget(w);
            } else {
                Widget playMatchButton = new PlayViewMatchButton();
                widgets.add(playMatchButton);

                Widget viewResultButton = new ViewResultButton();
                widgets.add(viewResultButton);

                if (cup.bothComputers() || cup.userPrefersResult) {
                    setSelectedWidget(viewResultButton);
                } else {
                    setSelectedWidget(playMatchButton);
                }
            }
        }
    }

    private class TeamButton extends Button {

        TeamButton(int x, int y, Team team, Font.Align align, int borderColor) {
            setGeometry(x, y, 240, 26);
            int bodyColor = (team.controlMode == COMPUTER) ? 0x8C8C8C : team.controlModeColor();
            setColors(bodyColor, borderColor, borderColor);
            setText(team.name, align, font10);
            setActive(false);
        }
    }

    private class VersusLabel extends Label {

        VersusLabel(int y, Match match) {
            setGeometry((game.gui.WIDTH - 30) / 2, y, 30, 26);
            // NOTE: max 2 characters
            setText(gettext("ABBREVIATIONS.VERSUS"), CENTER, font10);
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
            offset = EMath.slide(offset, 0, matches.size() - 8, direction);
            updateResultWidgets();
        }
    }

    private class PlayViewMatchButton extends Button {

        PlayViewMatchButton() {
            setGeometry(game.gui.WIDTH / 2 - 430, 660, 220, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText("", CENTER, font14);
            if (cup.bothComputers()) {
                setText(gettext("VIEW MATCH"));
            } else {
                setText("- " + gettext("MATCH") + " -");
            }
        }

        @Override
        public void onFire1Down() {
            cup.userPrefersResult = false;

            Team homeTeam = cup.getTeam(HOME);
            Team awayTeam = cup.getTeam(AWAY);

            Match match = cup.getMatch();
            match.setTeam(HOME, homeTeam);
            match.setTeam(AWAY, awayTeam);

            // reset input devices
            game.inputDevices.setAvailability(true);
            homeTeam.setInputDevice(null);
            homeTeam.releaseNonAiInputDevices();
            awayTeam.setInputDevice(null);
            awayTeam.releaseNonAiInputDevices();

            // choose the menu to set
            if (homeTeam.controlMode != COMPUTER) {
                if (lastFireInputDevice != null) {
                    homeTeam.setInputDevice(lastFireInputDevice);
                }
                navigation.competition = cup;
                navigation.team = homeTeam;
                game.setScreen(new SetTeam(game));
            } else if (awayTeam.controlMode != COMPUTER) {
                if (lastFireInputDevice != null) {
                    awayTeam.setInputDevice(lastFireInputDevice);
                }
                navigation.competition = cup;
                navigation.team = awayTeam;
                game.setScreen(new SetTeam(game));
            } else {
                navigation.competition = cup;
                game.setScreen(new MatchSetup(game));
            }
        }
    }

    private class NextMatchButton extends Button {

        private boolean onHold;

        NextMatchButton() {
            setGeometry(game.gui.WIDTH / 2 - 430, 660, 460, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            String label;
            if (cup.isLegEnded()) {
                if (cup.isRoundEnded()) {
                    label = "NEXT ROUND";
                } else {
                    switch (cup.currentLeg) {
                        case 0:
                            if (cup.getRound().numberOfLegs == 2) {
                                label = "CUP.2ND LEG ROUND";
                            } else {
                                label = "CUP.PLAY REPLAYS";
                            }
                            break;
                        default:
                            label = "CUP.PLAY REPLAYS";
                            break;
                    }
                }
                onHold = false;
            } else {
                label = "NEXT MATCH";
                onHold = true;
            }
            setText(gettext(label), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            nextMatch();
        }

        @Override
        public void onFire1Hold() {
            if (onHold) {
                nextMatch();
            }
        }

        private void nextMatch() {
            cup.nextMatch();
            game.setScreen(new PlayCup(game));
        }
    }

    private class ViewResultButton extends Button {

        ViewResultButton() {
            setGeometry(game.gui.WIDTH / 2 - 190, 660, 220, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText("", CENTER, font14);
            if (cup.bothComputers()) {
                setText(gettext("VIEW RESULT"));
            } else {
                setText("- " + gettext("RESULT") + " -");
            }
        }

        @Override
        public void onFire1Down() {
            viewResult();
        }

        @Override
        public void onFire1Hold() {
            if (cup.bothComputers()) {
                viewResult();
            }
        }

        private void viewResult() {
            if (!cup.bothComputers()) {
                cup.userPrefersResult = true;
            }

            cup.generateResult();

            game.setScreen(new PlayCup(game));
        }
    }

    private class ViewStatisticsButton extends Button {

        ViewStatisticsButton() {
            setGeometry(game.gui.WIDTH / 2 + 50, 660, 180, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText(gettext("STATS"), CENTER, font14);
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
            setText(gettext("EXIT"), CENTER, font14);
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