package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Widget;
import com.ysoccer.android.ysdemo.match.Weather;

public class MenuSettings extends GLScreen {

    MusicButton musicButton;
    SfxButton sfxButton;
    LengthButton lengthButton;
    WeatherButton weatherButton;
    RadarButton radarButton;
    AutoReplayButton autoReplayButton;

    class MusicMinusButton extends Button {
        public MusicMinusButton() {
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

    class MusicButton extends Button {
        public MusicButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 100, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        public void updateText() {
            setText("" + Math.round(10 * glGame.settings.musicVolume));
        }
    }

    class MusicPlusButton extends Button {
        public MusicPlusButton() {
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

    class SfxMinusButton extends Button {
        public SfxMinusButton() {
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

    class SfxButton extends Button {
        public SfxButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 160, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        public void updateText() {
            setText("" + Math.round(10 * glGame.settings.sfxVolume));
        }
    }

    class SfxPlusButton extends Button {
        public SfxPlusButton() {
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

    class LengthMinusButton extends Button {
        public LengthMinusButton() {
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

    class LengthButton extends Button {
        public LengthButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 220, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        public void updateText() {
            setText(_(R.string.$N_MINUTES).replaceFirst("\\$N",
                    "" + Settings.gameLengths[glGame.settings.gameLengthIndex]));
        }
    }

    class LengthPlusButton extends Button {
        public LengthPlusButton() {
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

    class WeatherMinusButton extends Button {
        public WeatherMinusButton() {
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

    class WeatherButton extends Button {
        public WeatherButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(Settings.GUI_WIDTH / 2 + 30 + 100, 280, 200, 40);
            setText("", 0, 14);
            isActive = false;
            updateText();
        }

        public void updateText() {
            setText(_(Weather.Strength.stringIds[glGame.settings.weatherMaxStrength]));
        }
    }

    class WeatherPlusButton extends Button {
        public WeatherPlusButton() {
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

    class RadarButton extends Button {
        public RadarButton() {
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

        public void updateText() {
            if (glGame.settings.displayRadar) {
                setText(_(R.string.YES));
            } else {
                setText(_(R.string.NO));
            }
        }
    }

    class AutoReplayButton extends Button {
        public AutoReplayButton() {
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

        public void updateText() {
            if (glGame.settings.autoReplay) {
                setText(_(R.string.YES));
            } else {
                setText(_(R.string.NO));
            }
        }
    }

    class BackButton extends Button {
        public BackButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((Settings.GUI_WIDTH - 180) / 2,
                    Settings.GUI_HEIGHT - 40 - 20, 180, 40);
            setText(_(R.string.BACK), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuMain(game));
        }
    }

    public MenuSettings(Game game) {
        super(game);

        setBackground(Assets.settingsMenuBackground);

        Widget w;

        w = new Button();
        w.setColors(0x536B90, 0x7090C2, 0x263142);
        w.setGeometry((Settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
        w.setText(_(R.string.SETTINGS), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        // music volume
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0x400000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 100, 400, 40);
        w.setText(_(R.string.MUSIC_VOLUME), 0, 14);
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
        w.setColors(0x800000, 0xB40000, 0x400000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 160, 400, 40);
        w.setText(_(R.string.SFX_VOLUME), 0, 14);
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
        w.setColors(0x800000, 0xB40000, 0x400000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 220, 400, 40);
        w.setText(_(R.string.GAME_LENGTH), 0, 14);
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
        w.setColors(0x800000, 0xB40000, 0x400000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 280, 400, 40);
        w.setText(_(R.string.WEATHER_EFFECTS), 0, 14);
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
        w.setColors(0x800000, 0xB40000, 0x400000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 340, 400, 40);
        w.setText(_(R.string.RADAR), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        radarButton = new RadarButton();
        getWidgets().add(radarButton);

        // auto replay
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0x400000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 400 - 30, 400, 400, 40);
        w.setText(_(R.string.AUTO_REPLAY), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        autoReplayButton = new AutoReplayButton();
        getWidgets().add(autoReplayButton);

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
