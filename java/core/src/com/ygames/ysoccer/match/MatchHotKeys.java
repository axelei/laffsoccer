package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;

import static com.badlogic.gdx.Input.Keys.F10;
import static com.badlogic.gdx.Input.Keys.F4;
import static com.badlogic.gdx.Input.Keys.F5;
import static com.badlogic.gdx.Input.Keys.F9;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.InputDevice.keyDescription;

class MatchHotKeys extends SceneHotKeys {

    private boolean keyCommentary;
    private boolean keyCrowdChants;
    private boolean keyAutoReplay;
    private boolean keyRadar;
    private boolean keyRecordAction;

    MatchHotKeys(Match match) {
        super(match);

        String[] matchCommentary = {keyDescription(F4), gettext("HELP.MATCH COMMENTARY")};
        keyMap.put(4, matchCommentary);

        String[] crowdChants = {keyDescription(F5), gettext("HELP.CROWD CHANTS")};
        keyMap.put(5, crowdChants);

        String[] autoReplay = {keyDescription(F9), gettext("HELP.AUTO REPLAYS")};
        keyMap.put(10, autoReplay);

        String[] radar = {keyDescription(F10), gettext("HELP.RADAR")};
        keyMap.put(11, radar);

        String[] recordAction = {keyDescription(SPACE), gettext("HELP.RECORD ACTION")};
        keyMap.put(13, recordAction);

        String[] pause = {keyDescription(P), gettext("HELP.PAUSE")};
        keyMap.put(14, pause);
    }

    private Match getMatch() {
        return (Match) scene;
    }

    @Override
    public void update() {
        super.update();

        if (Gdx.input.isKeyPressed(F4) && !keyCommentary) {
            scene.settings.commentary = !scene.settings.commentary;

            message = gettext("MATCH OPTIONS.COMMENTARY") + " ";
            if (scene.settings.commentary) {
                message += gettext("MATCH OPTIONS.COMMENTARY.ON");
            } else {
                message += gettext("MATCH OPTIONS.COMMENTARY.OFF");
            }
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(F5) && !keyCrowdChants) {
            getMatch().getSettings().crowdChants = !getMatch().getSettings().crowdChants;
            Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);

            message = gettext("MATCH OPTIONS.CROWD CHANTS") + " ";
            if (getMatch().getSettings().crowdChants) {
                message += gettext("MATCH OPTIONS.CROWD CHANTS.ON");
            } else {
                message += gettext("MATCH OPTIONS.CROWD CHANTS.OFF");
            }
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(F9) && !keyAutoReplay) {
            getMatch().getSettings().autoReplays = !getMatch().getSettings().autoReplays;

            message = gettext("AUTO REPLAYS") + " ";

            if (getMatch().getSettings().autoReplays) {
                message += gettext("AUTO REPLAYS.ON");
            } else {
                message += gettext("AUTO REPLAYS.OFF");
            }

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(F10) && !keyRadar) {
            getMatch().getSettings().radar = !getMatch().getSettings().radar;

            message = gettext("RADAR") + " ";
            if (getMatch().getSettings().radar) {
                message += gettext("RADAR.ON");
            } else {
                message += gettext("RADAR.OFF");
            }

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(SPACE) && !keyRecordAction) {
            getMatch().recorder.saveHighlight(scene.getRenderer());

            message = gettext("ACTION RECORDED");
            messageTimer = 60;
        }

        keyCommentary = Gdx.input.isKeyPressed(F4);
        keyCrowdChants = Gdx.input.isKeyPressed(F5);
        keyAutoReplay = Gdx.input.isKeyPressed(F9);
        keyRadar = Gdx.input.isKeyPressed(F10);
        keyRecordAction = Gdx.input.isKeyPressed(SPACE);
    }


    @Override
    void onChangeVolume() {
        super.onChangeVolume();

        Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
        Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);
    }
}
