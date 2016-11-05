package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

class ControlsSetup extends GLScreen {

    ControlsSetup(GLGame game) {
        super(game);
        background = new Image("images/backgrounds/menu_controls.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        selectedWidget = w;
    }

    private class TitleButton extends Button {

        TitleButton() {
            setColors(0xA905A3);
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
            setText(Assets.strings.get("CONTROLS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }
}
