package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.screens.Main;

public class YSoccer extends GLGame {

    @Override
    public void create() {
        super.create();
        this.setScreen(new Main(this));
    }
}
