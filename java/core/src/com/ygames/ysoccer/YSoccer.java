package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.screens.Title;
import com.ygames.ysoccer.screens.Intro;
import org.apache.commons.lang3.SystemUtils;

public class YSoccer extends GLGame {

    @Override
    public void create() {
        super.create();

        if (settings.showIntro && SystemUtils.IS_OS_WINDOWS) {
            this.setScreen(new Intro(this));
        } else {
            menuMusic.setMode(settings.musicMode);
            prematchMusic.setMode(MenuMusic.ALL);
            this.setScreen(new Title(this));
        }
    }
}
