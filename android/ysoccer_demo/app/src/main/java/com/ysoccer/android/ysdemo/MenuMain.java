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

        GameSettingsButton() {
            setColors(0x536B90, 0x7090C2, 0x263142);
            setGeometry(Settings.GUI_WIDTH / 2 - 30 - 320, 280, 320, 42);
            setText(gettext(R.string.SETTINGS), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuSettings(game));
        }
    }

    class PlayButton extends Button {

        PlayButton() {
            setColors(0x376E2F, 0x4E983F, 0x214014);
            setGeometry(Settings.GUI_WIDTH / 2 + 30, 280, 320, 42);
            setText(gettext(R.string.PLAY_MATCH), 0, 14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MenuSelectTeams(game));
        }
    }

    class FullVersionButton extends Button {

        FullVersionButton() {
            setColors(0x1B8A7F, 0x25BDAE, 0x115750);
            setGeometry(Settings.GUI_WIDTH / 2 -170, 380, 320, 36);
            setText(gettext(R.string.PC_VERSION), 0, 14);
        }

        @Override
        public void onFire1Down() {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://ysoccer.sf.net"));
            glGame.startActivity(browserIntent);
        }
    }

    MenuMain(Game game) {

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
