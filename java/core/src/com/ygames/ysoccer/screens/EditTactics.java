package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Tactics;

class EditTactics extends GLScreen {

    EditTactics(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("EDIT TACTICS") + " (" + Tactics.codes[game.tacticsToEdit] + ")", 0xBA9206);
        widgets.add(w);
    }
}
