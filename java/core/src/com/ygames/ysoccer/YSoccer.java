package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.framework.Video;
import com.ygames.ysoccer.screens.Intro;
import com.ygames.ysoccer.screens.Main;

public class YSoccer extends GLGame {

    @Override
    public void create() {
        super.create();

        if (settings.showIntro) {
            this.setScreen(new Intro(this));
        } else {
            menuMusic.setMode(settings.musicMode);
            prematchMusic.setMode(MenuMusic.ALL);
            this.setScreen(new Main(this));
        }
    }
}
