package com.ygames.ysoccer;

import com.badlogic.gdx.video.VideoPlayerInitException;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.screens.Title;
import com.ygames.ysoccer.screens.Intro;
import com.ygames.ysoccer.screens.Video;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;

public class YSoccer extends GLGame {

    @Override
    public void create() {
        super.create();
        //this.setScreen(new Intro(this));
        try {
            this.setScreen(new Video(this));
        } catch (IOException | VideoPlayerInitException e) {
            System.err.println(e.getMessage());
        }
    }
}
