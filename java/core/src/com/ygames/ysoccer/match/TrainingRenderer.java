package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

import static com.badlogic.gdx.Gdx.gl;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class TrainingRenderer extends SceneRenderer {

    private final Training training;
    private TrainingState trainingState;
    private final BallSprite ballSprite;

    TrainingRenderer(GLGraphics glGraphics, Training training) {
        this.batch = glGraphics.batch;
        this.shapeRenderer = glGraphics.shapeRenderer;
        this.camera = glGraphics.camera;
        this.training = training;
        this.ball = training.ball;
        actionCamera = new ActionCamera(ball);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), training.settings.zoom);

        actionCamera.x = 0.5f * (Const.PITCH_W - screenWidth / (zoom / 100.0f));
        actionCamera.y = 0.5f * (Const.PITCH_H - screenHeight / (zoom / 100.0f));
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vCameraX[i] = Math.round(actionCamera.x);
            vCameraY[i] = Math.round(actionCamera.y);
        }

        ballSprite = new BallSprite(glGraphics, training.ball);
        allSprites.add(ballSprite);
        CoachSprite coachSprite = new CoachSprite(glGraphics, training.team[HOME].coach);
        allSprites.add(coachSprite);

        for (int t = HOME; t <= AWAY; t++) {
            int len = training.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                PlayerSprite playerSprite = new PlayerSprite(glGraphics, training.team[t].lineup.get(i));
                allSprites.add(playerSprite);
            }
        }

        for (int xSide = -1; xSide <= 1; xSide += 2) {
            for (int ySide = -1; ySide <= 1; ySide += 2) {
                allSprites.add(new JumperSprite(glGraphics, xSide, ySide));
            }
        }

        cornerFlagSprites = new CornerFlagSprite[4];
        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i] = new CornerFlagSprite(glGraphics, training.settings, i / 2 * 2 - 1, i % 2 * 2 - 1);
            allSprites.add(cornerFlagSprites[i]);
        }
        allSprites.add(new GoalTopA(glGraphics));
        allSprites.add(new GoalTopB(glGraphics));
    }

    public void render() {
        trainingState = training.getFsm().getState();

        gl.glEnable(GL20.GL_BLEND);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(true, Gdx.graphics.getWidth() * 100f / zoom, Gdx.graphics.getHeight() * 100f / zoom);
        camera.translate(-Const.CENTER_X + vCameraX[training.subframe], -Const.CENTER_Y + vCameraY[training.subframe], 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground();

        if (Settings.showDevelopmentInfo && Settings.showBallPredictions) {
            drawBallPredictions(ball);
        }

        renderSprites(training.subframe);

        redrawBallShadowsOverGoals(batch, training.ball.data[training.subframe], training.settings);
        redrawBallOverTopGoal(ballSprite, training.subframe);

        // redraw bottom goal
        batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

        redrawBallShadowsOverGoals(batch, training.ball.data[training.subframe], training.settings);
        redrawBallOverBottomGoal(ballSprite, training.subframe);

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

        if (trainingState.displayControlledPlayer) {
            drawControlledPlayersNumbers();
        }

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
        if (training.fsm.getHotKeys().messageTimer > 0) {
            batch.setColor(0xFFFFFF, guiAlpha);
            Assets.font10.draw(batch, training.fsm.getHotKeys().message, guiWidth / 2, 1, Font.Align.CENTER);
        }

        // additional state-specific render
        TrainingState trainingState = training.getFsm().getState();
        if (trainingState != null) {
            trainingState.render();
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

        drawBallShadow(training.ball.data[subframe], training.settings, false);

        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i].drawShadow(subframe, batch);
        }

        // keepers
        for (int t = HOME; t <= AWAY; t++) {
            for (Player player : training.team[t].lineup) {
                if (player.role == Player.Role.GOALKEEPER) {
                    Data d = player.data[subframe];
                    if (d.isVisible) {
                        Integer[] origin = Assets.keeperOrigins[d.fmy][d.fmx];
                        batch.draw(Assets.keeperShadow[d.fmx][d.fmy][0], d.x - origin[0] + 0.65f * d.z, d.y - origin[1] + 0.46f * d.z);
                        if (training.settings.time == MatchSettings.Time.NIGHT) {
                            // TODO activate after getting keeper shadows
                            // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][1], d.x - 24 - 0.65f * d.z, d.y - 34 + 0.46f * d.z);
                            // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][2], d.x - 24 - 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                            // batch.draw(Assets.keeperShadow[d.fmx][d.fmy][3], d.x - 24 + 0.65f * d.z, d.y - 34 - 0.46f * d.z);
                        }
                    }
                }
            }
        }

        // players
        for (int i = 0; i < (training.settings.time == MatchSettings.Time.NIGHT ? 4 : 1); i++) {
            for (int t = HOME; t <= AWAY; t++) {
                for (Player player : training.team[t].lineup) {
                    if (player.role != Player.Role.GOALKEEPER) {
                        Data d = player.data[subframe];
                        if (d.isVisible) {
                            Integer[] origin = Assets.playerOrigins[d.fmy][d.fmx];
                            float mX = (i == 0 || i == 3) ? 0.65f : -0.65f;
                            float mY = (i == 0 || i == 1) ? 0.46f : -0.46f;
                            batch.draw(Assets.playerShadow[d.fmx][d.fmy][i], d.x - origin[0] + mX * d.z, d.y - origin[1] + 5 + mY * d.z);
                        }
                    }
                }
            }
        }

        batch.setColor(0xFFFFFF, 1f);
    }

    private void drawControlledPlayersNumbers() {
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            if (training.team[t] != null) {
                int len = training.team[t].lineup.size();
                for (int i = 0; i < len; i++) {
                    Player player = training.team[t].lineup.get(i);
                    if ((player.inputDevice != player.ai && player.isVisible)
                            || (Settings.development && Settings.showPlayerNumber)) {
                        drawPlayerNumber(player);
                    }
                }
            }
        }
    }

    private void drawPlayerNumber(Player player) {
        Data d = player.data[training.subframe];

        int f0 = player.number % 10;
        int f1 = (player.number - f0) / 10 % 10;

        int dx = Math.round(d.x) + 1;
        int dy = Math.round(d.y) - 40 - Math.round(d.z);

        int w0 = 6 - ((f0 == 1) ? 2 : 1);
        int w1 = 6 - ((f1 == 1) ? 2 : 1);

        int fy = training.settings.pitchType == Pitch.Type.WHITE ? 1 : 0;
        if (f1 > 0) {
            dx = dx - (w0 + 2 + w1) / 2;
            batch.draw(Assets.playerNumbers[f1][fy], dx, dy, 6, 10);
            dx = dx + w1 + 2;
            batch.draw(Assets.playerNumbers[f0][fy], dx, dy, 6, 10);
        } else {
            batch.draw(Assets.playerNumbers[f0][fy], dx - w0 / 2f, dy, 6, 10);
        }
    }

    @Override
    void save() {
        ball.save(training.subframe);
        training.team[HOME].save(training.subframe);
        training.team[AWAY].save(training.subframe);
        vCameraX[training.subframe] = Math.round(actionCamera.x);
        vCameraY[training.subframe] = Math.round(actionCamera.y);
    }

    private void drawPlayerNumberAndName(Player player) {
        Assets.font10.draw(batch, player.number + " " + player.shirtName, 10, 2, Font.Align.LEFT);
    }
}
