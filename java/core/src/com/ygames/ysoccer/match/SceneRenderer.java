package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SceneRenderer {

    private static final float VISIBLE_FIELD_WIDTH_MAX = 1.0f;
    private static final float VISIBLE_FIELD_WIDTH_OPT = 0.75f;
    private static final float VISIBLE_FIELD_WIDTH_MIN = 0.65f;

    public static int zoomMin() {
        return 5 * (int) (20.0f * VISIBLE_FIELD_WIDTH_OPT / VISIBLE_FIELD_WIDTH_MAX);
    }

    public static int zoomMax() {
        return 5 * (int) (20.0f * VISIBLE_FIELD_WIDTH_OPT / VISIBLE_FIELD_WIDTH_MIN);
    }

    static final float guiAlpha = 0.9f;

    GLSpriteBatch batch;
    protected GLShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    int screenWidth;
    int screenHeight;
    int zoom;
    int light;
    final int guiWidth = 1280;
    int guiHeight;

    public ActionCamera actionCamera;
    int[] vcameraX = new int[Const.REPLAY_SUBFRAMES];
    int[] vcameraY = new int[Const.REPLAY_SUBFRAMES];

    Ball ball;

    List<Sprite> allSprites;
    Sprite.SpriteComparator spriteComparator;
    CornerFlagSprite[] cornerFlagSprites;

    private final int modW = Const.REPLAY_FRAMES;
    private final int modH = 2 * Const.REPLAY_FRAMES;
    private final int modX = (int) Math.ceil(Const.PITCH_W / ((float) modW));
    private final int modY = (int) Math.ceil(Const.PITCH_H / ((float) modH));

    SceneRenderer() {
        allSprites = new ArrayList<>();
        spriteComparator = new Sprite.SpriteComparator();
    }

    abstract public void render();

    public void resize(int width, int height, int newZoom) {
        screenWidth = width;
        screenHeight = height;
        float zoomMin = width / (VISIBLE_FIELD_WIDTH_MAX * 2 * Const.TOUCH_LINE);
        float zoomOpt = width / (VISIBLE_FIELD_WIDTH_OPT * 2 * Const.TOUCH_LINE);
        float zoomMax = width / (VISIBLE_FIELD_WIDTH_MIN * 2 * Const.TOUCH_LINE);
        zoom = 20 * (int) (5.0f * Math.min(Math.max(0.01f * newZoom * zoomOpt, zoomMin), zoomMax));

        actionCamera.setScreenParameters(screenWidth, screenHeight, zoom);

        guiHeight = guiWidth * height / width;
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

    void drawRain(SceneSettings sceneSettings, int subframe) {
        batch.setColor(0xFFFFFF, 0.6f);
        Assets.random.setSeed(1);
        for (int i = 1; i <= 40 * sceneSettings.weatherStrength; i++) {
            int x = Assets.random.nextInt(modW);
            int y = Assets.random.nextInt(modH);
            int h = (Assets.random.nextInt(modH) + subframe) % modH;
            if (h > 0.3f * modH) {
                for (int fx = 0; fx <= modX; fx++) {
                    for (int fy = 0; fy <= modY; fy++) {
                        int px = ((x + modW - Math.round(subframe / ((float) GLGame.SUBFRAMES))) % modW) + modW * (fx - 1);
                        int py = ((y + 4 * Math.round(subframe / GLGame.SUBFRAMES)) % modH) + modH * (fy - 1);
                        int f = 3 * h / modH;
                        if (h > 0.9f * modH) {
                            f = 3;
                        }
                        batch.draw(Assets.rain[f], -Const.CENTER_X + px, -Const.CENTER_Y + py);
                    }
                }
            }
        }
        batch.setColor(0xFFFFFF, 1f);
    }

    void drawSnow(SceneSettings sceneSettings, int subframe) {
        batch.setColor(0xFFFFFF, 0.7f);

        Assets.random.setSeed(1);
        for (int i = 1; i <= 30 * sceneSettings.weatherStrength; i++) {
            int x = Assets.random.nextInt(modW);
            int y = Assets.random.nextInt(modH);
            int s = i % 3;
            int a = Assets.random.nextInt(360);
            for (int fx = 0; fx <= modX; fx++) {
                for (int fy = 0; fy <= modY; fy++) {
                    int px = (int) (((x + modW + 30 * Emath.sin(360 * subframe / ((float) Const.REPLAY_SUBFRAMES) + a)) % modW) + modW * (fx - 1));
                    int py = ((y + 2 * Math.round(subframe / GLGame.SUBFRAMES)) % modH) + modH * (fy - 1);
                    batch.draw(Assets.snow[s], -Const.CENTER_X + px, -Const.CENTER_Y + py);
                }
            }
        }
        batch.setColor(0xFFFFFF, 1f);
    }

    void drawFog(SceneSettings sceneSettings, int subframe) {
        batch.setColor(0xFFFFFF, 0.25f * sceneSettings.weatherStrength);

        int TILE_WIDTH = 256;
        int fogX = -Const.CENTER_X + vcameraX[subframe] - 2 * TILE_WIDTH
                + ((Const.CENTER_X - vcameraX[subframe]) % TILE_WIDTH + 2 * TILE_WIDTH) % TILE_WIDTH;
        int fogY = -Const.CENTER_Y + vcameraY[subframe] - 2 * TILE_WIDTH
                + ((Const.CENTER_Y - vcameraY[subframe]) % TILE_WIDTH + 2 * TILE_WIDTH) % TILE_WIDTH;
        int x = fogX;
        while (x < (fogX + screenWidth + 2 * TILE_WIDTH)) {
            int y = fogY;
            while (y < (fogY + screenHeight + 2 * TILE_WIDTH)) {
                batch.draw(Assets.fog, x + ((subframe / GLGame.SUBFRAMES) % TILE_WIDTH), y + ((2 * subframe / GLGame.SUBFRAMES) % TILE_WIDTH), 256, 256, 0, 0, 256, 256, false, true);
                y = y + TILE_WIDTH;
            }
            x = x + TILE_WIDTH;
        }
        batch.setColor(0xFFFFFF, 1f);
    }
}
