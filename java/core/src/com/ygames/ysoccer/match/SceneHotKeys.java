package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;

import static com.ygames.ysoccer.framework.Assets.gettext;

abstract class SceneHotKeys {

    String message;
    long messageTimer;

    private boolean keySoundUp;
    private boolean keySoundDown;

    public void update() {
        if (messageTimer > 0) messageTimer--;

        if (Gdx.input.isKeyPressed(Input.Keys.F2) && !keySoundDown) {
            Assets.Sounds.volume = Math.max(0, Assets.Sounds.volume - 10);

            onChangeVolume();

            setMessageSoundEffects();
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F3) && !keySoundUp) {
            Assets.Sounds.volume = Math.min(100, Assets.Sounds.volume + 10);

            onChangeVolume();

            setMessageSoundEffects();
            messageTimer = 60;
        }

        keySoundDown = Gdx.input.isKeyPressed(Input.Keys.F2);
        keySoundUp = Gdx.input.isKeyPressed(Input.Keys.F3);
    }

    void onChangeVolume() {
    }

    private void setMessageSoundEffects() {
        message = gettext("MATCH OPTIONS.SOUND VOLUME") + " <";
        for (int i = 10; i <= 100; i += 10) {
            message += (i <= Assets.Sounds.volume) ? "|" : " ";
        }
        message += ">";
    }
}
