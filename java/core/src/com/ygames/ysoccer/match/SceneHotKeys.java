package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.Settings;

import java.util.TreeMap;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.F12;
import static com.badlogic.gdx.Input.Keys.F2;
import static com.badlogic.gdx.Input.Keys.F3;
import static com.badlogic.gdx.Input.Keys.F6;
import static com.badlogic.gdx.Input.Keys.F7;
import static com.badlogic.gdx.Input.Keys.F8;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.InputDevice.keyDescription;

abstract class SceneHotKeys {

    final Scene scene;

    String message;
    long messageTimer;

    private boolean keySoundDown;
    private boolean keySoundUp;
    private boolean keyScreenMode;
    private boolean keyZoomIn;
    private boolean keyZoomOut;
    private boolean keyDevelopmentInfo;

    final TreeMap<Integer, String[]> keyMap = new TreeMap<>();

    SceneHotKeys(Scene scene) {
        this.scene = scene;

        String[] keySoundInfo = {keyDescription(F2) + "-" + keyDescription(F3), gettext("HELP.SOUND FX VOLUME")};
        keyMap.put(2, keySoundInfo);

        String[] keyScreenModeInfo = {keyDescription(F6), gettext("HELP.WINDOW / FULL SCREEN")};
        keyMap.put(7, keyScreenModeInfo);

        String[] keyZoomInfo = {keyDescription(F7) + "-" + keyDescription(F8), gettext("HELP.ZOOM IN / OUT")};
        keyMap.put(8, keyZoomInfo);

        String[] replay = {keyDescription(R), gettext("ACTION REPLAY")};
        keyMap.put(15, replay);

        String[] escape = {keyDescription(ESCAPE), gettext("HELP.QUIT")};
        keyMap.put(16, escape);
    }

    public void update() {
        if (messageTimer > 0) messageTimer--;

        if (Gdx.input.isKeyPressed(F2) && !keySoundDown) {
            Assets.Sounds.volume = Math.max(0, Assets.Sounds.volume - 10);

            onChangeVolume();

            setMessageSoundEffects();
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(F3) && !keySoundUp) {
            Assets.Sounds.volume = Math.min(100, Assets.Sounds.volume + 10);

            onChangeVolume();

            setMessageSoundEffects();
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(F6) && !keyScreenMode) {
            scene.settings.fullScreen = !scene.settings.fullScreen;
            scene.game.setScreenMode(scene.settings.fullScreen);

            if (scene.settings.fullScreen) {
                message = gettext("SCREEN MODE.FULL SCREEN");
            } else {
                message = gettext("SCREEN MODE.WINDOW");
            }

            messageTimer = 120;
        }

        if (Gdx.input.isKeyPressed(F7) && !keyZoomIn) {
            scene.settings.zoom = EMath.slide(scene.settings.zoom, SceneRenderer.zoomMin(), SceneRenderer.zoomMax(), +5);
            scene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            message = gettext("ZOOM") + " " + scene.settings.zoom + "%";

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(F8) && !keyZoomOut) {
            scene.settings.zoom = EMath.slide(scene.settings.zoom, SceneRenderer.zoomMin(), SceneRenderer.zoomMax(), -5);
            scene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            message = gettext("ZOOM") + " " + scene.settings.zoom + "%";

            messageTimer = 60;
        }

        if (Settings.development && Gdx.input.isKeyPressed(F12) && !keyDevelopmentInfo) {
            Settings.showDevelopmentInfo = !Settings.showDevelopmentInfo;

            message = "DEVELOPMENT INFO " + (Settings.showDevelopmentInfo ? "ON" : "OFF");

            messageTimer = 60;
        }

        keySoundDown = Gdx.input.isKeyPressed(F2);
        keySoundUp = Gdx.input.isKeyPressed(F3);
        keyScreenMode = Gdx.input.isKeyPressed(F6);
        keyZoomIn = Gdx.input.isKeyPressed(F7);
        keyZoomOut = Gdx.input.isKeyPressed(F8);
        keyDevelopmentInfo = Gdx.input.isKeyPressed(F12);
    }

    void onChangeVolume() {
    }

    private void setMessageSoundEffects() {
        StringBuilder sb = new StringBuilder(gettext("MATCH OPTIONS.SOUND VOLUME"));
        sb.append(" <");
        for (int i = 10; i <= 100; i += 10) {
            sb.append((i <= Assets.Sounds.volume) ? "|" : " ");
        }
        sb.append(">");
        message = sb.toString();
    }
}
