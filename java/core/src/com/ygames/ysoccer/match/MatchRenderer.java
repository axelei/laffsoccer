package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.Gdx.gl;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchRenderer {

    public static final float VISIBLE_FIELD_WIDTH_MAX = 1.0f;
    public static final float VISIBLE_FIELD_WIDTH_OPT = 0.75f;
    public static final float VISIBLE_FIELD_WIDTH_MIN = 0.65f;

    private static final float guiAlpha = 0.9f;

    GLGraphics glGraphics;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    int screenWidth;
    int screenHeight;
    int zoom;
    final int guiWidth = 1280;
    int guiHeight;

    public ActionCamera actionCamera;
    public int[] vcameraX = new int[Const.REPLAY_SUBFRAMES];
    public int[] vcameraY = new int[Const.REPLAY_SUBFRAMES];

    public Match match;
    private List<Sprite> allSprites;
    private Sprite.SpriteComparator spriteComparator;
    private CornerFlagSprite[] cornerFlagSprites;

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayGoalScorer;
    boolean displayTime;
    boolean displayWindVane;
    boolean displayScore;
    boolean displayStatistics;
    boolean displayRadar;

    public MatchRenderer(GLGraphics glGraphics, Match match) {
        this.glGraphics = glGraphics;
        this.batch = glGraphics.batch;
        this.camera = glGraphics.camera;
        this.match = match;

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), match.game.settings);

        actionCamera = new ActionCamera(this);
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        allSprites = new ArrayList<Sprite>();
        allSprites.add(new BallSprite(glGraphics, match.ball));
        for (int t = HOME; t <= AWAY; t++) {
            int len = match.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                PlayerSprite playerSprite = new PlayerSprite(glGraphics, match.team[t].lineup.get(i));
                allSprites.add(playerSprite);
            }
        }

        cornerFlagSprites = new CornerFlagSprite[4];
        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i] = new CornerFlagSprite(glGraphics, match.settings, i / 2 * 2 - 1, i % 2 * 2 - 1);
            allSprites.add(cornerFlagSprites[i]);
        }
        allSprites.add(new GoalTopA(glGraphics));

        spriteComparator = new Sprite.SpriteComparator();
    }

    public void resize(int width, int height, Settings settings) {
        screenWidth = width;
        screenHeight = height;
        float zoomMin = width / (VISIBLE_FIELD_WIDTH_MAX * 2 * Const.TOUCH_LINE);
        float zoomOpt = width / (VISIBLE_FIELD_WIDTH_OPT * 2 * Const.TOUCH_LINE);
        float zoomMax = width / (VISIBLE_FIELD_WIDTH_MIN * 2 * Const.TOUCH_LINE);
        zoom = 20 * (int) (5.0f * Math.min(Math.max(0.01f * settings.zoom * zoomOpt, zoomMin), zoomMax));

        guiHeight = guiWidth * height / width;
    }

    public void render(GLGame game) {
        gl.glEnable(GL20.GL_BLEND);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(true, Gdx.graphics.getWidth() * 100.0f / zoom, Gdx.graphics.getHeight() * 100.0f / zoom);
        camera.translate(-Const.CENTER_X + vcameraX[match.subframe], -Const.CENTER_Y + vcameraY[match.subframe], 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground();
        renderSprites(match.subframe);

        // redraw bottom goal
        batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

        batch.end();

        renderGui();
    }

    private void renderGui() {
        camera.setToOrtho(true, guiWidth, guiHeight);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        glGraphics.shapeRenderer.setProjectionMatrix(camera.combined);
        batch.begin();

        // ball owner
        if (displayBallOwner && match.ball.owner != null) {
            drawPlayerNumberAndName(match.ball.owner);
        }

        // clock
        if (displayTime) {
            drawTime();
        }

        // score
        if (displayScore) {
            drawScore();
        }

        // goal scorer
        if (displayGoalScorer && (match.subframe % 160 > 80)) {
            drawPlayerNumberAndName(match.ball.goalOwner);
        }

        // additional state-specific render
        MatchState matchState = match.fsm.getState();
        if (matchState != null) {
            matchState.render();
        }
        batch.end();
    }

    private void renderBackground() {
        batch.disableBlending();
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                batch.draw(Assets.stadium[r][c], -Const.CENTER_X + 512 * c, -Const.CENTER_Y + 512 * r);
            }
        }
        batch.enableBlending();
    }

    private void renderSprites(int subframe) {

        drawShadows(subframe);

        spriteComparator.subframe = subframe;
        Collections.sort(allSprites, spriteComparator);

        for (Sprite sprite : allSprites) {
            sprite.draw(subframe);
        }
    }

    private void drawShadows(int subframe) {
        batch.setColor(1, 1, 1, match.settings.shadowAlpha);

        Data d = match.ball.data[subframe];
        batch.draw(Assets.ball[4], d.x - 1 + 0.65f * d.z, d.y - 3 + 0.46f * d.z);
        if (match.settings.time == Time.NIGHT) {
            batch.draw(Assets.ball[4], d.x - 5 - 0.65f * d.z, d.y - 3 + 0.46f * d.z);
            batch.draw(Assets.ball[4], d.x - 5 - 0.65f * d.z, d.y - 3 - 0.46f * d.z);
            batch.draw(Assets.ball[4], d.x - 1 + 0.65f * d.z, d.y - 3 - 0.46f * d.z);
        }

        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i].drawShadow(subframe, batch);
        }

        // keepers
        for (int t = HOME; t <= AWAY; t++) {
            for (Player player : match.team[t].lineup) {
                if (player.role == Player.Role.GOALKEEPER) {
                    d = player.data[subframe];
                    if (d.isVisible) {
                        batch.draw(Assets.keeperShadow[d.fmx][d.fmy][0], d.x - 24 + 0.65f * d.z, d.y - 34 + 0.46f * d.z);
                        if (match.settings.time == Time.NIGHT) {
                            // TODO
                            // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][1], d.x - 24 - 0.65f * d.z, d.y - 34 + 0.46f * d.z);
                            // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][2], d.x - 24 - 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                            // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][3], d.x - 24 + 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                        }
                    }
                }
            }
        }

        // players
        for (int i = 0; i < (match.settings.time == Time.NIGHT ? 4 : 1); i++) {
            for (int t = HOME; t <= AWAY; t++) {
                for (Player player : match.team[t].lineup) {
                    if (player.role != Player.Role.GOALKEEPER) {
                        d = player.data[subframe];
                        if (d.isVisible) {
                            float offsetX = PlayerSprite.offsets[d.fmy][d.fmx][0];
                            float offsetY = PlayerSprite.offsets[d.fmy][d.fmx][1];
                            float mX = (i == 0 || i == 3) ? 0.65f : -0.65f;
                            float mY = (i == 0 || i == 1) ? 0.46f : -0.46f;
                            batch.draw(Assets.playerShadow[d.fmx][d.fmy][i], d.x - offsetX + mX * d.z, d.y - offsetY + 5 + mY * d.z);
                        }
                    }
                }
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void drawTime() {

        int minute = match.getMinute();

        // "minutes"
        batch.draw(Assets.time[10], 46, 22);

        // units
        int digit = minute % 10;
        batch.draw(Assets.time[digit], 34, 22);

        // tens
        minute = (minute - digit) / 10;
        digit = minute % 10;
        if (minute > 0) {
            batch.draw(Assets.time[digit], 22, 22);
        }

        // hundreds
        minute = (minute - digit) / 10;
        digit = minute % 10;
        if (digit > 0) {
            batch.draw(Assets.time[digit], 10, 22);
        }
    }

    private void drawScore() {

        int y0 = guiHeight - 16;

        // teams
        Assets.font14.draw(batch, match.team[HOME].name, +10, y0 - 22, Font.Align.LEFT);
        Assets.font14.draw(batch, match.team[AWAY].name, guiWidth - 8, y0 - 22, Font.Align.RIGHT);

        // bars
        batch.end();
        glGraphics.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        glGraphics.shapeRenderer.setColor(1, 1, 1, guiAlpha);
        glGraphics.shapeRenderer.rect(10, y0, guiWidth / 2 - 22, 2);
        glGraphics.shapeRenderer.rect(guiWidth / 2 + 12, y0, guiWidth / 2 - 22, 2);

        glGraphics.shapeRenderer.setColor(0.1f, 0.1f, 0.1f, guiAlpha);
        glGraphics.shapeRenderer.rect(12, y0 + 2, guiWidth / 2 - 22, 2);
        glGraphics.shapeRenderer.rect(guiWidth / 2 + 14, y0 + 2, guiWidth / 2 - 22, 2);

        glGraphics.shapeRenderer.end();
        batch.begin();

        // home score
        int f0 = match.stats[Match.HOME].goals % 10;
        int f1 = ((match.stats[Match.HOME].goals - f0) / 10) % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2 - 15 - 48, y0 - 40);
        }
        batch.draw(Assets.score[f0], guiWidth / 2 - 15 - 24, y0 - 40);

        // "-"
        batch.draw(Assets.score[10], guiWidth / 2 - 9, y0 - 40);

        // away score
        f0 = match.stats[Match.AWAY].goals % 10;
        f1 = (match.stats[Match.AWAY].goals - f0) / 10 % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2 + 17, y0 - 40);
            batch.draw(Assets.score[f0], guiWidth / 2 + 17 + 24, y0 - 40);
        } else {
            batch.draw(Assets.score[f0], guiWidth / 2 + 17, y0 - 40);
        }
    }

    void updateCameraX(int follow, int speed) {
        updateCameraX(follow, speed, 0, true);
    }

    void updateCameraX(int follow, int speed, int targetX) {
        updateCameraX(follow, speed, targetX, true);
    }

    void updateCameraX(int follow, int speed, int targetX, boolean limit) {
        vcameraX[match.subframe] = actionCamera.updateX(follow, speed, targetX, limit);
    }

    void updateCameraY(int follow, int speed) {
        updateCameraY(follow, speed, 0);
    }

    void updateCameraY(int follow, int speed, int targetY) {
        updateCameraY(follow, speed, targetY, true);
    }

    void updateCameraY(int follow, int speed, int targetY, boolean limit) {
        vcameraY[match.subframe] = actionCamera.updateY(follow, speed, targetY, limit);
    }

    private void drawPlayerNumberAndName(Player player) {
        Assets.font10.draw(batch, player.number + " " + player.shirtName, 10, 2, Font.Align.LEFT);
    }
}
