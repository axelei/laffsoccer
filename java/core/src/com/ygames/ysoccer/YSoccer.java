package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.GlGame;

public class YSoccer extends GlGame {

    @Override
    public void create() {
        super.create();
        this.setScreen(new MenuMain(this));
    }
}
