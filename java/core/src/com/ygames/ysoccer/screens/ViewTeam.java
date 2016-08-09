package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

public class ViewTeam extends GlScreen {

    public ViewTeam(GlGame game, Team team) {
        super(game);

        background = game.stateBackground;

        Widget w;
        w = new TitleBar(team);
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar(Team team) {
            setGeometry((game.settings.GUI_WIDTH - 600) / 2, 45, 601, 41);
            setColors(0x6A5ACD, 0x8F83D7, 0x372989);
            setText(team.name.toUpperCase(), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
