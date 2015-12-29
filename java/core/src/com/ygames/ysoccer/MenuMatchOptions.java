package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;

public class MenuMatchOptions extends GlScreen {

    public MenuMatchOptions(YSoccer game) {
        super(game);
        background = new Image("images/backgrounds/menu_game_options.jpg");
        widgets.add(new TitleButton());
    }

    class TitleButton extends Button {
        public TitleButton() {
            setColors(0x536B90, 0x7090C2, 0x263142);
            setGeometry((1280 - 400) / 2, 20, 400, 40);
            setText(Assets.strings.get("GAME OPTIONS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
