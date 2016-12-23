package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;

class SetupTraining extends GLScreen {

    private MatchSettings matchSettings;
    private TimePicture timePicture;

    SetupTraining(GLGame game) {
        super(game);

        matchSettings = new MatchSettings(game.settings);

        background = new Texture("images/backgrounds/menu_training.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("TRAINING"), game.stateColor.body);
        widgets.add(w);

        w = new TimeLabel();
        widgets.add(w);

        timePicture = new TimePicture();
        widgets.add(timePicture);

        w = new TimeButton();
        widgets.add(w);
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setColors(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 300 + 25, 130 - 40 / 2, 300, 40);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimePicture extends Button {

        TimePicture() {
            setColors(0x666666);
            setGeometry(game.gui.WIDTH / 2 - 300 - 65, 130 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = Assets.lightIcons[matchSettings.time.ordinal()];
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 65, 130 - 40 / 2, 300, 40);
            setColors(0x1F1F95);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(MatchSettings.getTimeLabel(matchSettings.time)));
        }

        @Override
        public void onFire1Down() {
            rotateTime(1);
        }

        @Override
        public void onFire2Down() {
            rotateTime(-1);
        }

        private void rotateTime(int n) {
            matchSettings.rotateTime(n);
            setDirty(true);
            timePicture.setDirty(true);
        }
    }
}
