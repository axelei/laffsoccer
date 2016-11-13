package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TopScorers extends GLScreen {

    public TopScorers(GLGame game) {
        super(game);

        background = game.stateBackground;

        List<Scorer> scorers = getScorersList(game);

        Widget w;
        int row = 0;
        int goals = 10000;
        for (Scorer scorer : scorers) {
            // goals group
            if (scorer.goals < goals) {
                row = row + 2;
                w = new GoalsGroupBar(22 * row, scorer.goals);
                widgets.add(w);
                goals = scorer.goals;
            }

            row = row + 1;

            w = new ScorerNameButton(22 * row, scorer.name);
            widgets.add(w);

            w = new TeamNameLabel(22 * row, scorer.team);
            widgets.add(w);

            if (row > 24) {
                break;
            }
        }

        // center list
        int y0 = 350 - 11 * row;
        for (Widget widget : widgets) {
            widget.y = widget.y + y0;
        }

        w = new TitleBar();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private List<Scorer> getScorersList(GLGame game) {
        List<Scorer> scorers = new ArrayList<Scorer>();
        for (Team team : game.competition.teams) {
            for (Player player : team.players) {
                if (player.goals > 0) {
                    Scorer scorer = new Scorer();
                    scorer.name = player.shirtName;
                    scorer.goals = player.goals;
                    scorer.team = team.name;
                    scorers.add(scorer);
                }
            }
        }

        Collections.sort(scorers, new CompareScorerByGoals());
        return scorers;
    }

    class GoalsGroupBar extends Button {

        public GoalsGroupBar(int y, int goals) {
            setGeometry((game.gui.WIDTH - 240) / 2, y, 240, 22);
            setColors(0x00825F, 0x00C28E, 0x00402F);
            setText(goals, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class ScorerNameButton extends Button {

        public ScorerNameButton(int y, String name) {
            setGeometry((game.gui.WIDTH - 240) / 2, y, 240, 22);
            setColors(0x1F1F95, 0x000000, 0x000000);
            setText(name, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class TeamNameLabel extends Label {

        public TeamNameLabel(int y, String name) {
            setGeometry((game.gui.WIDTH / 2) + 130, y, 240, 22);
            setText(" (" + name + ")", Font.Align.LEFT, Assets.font10);
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.gui.WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(Assets.strings.get("HIGHEST SCORER LIST"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }

    private class Scorer {
        public String name;
        public int goals;
        public String team;
    }

    static class CompareScorerByGoals implements Comparator<Scorer> {

        @Override
        public int compare(Scorer o1, Scorer o2) {
            // by goals
            if (o1.goals != o2.goals) {
                return o2.goals - o1.goals;
            }

            // by team
            if (!o1.team.equals(o2.team)) {
                return o1.team.compareTo(o2.team);
            }

            // by names
            return o1.name.compareTo(o2.name);
        }
    }
}
