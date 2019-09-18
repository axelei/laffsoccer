package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.framework.Assets.gettext;

abstract class SceneHotKeys {

    Scene scene;

    String message;
    long messageTimer;

    private boolean keySoundDown;
    private boolean keySoundUp;
    private boolean keyScreenMode;
    private boolean keyZoomOut;
    private boolean keyZoomIn;

    SceneHotKeys(Scene scene) {
        this.scene = scene;
    }

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

        if (Gdx.input.isKeyPressed(Input.Keys.F7) && !keyScreenMode) {
            scene.settings.fullScreen = !scene.settings.fullScreen;
            scene.game.setScreenMode(scene.settings.fullScreen);

            if (scene.settings.fullScreen) {
                message = gettext("SCREEN MODE.FULL SCREEN");
            } else {
                message = gettext("SCREEN MODE.WINDOW");
            }

            messageTimer = 120;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F8) && !keyZoomOut) {
            scene.settings.zoom = Emath.slide(scene.settings.zoom, SceneRenderer.zoomMin(), SceneRenderer.zoomMax(), -5);
            scene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            message = gettext("ZOOM") + " " + scene.settings.zoom + "%";

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F9) && !keyZoomIn) {
            scene.settings.zoom = Emath.slide(scene.settings.zoom, SceneRenderer.zoomMin(), SceneRenderer.zoomMax(), 5);
            scene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            message = gettext("ZOOM") + " " + scene.settings.zoom + "%";

            messageTimer = 60;
        }

        keySoundDown = Gdx.input.isKeyPressed(Input.Keys.F2);
        keySoundUp = Gdx.input.isKeyPressed(Input.Keys.F3);
        keyScreenMode = Gdx.input.isKeyPressed(Input.Keys.F7);
        keyZoomOut = Gdx.input.isKeyPressed(Input.Keys.F8);
        keyZoomIn = Gdx.input.isKeyPressed(Input.Keys.F9);
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
