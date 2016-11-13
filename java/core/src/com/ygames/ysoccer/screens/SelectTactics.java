package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Tactics;

class SelectTactics extends GLScreen {

    SelectTactics(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleButton();
        widgets.add(w);

        for (int t = 12; t < 18; t++) {
            w = new TacticsButton(t);
            widgets.add(w);
            if (selectedWidget == null) {
                selectedWidget = w;
            }
        }

        w = new ExitButton();
        widgets.add(w);
    }

    private class TitleButton extends Button {

        public TitleButton() {
            setColors(0xBA9206);
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
            setText("EDIT TACTICS", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TacticsButton extends Button {

        int tacticsIndex;

        TacticsButton(int tacticsIndex) {
            this.tacticsIndex = tacticsIndex;
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 150 + 75 * (tacticsIndex - 12), 340, 40);
            setColors(0x568200);
            setText(Tactics.codes[tacticsIndex], Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            // TODO
            // game.setScreen(new EditTactics(game, tactics, tacticsIndex, tacticsStack));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setText("EXIT", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
