package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.SceneRenderer;
import com.ygames.ysoccer.match.Weather;
import com.ygames.ysoccer.framework.EMath;

import java.util.Arrays;
import java.util.List;

class MatchOptions extends GLScreen {

    MatchOptions(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_match_options.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("MATCH OPTIONS"), 0x3847A3);
        widgets.add(w);

        w = new MatchLengthLabel();
        widgets.add(w);

        w = new MatchLengthButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new WeatherEffectsLabel();
        widgets.add(w);

        w = new WeatherEffectsButton();
        widgets.add(w);

        w = new RadarLabel();
        widgets.add(w);

        w = new RadarButton();
        widgets.add(w);

        w = new AutoReplaysLabel();
        widgets.add(w);

        w = new AutoReplaysButton();
        widgets.add(w);

        w = new ZoomLabel();
        widgets.add(w);

        w = new ZoomButton();
        widgets.add(w);

        w = new SfxVolumeLabel();
        widgets.add(w);

        w = new SfxVolumeButton();
        widgets.add(w);

        w = new CommentaryLabel();
        widgets.add(w);

        w = new CommentaryButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class MatchLengthLabel extends Button {

        MatchLengthLabel() {
            setColor(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 190, 440, 40);
            setText(Assets.strings.get("FRIENDLY/DIY GAME LENGTH"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class MatchLengthButton extends Button {

        List<Integer> matchLengths;

        MatchLengthButton() {
            matchLengths = Arrays.asList(Settings.matchLengths);
            setColor(0x2B4A61);
            setGeometry(game.gui.WIDTH / 2 + 10, 190, 440, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get("%n MINUTES").replaceFirst("%n", "" + game.settings.matchLength));
        }

        @Override
        public void onFire1Down() {
            updateMatchLength(1);
        }

        @Override
        public void onFire1Hold() {
            updateMatchLength(1);
        }

        @Override
        public void onFire2Down() {
            updateMatchLength(-1);
        }

        @Override
        public void onFire2Hold() {
            updateMatchLength(-1);
        }

        private void updateMatchLength(int n) {
            int index = matchLengths.indexOf(game.settings.matchLength);
            game.settings.matchLength = matchLengths.get(EMath.slide(index, 0, matchLengths.size() - 1, n));
            setDirty(true);
        }
    }

    private class WeatherEffectsLabel extends Button {

        WeatherEffectsLabel() {
            setColor(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 240, 440, 40);
            setText(Assets.strings.get("WEATHER.EFFECTS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class WeatherEffectsButton extends Button {

        WeatherEffectsButton() {
            setColor(0x2B4A61);
            setGeometry(game.gui.WIDTH / 2 + 10, 240, 440, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Weather.Strength.names[game.settings.weatherMaxStrength]));
        }

        @Override
        public void onFire1Down() {
            updateWeatherMaxStrength(1);
        }

        @Override
        public void onFire2Down() {
            updateWeatherMaxStrength(-1);
        }

        private void updateWeatherMaxStrength(int n) {
            game.settings.weatherMaxStrength = EMath.rotate(game.settings.weatherMaxStrength, Weather.Strength.NONE, Weather.Strength.STRONG, n);
            setDirty(true);
        }
    }

    private class RadarLabel extends Button {

        RadarLabel() {
            setColor(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 290, 440, 40);
            setText(Assets.strings.get("RADAR"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RadarButton extends Button {

        RadarButton() {
            setColor(0x2B4A61);
            setGeometry(game.gui.WIDTH / 2 + 10, 290, 440, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(game.settings.radar ? "RADAR.ON" : "RADAR.OFF"));
        }

        @Override
        public void onFire1Down() {
            toggleRadar();
        }

        @Override
        public void onFire2Down() {
            toggleRadar();
        }

        private void toggleRadar() {
            game.settings.radar = !game.settings.radar;
            setDirty(true);
        }
    }

    private class AutoReplaysLabel extends Button {

        AutoReplaysLabel() {
            setColor(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 340, 440, 40);
            setText(Assets.strings.get("AUTO REPLAYS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AutoReplaysButton extends Button {

        AutoReplaysButton() {
            setColor(0x2B4A61);
            setGeometry(game.gui.WIDTH / 2 + 10, 340, 440, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(game.settings.autoReplays ? "AUTO REPLAYS.ON" : "AUTO REPLAYS.OFF"));
        }

        @Override
        public void onFire1Down() {
            toggleAutoReplays();
        }

        @Override
        public void onFire2Down() {
            toggleAutoReplays();
        }

        private void toggleAutoReplays() {
            game.settings.autoReplays = !game.settings.autoReplays;
            setDirty(true);
        }
    }

    private class ZoomLabel extends Button {

        ZoomLabel() {
            setColor(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 390, 440, 40);
            setText(Assets.strings.get("ZOOM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ZoomButton extends Button {

        ZoomButton() {
            setColor(0x2B4A61);
            setGeometry(game.gui.WIDTH / 2 + 10, 390, 440, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(game.settings.zoom + "%");
        }

        @Override
        public void onFire1Down() {
            updateZoom(1);
        }

        @Override
        public void onFire1Hold() {
            updateZoom(1);
        }

        @Override
        public void onFire2Down() {
            updateZoom(-1);
        }

        @Override
        public void onFire2Hold() {
            updateZoom(-1);
        }

        private void updateZoom(int n) {
            game.settings.zoom = EMath.slide(game.settings.zoom, SceneRenderer.zoomMin(), SceneRenderer.zoomMax(), 5 * n);
            setDirty(true);
        }
    }

    private class SfxVolumeLabel extends Button {

        SfxVolumeLabel() {
            setColor(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 440, 440, 40);
            setText(Assets.strings.get("MATCH OPTIONS.SOUND VOLUME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SfxVolumeButton extends Button {

        SfxVolumeButton() {
            setColor(0x2B4A61);
            setGeometry(game.gui.WIDTH / 2 + 10, 440, 440, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            if (game.settings.soundVolume == 0) {
                setText(Assets.strings.get("MATCH OPTIONS.SOUND VOLUME.OFF"));
            } else {
                setText(game.settings.soundVolume / 10);
            }
        }

        @Override
        public void onFire1Down() {
            updateSfxVolume(1);
        }

        @Override
        public void onFire1Hold() {
            updateSfxVolume(1);
        }

        @Override
        public void onFire2Down() {
            updateSfxVolume(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSfxVolume(-1);
        }

        private void updateSfxVolume(int n) {
            game.settings.soundVolume = EMath.slide(game.settings.soundVolume, 0, 100, 10 * n);
            setDirty(true);
        }
    }

    private class CommentaryLabel extends Button {

        CommentaryLabel() {
            setColor(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 490, 440, 40);
            setText(Assets.strings.get("MATCH OPTIONS.COMMENTARY"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class CommentaryButton extends Button {

        CommentaryButton() {
            setColor(0x2B4A61);
            setGeometry(game.gui.WIDTH / 2 + 10, 490, 440, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(game.settings.commentary ? "MATCH OPTIONS.COMMENTARY.ON" : "MATCH OPTIONS.COMMENTARY.OFF"));
        }

        @Override
        public void onFire1Down() {
            toggleCommentary();
        }

        @Override
        public void onFire2Down() {
            toggleCommentary();
        }

        private void toggleCommentary() {
            game.settings.commentary = !game.settings.commentary;
            setDirty(true);
        }
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
            game.setScreen(new Main(game));
        }
    }
}
