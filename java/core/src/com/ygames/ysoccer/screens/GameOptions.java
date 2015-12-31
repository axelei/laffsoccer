package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class GameOptions extends GlScreen {

    public GameOptions(GlGame game) {
        super(game);
        background = new Image("images/backgrounds/menu_game_options.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);
    }

    class TitleButton extends Button {
        public TitleButton() {
            setColors(0x536B90, 0x7090C2, 0x263142);
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
            setText(Assets.strings.get("GAME OPTIONS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
