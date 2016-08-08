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

    public class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(Assets.strings.get("HIGHEST SCORER LIST"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
