package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.SpriteBatcher;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.match.Match;
import com.ysoccer.android.ysdemo.match.Weather;

import javax.microedition.khronos.opengles.GL10;

public class MatchScreen extends GLScreen {

    private Match match;
    private boolean matchStarted;
    private boolean matchPaused;
    private final ContinueButton continueButton;
    private final ExitButton exitButton;

    private float timer;

    class ContinueButton extends Button {

        ContinueButton() {
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setGeometry((Settings.GUI_WIDTH - 240) / 2,
                    (Settings.GUI_HEIGHT) / 2 - 50 - 10, 240, 50);
            setText(gettext(R.string.CONTINUE), 0, 14);
        }

        @Override
        public void onFire1Down() {
            matchPaused = false;
        }
    }

    class ExitButton extends Button {

        ExitButton() {
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setGeometry((Settings.GUI_WIDTH - 240) / 2,
                    Settings.GUI_HEIGHT / 2 + 10, 240, 50);
            setText(gettext(R.string.EXIT), 0, 14);
        }

        @Override
        public void onFire1Down() {
            onKeyBackHw();
        }
    }

    MatchScreen(Game game, Match match) {
        super(game);
        this.match = match;
        matchStarted = false;
        matchPaused = false;

        getWidgets().add(continueButton = new ContinueButton());
        getWidgets().add(exitButton = new ExitButton());
        setSelectedWidget(continueButton);

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // TODO: wait texture loading instead
        timer += deltaTime;
        if (!matchStarted && timer > 2.0f) {
            match.start();
            matchStarted = true;
        }

        if (!matchPaused) {
            match.update(deltaTime);
        }

        if (keyBackHw) {
            onKeyBackHw();
        }
    }

    @Override
    public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();
        gl.glEnable(GL10.GL_TEXTURE_2D);

        match.renderer.render(glGame);

        if (matchPaused) {
            continueButton.isActive = true;
            exitButton.isActive = true;

            glGraphics.fadeRect(-Settings.guiOriginX, -Settings.guiOriginY,
                    Settings.screenWidth, Settings.screenHeight, 0.35f,
                    0x000000);

            super.present(deltaTime);
        } else {
            continueButton.isActive = false;
            exitButton.isActive = false;
        }

    }

    @Override
    public void resume() {
        super.resume();

        Assets.music.stop();
        glGame.getAudio().resumeSfx();

        Assets.loadStadium(glGame, match.settings);

        for (int t = 0; t < 2; t++) {
            for (int c = 0; c < 10; c++) {
                if (Assets.player[t][c] != null) {
                    Assets.player[t][c].reload();
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            if (Assets.playerShadows[i] != null) {
                Assets.playerShadows[i].reload();
            }
        }
        Assets.keeperShadow.reload();

        if (match.settings.weatherStrength != Weather.Strength.NONE) {
            switch (match.settings.weatherEffect) {
                case Weather.WIND:
                    Assets.wind.reload();
                    break;

                case Weather.RAIN:
                    Assets.rain.reload();
                    break;

                case Weather.SNOW:
                    Assets.snow.reload();
                    break;

                case Weather.FOG:
                    Assets.fog.reload();
                    break;
            }
        }

        if (glGame.settings.displayRadar) {
            Assets.playerTinyNumbers.reload();
        }

        Assets.ball.reload();
        Assets.flagposts.reload();
        Assets.goalTopA.reload();
        Assets.goalTopB.reload();
        Assets.goalBottom.reload();
        Assets.jumper.reload();
        Assets.playerNumber.reload();
        Assets.time.reload();
        Assets.score.reload();
        Assets.replay.reload();

        Assets.teamFlags[0].reload();
        Assets.teamFlags[1].reload();

        if (glGame.settings.sfxVolume > 0) {
            Assets.crowdSound.play();
        }

        Assets.crowd.reload();

        if (glGame.gamepadInput == null) {
            Assets.joystick.reload();
        }
    }

    @Override
    public void pause() {
        Assets.crowdSound.stop();
        glGame.getAudio().pauseSfx();
        matchPaused = true;
    }

    @Override
    public void dispose() {
        super.dispose();

        for (int t = 0; t < 2; t++) {
            for (int c = 0; c < 10; c++) {
                if (Assets.player[t][c] != null) {
                    Assets.player[t][c].dispose();
                }
            }
        }
    }

    @Override
    public void onKeyBackHw() {
        if (!matchPaused) {
            matchPaused = true;
            keyBackHw = false;
        } else {
            glGame.setScreen(new MenuMain(glGame));
        }
    }

}
