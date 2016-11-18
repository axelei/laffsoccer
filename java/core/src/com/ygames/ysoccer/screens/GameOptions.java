package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

class GameOptions extends GLScreen {

    GameOptions(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        w = new ScreenModeLabel();
        widgets.add(w);

        w = new ScreenModeButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new MouseLabel();
        widgets.add(w);

        w = new MouseButton();
        widgets.add(w);

        w = new LanguageLabel();
        widgets.add(w);

        w = new LanguageButton();
        widgets.add(w);

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

        w = new ImportButton();
        widgets.add(w);

        w = new QuitToOsButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class TitleButton extends Button {

        TitleButton() {
            setColors(0x536B90);
            setGeometry((game.gui.WIDTH - 400) / 2, 20, 400, 40);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("GAME OPTIONS"));
        }
    }

    private class ScreenModeLabel extends Button {

        ScreenModeLabel() {
            setColors(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 30 - 440, 240, 440, 38);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("SCREEN MODE"));
        }
    }

    private class ScreenModeButton extends Button {

        ScreenModeButton() {
            setColors(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 30, 240, 440, 38);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(game.settings.fullScreen ? "SCREEN MODE.FULL SCREEN" : "SCREEN MODE.WINDOW"));
        }

        @Override
        public void onFire1Down() {
            toggleFullScreen();
        }

        @Override
        public void onFire2Down() {
            toggleFullScreen();
        }

        private void toggleFullScreen() {
            game.settings.fullScreen = !game.settings.fullScreen;
            game.setScreenMode();
            setDirty(true);
        }
    }

    private class MouseLabel extends Button {

        MouseLabel() {
            setColors(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 30 - 440, 290, 440, 38);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("MOUSE"));
        }
    }

    private class MouseButton extends Button {

        MouseButton() {
            setColors(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 30, 290, 440, 38);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(game.settings.mouseEnabled ? "MOUSE.ON" : "MOUSE.OFF"));
        }

        @Override
        public void onFire1Down() {
            toggleMouse();
        }

        @Override
        public void onFire2Down() {
            toggleMouse();
        }

        private void toggleMouse() {
            game.settings.mouseEnabled = !game.settings.mouseEnabled;
            game.setMouse();
            setDirty(true);
        }
    }

    private class LanguageLabel extends Button {

        LanguageLabel() {
            setColors(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 30 - 440, 340, 440, 38);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("LANGUAGE"));
        }
    }

    private class LanguageButton extends Button {

        LanguageButton() {
            setColors(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 30, 340, 440, 38);
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
                w.setDirty(true);
            }
        }
    }

    private class PlayerCountryLabel extends Button {

        PlayerCountryLabel() {
            setColors(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 30 - 440, 390, 440, 38);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("PLAYER'S COUNTRY"));
        }
    }

    private class PlayerCountryButton extends Button {

        PlayerCountryButton() {
            setColors(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 30, 390, 440, 38);
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
            setDirty(true);
        }
    }

    private class MaxPlayerValueLabel extends Button {

        MaxPlayerValueLabel() {
            setColors(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 30 - 440, 440, 440, 38);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("MAX PLAYER VALUE"));
        }
    }

    private class MaxPlayerValueButton extends Button {

        MaxPlayerValueButton() {
            setColors(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 30, 440, 300, 38);
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
            setDirty(true);
        }
    }

    private class CurrencyButton extends Button {

        CurrencyButton() {
            setColors(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 340, 440, 130, 38);
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
            setDirty(true);
        }
    }

    private class ImportButton extends Button {

        ImportButton() {
            setColors(0x762B8E);
            setGeometry((game.gui.WIDTH - 300) / 2, 530, 300, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("IMPORT"));
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ImportTeams(game));
        }
    }

    private class QuitToOsButton extends Button {

        QuitToOsButton() {
            setColors(0x008080);
            setGeometry((game.gui.WIDTH - 300) / 2, 590, 300, 36);
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

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
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
