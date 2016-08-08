package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopScorers extends GlScreen {

    public TopScorers(GlGame game) {
        super(game);

        background = game.stateBackground;

        List<Player> scorers = getScorersList(game);

        Widget w;
        int row = 0;
        int goals = 10000;
        for (Player player : scorers) {
            // goals group
            if (player.goals < goals) {
                row = row + 2;
                w = new GoalsGroupBar(22 * row, player.goals);
                widgets.add(w);
                goals = player.goals;
            }

            row = row + 1;
        }

        // center list
        int y0 = 375 - 11 * row;
        for (Widget widget : widgets) {
            widget.y = widget.y + y0;
        }

        w = new TitleBar();
        widgets.add(w);
    }

    private List<Player> getScorersList(GlGame game) {
        List<Player> scorers = new ArrayList<Player>();
        for (Team team : game.competition.teams) {
            for (Player player : team.players) {
                if (player.goals > 0) {
                    scorers.add(player);
                }
            }
        }

        Collections.sort(scorers, new Player.CompareByGoals());
        return scorers;
    }

    public class GoalsGroupBar extends Button {

        public GoalsGroupBar(int y, int goals) {
            setGeometry((game.settings.GUI_WIDTH - 240) / 2, y, 240, 22);
            setColors(0x00825F, 0x00C28E, 0x00402F);
            setText(goals, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    public class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(Assets.strings.get("HIGHEST SCORER LIST"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
