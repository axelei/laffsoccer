package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

import static com.badlogic.gdx.Gdx.gl;

public class TrainingRenderer extends Renderer {

    public Training training;

    TrainingRenderer(GLGraphics glGraphics, Training training) {
        super();
        this.batch = glGraphics.batch;
        this.shapeRenderer = glGraphics.shapeRenderer;
        this.camera = glGraphics.camera;
        this.training = training;
        this.ball = training.ball;

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), training.game.settings);

        actionCamera = new ActionCamera(this);
        actionCamera.y = 0.5f * (Const.PITCH_H - screenHeight / (zoom / 100.0f));
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        allSprites.add(new BallSprite(glGraphics, training.ball));
        CoachSprite coachSprite = new CoachSprite(glGraphics, training.team.coach);
        allSprites.add(coachSprite);
        int len = training.team.lineup.size();
        for (int i = 0; i < len; i++) {
            PlayerSprite playerSprite = new PlayerSprite(glGraphics, training.team.lineup.get(i));
            allSprites.add(playerSprite);
        }

        cornerFlagSprites = new CornerFlagSprite[4];
        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i] = new CornerFlagSprite(glGraphics, training.settings, i / 2 * 2 - 1, i % 2 * 2 - 1);
            allSprites.add(cornerFlagSprites[i]);
        }
        allSprites.add(new GoalTopA(glGraphics));
        allSprites.add(new GoalTopB(glGraphics));
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

    public void render() {
        gl.glEnable(GL20.GL_BLEND);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(true, Gdx.graphics.getWidth() * 100f / zoom, Gdx.graphics.getHeight() * 100f / zoom);
        camera.translate(-Const.CENTER_X + vcameraX[training.subframe], -Const.CENTER_Y + vcameraY[training.subframe], 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground();

        renderSprites(training.subframe);

        // redraw bottom goal
        batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

        // redraw jumpers
        // top
        if (training.ball.data[training.subframe].y < -Const.JUMPER_Y) {
            batch.draw(Assets.jumper, -Const.JUMPER_X, -Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
            batch.draw(Assets.jumper, +Const.JUMPER_X, -Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
        }
        // bottom
        if (training.ball.data[training.subframe].y < +Const.JUMPER_Y) {
            batch.draw(Assets.jumper, -Const.JUMPER_X, +Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
            batch.draw(Assets.jumper, +Const.JUMPER_X, +Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
        }

        if (training.settings.weatherStrength != Weather.Strength.NONE) {
            switch (training.settings.weatherEffect) {
                case Weather.RAIN:
                    drawRain(training.settings, training.subframe);
                    break;

                case Weather.SNOW:
                    drawSnow(training.settings, training.subframe);
                    break;

                case Weather.FOG:
                    drawFog(training.settings, training.subframe);
                    break;
            }
        }

        drawControlledPlayersNumbers();

        batch.end();

        renderGui();
    }

    private void renderGui() {
        camera.setToOrtho(true, guiWidth, guiHeight);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // ball owner
        if (training.ball.owner != null) {
            drawPlayerNumberAndName(training.ball.owner);
        }

        // wind vane
        if (training.settings.wind.speed > 0) {
            batch.draw(Assets.wind[training.settings.wind.direction][training.settings.wind.speed - 1], guiWidth - 50, 20);
        }

        // messages
        if (training.fsm.trainingKeys.messageTimer > 0) {
            batch.setColor(0xFFFFFF, guiAlpha);
            Assets.font10.draw(batch, training.fsm.trainingKeys.message, guiWidth / 2, 1, Font.Align.CENTER);
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

    @Override
    protected void drawShadows(int subframe) {
        batch.setColor(0xFFFFFF, training.settings.shadowAlpha);

        Data d = training.ball.data[subframe];
        batch.draw(Assets.ball[4], d.x - 1 + 0.65f * d.z, d.y - 3 + 0.46f * d.z);
        if (training.settings.time == MatchSettings.Time.NIGHT) {
            batch.draw(Assets.ball[4], d.x - 5 - 0.65f * d.z, d.y - 3 + 0.46f * d.z);
            batch.draw(Assets.ball[4], d.x - 5 - 0.65f * d.z, d.y - 3 - 0.46f * d.z);
            batch.draw(Assets.ball[4], d.x - 1 + 0.65f * d.z, d.y - 3 - 0.46f * d.z);
        }

        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i].drawShadow(subframe, batch);
        }

        // keepers
        for (Player player : training.team.lineup) {
            if (player.role == Player.Role.GOALKEEPER) {
                d = player.data[subframe];
                if (d.isVisible) {
                    batch.draw(Assets.keeperShadow[d.fmx][d.fmy][0], d.x - 24 + 0.65f * d.z, d.y - 34 + 0.46f * d.z);
                    if (training.settings.time == MatchSettings.Time.NIGHT) {
                        // TODO
                        // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][1], d.x - 24 - 0.65f * d.z, d.y - 34 + 0.46f * d.z);
                        // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][2], d.x - 24 - 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                        // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][3], d.x - 24 + 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                    }
                }
            }
        }

        // players
        for (int i = 0; i < (training.settings.time == MatchSettings.Time.NIGHT ? 4 : 1); i++) {
            for (Player player : training.team.lineup) {
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

        batch.setColor(0xFFFFFF, 1f);
    }

    private void drawControlledPlayersNumbers() {
        if (training.team != null) {
            int len = training.team.lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = training.team.lineup.get(i);
                if (player.inputDevice != player.ai && player.isVisible) {
                    drawPlayerNumber(player);
                }
            }
        }
    }

    private void drawPlayerNumber(Player player) {

        int f0 = player.number % 10;
        int f1 = (player.number - f0) / 10 % 10;

        int dx = Math.round(player.x) + 1;
        int dy = Math.round(player.y) - 40 - Math.round(player.z);

        int w0 = 6 - ((f0 == 1) ? 2 : 1);
        int w1 = 6 - ((f1 == 1) ? 2 : 1);

        int fy = training.settings.pitchType == Pitch.Type.WHITE ? 1 : 0;
        if (f1 > 0) {
            dx = dx - (w0 + 2 + w1) / 2;
            batch.draw(Assets.playerNumbers[f1][fy], dx, dy, 6, 10);
            dx = dx + w1 + 2;
            batch.draw(Assets.playerNumbers[f0][fy], dx, dy, 6, 10);
        } else {
            batch.draw(Assets.playerNumbers[f0][fy], dx - w0 / 2, dy, 6, 10);
        }
    }

    void updateCamera(ActionCamera.Mode mode) {
        vcameraX[training.subframe] = actionCamera.updateX(mode);
        vcameraY[training.subframe] = actionCamera.updateY(mode);
    }

    private void drawPlayerNumberAndName(Player player) {
        Assets.font10.draw(batch, player.number + " " + player.shirtName, 10, 2, Font.Align.LEFT);
    }
}
