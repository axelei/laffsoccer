package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;

class TrainingKeys {

    private Training training;

    String message;
    long messageTimer;

    private boolean keySoundUp;
    private boolean keySoundDown;
    private boolean keyScreenMode;

    TrainingKeys(Training training) {
        this.training = training;
    }

    public void update() {

        if (messageTimer > 0) messageTimer--;

        if (Gdx.input.isKeyPressed(Input.Keys.F2) && !keySoundDown) {
            training.settings.soundVolume = Math.max(0, training.settings.soundVolume - 10);
            Assets.Sounds.intro.setVolume(Assets.Sounds.introId, training.settings.soundVolume / 100f);
            Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, training.settings.soundVolume / 100f);

            setMessageSoundEffects();
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F3) && !keySoundUp) {
            training.settings.soundVolume = Math.min(100, training.settings.soundVolume + 10);
            Assets.Sounds.intro.setVolume(Assets.Sounds.introId, training.settings.soundVolume / 100f);
            Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, training.settings.soundVolume / 100f);

            setMessageSoundEffects();
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F7) && !keyScreenMode) {
            training.settings.fullScreen = !training.settings.fullScreen;
            training.game.setScreenMode(training.settings.fullScreen);

            if (training.settings.fullScreen) {
                message = Assets.strings.get("SCREEN MODE.FULL SCREEN");
            } else {
                message = Assets.strings.get("SCREEN MODE.WINDOW");
            }

            messageTimer = 120;
        }

        keySoundDown = Gdx.input.isKeyPressed(Input.Keys.F2);
        keySoundUp = Gdx.input.isKeyPressed(Input.Keys.F3);
        keyScreenMode = Gdx.input.isKeyPressed(Input.Keys.F7);
    }

    private void setMessageSoundEffects() {
        message = Assets.strings.get("MATCH OPTIONS.SOUND VOLUME") + " <";
        for (int i = 10; i <= 100; i += 10) {
            message += (i <= training.settings.soundVolume) ? "|" : " ";
        }
        message += ">";
    }
}
