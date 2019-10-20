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
import com.ygames.ysoccer.framework.EMath;

import static com.badlogic.gdx.Application.LOG_DEBUG;
import static com.badlogic.gdx.Application.LOG_ERROR;
import static com.badlogic.gdx.Application.LOG_INFO;
import static com.badlogic.gdx.Application.LOG_NONE;
import static com.ygames.ysoccer.framework.GLGame.LogType.AI_ATTACKING;
import static com.ygames.ysoccer.framework.GLGame.LogType.AI_KICKING;
import static com.ygames.ysoccer.framework.GLGame.LogType.BALL;
import static com.ygames.ysoccer.framework.GLGame.LogType.GUI;
import static com.ygames.ysoccer.framework.GLGame.LogType.PASSING;
import static com.ygames.ysoccer.framework.GLGame.LogType.PLAYER_SELECTION;

class DeveloperOptions extends GLScreen {

    int x0, y0, labelWidth;

    DeveloperOptions(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;

        w = new TitleBar("DEVELOPER OPTIONS", 0x191FB0);
        widgets.add(w);

        labelWidth = 180;
        x0 = (game.gui.WIDTH) / 2 - 185;
        y0 = 160;

        w = new SectionLabel("GUI");
        widgets.add(w);

        y0 += 22;
        w = new JavaHeapLabel();
        widgets.add(w);
        w = new JavaHeapButton();
        widgets.add(w);
        setSelectedWidget(w);

        y0 += 22;
        w = new TeamValuesLabel();
        widgets.add(w);
        w = new TeamValuesButton();
        widgets.add(w);

        y0 += 44;
        w = new SectionLabel("MATCH");
        widgets.add(w);

        y0 += 22;
        w = new PlayerNumberLabel();
        widgets.add(w);
        w = new PlayerNumberButton();
        widgets.add(w);

        y0 += 22;
        w = new BestDefenderLabel();
        widgets.add(w);
        w = new BestDefenderButton();
        widgets.add(w);

        y0 += 22;
        w = new FrameDistanceLabel();
        widgets.add(w);
        w = new FrameDistanceButton();
        widgets.add(w);

        y0 += 22;
        w = new PlayerStateLabel();
        widgets.add(w);
        w = new PlayerStateButton();
        widgets.add(w);

        y0 += 22;
        w = new PlayerAiStateLabel();
        widgets.add(w);
        w = new PlayerAiStateButton();
        widgets.add(w);

        y0 += 22;
        w = new BallZonesLabel();
        widgets.add(w);
        w = new BallZonesButton();
        widgets.add(w);

        y0 += 22;
        w = new BallPredictionsLabel();
        widgets.add(w);
        w = new BallPredictionsButton();
        widgets.add(w);

        y0 += 44;
        w = new SectionLabel("LOGS");
        widgets.add(w);

        y0 += 22;
        w = new LogLevelLabel();
        widgets.add(w);
        w = new LogLevelButton();
        widgets.add(w);

        y0 += 22;
        w = new LogFilterLabel(AI_ATTACKING);
        widgets.add(w);
        w = new LogFilterButton(AI_ATTACKING);
        widgets.add(w);

        y0 += 22;
        w = new LogFilterLabel(AI_KICKING);
        widgets.add(w);
        w = new LogFilterButton(AI_KICKING);
        widgets.add(w);

        y0 += 22;
        w = new LogFilterLabel(BALL);
        widgets.add(w);
        w = new LogFilterButton(BALL);
        widgets.add(w);

        y0 += 22;
        w = new LogFilterLabel(PASSING);
        widgets.add(w);
        w = new LogFilterButton(PASSING);
        widgets.add(w);

        y0 += 22;
        w = new LogFilterLabel(GUI);
        widgets.add(w);
        w = new LogFilterButton(GUI);
        widgets.add(w);

        y0 += 22;
        w = new LogFilterLabel(PLAYER_SELECTION);
        widgets.add(w);
        w = new LogFilterButton(PLAYER_SELECTION);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class SectionLabel extends Button {

        SectionLabel(String text) {
            setColor(0x601D5F);
            setGeometry(x0, y0, labelWidth, 22);
            setText(text, Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class JavaHeapLabel extends Button {

        JavaHeapLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("JAVA HEAP", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class JavaHeapButton extends ToggleButton {

        JavaHeapButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showJavaHeap ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showJavaHeap ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showJavaHeap = !Settings.showJavaHeap;
            setDirty(true);
        }
    }

    private class TeamValuesLabel extends Button {

        TeamValuesLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("TEAM VALUES", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class TeamValuesButton extends ToggleButton {

        TeamValuesButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showTeamValues ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showTeamValues ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showTeamValues = !Settings.showTeamValues;
            setDirty(true);
        }
    }

    private class PlayerNumberLabel extends Button {

        PlayerNumberLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("PLAYER NUMBER", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class PlayerNumberButton extends ToggleButton {

        PlayerNumberButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showPlayerNumber ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showPlayerNumber ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showPlayerNumber = !Settings.showPlayerNumber;
            setDirty(true);
        }
    }

    private class BestDefenderLabel extends Button {

        BestDefenderLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("BEST DEFENDER", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class BestDefenderButton extends ToggleButton {

        BestDefenderButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showBestDefender ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showBestDefender ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showBestDefender = !Settings.showBestDefender;
            setDirty(true);
        }
    }

    private class FrameDistanceLabel extends Button {

        FrameDistanceLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("FRAME DISTANCE", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class FrameDistanceButton extends ToggleButton {

        FrameDistanceButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showFrameDistance ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showFrameDistance ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showFrameDistance = !Settings.showFrameDistance;
            setDirty(true);
        }
    }

    private class PlayerStateLabel extends Button {

        PlayerStateLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("PLAYER STATE", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class PlayerStateButton extends ToggleButton {

        PlayerStateButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showPlayerState ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showPlayerState ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showPlayerState = !Settings.showPlayerState;
            setDirty(true);
        }
    }

    private class PlayerAiStateLabel extends Button {

        PlayerAiStateLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("PLAYER AI STATE", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class PlayerAiStateButton extends ToggleButton {

        PlayerAiStateButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showPlayerAiState ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showPlayerAiState ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showPlayerAiState = !Settings.showPlayerAiState;
            setDirty(true);
        }
    }

    private class BallZonesLabel extends Button {

        BallZonesLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("BALL ZONES", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class BallZonesButton extends ToggleButton {

        BallZonesButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showBallZones ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showBallZones ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showBallZones = !Settings.showBallZones;
            setDirty(true);
        }
    }

    private class BallPredictionsLabel extends Button {

        BallPredictionsLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("BALL PREDICTIONS", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class BallPredictionsButton extends ToggleButton {

        BallPredictionsButton() {
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(Settings.showBallPredictions ? 0x1D6051 : 0x4D4D4D);
            setText(Settings.showBallPredictions ? "ON" : "OFF");
        }

        @Override
        protected void toggle() {
            Settings.showBallPredictions = !Settings.showBallPredictions;
            setDirty(true);
        }
    }

    private class LogLevelLabel extends Button {

        LogLevelLabel() {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText("LOG LEVEL", Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class LogLevelButton extends Button {

        LogLevelButton() {
            setColor(0x1D6051);
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
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
            Settings.logLevel = EMath.rotate(Settings.logLevel, 0, 3, n);
            Gdx.app.setLogLevel(Settings.logLevel);
            setDirty(true);
        }
    }


    private class LogFilterLabel extends Button {

        LogFilterLabel(GLGame.LogType logType) {
            setColor(0x4D4D4D);
            setGeometry(x0, y0, labelWidth, 22);
            setText(logType.toString().replace("_", "-"), Font.Align.CENTER, Assets.font6);
            setActive(false);
        }
    }

    private class LogFilterButton extends ToggleButton {

        private GLGame.LogType logType;

        LogFilterButton(GLGame.LogType logType) {
            this.logType = logType;
            setGeometry(x0 + 10 + labelWidth, y0, 180, 22);
            setText("", Font.Align.LEFT, Assets.font6);
        }

        @Override
        public void refresh() {
            setColor(GLGame.isSetLogFilter(logType) ? 0x1D6051 : 0x4D4D4D);
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
