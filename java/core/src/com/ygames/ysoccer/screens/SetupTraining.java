package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.SceneSettings;

import static com.ygames.ysoccer.framework.Assets.favouritesFile;

class SetupTraining extends GLScreen {

    private SceneSettings sceneSettings;
    private TimePicture timePicture;
    private PitchTypePicture pitchTypePicture;
    private WeatherButton weatherButton;
    private WeatherPicture weatherPicture;

    SetupTraining(GLGame game) {
        super(game);

        sceneSettings = new SceneSettings(game.settings);

        Assets.Sounds.volume = game.settings.soundVolume;

        background = new Texture("images/backgrounds/menu_training.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("TRAINING") + " - " + navigation.team.name, game.stateColor.body);
        widgets.add(w);

        w = new TimeLabel();
        widgets.add(w);

        timePicture = new TimePicture();
        widgets.add(timePicture);

        w = new TimeButton();
        widgets.add(w);

        w = new PitchTypeLabel();
        widgets.add(w);

        pitchTypePicture = new PitchTypePicture();
        widgets.add(pitchTypePicture);

        w = new PitchTypeButton();
        widgets.add(w);

        w = new WeatherLabel();
        widgets.add(w);

        weatherPicture = new WeatherPicture();
        widgets.add(weatherPicture);

        weatherButton = new WeatherButton();
        widgets.add(weatherButton);

        w = new FreeTrainingButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new FreeKicksButton();
        widgets.add(w);

        w = new PenaltyKicksButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 300 + 25, 130 - 40 / 2, 300, 40);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimePicture extends Button {

        TimePicture() {
            setColor(0x666666);
            setGeometry(game.gui.WIDTH / 2 - 300 - 65, 130 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = gui.lightIcons[sceneSettings.time.ordinal()];
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 65, 130 - 40 / 2, 300, 40);
            setColor(0x1F1F95);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(SceneSettings.getTimeLabel(sceneSettings.time)));
        }

        @Override
        public void onFire1Down() {
            rotateTime(1);
        }

        @Override
        public void onFire2Down() {
            rotateTime(-1);
        }

        private void rotateTime(int n) {
            sceneSettings.rotateTime(n);
            setDirty(true);
            timePicture.setDirty(true);
        }
    }

    private class PitchTypeLabel extends Button {

        PitchTypeLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 300 + 25, 200 - 40 / 2, 300, 40);
            setText(Assets.strings.get("PITCH TYPE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PitchTypePicture extends Button {

        PitchTypePicture() {
            setColor(0x666666);
            setGeometry(game.gui.WIDTH / 2 - 300 - 65, 200 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = gui.pitchIcons[sceneSettings.pitchType.ordinal()];
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setColor(0x1F1F95);
            setGeometry(game.gui.WIDTH / 2 + 65, 200 - 40 / 2, 300, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Pitch.names[sceneSettings.pitchType.ordinal()]));
        }

        @Override
        public void onFire1Down() {
            rotatePitchType(1);
        }

        @Override
        public void onFire2Down() {
            rotatePitchType(-1);
        }

        private void rotatePitchType(int n) {
            sceneSettings.rotatePitchType(n);
            setDirty(true);
            pitchTypePicture.setDirty(true);
            weatherPicture.setDirty(true);
            weatherButton.setDirty(true);
        }
    }

    private class WeatherLabel extends Button {

        WeatherLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 300 + 25, 270 - 40 / 2, 300, 40);
            setText(Assets.strings.get("WEATHER"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class WeatherPicture extends Button {

        WeatherPicture() {
            setColor(0x666666);
            setGeometry(game.gui.WIDTH / 2 - 300 - 65, 270 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = gui.weatherIcons[sceneSettings.weatherOffset()];
        }
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(game.gui.WIDTH / 2 + 65, 270 - 40 / 2, 300, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setColor(0x1F1F95);
            setActive(true);
            setText(Assets.strings.get(sceneSettings.getWeatherLabel()));
        }

        @Override
        public void onFire1Down() {
            sceneSettings.rotateWeather();
            setDirty(true);
            weatherPicture.setDirty(true);
        }
    }

    private class FreeTrainingButton extends Button {

        FreeTrainingButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 400, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("TRAINING.FREE TRAINING"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new TrainingLoading(game, sceneSettings));
        }
    }

    private class FreeKicksButton extends Button {

        FreeKicksButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 460, 340, 40);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TRAINING.FREE KICKS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PenaltyKicksButton extends Button {

        PenaltyKicksButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 520, 340, 40);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TRAINING.PENALTY KICKS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry((game.gui.WIDTH - 196) / 2, 660, 196, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            if (navigation.folder.equals(favouritesFile)) {
                game.setScreen(new SelectFavourite(game));
            } else {
                game.setScreen(new SelectTeam(game));
            }
        }
    }
}
