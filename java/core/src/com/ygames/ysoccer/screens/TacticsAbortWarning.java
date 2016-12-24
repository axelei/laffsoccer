package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import static com.ygames.ysoccer.framework.GLGame.State.NONE;

class TacticsAbortWarning extends GLScreen {

    TacticsAbortWarning(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("EDIT TACTICS"), 0xBA9206);
        widgets.add(w);

        w = new WarningButton();
        widgets.add(w);

        w = new ContinueButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class WarningButton extends Button {

        WarningButton() {
            setGeometry(640 - 620 / 2, 260, 620, 180);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(Assets.strings.get("TACTICS.EDITED TACTICS WILL BE LOST"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ContinueButton extends Button {

        ContinueButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 590, 180, 36);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(Assets.strings.get("CONTINUE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            if (game.getState() == NONE) {
                game.setScreen(new Main(game));
            } else {
                game.setScreen(new SetTeam(game));
            }
        }
    }

    private class AbortButton extends Button {

        public AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            game.setScreen(new EditTactics(game));
        }
    }
}
