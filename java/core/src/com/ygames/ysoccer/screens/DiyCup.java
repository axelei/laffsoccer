package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class DiyCup extends GlScreen {

    public DiyCup(GlGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleButton();
        widgets.add(w);
    }

    class TitleButton extends Button {

        public TitleButton() {
            setGeometry((game.settings.GUI_WIDTH - 520) / 2, 30, 520, 40);
            setColors(game.stateColor);
            setText(Assets.strings.get("DESIGN DIY CUP"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
