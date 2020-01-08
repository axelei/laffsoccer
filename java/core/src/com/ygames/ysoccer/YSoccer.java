package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.screens.Main;
import com.ygames.ysoccer.screens.Video;

public class YSoccer extends GLGame {

    @Override
    public void create() {
        super.create();

        if (settings.showIntro) {
            this.setScreen(new Video(this));
        } else {
            menuMusic.setMode(settings.musicMode);
            prematchMusic.setMode(MenuMusic.ALL);
            this.setScreen(new Main(this));
        }
    }
}
