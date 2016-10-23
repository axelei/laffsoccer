package com.ysoccer.android.ysdemo;

import android.content.Intent;
import android.net.Uri;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Picture;
import com.ysoccer.android.ysdemo.gui.Widget;

public class MenuMain extends GLScreen {

    class GameSettingsButton extends Button {
        public GameSettingsButton() {
            setColors(0x536B90, 0x7090C2, 0x263142);
            setGeometry(Settings.GUI_WIDTH / 2 - 30 - 320, 290, 320, 36);
            setText(_(R.string.SETTINGS), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuSettings(game));
        }
    }

    class HelpButton extends Button {
        public HelpButton() {
            setColors(0x6101D7, 0x7D1DFF, 0x3A0181);
            setGeometry(Settings.GUI_WIDTH / 2 - 30 - 320, 340, 320, 36);
            setText(_(R.string.CONTROLS), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuHelp(game));
        }
    }

    class PlayButton extends Button {
        public PlayButton() {
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 290, 320, 36);
            setText(_(R.string.PLAY_MATCH), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuSelectTeams(game));
        }
    }

    class FullVersionButton extends Button {
        public FullVersionButton() {
            setColors(0x415600, 0x5E7D00, 0x243000);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 340, 320, 36);
            setText(_(R.string.PC_VERSION), 0, 14);
        }

        @Override
        public void onFire1Down() {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://ysoccer.sf.net"));
            glGame.startActivity(browserIntent);
        }
    }

    public MenuMain(Game game) {

        super(game);

        Assets.mainMenuBackground = new Texture(glGame,
                "images/backgrounds/menu_main.jpg");
        Assets.logo = new Texture(glGame, "images/logo.png");

        setBackground(Assets.mainMenuBackground);

        Widget w;

        w = new Picture();
        w.setTexture(Assets.logo);
        w.setGeometry((Settings.GUI_WIDTH - Assets.logo.width) / 2, 100,
                Assets.logo.width, Assets.logo.height);
        getWidgets().add(w);

        w = new GameSettingsButton();
        getWidgets().add(w);

        setSelectedWidget(w);

        w = new HelpButton();
        getWidgets().add(w);

        w = new PlayButton();
        getWidgets().add(w);

        w = new FullVersionButton();
        getWidgets().add(w);

    }

    @Override
    public void update(float deltaTime) {
        if (glGraphics.light < 255) {
            glGraphics.light = Math.min(glGraphics.light + 4, 255);
        } else {
            super.update(deltaTime);
        }
    }

    @Override
    public void resume() {
        super.resume();
        Assets.mainMenuBackground.reload();
        Assets.logo.reload();
        Assets.music.play();
    }

    @Override
    public boolean keyBack() {
        return false;
    }

}
