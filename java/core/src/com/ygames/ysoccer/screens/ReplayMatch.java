package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Widget;

class ReplayMatch extends GLScreen {

    ReplayMatch(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_match_presentation.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("FRIENDLY"), game.stateColor.body);
        widgets.add(w);
    }
}
