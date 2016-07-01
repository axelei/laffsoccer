package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class LoadCompetition extends GlScreen {

    public LoadCompetition(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_competition.jpg");

        Widget w;

        w = new TitleBar();
        widgets.add(w);
    }

    public class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x2898c7, 0x32bffa, 0x1e7194);
            setText(Assets.strings.get("LOAD OLD COMPETITION"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
