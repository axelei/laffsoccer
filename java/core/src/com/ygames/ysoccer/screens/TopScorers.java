package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

class TopScorers extends GLScreen {

    TopScorers(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;
        int row = 0;
        int goals = 10000;
        int position = 0;
        for (Competition.Scorer scorer : game.competition.scorers) {

            if (scorer.goals < goals) {
                goals = scorer.goals;
                position++;
            }

            row = row + 1;

            w = new PositionLabel(21 * row, position);
            widgets.add(w);

            if (game.settings.useFlags) {
                w = new ScorerNationalityFlagButton(21 * row, scorer.player.nationality);
                widgets.add(w);
            } else {
                w = new ScorerNationalityCodeButton(21 * row, scorer.player.nationality);
                widgets.add(w);
            }

            int color;
            switch (position) {
                case 1:
                    color = 0xBAAD21;
                    break;
                case 2:
                    color = 0xADADAD;
                    break;
                default:
                    color = position % 2 == 0 ? 0x968245 : 0x7D6C39;
                    break;
            }

            w = new ScorerNameButton(21 * row, scorer.player, color);
            widgets.add(w);

            w = new TeamNameLabel(21 * row, scorer.player.team, color);
            widgets.add(w);

            w = new GoalsLabel(21 * row, scorer.goals, color);
            widgets.add(w);

            if (row > 21) {
                break;
            }
        }

        // center list
        int y0 = 360 - 11 * row;
        for (Widget widget : widgets) {
            widget.y = widget.y + y0;
        }

        w = new TitleBar(Assets.strings.get("HIGHEST SCORER LIST"), game.stateColor.body);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class PositionLabel extends Button {

        PositionLabel(int y, int position) {
            setGeometry(game.gui.WIDTH / 2 - 240 + 3 - 30 + 2 - (game.settings.useFlags ? 32 : 58), y, 30, 23);
            setText(position, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class ScorerNationalityFlagButton extends Button {

        ScorerNationalityFlagButton(int y, String nationality) {
            setGeometry(game.gui.WIDTH / 2 - 240 + 3 - 32, y, 32, 23);
            textureRegion = Assets.getNationalityFlag(nationality);
            setImagePosition(2, 4);
            setActive(false);
            setAddShadow(true);
        }
    }

    private class ScorerNationalityCodeButton extends Button {

        ScorerNationalityCodeButton(int y, String nationality) {
            setGeometry(game.gui.WIDTH / 2 - 240 + 3 - 58, y, 58, 23);
            setText("(" + nationality + ")", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class ScorerNameButton extends Button {

        ScorerNameButton(int y, Player player, int color) {
            setGeometry(game.gui.WIDTH / 2 - 240 + 1, y, 240, 23);
            setColors(color, 0x1E1E1E, 0x1E1E1E);
            setText(player.shirtName, Font.Align.LEFT, Assets.font10);
            setActive(false);
        }
    }

    private class TeamNameLabel extends Button {

        TeamNameLabel(int y, Team team, int color) {
            setGeometry(game.gui.WIDTH / 2 - 1, y, 240, 23);
            setColors(color, 0x1E1E1E, 0x1E1E1E);
            setText(team.name, Font.Align.LEFT, Assets.font10);
            setActive(false);
        }
    }

    private class GoalsLabel extends Button {

        GoalsLabel(int y, int goals, int color) {
            setColors(color, 0x1E1E1E, 0x1E1E1E);
            setGeometry(game.gui.WIDTH / 2 + 240 - 3, y, 40, 23);
            setText(goals, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }
}
