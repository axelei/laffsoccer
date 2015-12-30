package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class MenuMain extends GlScreen {

    public MenuMain(YSoccer game) {
        super(game);
        background = new Image("images/backgrounds/menu_main.jpg");

        Widget w;
        w = new GameOptionsButton();
        widgets.add(w);
        selectedWidget = w;

        w = new FriendlyButton();
        widgets.add(w);
    }

    class GameOptionsButton extends Button {
        public GameOptionsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 30 - 320, 290, 320, 36);
            setColors(0x536B90, 0x7090C2, 0x263142);
            setText(Assets.strings.get("GAME OPTIONS"), Font.Align.CENTER, Assets.font14);
            setSelected(true);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuGameOptions(game));
        }
    }

    class FriendlyButton extends Button {
        public FriendlyButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 30, 290, 320, 36);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(Assets.strings.get("FRIENDLY"), Font.Align.CENTER, Assets.font14);
        }
    }
}
