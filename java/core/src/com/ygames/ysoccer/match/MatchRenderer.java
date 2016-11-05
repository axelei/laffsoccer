package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchRenderer {

    GLGraphics glGraphics;
    int screenWidth;
    int screenHeight;
    int zoom;
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

        spriteComparator = new Sprite.SpriteComparator();
    }

    public void render(GLGame game) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        glGraphics.camera.setToOrtho(true, Gdx.graphics.getWidth() * 100.0f / zoom, Gdx.graphics.getHeight() * 100.0f / zoom);
        glGraphics.camera.translate(-Const.CENTER_X + vcameraX[match.subframe], -Const.CENTER_Y + vcameraY[match.subframe], 0);
        glGraphics.batch.begin();
        renderBackground();

        renderSprites(match.subframe);

        // redraw bottom goal
        glGraphics.batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

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

        glGraphics.batch.setColor(1, 1, 1, 1);
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
}
