package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import static com.ygames.ysoccer.framework.Assets.font6;
import static com.ygames.ysoccer.framework.Font.Align.LEFT;

class DeveloperInfo extends GLScreen {

    DeveloperInfo(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;

        w = new TitleBar("DEVELOPER INFO", 0x191FB0);
        widgets.add(w);

        int labelWidth = 230;
        int x0 = (game.gui.WIDTH) / 2 - 460;
        int y0 = 160;

        w = new Button();
        w.setColor(0x601D5F);
        w.setGeometry(x0, y0, labelWidth, 22);
        w.setText("LOCAL STORAGE PATH", LEFT, font6);
        w.setActive(false);
        widgets.add(w);

        w = new Button();
        w.setColor(0x4D4D4D);
        w.setGeometry(x0 + 10 + labelWidth, y0, 680, 22);
        w.setText(Gdx.files.getLocalStoragePath().toUpperCase(), LEFT, font6);
        w.setActive(false);
        widgets.add(w);

        y0 += 22;
        w = new Button();
        w.setColor(0x601D5F);
        w.setGeometry(x0, y0, labelWidth, 22);
        w.setText("LOCAL STORAGE AVAILABLE", LEFT, font6);
        w.setActive(false);
        widgets.add(w);

        w = new Button();
        w.setColor(0x4D4D4D);
        w.setGeometry(x0 + 10 + labelWidth, y0, 100, 22);
        w.setText(Gdx.files.isLocalStorageAvailable() ? "YES" : "NO", LEFT, font6);
        w.setActive(false);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
        setSelectedWidget(w);
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.settings.save();
            game.setScreen(new DeveloperTools(game));
        }
    }
}
