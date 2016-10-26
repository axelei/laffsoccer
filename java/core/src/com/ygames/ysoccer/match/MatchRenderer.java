package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchRenderer {

    GlGraphics glGraphics;
    int screenWidth;
    int screenHeight;
    int zoom;
    public ActionCamera actionCamera;
    public int[] vcameraX = new int[Const.REPLAY_SUBFRAMES];
    public int[] vcameraY = new int[Const.REPLAY_SUBFRAMES];

    public MatchCore match;
    List<Sprite> allSprites;
    Sprite.SpriteComparator spriteComparator;

    public MatchRenderer(GlGraphics glGraphics, MatchCore match) {
        this.glGraphics = glGraphics;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        zoom = Math.max(100, 20 * (int) (5.0f * Gdx.graphics.getWidth() / 1280));

        actionCamera = new ActionCamera(this);
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        this.match = match;
        allSprites = new ArrayList<Sprite>();
        spriteComparator = new Sprite.SpriteComparator();
    }

    public void render(GlGame game) {
        glGraphics.camera.setToOrtho(true, Gdx.graphics.getWidth() * 100.0f / zoom, Gdx.graphics.getHeight() * 100.0f / zoom);
        glGraphics.camera.translate(-Const.CENTER_X + vcameraX[match.subframe], -Const.CENTER_Y + vcameraY[match.subframe], 0);
        glGraphics.batch.begin();
        renderBackground();

        renderSprites(match.subframe);

        glGraphics.batch.end();
    }

    private void renderBackground() {
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                glGraphics.batch.draw(Assets.stadium[r][c], -Const.CENTER_X + 512 * c, -Const.CENTER_Y + 512 * r);
            }
        }
    }

    private void renderSprites(int subframe) {

        spriteComparator.subframe = subframe;
        Collections.sort(allSprites, spriteComparator);

        for (Sprite sprite : allSprites) {
            sprite.draw(subframe);
        }
    }
}
