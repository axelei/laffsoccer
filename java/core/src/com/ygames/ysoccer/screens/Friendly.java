package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class Friendly extends GlScreen {

    public Friendly(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_friendly.jpg");

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        w = new OkButton();
        widgets.add(w);
        selectedWidget = w;

        w = new ExitButton();
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            String title = Assets.strings.get("FRIENDLY");
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 30, 340, 40);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class OkButton extends Button {

        public OkButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 590, 180, 36);
            setText(Assets.strings.get("OK"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectFolder(game, Assets.dataFolder));
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}