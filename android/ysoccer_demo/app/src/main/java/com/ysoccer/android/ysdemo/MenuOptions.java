package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Widget;
import com.ysoccer.android.ysdemo.match.MatchSettings;
import com.ysoccer.android.ysdemo.match.Pitch;
import com.ysoccer.android.ysdemo.match.Team;
import com.ysoccer.android.ysdemo.match.Time;
import com.ysoccer.android.ysdemo.match.Weather;

import java.util.ArrayList;

public class MenuOptions extends GLScreen {

    private final Team[] teams;

    private MatchSettings matchSettings;
    private TimePicture timePicture;
    private TimeButton timeButton;
    private PitchPicture pitchPicture;
    private WeatherPicture weatherPicture;
    private WeatherButton weatherButton;
    private ArrayList<KitSelectedButton>[] kitSelectedButtons = (ArrayList<KitSelectedButton>[]) new ArrayList[2];

    class TimePicture extends Button {

        TimePicture() {
            setColors(0x000000, 0x8F8D8D, 0x404040);
            setGeometry((Settings.GUI_WIDTH - 50) / 2, 130 - 50 / 2, 50, 50);
            setTexture(Assets.light, 0, 0, 46, 46);
            isActive = false;
            updateFrame();
        }

        void updateFrame() {
            setFrame(47, 46, matchSettings.time, 0);
        }
    }

    class TimeButton extends Button {

        TimeButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(Settings.GUI_WIDTH / 2 + 65, 130 - 40 / 2, 300, 40);
            setText("", 0, 14);
            updateText();
        }

        void updateText() {
            setText(gettext(matchSettings.timeStringId()));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotateTime(1);
            timePicture.updateFrame();
            timeButton.updateText();
        }
    }

    class PitchPicture extends Button {

        PitchPicture() {
            setColors(0x000000, 0x8F8D8D, 0x404040);
            setGeometry((Settings.GUI_WIDTH - 50) / 2, 200 - 50 / 2, 50, 50);
            setTexture(Assets.pitches, 0, 0, 46, 46);
            isActive = false;
            updateFrame();
        }

        void updateFrame() {
            setFrame(47, 46, matchSettings.pitchType, 0);
        }
    }

    class PitchButton extends Button {

        PitchButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(Settings.GUI_WIDTH / 2 + 65, 200 - 40 / 2, 300, 40);
            setText("", 0, 14);
            updateText();
        }

        void updateText() {
            setText(gettext(matchSettings.pitchStringId()));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotatePitchType(1);
            updateText();
            pitchPicture.updateFrame();
            weatherPicture.updateFrame();
            weatherButton.updateText();
        }
    }

    class WeatherPicture extends Button {

        WeatherPicture() {
            setColors(0x000000, 0x8F8D8D, 0x404040);
            setGeometry((Settings.GUI_WIDTH - 50) / 2, 270 - 50 / 2, 50, 50);
            setTexture(Assets.weather, 0, 0, 46, 46);
            isActive = false;
            updateFrame();
        }

        void updateFrame() {
            setFrame(46, 46, matchSettings.weatherOffset(), 0);
        }
    }

    class WeatherButton extends Button {

        WeatherButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(Settings.GUI_WIDTH / 2 + 65, 270 - 40 / 2, 300, 40);
            setText("", 0, 14);
            updateText();
        }

        void updateText() {
            setText(gettext(matchSettings.weatherStringId()));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotateWeather();
            updateText();
            weatherPicture.updateFrame();
        }
    }

    class KitButton extends Button {

        Team team;

        KitButton(Team team) {
            this.team = team;
            setGeometry((1 + 3 * team.index) * (Settings.GUI_WIDTH) / 5 - 43, 330, 86, 154);
            setFrame(163, 300, 0, 0);
            addShadow = true;
            updateKit();
        }

        void updateKit() {
            Assets.loadKit(glGame, team);
            setTexture(Assets.kit[team.index], 0, 0, 82, 150);
        }

        @Override
        public void onFire1Down() {
            team.kitIndex = Emath.rotate(team.kitIndex, 0,
                    team.kits.size() - 1, 1);
            updateKit();
            int len = kitSelectedButtons[team.index].size();
            for (int i = 0; i < len; i++) {
                kitSelectedButtons[team.index].get(i).updateColor();
            }
        }

    }

    class KitSelectedButton extends Button {

        Team team;
        int index;

        KitSelectedButton(Team team, int index) {
            this.team = team;
            this.index = index;
            setGeometry((1 + 3 * team.index) * (Settings.GUI_WIDTH) / 5 - 43 + 18 * index, 330 + 165, 16, 16);
            updateColor();
        }

        private void updateColor() {
            if (index == team.kitIndex) {
                setColors(0x4444FF, 0x000000, 0x000000);
            } else {
                setColors(0x000088, 0x000000, 0x000000);
            }
        }
    }

    class PlayButton extends Button {

        PlayButton() {
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setGeometry((Settings.GUI_WIDTH - 340) / 2, 375, 340, 40);
            setText(gettext(R.string.PLAY_MATCH), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MatchLoading(game, teams, matchSettings));
        }
    }

    class BackButton extends Button {

        BackButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((Settings.GUI_WIDTH - 180) / 2,
                    Settings.GUI_HEIGHT - 40 - 20, 180, 40);
            setText(gettext(R.string.BACK), 0, 14);
        }

        @Override
        public void onFire1Down() {
            onKeyBackHw();
        }
    }

    MenuOptions(Game game, Team[] teams) {
        super(game);
        this.teams = teams;

        Team.kitAutoselection(teams[0], teams[1]);

        matchSettings = new MatchSettings(glGame);
        matchSettings.sfxVolume = glGame.settings.sfxVolume;
        matchSettings.time = Time.DAY;
        matchSettings.pitchType = Pitch.RANDOM;
        matchSettings.weatherEffect = Weather.RANDOM;

        for (int i = glGame.random.nextInt(Pitch.RANDOM); i >= 0; i--) {
            matchSettings.rotatePitchType(1);
        }
        for (int i = glGame.random.nextInt(2 + 4 * glGame.settings.weatherMaxStrength); i >= 0; i--) {
            matchSettings.rotateWeather();
        }

        setBackground(Assets.settingsMenuBackground);

        Assets.light = new Texture(glGame, "images/light.png");
        Assets.pitches = new Texture(glGame, "images/pitches.png");
        Assets.weather = new Texture(glGame, "images/weather.png");

        Widget w;

        w = new Button();
        w.setColors(0x536B90, 0x7090C2, 0x7090C2);
        String title = teams[0].name + "  -  " + teams[1].name;
        int titleWidth = Math.max(340, 40 + 16 * title.length());
        w.setGeometry((Settings.GUI_WIDTH - titleWidth) / 2, 20, titleWidth, 40);
        w.setText(title, 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        // time of the day
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 300 - 65, 130 - 40 / 2, 300, 40);
        w.setText(gettext(R.string.TIME), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        timePicture = new TimePicture();
        getWidgets().add(timePicture);

        timeButton = new TimeButton();
        getWidgets().add(timeButton);

        // pitch type
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 300 - 65, 200 - 40 / 2, 300, 40);
        w.setText(gettext(R.string.PITCH), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        pitchPicture = new PitchPicture();
        getWidgets().add(pitchPicture);

        PitchButton pitchButton = new PitchButton();
        getWidgets().add(pitchButton);

        // weather effects
        w = new Button();
        w.setColors(0x800000, 0xB40000, 0xB40000);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 300 - 65, 270 - 40 / 2, 300, 40);
        w.setText(gettext(R.string.WEATHER), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        weatherPicture = new WeatherPicture();
        getWidgets().add(weatherPicture);

        weatherButton = new WeatherButton();
        getWidgets().add(weatherButton);

        for (int t = 0; t < 2; t++) {
            w = new KitButton(teams[t]);
            getWidgets().add(w);

            kitSelectedButtons[t] = new ArrayList<>();
            for (int i = 0; i < teams[t].kits.size(); i++) {
                KitSelectedButton kitSelectedButton = new KitSelectedButton(
                        teams[t], i);
                kitSelectedButtons[t].add(kitSelectedButton);
                getWidgets().add(kitSelectedButton);
            }
        }

        w = new PlayButton();
        getWidgets().add(w);

        setSelectedWidget(w);

        w = new BackButton();
        getWidgets().add(w);

    }

    @Override
    public void resume() {
        super.resume();
        Assets.settingsMenuBackground.reload();
        Assets.light.reload();
        Assets.pitches.reload();
        Assets.weather.reload();
        Assets.kit[0].reload();
        Assets.kit[1].reload();
    }

    @Override
    public void onKeyBackHw() {
        game.setScreen(new MenuSelectTeams(game));
    }

}
