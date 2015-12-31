package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.screens.Main;

public class YSoccer extends GlGame {

    @Override
    public void create() {
        super.create();
        this.setScreen(new Main(this));
    }
}
