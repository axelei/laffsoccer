package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Renderer {

    public static final float VISIBLE_FIELD_WIDTH_MAX = 1.0f;
    public static final float VISIBLE_FIELD_WIDTH_OPT = 0.75f;
    public static final float VISIBLE_FIELD_WIDTH_MIN = 0.65f;

    static final float guiAlpha = 0.9f;

    GLSpriteBatch batch;
    protected GLShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    int screenWidth;
    int screenHeight;
    int zoom;
    final int guiWidth = 1280;
    int guiHeight;

    public ActionCamera actionCamera;
    int[] vcameraX = new int[Const.REPLAY_SUBFRAMES];
    int[] vcameraY = new int[Const.REPLAY_SUBFRAMES];

    Ball ball;

    List<Sprite> allSprites;
    Sprite.SpriteComparator spriteComparator;
    CornerFlagSprite[] cornerFlagSprites;

    final int modW = Const.REPLAY_FRAMES;
    final int modH = 2 * Const.REPLAY_FRAMES;
    final int modX = (int) Math.ceil(Const.PITCH_W / ((float) modW));
    final int modY = (int) Math.ceil(Const.PITCH_H / ((float) modH));

    Renderer() {
        allSprites = new ArrayList<Sprite>();
        spriteComparator = new Sprite.SpriteComparator();
    }

    void renderSprites(int subframe) {

        drawShadows(subframe);

        spriteComparator.subframe = subframe;
        Collections.sort(allSprites, spriteComparator);

        for (Sprite sprite : allSprites) {
            sprite.draw(subframe);
        }
    }

    protected void drawShadows(int subframe) {
    }
}
