package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.screens.Main;
import com.ygames.ysoccer.screens.Video;

import java.io.IOException;

public class YSoccer extends GLGame {

    @Override
    public void create() {
        super.create();
        try {
            this.setScreen(new Video(this));
        } catch (IOException e) {
            e.printStackTrace();
            this.setScreen(new Main(this));
        }
    }
}
