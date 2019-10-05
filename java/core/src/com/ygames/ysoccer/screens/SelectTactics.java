package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Tactics;

import java.util.Stack;

class SelectTactics extends GLScreen {

    SelectTactics(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("EDIT TACTICS"), 0xBA9206);
        widgets.add(w);

        for (int t = 12; t < 18; t++) {
            w = new TacticsButton(t);
            widgets.add(w);
            if (getSelectedWidget() == null) {
                setSelectedWidget(w);
            }
        }

        w = new ExitButton();
        widgets.add(w);
    }

    private class TacticsButton extends Button {

        private int tacticsIndex;

        TacticsButton(int tacticsIndex) {
            this.tacticsIndex = tacticsIndex;
            setGeometry((game.gui.WIDTH - 340) / 2, 150 + 75 * (tacticsIndex - 12), 340, 44);
            setColor(0x568200);
            setText(Tactics.codes[tacticsIndex], Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.tacticsToEdit = tacticsIndex;
            game.editedTactics = new Tactics();
            game.editedTactics.copyFrom(Assets.tactics[game.tacticsToEdit]);
            game.tacticsUndo = new Stack<Tactics>();

            game.setScreen(new EditTactics(game));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
