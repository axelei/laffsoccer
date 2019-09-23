package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.ToggleButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

import static com.badlogic.gdx.Application.LOG_DEBUG;
import static com.badlogic.gdx.Application.LOG_ERROR;
import static com.badlogic.gdx.Application.LOG_INFO;
import static com.badlogic.gdx.Application.LOG_NONE;
import static com.ygames.ysoccer.framework.GLGame.LogType.ATTACKING_AI;
import static com.ygames.ysoccer.framework.GLGame.LogType.PASSING;
import static com.ygames.ysoccer.framework.GLGame.LogType.PLAYER_SELECTION;

class DeveloperOptions extends GLScreen {

    DeveloperOptions(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;

        w = new TitleBar("DEVELOPER OPTIONS", 0x191FB0);
        widgets.add(w);

        int y = 180;

        w = new SectionLabel("GUI", y);
        widgets.add(w);

        y += 22;
        w = new JavaHeapLabel(y);
        widgets.add(w);
        w = new JavaHeapButton(y);
        widgets.add(w);

        y += 22;
        w = new TeamValuesLabel(y);
        widgets.add(w);
        w = new TeamValuesButton(y);
        widgets.add(w);

        y += 44;
        w = new SectionLabel("MATCH", y);
        widgets.add(w);

        y += 22;
        w = new PlayerNumberLabel(y);
        widgets.add(w);
        w = new PlayerNumberButton(y);
        widgets.add(w);

        y += 22;
        w = new PlayerStateLabel(y);
        widgets.add(w);
        w = new PlayerStateButton(y);
        widgets.add(w);

        y += 22;
        w = new PlayerAiStateLabel(y);
        widgets.add(w);
        w = new PlayerAiStateButton(y);
        widgets.add(w);

        y += 22;
        w = new BallZonesLabel(y);
        widgets.add(w);
        w = new BallZonesButton(y);
        widgets.add(w);

        y += 22;
        w = new BallPredictionsLabel(y);
        widgets.add(w);
        w = new BallPredictionsButton(y);
        widgets.add(w);

        y += 44;
        w = new SectionLabel("LOGS", y);
        widgets.add(w);

        y += 22;
        w = new LogLevelLabel(y);
        widgets.add(w);
        w = new LogLevelButton(y);
        widgets.add(w);

        y += 22;
        w = new LogFilterLabel(ATTACKING_AI, y);
        widgets.add(w);
        w = new LogFilterButton(ATTACKING_AI, y);
        widgets.add(w);

        y += 22;
        w = new LogFilterLabel(PASSING, y);
        widgets.add(w);
        w = new LogFilterButton(PASSING, y);
        widgets.add(w);

        y += 22;
        w = new LogFilterLabel(PLAYER_SELECTION, y);
        widgets.add(w);
        w = new LogFilterButton(PLAYER_SELECTION, y);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class SectionLabel extends Button {

        SectionLabel(String text, int y) {
            setColor(0x601D5F);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText(text, Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class JavaHeapLabel extends Button {

        JavaHeapLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("JAVA HEAP", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class JavaHeapButton extends ToggleButton {

        JavaHeapButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(Settings.showJavaHeap ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showJavaHeap = !Settings.showJavaHeap;
            setDirty(true);
        }
    }

    private class TeamValuesLabel extends Button {

        TeamValuesLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("TEAM VALUES", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class TeamValuesButton extends ToggleButton {

        TeamValuesButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(Settings.showTeamValues ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showTeamValues = !Settings.showTeamValues;
            setDirty(true);
        }
    }

    private class PlayerNumberLabel extends Button {

        PlayerNumberLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("PLAYER NUMBER", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class PlayerNumberButton extends ToggleButton {

        PlayerNumberButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(Settings.showPlayerNumber ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showPlayerNumber = !Settings.showPlayerNumber;
            setDirty(true);
        }
    }

    private class PlayerStateLabel extends Button {

        PlayerStateLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("PLAYER STATE", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class PlayerStateButton extends ToggleButton {

        PlayerStateButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(Settings.showPlayerState ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showPlayerState = !Settings.showPlayerState;
            setDirty(true);
        }
    }

    private class PlayerAiStateLabel extends Button {

        PlayerAiStateLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("PLAYER AI STATE", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class PlayerAiStateButton extends ToggleButton {

        PlayerAiStateButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(Settings.showPlayerAiState ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showPlayerAiState = !Settings.showPlayerAiState;
            setDirty(true);
        }
    }

    private class BallZonesLabel extends Button {

        BallZonesLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("BALL ZONES", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class BallZonesButton extends ToggleButton {

        BallZonesButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(Settings.showBallZones ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showBallZones = !Settings.showBallZones;
            setDirty(true);
        }
    }

    private class BallPredictionsLabel extends Button {

        BallPredictionsLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("BALL PREDICTIONS", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class BallPredictionsButton extends ToggleButton {

        BallPredictionsButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(Settings.showBallPredictions ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showBallPredictions = !Settings.showBallPredictions;
            setDirty(true);
        }
    }

    private class LogLevelLabel extends Button {

        LogLevelLabel(int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText("LOG LEVEL", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class LogLevelButton extends Button {

        LogLevelButton(int y) {
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            switch (Settings.logLevel) {
                case LOG_NONE:
                    setText("0: NONE");
                    break;

                case LOG_ERROR:
                    setText("1: ERRORS");
                    break;

                case LOG_INFO:
                    setText("2: ERRORS + INFO");
                    break;

                case LOG_DEBUG:
                    setText("3: ERRORS + INFO + DEBUG");
                    break;

            }
        }

        @Override
        public void onFire1Down() {
            rotate(1);
        }

        @Override
        public void onFire2Down() {
            rotate(-1);
        }

        private void rotate(int n) {
            Settings.logLevel = Emath.rotate(Settings.logLevel, 0, 3, n);
            Gdx.app.setLogLevel(Settings.logLevel);
            setDirty(true);
        }
    }


    private class LogFilterLabel extends Button {

        LogFilterLabel(GLGame.LogType logType, int y) {
            setColor(0x4D4D4D);
            setGeometry(game.gui.WIDTH / 2 - 10 - 240, y, 240, 22);
            setText(logType.toString().replace("_","-"), Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class LogFilterButton extends ToggleButton {

        private GLGame.LogType logType;

        LogFilterButton(GLGame.LogType logType, int y) {
            this.logType = logType;
            setColor(0x1D6051);
            setGeometry(game.gui.WIDTH / 2 + 10, y, 220, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setText(GLGame.isSetLogFilter(logType) ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            GLGame.toggleLogFilter(logType);
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
            game.setScreen(new DeveloperTools(game));
        }
    }
}
