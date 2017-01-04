package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Picture;
import com.ysoccer.android.ysdemo.gui.Widget;

class MenuHelp extends GLScreen {

    private Texture directionTexture;
    private Texture ballTexture;
    private Texture leftHandTexture;
    private Texture rightHandTexture;

    private DirectionsPicture directionsPicture;
    private BallPicture ballPicture;

    MenuHelp(Game game) {
        super(game);

        Assets.helpMenuBackground = new Texture(glGame, "images/backgrounds/menu_help.png");

        setBackground(Assets.helpMenuBackground);

        Widget w;

        directionTexture = new Texture(glGame, "images/controls_directions.png");
        directionsPicture = new DirectionsPicture();
        getWidgets().add(directionsPicture);

        ballTexture = new Texture(glGame, "images/controls_ball.png");
        ballPicture = new BallPicture();
        getWidgets().add(ballPicture);

        leftHandTexture = new Texture(glGame, "images/controls_left_hand.png");
        w = new LeftHandPicture();
        getWidgets().add(w);

        rightHandTexture = new Texture(glGame, "images/controls_right_hand.png");
        w = new RightHandPicture();
        getWidgets().add(w);

        w = new SwapButton();
        getWidgets().add(w);

        w = new BackButton();
        getWidgets().add(w);

        setSelectedWidget(w);
    }

    @Override
    public void resume() {
        super.resume();
        directionTexture.reload();
        ballTexture.reload();
        leftHandTexture.reload();
        rightHandTexture.reload();
        Assets.helpMenuBackground.reload();
    }

    @Override
    public void dispose() {
        super.dispose();
        directionTexture.dispose();
        ballTexture.dispose();
        leftHandTexture.dispose();
        rightHandTexture.dispose();
        Assets.helpMenuBackground.dispose();
    }

    @Override
    public void onKeyBackHw() {
        game.setScreen(new MenuMain(game));
    }

    private class BallPicture extends Picture {

        BallPicture() {
            setTexture(ballTexture);
            setSize(ballTexture.width, ballTexture.height);
            setFrame(ballTexture.width, ballTexture.height, 0, 0);
        }

        @Override
        public void refresh() {
            setPosition(glGame.settings.invertedControls ? (3 * Settings.GUI_WIDTH / 4 - 65) : (Settings.GUI_WIDTH / 4 - 65), Settings.GUI_HEIGHT / 2 - 55);
        }
    }

    private class DirectionsPicture extends Picture {

        DirectionsPicture() {
            setTexture(directionTexture);
            setSize(directionTexture.width, directionTexture.height);
            setFrame(directionTexture.width, directionTexture.height, 0, 0);
        }

        @Override
        public void refresh() {
            setPosition(glGame.settings.invertedControls ? (Settings.GUI_WIDTH / 4 - 130) : (3 * Settings.GUI_WIDTH / 4 - 130), Settings.GUI_HEIGHT / 2 - 110);
        }
    }

    private class LeftHandPicture extends Picture {

        LeftHandPicture() {
            setTexture(leftHandTexture);
            setGeometry(-leftHandTexture.width + 280, Settings.GUI_HEIGHT / 2 - 90, leftHandTexture.width, leftHandTexture.height);
        }
    }

    private class RightHandPicture extends Picture {

        RightHandPicture() {
            setTexture(rightHandTexture);
            setGeometry(Settings.GUI_WIDTH - 280, Settings.GUI_HEIGHT / 2 - 90, rightHandTexture.width, rightHandTexture.height);
        }
    }

    private class SwapButton extends Button {

        SwapButton() {
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setGeometry((Settings.GUI_WIDTH - 90) / 2, (Settings.GUI_HEIGHT - 55) / 2, 90, 70);
            setText("< >", 0, 14);
        }

        public void onFire1Down() {
            glGame.settings.invertedControls = !glGame.settings.invertedControls;
            directionsPicture.setDirty(true);
            ballPicture.setDirty(true);
        }
    }

    private class BackButton extends Button {

        BackButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((Settings.GUI_WIDTH - 180) / 2, Settings.GUI_HEIGHT - 40 - 20, 180, 40);
            setText(gettext(R.string.BACK), 0, 14);
        }

        public void onFire1Down() {
            game.setScreen(new MenuMain(game));
        }
    }
}
