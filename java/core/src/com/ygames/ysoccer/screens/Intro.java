package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.framework.Slide;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.List;

public class Intro extends GLScreen {

    public static final float FADING_TIME = 1.5F;
    public static final float NO_FADE = 0.000001F;

    private Music music;
    private static List<Slide> introSlides;
    private Slide currSlide;
    private int currSlideNumber = -1;
    private float clock = 0;

    private static String CREDITS =
            "PROGRAMACIÓN: KRUSHER\n" +
            "NARRACIONES: SUPERLAFF\n" +
            "MÚSICA: ANTON SAPRISTI";

    private boolean fadingIn = false;

    public Intro(GLGame game) {
        super(game);

        introSlides = new ArrayList<>();
        introSlides.add(new Slide(new Texture("images/intro/charnegologo.jpg"), null, 5, FADING_TIME));
        introSlides.add(new Slide(new Texture("images/intro/enloartolameza.jpg"), null, 5, FADING_TIME));
        introSlides.add(new Slide(new Texture("images/intro/logojuego.jpg"), null, 8, NO_FADE));
        introSlides.add(new Slide(null, CREDITS, 6, FADING_TIME));
        introSlides.add(new Slide(new Texture("images/intro/logojuego.jpg"), null, 18, NO_FADE));

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
            currSlideNumber++;
            if (currSlideNumber + 1 > introSlides.size()) {
                currSlideNumber = 0;
            }
            currSlide = introSlides.get(currSlideNumber);
            if (currSlide.fade >= NO_FADE) {
                fadingIn = true;
            }
            background = currSlide.texture;
            batch.setColor(0, 1);
        }

        if (widgetEvent == Widget.Event.FIRE1_UP || widgetEvent == Widget.Event.FIRE2_UP) {
            setMainMenu();
        }

        batch.begin();
        if (fadingIn) {
            float alpha = clock / currSlide.fade;
            batch.setColor(alpha, alpha, alpha,1);
            if (alpha >= 1) {
                fadingIn = false;
            }
        }
        if (clock + currSlide.fade > currSlide.duration && currSlide.fade >= NO_FADE) {
            float alpha = (currSlide.duration - clock) / currSlide.fade;
            if (alpha <= 0) {
                alpha = 0;
            }
            batch.setColor(alpha, alpha, alpha,1);
        }
        if (currSlide.texture != null) {
            batch.draw(currSlide.texture, 0, 0, game.gui.WIDTH, game.gui.HEIGHT, 0, 0, currSlide.texture.getWidth(), currSlide.texture.getHeight(), false, true);
        }
        if (currSlide.text != null) {
            String[] lines = currSlide.text.split("\n");
            int lineCount = 1;
            for (String line : lines) {
                Assets.font14.draw(batch, line, game.gui.WIDTH / 2, game.gui.HEIGHT / 2 - 7 - ((int) currSlide.text.chars().filter(ch -> ch == '\n').count() * 17) + lineCount * 17, Font.Align.CENTER);
                lineCount++;
            }
        }
        batch.end();

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
