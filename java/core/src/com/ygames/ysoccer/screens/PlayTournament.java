package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.competitions.tournament.knockout.Knockout;
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

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class PlayTournament extends GLScreen {

    private Tournament tournament;
    private Knockout knockout;
    private ArrayList<Match> matches;
    private int offset;
    private ArrayList<Widget> resultWidgets;

    PlayTournament(GLGame game) {
        super(game);

        tournament = (Tournament) game.competition;

        background = game.stateBackground;

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

                    w = new VersusLabel(dy + 64 * m, match);
                    resultWidgets.add(w);
                    widgets.add(w);

                    w = new TeamButton(705, dy + 64 * m, tournament.teams.get(match.teams[AWAY]), Font.Align.LEFT, qualified == match.teams[AWAY]);
                    resultWidgets.add(w);
                    widgets.add(w);
                }
                break;
        }

        w = new ViewStatisticsButton();
        widgets.add(w);

        Widget exitButton = new ExitButton();
        widgets.add(exitButton);
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
