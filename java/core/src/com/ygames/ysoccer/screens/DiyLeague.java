package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;

public class DiyLeague extends GlScreen {

    public DiyLeague(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_competition.jpg");
    }
}
