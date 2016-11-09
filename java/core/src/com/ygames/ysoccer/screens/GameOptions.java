package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

public class GameOptions extends GLScreen {

    public GameOptions(GLGame game) {
        super(game);
        background = new Image("images/backgrounds/menu_game_options.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        w = new LanguageLabel();
        widgets.add(w);

        w = new LanguageButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new PlayerCountryLabel();
        widgets.add(w);

        w = new PlayerCountryButton();
        widgets.add(w);

        w = new MaxPlayerValueLabel();
        widgets.add(w);

        w = new MaxPlayerValueButton();
        widgets.add(w);

        w = new CurrencyButton();
        widgets.add(w);

        w = new QuitToOsButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    class TitleButton extends Button {

        public TitleButton() {
            setColors(0x536B90);
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("GAME OPTIONS"));
        }
    }

    class LanguageLabel extends Button {

        public LanguageLabel() {
            setColors(0x800000);
            setGeometry(game.settings.GUI_WIDTH / 2 - 30 - 440, 285, 440, 36);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("LANGUAGE"));
        }
    }

    class LanguageButton extends Button {

        public LanguageButton() {
            setColors(0x1F1F95);
            setGeometry(game.settings.GUI_WIDTH / 2 + 30, 285, 440, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("// THIS LANGUAGE NAME //"));
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
            for (Widget w : widgets) {
                w.setChanged(true);
            }
        }
    }

    class PlayerCountryLabel extends Button {

        public PlayerCountryLabel() {
            setColors(0x800000);
            setGeometry(game.settings.GUI_WIDTH / 2 - 30 - 440, 330, 440, 36);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("PLAYER'S COUNTRY"));
        }
    }

    class PlayerCountryButton extends Button {
        public PlayerCountryButton() {
            setColors(0x1F1F95);
            setGeometry(game.settings.GUI_WIDTH / 2 + 30, 330, 440, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(game.settings.useFlags ? "FLAG" : "CODE"));
        }

        @Override
        public void onFire1Down() {
            updatePlayerCountry();
        }

        @Override
        public void onFire2Down() {
            updatePlayerCountry();
        }

        private void updatePlayerCountry() {
            game.settings.useFlags = !game.settings.useFlags;
            setChanged(true);
        }
    }

    class MaxPlayerValueLabel extends Button {

        public MaxPlayerValueLabel() {
            setColors(0x800000);
            setGeometry(game.settings.GUI_WIDTH / 2 - 30 - 440, 375, 440, 36);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("MAX PLAYER VALUE"));
        }
    }

    class MaxPlayerValueButton extends Button {

        public MaxPlayerValueButton() {
            setColors(0x1F1F95);
            setGeometry(game.settings.GUI_WIDTH / 2 + 30, 375, 300, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.moneyFormat(game.settings.maxPlayerValue));
        }

        @Override
        public void onFire1Down() {
            updateMaxPlayerValue(1);
        }

        @Override
        public void onFire1Hold() {
            updateMaxPlayerValue(1);
        }

        @Override
        public void onFire2Down() {
            updateMaxPlayerValue(-1);
        }

        @Override
        public void onFire2Hold() {
            updateMaxPlayerValue(-1);
        }

        private void updateMaxPlayerValue(int direction) {
            int e = (int) Math.log10(game.settings.maxPlayerValue);
            int m = (int) (game.settings.maxPlayerValue / Math.pow(10, e));
            if (direction == 1) {
                if (e < 11 || m < 5) {
                    switch (m) {
                        case 1:
                            m = 2;
                            break;
                        case 2:
                            m = 5;
                            break;
                        case 5:
                            m = 1;
                            e += 1;
                            break;
                    }
                }
            } else if (direction == -1) {
                if (e > 4 || m > 1) {
                    switch (m) {
                        case 5:
                            m = 2;
                            break;
                        case 2:
                            m = 1;
                            break;
                        case 1:
                            m = 5;
                            e -= 1;
                            break;
                    }
                }
            }
            game.settings.maxPlayerValue = m * Math.pow(10, e);
            setChanged(true);
        }
    }

    class CurrencyButton extends Button {

        public CurrencyButton() {
            setColors(0x1F1F95);
            setGeometry(game.settings.GUI_WIDTH / 2 + 340, 375, 130, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(game.settings.currency);
        }

        @Override
        public void onFire1Down() {
            updateCurrency(1);
        }

        @Override
        public void onFire2Down() {
            updateCurrency(-1);
        }

        private void updateCurrency(int direction) {
            int i = Assets.currencies.indexOf(game.settings.currency);
            if (i == -1) {
                i = 0;
            } else {
                i = Emath.rotate(i, 0, Assets.currencies.size() - 1, direction);
            }
            game.settings.currency = Assets.currencies.get(i);
            setChanged(true);
        }
    }

    private class QuitToOsButton extends Button {

        QuitToOsButton() {
            setColors(0x008080);
            setGeometry((game.settings.GUI_WIDTH - 300) / 2, 590, 300, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("QUIT TO OS"));
        }

        @Override
        public void onFire1Down() {
            Gdx.app.exit();
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setColors(0xC84200);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("EXIT"));
        }

        @Override
        public void onFire1Down() {
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }
}
