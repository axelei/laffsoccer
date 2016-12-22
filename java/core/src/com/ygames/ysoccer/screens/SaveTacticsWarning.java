package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

class SaveTacticsWarning extends GLScreen {

    SaveTacticsWarning(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("SAVE TACTICS"), 0xBA9206);
        widgets.add(w);

        w = new WarningButton();
        widgets.add(w);

        w = new SaveButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class WarningButton extends Button {

        WarningButton() {
            setGeometry((game.gui.WIDTH - 620) / 2, 260, 620, 180);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(Assets.strings.get("TACTICS.TACTICS HAVE BEEN CHANGED"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SaveButton extends Button {

        SaveButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 590, 180, 36);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(Assets.strings.get("SAVE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            // TODO
            //game.setScreen(new SaveTactics(game));
        }
    }
}
