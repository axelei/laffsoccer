package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.framework.GLGame.SUBFRAMES_PER_SECOND;
import static com.ygames.ysoccer.match.Const.BALL_R;
import static com.ygames.ysoccer.match.Const.CROSSBAR_H;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.POST_X;
import static com.ygames.ysoccer.match.Const.isInsideGoal;

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

    final Scene scene;
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
    final int[] vCameraX = new int[Const.REPLAY_SUBFRAMES];
    final int[] vCameraY = new int[Const.REPLAY_SUBFRAMES];

    Ball ball;

    final List<Sprite> allSprites = new ArrayList<>();
    final Sprite.SpriteComparator spriteComparator = new Sprite.SpriteComparator();
    CornerFlagSprite[] cornerFlagSprites;

    private final int modW = Const.REPLAY_FRAMES;
    private final int modH = 2 * Const.REPLAY_FRAMES;
    private final int modX = (int) Math.ceil(Const.PITCH_W / ((float) modW));
    private final int modY = (int) Math.ceil(Const.PITCH_H / ((float) modH));

    protected SceneRenderer(Scene scene) {
        this.scene = scene;
    }

    abstract public void render();

    void resize(int width, int height, int newZoom) {
        screenWidth = width;
        screenHeight = height;
        float zoomMin = width / (VISIBLE_FIELD_WIDTH_MAX * 2 * Const.TOUCH_LINE);
        float zoomOpt = width / (VISIBLE_FIELD_WIDTH_OPT * 2 * Const.TOUCH_LINE);
        float zoomMax = width / (VISIBLE_FIELD_WIDTH_MIN * 2 * Const.TOUCH_LINE);
        zoom = 20 * (int) (5.0f * Math.min(Math.max(0.01f * newZoom * zoomOpt, zoomMin), zoomMax));

        actionCamera.setScreenParameters(screenWidth, screenHeight, zoom);

        guiHeight = guiWidth * height / width;
    }

    abstract void save();

    void renderSprites() {

        drawShadows();

        spriteComparator.setSubframe(scene.subframe);
        Collections.sort(allSprites, spriteComparator);

        for (Sprite sprite : allSprites) {
            sprite.draw(scene.subframe);
        }
    }

    abstract void drawShadows();

    void drawBallShadow(Ball ball, boolean redrawing) {
        Data d = ball.data[scene.subframe];
        for (int i = 0; i < (scene.settings.time == MatchSettings.Time.NIGHT ? 4 : 1); i++) {
            float oX = (i == 0 || i == 3) ? -1 : -5;
            float mX = (i == 0 || i == 3) ? 0.65f : -0.65f;
            float oY = -3;
            float mY = (i == 0 || i == 1) ? 0.46f : -0.46f;
            float x = d.x + oX + mX * d.z;
            float y = d.y + oY + mY * d.z;

            boolean overTheGoal = false;
            if (d.z > CROSSBAR_H) {
                float x1 = d.x + oX + mX * (d.z - CROSSBAR_H);
                float y1 = d.y + oY + mY * (d.z - CROSSBAR_H);
                if (isInsideGoal(x1 + BALL_R, y1 + BALL_R)) {
                    overTheGoal = true;
                    x = x1;
                    y = y1 - CROSSBAR_H;
                }
            }

            // while drawing all shadows (redrawing == false) -> draw only on-the-ground shadows
            // while redrawing ball shadows (redrawing == true) -> draw only if over the goal
            if (!overTheGoal ^ redrawing) {
                batch.draw(Assets.ball[4], x, y);
            }
        }
    }

    void redrawBallShadowsOverGoals(Ball ball) {
        batch.setColor(0xFFFFFF, scene.settings.shadowAlpha);
        drawBallShadow(ball, true);
        batch.setColor(0xFFFFFF, 1f);
    }

    void drawRain() {
        batch.setColor(0xFFFFFF, 0.6f);
        int subframe = scene.subframe;
        Assets.random.setSeed(1);
        for (int i = 1; i <= 40 * scene.settings.weatherStrength; i++) {
            int x = Assets.random.nextInt(modW);
            int y = Assets.random.nextInt(modH);
            int h = (Assets.random.nextInt(modH) + subframe) % modH;
            if (h > 0.3f * modH) {
                for (int fx = 0; fx <= modX; fx++) {
                    for (int fy = 0; fy <= modY; fy++) {
                        int px = ((x + modW - Math.round(subframe / ((float) GLGame.SUBFRAMES))) % modW) + modW * (fx - 1);
                        int py = ((y + 4 * Math.round(1f * subframe / GLGame.SUBFRAMES)) % modH) + modH * (fy - 1);
                        int f = 3 * h / modH;
                        if (h > 0.9f * modH) {
                            f = 3;
                        }
                        batch.draw(Assets.rain[f], -Const.CENTER_X + px, -Const.CENTER_Y + py);
                    }
                }
            }
        }
        Assets.random.setSeed(System.currentTimeMillis());
        batch.setColor(0xFFFFFF, 1f);
    }

    void drawSnow() {
        batch.setColor(0xFFFFFF, 0.7f);

        int subframe = scene.subframe;
        Assets.random.setSeed(1);
        for (int i = 1; i <= 30 * scene.settings.weatherStrength; i++) {
            int x = Assets.random.nextInt(modW);
            int y = Assets.random.nextInt(modH);
            int s = i % 3;
            int a = Assets.random.nextInt(360);
            for (int fx = 0; fx <= modX; fx++) {
                for (int fy = 0; fy <= modY; fy++) {
                    int px = (int) (((x + modW + 30 * EMath.sin(360 * subframe / ((float) Const.REPLAY_SUBFRAMES) + a)) % modW) + modW * (fx - 1));
                    int py = ((y + 2 * Math.round(1f * subframe / GLGame.SUBFRAMES)) % modH) + modH * (fy - 1);
                    batch.draw(Assets.snow[s], -Const.CENTER_X + px, -Const.CENTER_Y + py);
                }
            }
        }
        Assets.random.setSeed(System.currentTimeMillis());
        batch.setColor(0xFFFFFF, 1f);
    }

    void drawFog() {
        batch.setColor(0xFFFFFF, 0.25f * scene.settings.weatherStrength);

        int subframe = scene.subframe;
        int TILE_WIDTH = 256;
        int fogX = -Const.CENTER_X + vCameraX[subframe] - 2 * TILE_WIDTH
                + ((Const.CENTER_X - vCameraX[subframe]) % TILE_WIDTH + 2 * TILE_WIDTH) % TILE_WIDTH;
        int fogY = -Const.CENTER_Y + vCameraY[subframe] - 2 * TILE_WIDTH
                + ((Const.CENTER_Y - vCameraY[subframe]) % TILE_WIDTH + 2 * TILE_WIDTH) % TILE_WIDTH;
        int x = fogX;
        while (x < (fogX + screenWidth + 2 * TILE_WIDTH)) {
            int y = fogY;
            while (y < (fogY + screenHeight + 2 * TILE_WIDTH)) {
                batch.draw(Assets.fog, x + ((1f * subframe / GLGame.SUBFRAMES) % TILE_WIDTH), y + ((2f * subframe / GLGame.SUBFRAMES) % TILE_WIDTH), 256, 256, 0, 0, 256, 256, false, true);
                y = y + TILE_WIDTH;
            }
            x = x + TILE_WIDTH;
        }
        batch.setColor(0xFFFFFF, 1f);
    }

    void drawBallPredictions(Ball ball) {
        batch.end();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();

        shapeRenderer.setColor(255, 255, 255, 255);
        for (int frm = 0; frm < Const.BALL_PREDICTION; frm += 10) {
            Vector3 p = ball.predictionL[frm];
            shapeRenderer.circle(p.x, p.y, 1);
        }

        shapeRenderer.setColor(255, 255, 0, 255);
        for (int frm = 0; frm < Const.BALL_PREDICTION; frm += 10) {
            Vector3 p = ball.prediction[frm];
            shapeRenderer.circle(p.x, p.y, 1);
        }

        shapeRenderer.setColor(255, 0, 0, 255);
        for (int frm = 0; frm < Const.BALL_PREDICTION; frm += 10) {
            Vector3 p = ball.predictionR[frm];
            shapeRenderer.circle(p.x, p.y, 1);
        }

        shapeRenderer.end();
        batch.begin();
    }

    void fadeRect(int x0, int y0, int x1, int y1, float alpha, int color) {
        shapeRenderer.setColor(color, alpha);
        shapeRenderer.rect(x0, y0, x1 - x0, y1 - y0);
    }

    void drawFrame(int x, int y, int w, int h) {
        int r = x + w;
        int b = y + h;

        // top
        shapeRenderer.rect(x + 5, y, w - 8, 1);
        shapeRenderer.rect(x + 3, y + 1, w - 4, 1);

        // top-left
        shapeRenderer.rect(x + 2, y + 2, 4, 1);
        shapeRenderer.rect(x + 2, y + 3, 1, 3);
        shapeRenderer.rect(x + 3, y + 3, 1, 1);

        // top-right
        shapeRenderer.rect(r - 4, y + 2, 4, 1);
        shapeRenderer.rect(r - 1, y + 3, 1, 3);
        shapeRenderer.rect(r - 2, y + 3, 1, 1);

        // left
        shapeRenderer.rect(x, y + 5, 1, h - 8);
        shapeRenderer.rect(x + 1, y + 3, 1, h - 4);

        // right
        shapeRenderer.rect(r + 1, y + 5, 1, h - 8);
        shapeRenderer.rect(r, y + 3, 1, h - 4);

        // bottom-left
        shapeRenderer.rect(x + 2, b - 4, 1, 3);
        shapeRenderer.rect(x + 2, b - 1, 4, 1);
        shapeRenderer.rect(x + 3, b - 2, 1, 1);

        // bottom-right
        shapeRenderer.rect(r - 1, b - 4, 1, 3);
        shapeRenderer.rect(r - 4, b - 1, 4, 1);
        shapeRenderer.rect(r - 2, b - 2, 1, 1);

        // bottom
        shapeRenderer.rect(x + 5, b + 1, w - 8, 1);
        shapeRenderer.rect(x + 3, b, w - 4, 1);
    }

    void drawHelp(TreeMap<Integer, String[]> keyMap) {

        int rows = keyMap.size() + 1;
        int rowHeight = 52;

        int left = 13 + guiWidth / 5 + 2;
        int right = guiWidth - left + 2;
        int width = right - left;
        int top = guiHeight / 2 - rowHeight * rows / 2 + 2;
        int bottom = top + rowHeight * rows;
        int halfWay = guiWidth / 2;

        // fading
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // top strip
        fadeRect(left + 2, top + 2, right - 2, top + rowHeight + 1, 0.35f, 0x000000);

        // middle strips
        int i = top + rowHeight + 2;
        for (int j = 1; j < rows - 1; j++) {
            fadeRect(left + 2, i + 1, right - 2, i + rowHeight - 1, 0.35f, 0x000000);
            i = i + rowHeight;
        }

        // bottom strip
        fadeRect(left + 2, i + 1, right - 2, bottom - 2, 0.35f, 0x000000);

        // frame shadow
        shapeRenderer.setColor(0x242424, guiAlpha);
        drawFrame(left, top, right - left, bottom - top);

        left = left - 2;
        right = right - 2;
        top = top - 2;
        bottom = bottom - 2;

        // frame
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);
        drawFrame(left, top, right - left, bottom - top);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        int lc = left + 3 * width / 18;
        int rc = right - 12 * width / 18;
        i = top + rowHeight / 2 - 8;
        Assets.font14.draw(batch, gettext("HELP"), halfWay, i, Font.Align.CENTER);

        for (Integer key : keyMap.keySet()) {
            i = i + rowHeight;
            String[] info = keyMap.get(key);
            Assets.font14.draw(batch, info[0], lc, i, Font.Align.CENTER);
            Assets.font14.draw(batch, info[1], rc, i, Font.Align.LEFT);
        }

        batch.setColor(0xFFFFFF, 1f);
    }

    void redrawBallOverTopGoal(BallSprite ballSprite) {
        Data d = ball.data[scene.subframe];
        if (EMath.isIn(d.x, -POST_X, POST_X)
                && d.y < -GOAL_LINE
                && d.z > (CROSSBAR_H - (Math.abs(d.y) - GOAL_LINE) / 3f)) {
            ballSprite.draw(scene.subframe);
        }
    }

    void redrawBallOverBottomGoal(BallSprite ballSprite) {
        Data d = ball.data[scene.subframe];
        if (EMath.isIn(d.x, -POST_X - BALL_R, POST_X + BALL_R)
                && (d.y >= GOAL_LINE + 21 || (d.y > GOAL_LINE && d.z > (CROSSBAR_H - (Math.abs(d.y) - GOAL_LINE) / 3f)))) {
            ballSprite.draw(scene.subframe);
        }
    }

    void drawPlayerNumber(Player player) {
        Data d = player.data[scene.subframe];

        int f0 = player.number % 10;
        int f1 = (player.number - f0) / 10 % 10;

        int dx = Math.round(d.x);
        int dy = Math.round(d.y) - 40 - Math.round(d.z);

        int w0 = 6 - ((f0 == 1) ? 2 : 1);
        int w1 = 6 - ((f1 == 1) ? 2 : 1);

        int fy = scene.settings.pitchType == Pitch.Type.WHITE ? 1 : 0;
        if (f1 > 0) {
            dx = dx - (w0 + 2 + w1) / 2;
            batch.draw(Assets.playerNumbers[f1][fy], dx, dy, 6, 10);
            dx = dx + w1 + 2;
            batch.draw(Assets.playerNumbers[f0][fy], dx, dy, 6, 10);
        } else {
            batch.draw(Assets.playerNumbers[f0][fy], dx - w0 / 2f, dy, 6, 10);
        }
    }

    void drawPlayerNumberAndName(Player player) {
        Assets.font10.draw(batch, player.number + " " + player.shirtName, 10, 2, Font.Align.LEFT);
    }
}
