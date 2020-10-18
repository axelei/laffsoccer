package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.VideoPlayerDesktop;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.gui.TextBox;
import com.ygames.ysoccer.gui.Widget;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Video extends GLScreen {

    private VideoPlayer player = VideoPlayerCreator.createVideoPlayer();

    public Video(GLGame game) {
        super(game);

        usesMouse = false;

        game.disableMouse();
        Gdx.input.setInputProcessor(new IntroInputProcessor());

        FileHandle intro = Gdx.files.local("videos").child("intro1.ogg");

        try {
            player.play(intro);
        } catch (FileNotFoundException | NoClassDefFoundError e) {
            e.printStackTrace();
            setMainMenu();
        }

        player.setOnCompletionListener(event -> setMainMenu());
        player.resize(1,1);

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        try {
            player.render();
        } catch (NullPointerException e) {
            setMainMenu();
        }

        if (widgetEvent == Widget.Event.FIRE1_UP || widgetEvent == Widget.Event.FIRE2_UP) {
            setMainMenu();
        }
    }

    private class IntroInputProcessor extends InputAdapter {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            setMainMenu();
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            setMainMenu();
            return true;
        }
    }

    private void setMainMenu() {
        if (player.isPlaying()) {
            player.stop();
        }
        Gdx.input.setInputProcessor(null);
        game.enableMouse();
        game.menuMusic.setMode(game.settings.musicMode);
        game.prematchMusic.setMode(MenuMusic.ALL);
        game.setScreen(new Intro(game));
    }
}
