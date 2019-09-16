package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.framework.Assets.gettext;

class MatchHotKeys extends SceneHotKeys {

    Match match;

    private boolean keyRecordAction;
    private boolean keyScreenMode;
    private boolean keyZoomOut;
    private boolean keyZoomIn;
    private boolean keyCommentary;
    private boolean keyCrowdChants;
    private boolean keyRadar;
    private boolean keyAutoReplay;

    MatchHotKeys(Match match) {
        this.match = match;
    }

    @Override
    public void update() {
        super.update();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !keyRecordAction) {
            match.recorder.saveHighlight(match.getRenderer());

            message = gettext("ACTION RECORDED");
            messageTimer = 60;
        }


        if (Gdx.input.isKeyPressed(Input.Keys.F4) && !keyCommentary) {
            match.settings.commentary = !match.settings.commentary;

            message = gettext("MATCH OPTIONS.COMMENTARY") + " ";
            if (match.settings.commentary) {
                message += gettext("MATCH OPTIONS.COMMENTARY.ON");
            } else {
                message += gettext("MATCH OPTIONS.COMMENTARY.OFF");
            }
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && !keyCrowdChants) {
            match.getSettings().crowdChants = !match.getSettings().crowdChants;
            Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);

            message = gettext("MATCH OPTIONS.CROWD CHANTS") + " ";
            if (match.getSettings().crowdChants) {
                message += gettext("MATCH OPTIONS.CROWD CHANTS.ON");
            } else {
                message += gettext("MATCH OPTIONS.CROWD CHANTS.OFF");
            }
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F7) && !keyScreenMode) {
            match.settings.fullScreen = !match.settings.fullScreen;
            match.game.setScreenMode(match.settings.fullScreen);

            if (match.settings.fullScreen) {
                message = gettext("SCREEN MODE.FULL SCREEN");
            } else {
                message = gettext("SCREEN MODE.WINDOW");
            }

            messageTimer = 120;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F8) && !keyZoomOut) {
            match.settings.zoom = Emath.slide(match.settings.zoom, SceneRenderer.zoomMin(), SceneRenderer.zoomMax(), -5);
            match.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            message = gettext("ZOOM") + " " + match.settings.zoom + "%";

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F9) && !keyZoomIn) {
            match.settings.zoom = Emath.slide(match.settings.zoom, SceneRenderer.zoomMin(), SceneRenderer.zoomMax(), 5);
            match.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            message = gettext("ZOOM") + " " + match.settings.zoom + "%";

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F11) && !keyRadar) {
            match.getSettings().radar = !match.getSettings().radar;

            message = gettext("RADAR") + " ";
            if (match.getSettings().radar) {
                message += gettext("RADAR.ON");
            } else {
                message += gettext("RADAR.OFF");
            }

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F12) && !keyAutoReplay) {
            match.getSettings().autoReplays = !match.getSettings().autoReplays;

            message = gettext("AUTO REPLAYS") + " ";

            if (match.getSettings().autoReplays) {
                message += gettext("AUTO REPLAYS.ON");
            } else {
                message += gettext("AUTO REPLAYS.OFF");
            }

            messageTimer = 60;
        }

        keyRecordAction = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        keyCommentary = Gdx.input.isKeyPressed(Input.Keys.F4);
        keyCrowdChants = Gdx.input.isKeyPressed(Input.Keys.F5);
        keyScreenMode = Gdx.input.isKeyPressed(Input.Keys.F7);
        keyZoomOut = Gdx.input.isKeyPressed(Input.Keys.F8);
        keyZoomIn = Gdx.input.isKeyPressed(Input.Keys.F9);
        keyRadar = Gdx.input.isKeyPressed(Input.Keys.F11);
        keyAutoReplay = Gdx.input.isKeyPressed(Input.Keys.F12);
    }


    @Override
    void onChangeVolume() {
        super.onChangeVolume();

        Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
        Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);
    }
}
