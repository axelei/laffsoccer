package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Widget;
import com.ysoccer.android.ysdemo.match.Weather;

class MenuSettings extends GLScreen {

    private MusicButton musicButton;
    private SfxButton sfxButton;
    private LengthButton lengthButton;
    private WeatherButton weatherButton;

    private class MusicMinusButton extends Button {

        MusicMinusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 100, 100, 40);
            setText("-", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.musicVolume = Emath.slide(
                    glGame.settings.musicVolume, 0, 1, -0.1f);
            musicButton.updateText();
        }
    }

    private class MusicButton extends Button {

        MusicButton() {
            setColors(0x1F1F95);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 100, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        void updateText() {
            setText("" + Math.round(10 * glGame.settings.musicVolume));
        }
    }

    private class MusicPlusButton extends Button {

        MusicPlusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 300, 100, 100, 40);
            setText("+", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.musicVolume = Emath.slide(
                    glGame.settings.musicVolume, 0, 1, 0.1f);
            musicButton.updateText();
        }
    }

    private class SfxMinusButton extends Button {

        SfxMinusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 160, 100, 40);
            setText("-", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.sfxVolume = Emath.slide(glGame.settings.sfxVolume,
                    0, 1, -0.1f);
            sfxButton.updateText();
        }
    }

    private class SfxButton extends Button {

        SfxButton() {
            setColors(0x1F1F95);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 160, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        void updateText() {
            setText("" + Math.round(10 * glGame.settings.sfxVolume));
        }
    }

    private class SfxPlusButton extends Button {

        SfxPlusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 300, 160, 100, 40);
            setText("+", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.sfxVolume = Emath.slide(glGame.settings.sfxVolume,
                    0, 1, 0.1f);
            sfxButton.updateText();
        }
    }

    private class LengthMinusButton extends Button {

        LengthMinusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 220, 100, 40);
            setText("-", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.gameLengthIndex = Emath.slide(
                    glGame.settings.gameLengthIndex, 0,
                    Settings.gameLengths.length - 1, -1);
            lengthButton.updateText();
        }
    }

    private class LengthButton extends Button {

        LengthButton() {
            setColors(0x1F1F95);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 220, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        void updateText() {
            setText(gettext(R.string.N_MINUTES).replaceFirst("\\$N",
                    "" + Settings.gameLengths[glGame.settings.gameLengthIndex]));
        }
    }

    private class LengthPlusButton extends Button {

        LengthPlusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 300, 220, 100, 40);
            setText("+", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.gameLengthIndex = Emath.slide(
                    glGame.settings.gameLengthIndex, 0,
                    Settings.gameLengths.length - 1, 1);
            lengthButton.updateText();
        }
    }

    private class WeatherMinusButton extends Button {

        WeatherMinusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 280, 100, 40);
            setText("-", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.weatherMaxStrength = Emath.slide(
                    glGame.settings.weatherMaxStrength, Weather.Strength.NONE,
                    Weather.Strength.STRONG, -1);
            weatherButton.updateText();
        }
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setColors(0x1F1F95);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 280, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        void updateText() {
            setText(gettext(Weather.Strength.stringIds[glGame.settings.weatherMaxStrength]));
        }
    }

    private class WeatherPlusButton extends Button {

        WeatherPlusButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 300, 280, 100, 40);
            setText("+", 0, 14);
        }

        @Override
        public void onFire1Down() {
            glGame.settings.weatherMaxStrength = Emath.slide(
                    glGame.settings.weatherMaxStrength, Weather.Strength.NONE,
                    Weather.Strength.STRONG, +1);
            weatherButton.updateText();
        }
    }

    private class RadarButton extends Button {

        RadarButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 340, 400, 40);
            setText("", 0, 14);
            updateText();
        }

        @Override
        public void onFire1Down() {
            glGame.settings.displayRadar = !glGame.settings.displayRadar;
            updateText();
        }

        void updateText() {
            if (glGame.settings.displayRadar) {
                setText(gettext(R.string.YES));
            } else {
                setText(gettext(R.string.NO));
            }
        }
    }

    private class AutoReplayButton extends Button {

        AutoReplayButton() {
            setColors(0x3C3C78, 0x5858AC, 0x202040);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 400, 400, 40);
            setText("", 0, 14);
            updateText();
        }

        @Override
        public void onFire1Down() {
            glGame.settings.autoReplay = !glGame.settings.autoReplay;
            updateText();
        }

        void updateText() {
            if (glGame.settings.autoReplay) {
                setText(gettext(R.string.YES));
            } else {
                setText(gettext(R.string.NO));
            }
        }
    }

    private class BackButton extends Button {

        BackButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((Settings.GUI_WIDTH - 180) / 2,
                    Settings.GUI_HEIGHT - 40 - 20, 180, 40);
            setText(gettext(R.string.BACK), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuMain(game));
        }
    }

    MenuSettings(Game game) {
        super(game);

        setBackground(Assets.settingsMenuBackground);

        Widget w;

        w = new Button();
        w.setColors(0x536B90, 0x7090C2, 0x7090C2);
        w.setGeometry((Settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
        w.setText(gettext(R.string.SETTINGS), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        // music volume
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 100, 400, 40);
        w.setText(gettext(R.string.MUSIC_VOLUME), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        w = new MusicMinusButton();
        getWidgets().add(w);

        musicButton = new MusicButton();
        getWidgets().add(musicButton);

        w = new MusicPlusButton();
        getWidgets().add(w);

        // sfx volume
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 160, 400, 40);
        w.setText(gettext(R.string.SFX_VOLUME), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        w = new SfxMinusButton();
        getWidgets().add(w);

        sfxButton = new SfxButton();
        getWidgets().add(sfxButton);

        w = new SfxPlusButton();
        getWidgets().add(w);

        // game length
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 220, 400, 40);
        w.setText(gettext(R.string.GAME_LENGTH), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        w = new LengthMinusButton();
        getWidgets().add(w);

        lengthButton = new LengthButton();
        getWidgets().add(lengthButton);

        w = new LengthPlusButton();
        getWidgets().add(w);

        // weather effects
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 280, 400, 40);
        w.setText(gettext(R.string.WEATHER_EFFECTS), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        w = new WeatherMinusButton();
        getWidgets().add(w);

        weatherButton = new WeatherButton();
        getWidgets().add(weatherButton);

        w = new WeatherPlusButton();
        getWidgets().add(w);

        // display radar
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 340, 400, 40);
        w.setText(gettext(R.string.RADAR), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        w = new RadarButton();
        getWidgets().add(w);

        // auto replay
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 400, 400, 40);
        w.setText(gettext(R.string.AUTO_REPLAY), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        w = new AutoReplayButton();
        getWidgets().add(w);

        w = new BackButton();
        getWidgets().add(w);

        setSelectedWidget(w);
    }

    @Override
    public void resume() {
        super.resume();
        Assets.settingsMenuBackground.reload();
    }

    @Override
    public void onKeyBackHw() {
        game.setScreen(new MenuMain(game));
    }
}
