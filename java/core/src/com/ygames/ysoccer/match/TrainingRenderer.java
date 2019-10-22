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

    private TrainingState trainingState;
    private final BallSprite ballSprite;

    TrainingRenderer(GLGraphics glGraphics, Training training) {
        super(training);
        this.batch = glGraphics.batch;
        this.shapeRenderer = glGraphics.shapeRenderer;
        this.camera = glGraphics.camera;
        this.ball = training.ball;
        actionCamera = new ActionCamera(ball);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scene.settings.zoom);

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
            cornerFlagSprites[i] = new CornerFlagSprite(glGraphics, scene.settings, i / 2 * 2 - 1, i % 2 * 2 - 1);
            allSprites.add(cornerFlagSprites[i]);
        }
        allSprites.add(new GoalTopA(glGraphics));
        allSprites.add(new GoalTopB(glGraphics));
    }

    private Training getTraining() {
        return (Training) scene;
    }

    public void render() {
        trainingState = getTraining().getFsm().getState();

        gl.glEnable(GL20.GL_BLEND);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(true, Gdx.graphics.getWidth() * 100f / zoom, Gdx.graphics.getHeight() * 100f / zoom);
        camera.translate(-Const.CENTER_X + vCameraX[scene.subframe], -Const.CENTER_Y + vCameraY[scene.subframe], 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground();

        if (Settings.showDevelopmentInfo && Settings.showBallPredictions) {
            drawBallPredictions(ball);
        }

        renderSprites();

        redrawBallShadowsOverGoals(getTraining().ball);
        redrawBallOverTopGoal(ballSprite);

        // redraw bottom goal
        batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

        redrawBallShadowsOverGoals(getTraining().ball);
        redrawBallOverBottomGoal(ballSprite);

        if (scene.settings.weatherStrength != Weather.Strength.NONE) {
            switch (scene.settings.weatherEffect) {
                case Weather.RAIN:
                    drawRain();
                    break;

                case Weather.SNOW:
                    drawSnow();
                    break;

                case Weather.FOG:
                    drawFog();
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
        if (getTraining().ball.owner != null) {
            drawPlayerNumberAndName(getTraining().ball.owner);
        }

        // wind vane
        if (scene.settings.wind.speed > 0) {
            batch.draw(Assets.wind[scene.settings.wind.direction][scene.settings.wind.speed - 1], guiWidth - 50, 20);
        }

        // messages
        if (getTraining().fsm.getHotKeys().messageTimer > 0) {
            batch.setColor(0xFFFFFF, guiAlpha);
            Assets.font10.draw(batch, getTraining().fsm.getHotKeys().message, guiWidth / 2, 1, Font.Align.CENTER);
        }

        // additional state-specific render
        TrainingState trainingState = getTraining().getFsm().getState();
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
    protected void drawShadows() {
        batch.setColor(0xFFFFFF, scene.settings.shadowAlpha);

        drawBallShadow(getTraining().ball, false);

        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i].drawShadow(scene.subframe, batch);
        }

        // keepers
        for (int t = HOME; t <= AWAY; t++) {
            for (Player player : getTraining().team[t].lineup) {
                if (player.role == Player.Role.GOALKEEPER) {
                    Data d = player.data[scene.subframe];
                    if (d.isVisible) {
                        Integer[] origin = Assets.keeperOrigins[d.fmy][d.fmx];
                        batch.draw(Assets.keeperShadow[d.fmx][d.fmy][0], d.x - origin[0] + 0.65f * d.z, d.y - origin[1] + 0.46f * d.z);
                        if (scene.settings.time == MatchSettings.Time.NIGHT) {
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
        for (int i = 0; i < (scene.settings.time == MatchSettings.Time.NIGHT ? 4 : 1); i++) {
            for (int t = HOME; t <= AWAY; t++) {
                for (Player player : getTraining().team[t].lineup) {
                    if (player.role != Player.Role.GOALKEEPER) {
                        Data d = player.data[scene.subframe];
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
            if (getTraining().team[t] != null) {
                int len = getTraining().team[t].lineup.size();
                for (int i = 0; i < len; i++) {
                    Player player = getTraining().team[t].lineup.get(i);
                    if ((player.inputDevice != player.ai && player.isVisible)
                            || (Settings.development && Settings.showPlayerNumber)) {
                        drawPlayerNumber(player);
                    }
                }
            }
        }
    }

    @Override
    void save() {
        ball.save(scene.subframe);
        getTraining().team[HOME].save(scene.subframe);
        getTraining().team[AWAY].save(scene.subframe);
        vCameraX[scene.subframe] = Math.round(actionCamera.x);
        vCameraY[scene.subframe] = Math.round(actionCamera.y);
    }
}
