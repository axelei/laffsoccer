package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Tactics;

import java.io.IOException;
import java.io.InputStream;

class ImportTactics extends GLScreen {

    public ImportTactics(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("IMPORT TACTICS"), 0xBA9206);
        widgets.add(w);

        for (int i = 0; i < 18; i++) {
            w = new TacticsButton(i);
            widgets.add(w);
            if (i == 1) {
                setSelectedWidget(w);
            }
        }

        w = new LoadTacticsButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }


    private class TacticsButton extends Button {

        int tacticsIndex;

        TacticsButton(int tacticsIndex) {
            this.tacticsIndex = tacticsIndex;
            if (tacticsIndex < 12) {
                int dx = -3 * 120 / 2 - 6 + (tacticsIndex % 3) * (120 + 6);
                int dy = (tacticsIndex / 3) * (36 + 6);
                setGeometry(640 + dx, 220 + dy, 120, 36);
            } else {
                int dx = -183 - 3 + ((tacticsIndex - 12) % 2) * (183 + 6);
                int dy = ((tacticsIndex - 12) / 2) * (36 + 6);
                setGeometry(640 + dx, 228 + 4 * (34 + 6) + dy, 183, 36);
            }
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(Tactics.codes[tacticsIndex], Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            Tactics tactics = new Tactics();
            tactics.copyFrom(game.editedTactics);
            game.tacticsUndo.push(tactics);
            InputStream in = Gdx.files.internal("data/tactics/preset/" + Tactics.fileNames[tacticsIndex] + ".TAC").read();
            try {
                game.editedTactics.loadFile(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            game.setScreen(new EditTactics(game));
        }
    }

    private class LoadTacticsButton extends Button {

        LoadTacticsButton() {
            setGeometry(640 - 183 - 3, 234 + 7 * (34 + 6), 372, 36);
            setColors(0xAB148D, 0xDE1AB7, 0x780E63);
            setText(Assets.strings.get("TACTICS.LOAD"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            game.setScreen(new LoadTactics(game));
        }
    }

    private class ExitButton extends Button {

        public ExitButton() {
            setGeometry(640 - 90, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            game.setScreen(new EditTactics(game));
        }
    }
}
