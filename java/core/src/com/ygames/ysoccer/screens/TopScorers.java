package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

class TopScorers extends GLScreen {

    TopScorers(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;
        int row = 0;
        int goals = 10000;
        for (Competition.Scorer scorer : game.competition.scorers) {
            // group
            if (scorer.goals < goals) {
                row = row + 2;
                w = new GoalsGroupBar(22 * row, scorer.goals);
                widgets.add(w);
                goals = scorer.goals;
            }

            row = row + 1;

            w = new ScorerNameButton(22 * row, scorer.player.shirtName);
            widgets.add(w);

            w = new TeamNameLabel(22 * row, scorer.player.team.name);
            widgets.add(w);

            if (row > 23) {
                break;
            }
        }

        // center list
        int y0 = 350 - 11 * row;
        for (Widget widget : widgets) {
            widget.y = widget.y + y0;
        }

        w = new TitleBar(Assets.strings.get("HIGHEST SCORER LIST"), game.stateColor.body);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class GoalsGroupBar extends Button {

        GoalsGroupBar(int y, int goals) {
            setGeometry((game.gui.WIDTH - 240) / 2, y, 240, 22);
            setColors(0x00825F, 0x00C28E, 0x00402F);
            setText(goals, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class ScorerNameButton extends Button {

        ScorerNameButton(int y, String name) {
            setGeometry((game.gui.WIDTH - 240) / 2, y, 240, 22);
            setColors(0x1F1F95, null, null);
            setText(name, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class TeamNameLabel extends Label {

        TeamNameLabel(int y, String name) {
            setGeometry((game.gui.WIDTH / 2) + 130, y, 240, 22);
            setText(" (" + name + ")", Font.Align.LEFT, Assets.font10);
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
