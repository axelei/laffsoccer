package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Widget;

class DesignDiyTournament extends GLScreen {

    DesignDiyTournament(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(Assets.strings.get("DESIGN DIY TOURNAMENT"), game.stateColor.body);
        widgets.add(w);
    }
}
