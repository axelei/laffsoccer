package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import static com.ygames.ysoccer.framework.Assets.gettext;

class TrainingHotKeys extends SceneHotKeys {

    private Training training;

    private boolean keyScreenMode;

    TrainingHotKeys(Training training) {
        this.training = training;
    }

    @Override
    public void update() {
        super.update();

        if (Gdx.input.isKeyPressed(Input.Keys.F7) && !keyScreenMode) {
            training.settings.fullScreen = !training.settings.fullScreen;
            training.game.setScreenMode(training.settings.fullScreen);

            if (training.settings.fullScreen) {
                message = gettext("SCREEN MODE.FULL SCREEN");
            } else {
                message = gettext("SCREEN MODE.WINDOW");
            }

            messageTimer = 120;
        }

        keyScreenMode = Gdx.input.isKeyPressed(Input.Keys.F7);
    }
}
