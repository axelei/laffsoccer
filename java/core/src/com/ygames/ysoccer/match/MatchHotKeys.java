package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;

import static com.ygames.ysoccer.framework.Assets.gettext;

class MatchHotKeys extends SceneHotKeys {

    private boolean keyRecordAction;
    private boolean keyCommentary;
    private boolean keyCrowdChants;
    private boolean keyRadar;
    private boolean keyAutoReplay;

    MatchHotKeys(Match match) {
        super(match);
    }

    private Match getMatch() {
        return (Match) scene;
    }

    @Override
    public void update() {
        super.update();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !keyRecordAction) {
            getMatch().recorder.saveHighlight(scene.getRenderer());

            message = gettext("ACTION RECORDED");
            messageTimer = 60;
        }


        if (Gdx.input.isKeyPressed(Input.Keys.F4) && !keyCommentary) {
            scene.settings.commentary = !scene.settings.commentary;

            message = gettext("MATCH OPTIONS.COMMENTARY") + " ";
            if (scene.settings.commentary) {
                message += gettext("MATCH OPTIONS.COMMENTARY.ON");
            } else {
                message += gettext("MATCH OPTIONS.COMMENTARY.OFF");
            }
            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && !keyCrowdChants) {
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


        if (Gdx.input.isKeyPressed(Input.Keys.F11) && !keyRadar) {
            getMatch().getSettings().radar = !getMatch().getSettings().radar;

            message = gettext("RADAR") + " ";
            if (getMatch().getSettings().radar) {
                message += gettext("RADAR.ON");
            } else {
                message += gettext("RADAR.OFF");
            }

            messageTimer = 60;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F12) && !keyAutoReplay) {
            getMatch().getSettings().autoReplays = !getMatch().getSettings().autoReplays;

            message = gettext("AUTO REPLAYS") + " ";

            if (getMatch().getSettings().autoReplays) {
                message += gettext("AUTO REPLAYS.ON");
            } else {
                message += gettext("AUTO REPLAYS.OFF");
            }

            messageTimer = 60;
        }

        keyRecordAction = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        keyCommentary = Gdx.input.isKeyPressed(Input.Keys.F4);
        keyCrowdChants = Gdx.input.isKeyPressed(Input.Keys.F5);
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
