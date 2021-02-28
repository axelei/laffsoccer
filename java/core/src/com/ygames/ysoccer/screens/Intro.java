package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.framework.Slide;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.List;

public class Intro extends GLScreen {

    public static final float FADING_TIME = 1.5F;

    private Music music;
    private static List<Slide> introSlides;
    private Slide currSlide;
    private int currSlideNumber = 0;
    private float clock = 0;

    private boolean fadingIn = false;

    public Intro(GLGame game) {
        super(game);

        introSlides = new ArrayList<>();
        introSlides.add(new Slide(new Texture("images/intro/charnegologo.jpg"), null, 7));

        music = Gdx.audio.newMusic(Gdx.files.internal("music/Anton Sapristi - Disfraz Extraño.mp3"));
        music.setLooping(true);

        usesMouse = false;

        game.disableMouse();
        music.play();
        Gdx.input.setInputProcessor(new IntroInputProcessor());

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        clock += delta;

        if (currSlide == null || currSlide.duration < clock) {
            clock = 0;
            fadingIn = true;
            if (currSlideNumber + 1 > introSlides.size()) {
                currSlideNumber = 0;
            }
            currSlide = introSlides.get(currSlideNumber);
            background = currSlide.texture;
            batch.setColor(0, 1);
        }

        if (widgetEvent == Widget.Event.FIRE1_UP || widgetEvent == Widget.Event.FIRE2_UP) {
            setMainMenu();
        }

        if (currSlide.texture != null) {
            batch.begin();
            if (fadingIn) {
                float alpha = clock / FADING_TIME;
                batch.setColor(alpha, alpha, alpha,1);
                if (alpha >= 1) {
                    fadingIn = false;
                }
            }
            if (clock + FADING_TIME > currSlide.duration) {
                float alpha = (currSlide.duration - clock) / FADING_TIME;
                batch.setColor(alpha, alpha, alpha,1);
            }
            batch.draw(currSlide.texture, 0, 0, game.gui.WIDTH, game.gui.HEIGHT, 0, 0, currSlide.texture.getWidth(), currSlide.texture.getHeight(), false, true);
            batch.end();
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

        if (music != null && music.isPlaying()) {
            music.stop();
            music.dispose();
        }

        for (Slide introSlide : introSlides) {
            if (introSlide.texture != null) {
                introSlide.texture.dispose();
            }
        }

        Gdx.input.setInputProcessor(null);
        game.enableMouse();
        game.menuMusic.setMode(game.settings.musicMode);
        game.prematchMusic.setMode(MenuMusic.ALL);
        game.setScreen(new Title(game));
    }
}
