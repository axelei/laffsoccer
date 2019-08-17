package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.impl.GLScreen;

public class MenuIntro extends GLScreen {

    MenuIntro(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        if (Assets.ucode14 != null) {
            game.setScreen(new MenuMain(game));
        }
    }

}
