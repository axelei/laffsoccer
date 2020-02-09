package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchStats;

import java.util.Locale;

import static com.badlogic.gdx.Gdx.gl;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class ReplayMatch extends GLScreen {

    private Match match;

    ReplayMatch(GLGame game, Match match) {
        super(game);
        this.match = match;

        background = new Texture("images/backgrounds/menu_match_presentation.jpg");

        Widget w;

        w = new TitleBar(match.competition.name, game.stateColor.body);
        widgets.add(w);

        w = new ReplayButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new ExitButton();
        widgets.add(w);
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);
        batch.begin();

        // default values
        int h0 = 0;
        int w0 = 0;
        int w1 = 0;
        int h1 = 0;
        float imageScale0 = 1f;
        float imageScale1 = 1f;
        int guiHeight = 720;
        int guiWidth = 1280;
        float guiAlpha = 1f;

        // max rows of rows
        int rows = Math.max(match.getScorersRows()[HOME].size(), match.getScorersRows()[AWAY].size());

        // size of club logos / national flags
        if (match.team[HOME].image != null) {
            w0 = match.team[HOME].image.getRegionWidth();
            h0 = match.team[HOME].image.getRegionHeight();
            if (h0 > 70) {
                imageScale0 = 70f / h0;
            }
        }
        if (match.team[AWAY].image != null) {
            w1 = match.team[AWAY].image.getRegionWidth();
            h1 = match.team[AWAY].image.getRegionHeight();
            if (h1 > 70) {
                imageScale1 = 70f / h1;
            }
        }

        int hMax = Math.max((int) (imageScale0 * h0), (int) (imageScale1 * h1));
        int y0 = guiHeight - 16 - Math.max(hMax, 14 * rows);

        // club logos / national flags
        if (match.team[HOME].image != null) {
            int x = 12;
            int y = y0 + 8 + (hMax - (int) (imageScale0 * h0)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[HOME].image, x + 2, y + 2, 0, 0, w0, h0, imageScale0, imageScale0, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[HOME].image, x, y, 0, 0, w0, h0, imageScale0, imageScale0, 0);
        }
        if (match.team[AWAY].image != null) {
            int x = guiWidth - (int) (imageScale1 * w1) - 12;
            int y = y0 + 8 + (hMax - (int) (imageScale1 * h1)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[AWAY].image, x + 2, y + 2, 0, 0, w1, h1, imageScale1, imageScale1, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[AWAY].image, x, y, 0, 0, w1, h1, imageScale1, imageScale1, 0);
        }

        // teams
        Assets.font14.draw(batch, match.team[HOME].name, +12, y0 - 22, Font.Align.LEFT);
        Assets.font14.draw(batch, match.team[AWAY].name, guiWidth - 10, y0 - 22, Font.Align.RIGHT);

        // bars
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);

        shapeRenderer.rect(10, y0, guiWidth / 2f - 22, 2);
        shapeRenderer.rect(guiWidth / 2f + 12, y0, guiWidth / 2f - 22, 2);

        shapeRenderer.setColor(0x242424, guiAlpha);
        shapeRenderer.rect(12, y0 + 2, guiWidth / 2f - 22, 2);
        shapeRenderer.rect(guiWidth / 2f + 14, y0 + 2, guiWidth / 2f - 22, 2);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // home score
        int f0 = match.stats[Match.HOME].goals % 10;
        int f1 = ((match.stats[Match.HOME].goals - f0) / 10) % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2 - 15 - 48, y0 - 40);
        }
        batch.draw(Assets.score[f0], guiWidth / 2 - 15 - 24, y0 - 40);

        // "-"
        batch.draw(Assets.score[10], guiWidth / 2f - 9, y0 - 40);

        // away score
        f0 = match.stats[Match.AWAY].goals % 10;
        f1 = (match.stats[Match.AWAY].goals - f0) / 10 % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2f + 17, y0 - 40);
            batch.draw(Assets.score[f0], guiWidth / 2f + 17 + 24, y0 - 40);
        } else {
            batch.draw(Assets.score[f0], guiWidth / 2f + 17, y0 - 40);
        }

        // scorers
        for (int t = HOME; t <= AWAY; t++) {
            int y = y0 + 4;
            for (String row : match.getScorersRows()[t]) {
                int x = guiWidth / 2 + (t == HOME ? -12 : +14);
                Font.Align align = t == HOME ? Font.Align.RIGHT : Font.Align.LEFT;
                Assets.font10.draw(batch, row, x, y, align);
                y += 14;
            }
        }

        batch.end();
    }

    private class ReplayButton extends Button {

        ReplayButton() {
            setGeometry((game.gui.WIDTH - 240) / 2, 460, 240, 50);
            setColor(0xDC0000);
            setText(Assets.strings.get("REPLAY"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            Friendly friendly = new Friendly();
            friendly.getMatch().setTeam(HOME, match.team[HOME]);
            friendly.getMatch().setTeam(AWAY, match.team[AWAY]);

            game.setScreen(new MatchLoading(game, match.getSettings(), friendly));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 560, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

}
