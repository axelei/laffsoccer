package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.gui.Widget;
import org.apache.commons.lang3.SystemUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Video extends GLScreen {

    private VideoPlayer videoPlayer = VideoPlayerCreator.createVideoPlayer();

    public Video(GLGame game) throws IOException {
        super(game);

        usesMouse = false;

        game.disableMouse();
        Gdx.input.setInputProcessor(new IntroInputProcessor());

        FileHandle intro = Gdx.files.local("videos").child("intro1.ogg");

        try {
            videoPlayer.play(intro);
        } catch (FileNotFoundException | NoClassDefFoundError e) {
            e.printStackTrace();
            setMainMenu();
        }

        videoPlayer.setOnCompletionListener(event -> setMainMenu());


    }

    @Override
    public void render(float delta) {
        super.render(delta);

        try {
            videoPlayer.update();
            batch.begin();
            Texture frame = videoPlayer.getTexture();
            if (frame != null) {
                batch.draw(frame, 0, 0, game.gui.WIDTH, game.gui.HEIGHT, 0, 0, frame.getWidth(), frame.getHeight(), false, true);
            }
            batch.end();
        } catch (NullPointerException e) {
            e.printStackTrace();
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
        // En linux no funciona el dispose, esto es una ñapa.
        if(SystemUtils.IS_OS_LINUX) {
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.pause();
            }
        } else {
            if (videoPlayer != null) {
                if (videoPlayer.isPlaying()) {
                    videoPlayer.stop();
                }
                videoPlayer.dispose();
            }
        }
        Gdx.input.setInputProcessor(null);
        game.enableMouse();
        game.menuMusic.setMode(game.settings.musicMode);
        game.prematchMusic.setMode(MenuMusic.ALL);
        game.setScreen(new Title(game));
    }
}
