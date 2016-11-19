package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.Gdx.gl;

public class MatchRenderer {

    public static final float VISIBLE_FIELD_WIDTH_MAX = 1.0f;
    public static final float VISIBLE_FIELD_WIDTH_OPT = 0.75f;
    public static final float VISIBLE_FIELD_WIDTH_MIN = 0.65f;

    GLGraphics glGraphics;
    int screenWidth;
    int screenHeight;
    int zoom;
    final int guiWidth = 1280;
    int guiHeight;

    public ActionCamera actionCamera;
    public int[] vcameraX = new int[Const.REPLAY_SUBFRAMES];
    public int[] vcameraY = new int[Const.REPLAY_SUBFRAMES];

    public MatchCore match;
    List<Sprite> allSprites;
    Sprite.SpriteComparator spriteComparator;
    private CornerFlagSprite[] cornerFlagSprites;

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayGoalScorer;
    boolean displayTime;
    boolean displayWindVane;
    boolean displayScore;
    boolean displayStatistics;
    boolean displayRadar;

    public MatchRenderer(GLGraphics glGraphics, MatchCore match) {
        this.glGraphics = glGraphics;
        this.match = match;

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), match.game.settings);

        actionCamera = new ActionCamera(this);
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        allSprites = new ArrayList<Sprite>();
        allSprites.add(new BallSprite(glGraphics, match.ball));
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
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
        glGraphics.camera.setToOrtho(true, Gdx.graphics.getWidth() * 100.0f / zoom, Gdx.graphics.getHeight() * 100.0f / zoom);
        glGraphics.camera.translate(-Const.CENTER_X + vcameraX[match.subframe], -Const.CENTER_Y + vcameraY[match.subframe], 0);
        glGraphics.camera.update();
        glGraphics.batch.setProjectionMatrix(glGraphics.camera.combined);
        glGraphics.batch.begin();

        renderBackground();
        renderSprites(match.subframe);

        // redraw bottom goal
        glGraphics.batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

        glGraphics.batch.end();

        renderGui();
    }

    private void renderGui() {
        glGraphics.camera.setToOrtho(true, guiWidth, guiHeight);
        glGraphics.camera.update();
        glGraphics.batch.setProjectionMatrix(glGraphics.camera.combined);
        glGraphics.batch.begin();

        // ball owner
        if (displayBallOwner && match.ball.owner != null) {
            drawPlayerNumberAndName(match.ball.owner);
        }

        // clock
        if (displayTime) {
            drawTime();
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
        glGraphics.batch.end();
    }

    private void renderBackground() {
        glGraphics.batch.disableBlending();
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                glGraphics.batch.draw(Assets.stadium[r][c], -Const.CENTER_X + 512 * c, -Const.CENTER_Y + 512 * r);
            }
        }
        glGraphics.batch.enableBlending();
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
        glGraphics.batch.setColor(1, 1, 1, match.settings.shadowAlpha);

        Data d = match.ball.data[subframe];
        glGraphics.batch.draw(Assets.ball[4], d.x - 1 + 0.65f * d.z, d.y - 3 + 0.46f * d.z);
        if (match.settings.time == Time.NIGHT) {
            glGraphics.batch.draw(Assets.ball[4], d.x - 5 - 0.65f * d.z, d.y - 3 + 0.46f * d.z);
            glGraphics.batch.draw(Assets.ball[4], d.x - 5 - 0.65f * d.z, d.y - 3 - 0.46f * d.z);
            glGraphics.batch.draw(Assets.ball[4], d.x - 1 + 0.65f * d.z, d.y - 3 - 0.46f * d.z);
        }

        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i].drawShadow(subframe, glGraphics.batch);
        }

        // keepers
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            for (Player player : match.team[t].lineup) {
                if (player.role == Player.Role.GOALKEEPER) {
                    d = player.data[subframe];
                    if (d.isVisible) {
                        glGraphics.batch.draw(Assets.keeperShadow[d.fmx][d.fmy][0], d.x - 24 + 0.65f * d.z, d.y - 34 + 0.46f * d.z);
                        if (match.settings.time == Time.NIGHT) {
                            // TODO
                            // glGraphics.batch.draw(Assets.keeperShadow[d.fmx][d.fmy][1], d.x - 24 - 0.65f * d.z, d.y - 34 + 0.46f * d.z);
                            // glGraphics.batch.draw(Assets.keeperShadow[d.fmx][d.fmy][2], d.x - 24 - 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                            // glGraphics.batch.draw(Assets.keeperShadow[d.fmx][d.fmy][3], d.x - 24 + 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                        }
                    }
                }
            }
        }

        // players
        for (int i = 0; i < (match.settings.time == Time.NIGHT ? 4 : 1); i++) {
            for (int t = Match.HOME; t <= Match.AWAY; t++) {
                for (Player player : match.team[t].lineup) {
                    if (player.role != Player.Role.GOALKEEPER) {
                        d = player.data[subframe];
                        if (d.isVisible) {
                            float offsetX = PlayerSprite.offsets[d.fmy][d.fmx][0];
                            float offsetY = PlayerSprite.offsets[d.fmy][d.fmx][1];
                            float mX = (i == 0 || i == 3) ? 0.65f : -0.65f;
                            float mY = (i == 0 || i == 1) ? 0.46f : -0.46f;
                            glGraphics.batch.draw(Assets.playerShadow[d.fmx][d.fmy][i], d.x - offsetX + mX * d.z, d.y - offsetY + 5 + mY * d.z);
                        }
                    }
                }
            }
        }

        glGraphics.batch.setColor(1, 1, 1, 1);
    }

    private void drawTime() {

        int minute = match.getMinute();

        // "minutes"
        glGraphics.batch.draw(Assets.time[10], 46, 22);

        // units
        int digit = minute % 10;
        glGraphics.batch.draw(Assets.time[digit], 34, 22);

        // tens
        minute = (minute - digit) / 10;
        digit = minute % 10;
        if (minute > 0) {
            glGraphics.batch.draw(Assets.time[digit], 22, 22);
        }

        // hundreds
        minute = (minute - digit) / 10;
        digit = minute % 10;
        if (digit > 0) {
            glGraphics.batch.draw(Assets.time[digit], 10, 22);
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
        Assets.font10.draw(glGraphics.batch, player.number + " " + player.shirtName, 10, 2, Font.Align.LEFT);
    }
}
