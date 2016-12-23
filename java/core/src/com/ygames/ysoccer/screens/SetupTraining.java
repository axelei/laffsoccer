package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Widget;

class SetupTraining extends GLScreen {

    SetupTraining(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_training.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("TRAINING"), game.stateColor.body);
        widgets.add(w);
    }
}
