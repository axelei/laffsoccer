package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

public class GameOptions extends GlScreen {

    public GameOptions(GlGame game) {
        super(game);
        background = new Image("images/backgrounds/menu_game_options.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        w = new LanguageLabel();
        widgets.add(w);

        w = new LanguageButton();
        widgets.add(w);
        selectedWidget = w;

        w = new ExitButton();
        widgets.add(w);
    }

    class TitleButton extends Button {
        public TitleButton() {
            setColors(0x536B90, 0x7090C2, 0x263142);
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
            setText(Assets.strings.get("GAME OPTIONS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class LanguageLabel extends Button {
        public LanguageLabel() {
            setColors(0x800000, 0xB40000, 0x400000);
            setGeometry(game.settings.GUI_WIDTH / 2 - 30 - 440, 390, 440, 36);
            setText(Assets.strings.get("LANGUAGE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class LanguageButton extends Button {
        public LanguageButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(game.settings.GUI_WIDTH / 2 + 30, 390, 440, 36);
            setText(Assets.strings.get("// THIS LANGUAGE NAME //"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateLanguage(1);
        }

        @Override
        public void onFire1Hold() {
            updateLanguage(1);
        }

        @Override
        public void onFire2Down() {
            updateLanguage(-1);
        }

        @Override
        public void onFire2Hold() {
            updateLanguage(-1);
        }

        private void updateLanguage(int direction) {
            int index = Assets.locales.indexOf(game.settings.locale);
            game.settings.locale = Assets.locales.get(Emath.rotate(index, 0, Assets.locales.size() - 1, direction));
            Assets.loadStrings(game.settings);
            setText(Assets.strings.get("// THIS LANGUAGE NAME //"));
        }
    }


    class ExitButton extends Button {
        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 708, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }
}
