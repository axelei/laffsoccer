package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.ToggleButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;

class GameOptions extends GLScreen {

    GameOptions(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;

        w = new TitleBar();
        widgets.add(w);

        w = new LanguageLabel();
        widgets.add(w);

        w = new LanguageButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new ScreenModeLabel();
        widgets.add(w);

        w = new ScreenModeButton();
        widgets.add(w);

        w = new ShowIntroLabel();
        widgets.add(w);

        w = new ShowIntroButton();
        widgets.add(w);

        w = new MusicModeLabel();
        widgets.add(w);

        w = new MusicModeButton();
        widgets.add(w);

        w = new MusicVolumeLabel();
        widgets.add(w);

        w = new MusicVolumeButton();
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

        w = new ExportButton();
        widgets.add(w);

        w = new QuitToOsButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class TitleBar extends Button {

        TitleBar() {
            setColor(0x536B90);
            setGeometry((game.gui.WIDTH - 960) / 2, 20, 960, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("GAME OPTIONS"));
        }
    }

    private class LanguageLabel extends Button {

        LanguageLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 150, 440, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("LANGUAGE"));
        }
    }

    private class LanguageButton extends Button {

        LanguageButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10, 150, 440, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext("// THIS LANGUAGE NAME //"));
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
            game.settings.locale = Assets.locales.get(EMath.rotate(index, 0, Assets.locales.size() - 1, direction));
            Assets.loadStrings(game.settings);
            refreshAllWidgets();
        }
    }

    private class ScreenModeLabel extends Button {

        ScreenModeLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 200, 440, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("SCREEN MODE"));
        }
    }

    private class ScreenModeButton extends Button {

        ScreenModeButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10, 200, 440, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext(game.settings.fullScreen ? "SCREEN MODE.FULL SCREEN" : "SCREEN MODE.WINDOW"));
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
            game.setScreenMode(game.settings.fullScreen);
            setDirty(true);
        }
    }

    private class ShowIntroLabel extends Button {

        ShowIntroLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 250, 440, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("SHOW INTRO"));
        }
    }

    private class ShowIntroButton extends ToggleButton {

        ShowIntroButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10, 250, 440, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext(game.settings.showIntro ? "SHOW INTRO.ON" : "SHOW INTRO.OFF"));
        }

        @Override
        protected void toggle() {
            game.settings.showIntro = !game.settings.showIntro;
            setDirty(true);
        }
    }

    private class MusicModeLabel extends Button {

        MusicModeLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 300, 440, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("MUSIC.MODE"));
        }
    }

    private class MusicModeButton extends Button {

        MusicModeButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10, 300, 440, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (game.settings.musicMode) {

                case MenuMusic.ALL:
                    setText(gettext("MUSIC.ALL"));
                    break;

                case MenuMusic.SHUFFLE:
                    setText(gettext("MUSIC.SHUFFLE"));
                    break;

                default:
                    String trackName = game.menuMusic.getCurrentTrackName();
                    setText(trackName.length() > 28 ? trackName.substring(0, 28) + "..." : trackName);
                    break;
            }
        }

        @Override
        public void onFire1Down() {
            updateMusicMode(1);
        }

        @Override
        public void onFire1Hold() {
            updateMusicMode(1);
        }

        @Override
        public void onFire2Down() {
            updateMusicMode(-1);
        }

        @Override
        public void onFire2Hold() {
            updateMusicMode(-1);
        }

        private void updateMusicMode(int n) {
            game.settings.musicMode = EMath.rotate(game.settings.musicMode, game.menuMusic.getModeMin(), game.menuMusic.getModeMax(), n);
            game.menuMusic.setMode(game.settings.musicMode);
            setDirty(true);
        }
    }

    private class MusicVolumeLabel extends Button {

        MusicVolumeLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 350, 440, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("MUSIC.VOLUME"));
        }
    }

    private class MusicVolumeButton extends Button {

        MusicVolumeButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10, 350, 440, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            if (game.settings.musicVolume == 0) {
                setText(gettext("MUSIC.OFF"));
            } else {
                setText(game.settings.musicVolume / 10);
            }
        }

        @Override
        public void onFire1Down() {
            updateMusicVolume(1);
        }

        @Override
        public void onFire1Hold() {
            updateMusicVolume(1);
        }

        @Override
        public void onFire2Down() {
            updateMusicVolume(-1);
        }

        @Override
        public void onFire2Hold() {
            updateMusicVolume(-1);
        }

        private void updateMusicVolume(int n) {
            game.settings.musicVolume = EMath.slide(game.settings.musicVolume, 0, 100, 10 * n);
            setDirty(true);
        }
    }

    private class PlayerCountryLabel extends Button {

        PlayerCountryLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 400, 440, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("PLAYER'S COUNTRY"));
        }
    }

    private class PlayerCountryButton extends Button {

        PlayerCountryButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10, 400, 440, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext(game.settings.useFlags ? "FLAG" : "CODE"));
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
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 450, 440, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(gettext("MAX PLAYER VALUE"));
        }
    }

    private class MaxPlayerValueButton extends Button {

        MaxPlayerValueButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10, 450, 300, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
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
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 10 + 300 + 10, 450, 130, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
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
                i = 0; // not found, start from 0
            } else {
                i = EMath.rotate(i, 0, Assets.currencies.size() - 1, direction);
            }
            game.settings.currency = Assets.currencies.get(i);
            setDirty(true);
        }
    }

    private class ImportButton extends Button {

        ImportButton() {
            setColor(0x762B8E);
            setGeometry(game.gui.WIDTH / 2 - 260 - 10, 530, 260, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext("IMPORT"));
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ImportTeams(game));
        }
    }

    private class ExportButton extends Button {

        ExportButton() {
            setColor(0x762B8E);
            setGeometry(game.gui.WIDTH / 2 + 10, 530, 260, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext("EXPORT"));
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ExportTeams(game));
        }
    }

    private class QuitToOsButton extends Button {

        QuitToOsButton() {
            setColor(0x008080);
            setGeometry((game.gui.WIDTH - 300) / 2, 580, 300, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext("QUIT TO OS"));
        }

        @Override
        public void onFire1Down() {
            Gdx.app.exit();
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext("EXIT"));
        }

        @Override
        public void onFire1Down() {
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }
}
