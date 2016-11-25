package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Weather;
import com.ygames.ysoccer.math.Emath;

import java.util.Arrays;
import java.util.List;

import static com.ygames.ysoccer.match.MatchRenderer.VISIBLE_FIELD_WIDTH_MAX;
import static com.ygames.ysoccer.match.MatchRenderer.VISIBLE_FIELD_WIDTH_MIN;
import static com.ygames.ysoccer.match.MatchRenderer.VISIBLE_FIELD_WIDTH_OPT;

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

        w = new ExitButton();
        widgets.add(w);
    }

    private class MatchLengthLabel extends Button {

        MatchLengthLabel() {
            setColors(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 190, 440, 40);
            setText(Assets.strings.get("FRIENDLY/DIY GAME LENGTH"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class MatchLengthButton extends Button {

        List<Integer> matchLengths;

        MatchLengthButton() {
            matchLengths = Arrays.asList(Settings.matchLengths);
            setColors(0x2B4A61);
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
            game.settings.matchLength = matchLengths.get(Emath.slide(matchLengths.indexOf(game.settings.matchLength), 0, matchLengths.size() - 1, n));
            setDirty(true);
        }
    }

    private class WeatherEffectsLabel extends Button {

        WeatherEffectsLabel() {
            setColors(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 240, 440, 40);
            setText(Assets.strings.get("WEATHER.EFFECTS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class WeatherEffectsButton extends Button {

        WeatherEffectsButton() {
            setColors(0x2B4A61);
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
            game.settings.weatherMaxStrength = Emath.rotate(game.settings.weatherMaxStrength, Weather.Strength.NONE, Weather.Strength.STRONG, n);
            setDirty(true);
        }
    }

    private class RadarLabel extends Button {

        RadarLabel() {
            setColors(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 290, 440, 40);
            setText(Assets.strings.get("RADAR"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RadarButton extends Button {

        RadarButton() {
            setColors(0x2B4A61);
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
            setColors(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 340, 440, 40);
            setText(Assets.strings.get("AUTO REPLAYS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AutoReplaysButton extends Button {

        AutoReplaysButton() {
            setColors(0x2B4A61);
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
            setColors(0x76683C);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 390, 440, 40);
            setText(Assets.strings.get("ZOOM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ZoomButton extends Button {

        ZoomButton() {
            setColors(0x2B4A61);
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
            int zoomMin = 5 * (int) (20.0f * VISIBLE_FIELD_WIDTH_OPT / VISIBLE_FIELD_WIDTH_MAX);
            int zoomMax = 5 * (int) (20.0f * VISIBLE_FIELD_WIDTH_OPT / VISIBLE_FIELD_WIDTH_MIN);
            game.settings.zoom = Emath.slide(game.settings.zoom, zoomMin, zoomMax, 5 * n);
            setDirty(true);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText("EXIT", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }
}
