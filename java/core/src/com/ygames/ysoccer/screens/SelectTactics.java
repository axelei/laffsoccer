package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

class SelectTactics extends GLScreen {

    SelectTactics(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_set_team.jpg");
        Widget w;

        w = new TitleButton();
        widgets.add(w);
    }

    private class TitleButton extends Button {

        public TitleButton() {
            setColors(0xBA9206);
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
            setText("EDIT TACTICS", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
