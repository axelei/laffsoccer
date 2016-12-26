package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;

class MatchKeys {

    Match match;

    // onscreen messages
    String message;
    long messageTimer;

    private boolean keyRecordAction;
    private boolean keySoundUp;
    private boolean keySoundDown;
    private boolean keyScreenMode;
    private boolean keyCommentary;
    private boolean keyCrowdChants;
    private boolean keyRadar;
    private boolean keyAutoReplay;

    MatchKeys(Match match) {
        this.match = match;
    }

    public void update() {

        if (messageTimer > 0)  messageTimer--;

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !keyRecordAction) {
            match.recorder.saveHighlight(match.fsm.getMatchRenderer());

            message = Assets.strings.get("ACTION RECORDED");
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F2) && !keySoundDown) {
            Assets.Sounds.volume = Math.max(0, Assets.Sounds.volume - 10);
            Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);

            setMessageSoundEffects();
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F3) && !keySoundUp) {
            Assets.Sounds.volume = Math.min(100, Assets.Sounds.volume + 10);
            Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);

            setMessageSoundEffects();
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F4) && !keyCommentary) {
            match.settings.commentary = !match.settings.commentary;

            message = Assets.strings.get("MATCH OPTIONS.COMMENTARY") + " ";
            if (match.settings.commentary) {
                message += Assets.strings.get("MATCH OPTIONS.COMMENTARY.ON");
            } else {
                message += Assets.strings.get("MATCH OPTIONS.COMMENTARY.OFF");
            }
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && !keyCrowdChants) {
            match.settings.crowdChants = !match.settings.crowdChants;
            Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);

            message = Assets.strings.get("MATCH OPTIONS.CROWD CHANTS") + " ";
            if (match.settings.crowdChants) {
                message += Assets.strings.get("MATCH OPTIONS.CROWD CHANTS.ON");
            } else {
                message += Assets.strings.get("MATCH OPTIONS.CROWD CHANTS.OFF");
            }
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F7) && !keyScreenMode) {
            match.settings.fullScreen = !match.settings.fullScreen;
            match.game.setScreenMode(match.settings.fullScreen);

            if (match.settings.fullScreen) {
                message = Assets.strings.get("SCREEN MODE.FULL SCREEN");
            } else {
                message = Assets.strings.get("SCREEN MODE.WINDOW");
            }

            messageTimer = 120;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F11) && !keyRadar) {
            match.settings.radar = !match.settings.radar;

            message = Assets.strings.get("RADAR") + " ";
            if (match.settings.radar) {
                message += Assets.strings.get("RADAR.ON");
            } else {
                message += Assets.strings.get("RADAR.OFF");
            }

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F12) && !keyAutoReplay) {
            match.settings.autoReplays = !match.settings.autoReplays;

            message = Assets.strings.get("AUTO REPLAYS") + " ";

            if (match.settings.autoReplays) {
                message += Assets.strings.get("AUTO REPLAYS.ON");
            } else {
                message += Assets.strings.get("AUTO REPLAYS.OFF");
            }

            messageTimer = 60;
        }

        keyRecordAction = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        keySoundDown = Gdx.input.isKeyPressed(Input.Keys.F2);
        keySoundUp = Gdx.input.isKeyPressed(Input.Keys.F3);
        keyCommentary = Gdx.input.isKeyPressed(Input.Keys.F4);
        keyCrowdChants = Gdx.input.isKeyPressed(Input.Keys.F5);
        keyScreenMode = Gdx.input.isKeyPressed(Input.Keys.F7);
        keyRadar = Gdx.input.isKeyPressed(Input.Keys.F11);
        keyAutoReplay = Gdx.input.isKeyPressed(Input.Keys.F12);
    }

    private void setMessageSoundEffects() {
        message = Assets.strings.get("MATCH OPTIONS.SOUND VOLUME") + " <";
        for (int i = 10; i <= 100; i += 10) {
            message += (i <= Assets.Sounds.volume) ? "|" : " ";
        }
        message += ">";
    }
}
