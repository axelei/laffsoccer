package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLColor;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.Gdx.gl;
import static com.ygames.ysoccer.match.Const.BALL_ZONE_DX;
import static com.ygames.ysoccer.match.Const.BALL_ZONE_DY;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static java.lang.Math.min;

public class MatchRenderer extends Renderer {

    public Match match;
    private MatchState matchState;
    private List<PlayerSprite> radarPlayers;

    MatchRenderer(GLGraphics glGraphics, Match match) {
        super();
        this.batch = glGraphics.batch;
        this.shapeRenderer = glGraphics.shapeRenderer;
        this.camera = glGraphics.camera;
        this.match = match;
        this.ball = match.ball;
        actionCamera = new ActionCamera(ball);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), match.settings.zoom);

        actionCamera.x = 0.5f * (Const.PITCH_W - screenWidth / (zoom / 100.0f));
        actionCamera.y = 0;
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        radarPlayers = new ArrayList<>();
        allSprites.add(new BallSprite(glGraphics, match.ball));
        for (int t = HOME; t <= AWAY; t++) {
            CoachSprite coachSprite = new CoachSprite(glGraphics, match.team[t].coach);
            allSprites.add(coachSprite);
            int len = match.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                PlayerSprite playerSprite = new PlayerSprite(glGraphics, match.team[t].lineup.get(i));
                allSprites.add(playerSprite);
                if (i < Const.TEAM_SIZE) {
                    radarPlayers.add(playerSprite);
                }
            }
        }

        cornerFlagSprites = new CornerFlagSprite[4];
        for (int i = 0; i < 4; i++) {
            cornerFlagSprites[i] = new CornerFlagSprite(glGraphics, match.settings, i / 2 * 2 - 1, i % 2 * 2 - 1);
            allSprites.add(cornerFlagSprites[i]);
        }
        allSprites.add(new GoalTopA(glGraphics));
        allSprites.add(new GoalTopB(glGraphics));

        Assets.crowdRenderer.setMaxRank(match.getRank());
    }

    public void render() {
        matchState = match.fsm.getState();

        gl.glEnable(GL20.GL_BLEND);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(true, Gdx.graphics.getWidth() * 100f / zoom, Gdx.graphics.getHeight() * 100f / zoom);
        camera.translate(-Const.CENTER_X + vcameraX[match.subframe], -Const.CENTER_Y + vcameraY[match.subframe], 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground();

        if (Settings.development && Settings.showBallZones) {
            drawBallZones();
        }

        Assets.crowdRenderer.draw(batch);

        renderSprites(match.subframe);

        // redraw bottom goal
        batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

        // redraw jumpers
        // top
        if (match.ball.data[match.subframe].y < -Const.JUMPER_Y) {
            batch.draw(Assets.jumper, -Const.JUMPER_X, -Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
            batch.draw(Assets.jumper, +Const.JUMPER_X, -Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
        }
        // bottom
        if (match.ball.data[match.subframe].y < +Const.JUMPER_Y) {
            batch.draw(Assets.jumper, -Const.JUMPER_X, +Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
            batch.draw(Assets.jumper, +Const.JUMPER_X, +Const.JUMPER_Y - 40, 2, 42, 0, 0, 2, 42, false, true);
        }

        if (match.settings.weatherStrength != Weather.Strength.NONE) {
            switch (match.settings.weatherEffect) {
                case Weather.RAIN:
                    drawRain(match.settings, match.subframe);
                    break;

                case Weather.SNOW:
                    drawSnow(match.settings, match.subframe);
                    break;

                case Weather.FOG:
                    drawFog(match.settings, match.subframe);
                    break;
            }
        }

        if (matchState.displayControlledPlayer) {
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
        if (matchState.displayBallOwner && match.ball.owner != null) {
            drawPlayerNumberAndName(match.ball.owner);
        }

        // clock
        if (matchState.displayTime) {
            drawTime();
        }

        // radar
        if (matchState.displayRadar && match.settings.radar) {
            drawRadar(match.subframe);
        }

        // wind vane
        if (matchState.displayWindVane && (match.settings.wind.speed > 0)) {
            batch.draw(Assets.wind[match.settings.wind.direction][match.settings.wind.speed - 1], guiWidth - 50, 20);
        }

        // rosters
        if (matchState.displayRosters) {
            drawRosters();
        }

        // score
        if (matchState.displayScore) {
            drawScore();
        }

        // penalties score
        if (matchState.displayPenaltiesScore) {
            drawPenaltiesScore();
        }

        // messages
        if (match.fsm.matchKeys.messageTimer > 0) {
            batch.setColor(0xFFFFFF, guiAlpha);
            Assets.font10.draw(batch, match.fsm.matchKeys.message, guiWidth / 2, 1, Font.Align.CENTER);
        }

        // statistics
        if (matchState.displayStatistics) {
            drawStatistics();
        }

        // goal scorer
        if (matchState.displayGoalScorer && (match.subframe % 160 > 80)) {
            drawPlayerNumberAndName(match.ball.goalOwner);
        }

        if (matchState.displayBenchPlayers) {
            drawBenchPlayers();
        }

        if (matchState.displayBenchFormation) {
            drawBenchFormation();
        }

        if (matchState.displayTacticsSwitch) {
            drawTacticsSwitch();
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

    private void drawBallZones() {
        batch.end();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();
        shapeRenderer.setColor(0xAAAA00, 0.4f);
        for (int x = 0; x < 6; x++) {
            shapeRenderer.line((-2.5f + x) * BALL_ZONE_DX, (-3.5f) * BALL_ZONE_DY, (-2.5f + x) * BALL_ZONE_DX, 3.5f * BALL_ZONE_DY);
        }
        for (int y = 0; y < 8; y++) {
            shapeRenderer.line(-2.5f * BALL_ZONE_DX, (-3.5f + y) * BALL_ZONE_DY, 2.5f * BALL_ZONE_DX, (-3.5f + y) * BALL_ZONE_DY);
        }
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    protected void drawShadows(int subframe) {
        batch.setColor(0xFFFFFF, match.settings.shadowAlpha);

        Data d = match.ball.data[subframe];
        batch.draw(Assets.ball[4], d.x - 1 + 0.65f * d.z, d.y - 3 + 0.46f * d.z);
        if (match.settings.time == MatchSettings.Time.NIGHT) {
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
                        if (match.settings.time == MatchSettings.Time.NIGHT) {
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
        for (int i = 0; i < (match.settings.time == MatchSettings.Time.NIGHT ? 4 : 1); i++) {
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

        batch.setColor(0xFFFFFF, 1f);
    }

    private void drawControlledPlayersNumbers() {
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            if (match.team[t] != null) {
                int len = match.team[t].lineup.size();
                for (int i = 0; i < len; i++) {
                    Player player = match.team[t].lineup.get(i);
                    if (player.inputDevice != player.ai && player.isVisible) {
                        drawPlayerNumber(player);
                    }
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

        int fy = match.settings.pitchType == Pitch.Type.WHITE ? 1 : 0;
        if (f1 > 0) {
            dx = dx - (w0 + 2 + w1) / 2;
            batch.draw(Assets.playerNumbers[f1][fy], dx, dy, 6, 10);
            dx = dx + w1 + 2;
            batch.draw(Assets.playerNumbers[f0][fy], dx, dy, 6, 10);
        } else {
            batch.draw(Assets.playerNumbers[f0][fy], dx - w0 / 2, dy, 6, 10);
        }
    }

    void drawRosters() {

        int l = 13 + (guiWidth - 360) / 5 + 2;
        int r = guiWidth - l + 2;
        int w = r - l;
        int t = guiHeight / 2 - 270 + 2;
        int b = guiHeight / 2 + 270 + 2;
        int h = b - t;
        int m1 = t + h / 8 + 2;
        int m2 = t + h / 3 + 2;
        int hw = guiWidth / 2 + 2;

        // fading
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        fadeRect(l + 2, t + 2, r - 2, b - 2, 0.35f, 0x000000);

        // line's shadows
        shapeRenderer.setColor(0x242424, guiAlpha);
        drawRosterLines(l, r, w, t, b, m1, m2, hw);

        l = l - 2;
        r = r - 2;
        t = t - 2;
        b = b - 2;
        m1 = m1 - 2;
        m2 = m2 - 2;
        hw = hw - 2;

        // lines
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);
        drawRosterLines(l, r, w, t, b, m1, m2, hw);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // title
        int y = t + h / 23;
        Assets.font14.draw(batch, match.competition.name, guiWidth / 2, y, Font.Align.CENTER);

        // city & stadium
        if (match.team[HOME].city.length() > 0 && match.team[HOME].stadium.length() > 0) {
            y = t + h / 6;
            Assets.font14.draw(batch, match.team[HOME].stadium + ", " + match.team[HOME].city, guiWidth / 2, y, Font.Align.CENTER);
        }

        // club logos / national flags
        y = t + 13 * h / 100;

        if (match.team[HOME].image != null) {
            int w0 = match.team[HOME].image.getRegionWidth();
            int h0 = match.team[HOME].image.getRegionHeight();
            float imageScale0 = (h0 > 70) ? 70f / h0 : 1f;
            int x0 = l + w / 23;
            int y0 = y - (int) (imageScale0 * h0) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[HOME].image, x0 + 2, y0 + 2, 0, 0, w0, h0, imageScale0, imageScale0, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[HOME].image, x0, y0, 0, 0, w0, h0, imageScale0, imageScale0, 0);
        }
        if (match.team[AWAY].image != null) {
            int w1 = match.team[AWAY].image.getRegionWidth();
            int h1 = match.team[AWAY].image.getRegionHeight();
            float imageScale1 = (h1 > 70) ? 70f / h1 : 1f;
            int x1 = r - w / 23 - (int) (imageScale1 * w1);
            int y1 = y - (int) (imageScale1 * h1) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[AWAY].image, x1 + 2, y1 + 2, 0, 0, w1, h1, imageScale1, imageScale1, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[AWAY].image, x1, y1, 0, 0, w1, h1, imageScale1, imageScale1, 0);
        }

        // team name
        y = t + h / 4;
        Assets.font14.draw(batch, match.team[HOME].name, l + w / 4, y, Font.Align.CENTER);
        Assets.font14.draw(batch, match.team[AWAY].name, l + 3 * w / 4, y, Font.Align.CENTER);

        // players
        for (int tm = HOME; tm <= AWAY; tm++) {
            y = t + 16 * h / 42;
            for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
                Player player = match.team[tm].playerAtPosition(pos);
                Assets.font10.draw(batch, player.number, l + tm * w / 2 + w / 10, y, Font.Align.CENTER);
                Assets.font10.draw(batch, player.shirtName, l + tm * w / 2 + w / 7, y, Font.Align.LEFT);
                y = y + h / 23;
            }
        }

        // coach
        y = t + 7 * h / 8;
        Assets.font10.draw(batch, Assets.strings.get("COACH") + ":", l + 2 * w / 25, y, Font.Align.LEFT);
        Assets.font10.draw(batch, Assets.strings.get("COACH") + ":", l + 5 * w / 9, y, Font.Align.LEFT);

        y = t + 37 * h / 40;
        Assets.font10.draw(batch, match.team[HOME].coach.name, l + w / 4, y, Font.Align.CENTER);
        Assets.font10.draw(batch, match.team[AWAY].coach.name, l + 3 * w / 4, y, Font.Align.CENTER);
    }

    private void drawRosterLines(int l, int r, int w, int t, int b, int m1, int m2, int hw) {
        drawFrame(l, t, r - l, b - t);

        // middle
        shapeRenderer.rect(hw - 0.2f * w, m1, 0.4f * w, 1);
        shapeRenderer.rect(hw - 0.2f * w, m1 + 1, 0.4f * w, 1);

        // middle left
        shapeRenderer.rect(l + 0.05f * w, m2, hw - l - 0.1f * w, 1);
        shapeRenderer.rect(l + 0.05f * w, m2 + 1, hw - l - 0.1f * w, 1);

        // middle right
        shapeRenderer.rect(hw + 0.05f * w, m2, r - hw - 0.1f * w, 1);
        shapeRenderer.rect(hw + 0.05f * w, m2 + 1, r - hw - 0.1f * w, 1);
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

    private void drawRadar(int subframe) {

        final int RX = 10;
        final int RY = 60;
        final int RW = 132;
        final int RH = 166;

        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        fadeRect(RX, RY, RX + RW, RY + RH, 0.6f, match.settings.grass.darkShadow);

        shapeRenderer.setColor(0x000000, 1f);
        shapeRenderer.rect(RX, RY, 1, RH);
        shapeRenderer.rect(RX + 1, RY, RW - 2, 1);
        shapeRenderer.rect(RX + 1, RY + RH / 2, RW - 2, 1);
        shapeRenderer.rect(RX + 1, RY + RH - 1, RW - 2, 1);
        shapeRenderer.rect(RX + RW - 1, RY, 1, RH);

        // prepare y-sorted list
        spriteComparator.subframe = subframe;
        Collections.sort(radarPlayers, spriteComparator);

        // shirt colors
        int[] shirt1 = new int[2];
        int[] shirt2 = new int[2];
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            Kit kit = match.team[t].getKit();
            shirt1[t] = kit.shirt1;
            shirt2[t] = kit.shirt2;
        }

        // placeholders
        for (PlayerSprite playerSprite : radarPlayers) {
            Player player = playerSprite.player;
            Data d = player.data[subframe];
            if (d.isVisible) {
                int dx = RX + RW / 2 + d.x / 8;
                int dy = RY + RH / 2 + d.y / 8;

                shapeRenderer.setColor(0x242424, 1f);
                shapeRenderer.rect(dx - 3, dy - 3, 6, 1);
                shapeRenderer.rect(dx - 4, dy - 2, 1, 4);
                shapeRenderer.rect(dx - 3, dy + 2, 6, 1);
                shapeRenderer.rect(dx + 3, dy - 2, 1, 4);

                shapeRenderer.setColor(shirt1[player.team.index], 1f);
                shapeRenderer.rect(dx - 3, dy - 2, 3, 4);

                shapeRenderer.setColor(shirt2[player.team.index], 1f);
                shapeRenderer.rect(dx, dy - 2, 3, 4);
            }
        }

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // controlled players numbers
        if (matchState.displayControlledPlayer) {
            for (PlayerSprite playerSprite : radarPlayers) {
                Player player = playerSprite.player;
                Data d = player.data[subframe];
                if ((d.isVisible) && (player.inputDevice != player.ai)) {
                    int dx = RX + RW / 2 + d.x / 8 + 1;
                    int dy = RY + RH / 2 + d.y / 8 - 10;

                    int f0 = player.number % 10;
                    int f1 = (player.number - f0) / 10 % 10;

                    int w0, w1;
                    if (f1 > 0) {
                        w0 = 4 - (f0 == 1 ? 2 : 0);
                        w1 = 4 - (f1 == 1 ? 2 : 0);
                        dx = dx - (w0 + w1) / 2;
                        batch.draw(Assets.tinyNumbers[f1], dx, dy);
                        dx = dx + w1;
                        batch.draw(Assets.tinyNumbers[f0], dx, dy);
                    } else {
                        w0 = 4 - (f0 == 1 ? 2 : 0);
                        dx = dx - w0 / 2;
                        batch.draw(Assets.tinyNumbers[f0], dx, dy);
                    }
                }
            }
        }
    }

    private void drawScore() {
        // default values
        int h0 = 0;
        int w0 = 0;
        int w1 = 0;
        int h1 = 0;
        float imageScale0 = 1f;
        float imageScale1 = 1f;

        // max rows of rows
        int rows = Math.max(match.scorers.rows[HOME].size(), match.scorers.rows[AWAY].size());

        // size of club logos / national flags
        if (match.team[HOME].image != null) {
            w0 = match.team[HOME].image.getRegionWidth();
            h0 = match.team[HOME].image.getRegionHeight();
            if (h0 > 70) {
                imageScale0 = 70f / h0;
            }
        }
        if (match.team[AWAY].image != null) {
            w1 = match.team[AWAY].image.getRegionWidth();
            h1 = match.team[AWAY].image.getRegionHeight();
            if (h1 > 70) {
                imageScale1 = 70f / h1;
            }
        }

        int hMax = Math.max((int) (imageScale0 * h0), (int) (imageScale1 * h1));
        int y0 = guiHeight - 16 - Math.max(hMax, 14 * rows);

        // club logos / national flags
        if (match.team[HOME].image != null) {
            int x = 12;
            int y = y0 + 8 + (hMax - (int) (imageScale0 * h0)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[HOME].image, x + 2, y + 2, 0, 0, w0, h0, imageScale0, imageScale0, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[HOME].image, x, y, 0, 0, w0, h0, imageScale0, imageScale0, 0);
        }
        if (match.team[AWAY].image != null) {
            int x = guiWidth - (int) (imageScale1 * w1) - 12;
            int y = y0 + 8 + (hMax - (int) (imageScale1 * h1)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[AWAY].image, x + 2, y + 2, 0, 0, w1, h1, imageScale1, imageScale1, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[AWAY].image, x, y, 0, 0, w1, h1, imageScale1, imageScale1, 0);
        }

        // teams
        Assets.font14.draw(batch, match.team[HOME].name, +12, y0 - 22, Font.Align.LEFT);
        Assets.font14.draw(batch, match.team[AWAY].name, guiWidth - 10, y0 - 22, Font.Align.RIGHT);

        // bars
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);

        shapeRenderer.rect(10, y0, guiWidth / 2f - 22, 2);
        shapeRenderer.rect(guiWidth / 2f + 12, y0, guiWidth / 2f - 22, 2);

        shapeRenderer.setColor(0x242424, guiAlpha);
        shapeRenderer.rect(12, y0 + 2, guiWidth / 2f - 22, 2);
        shapeRenderer.rect(guiWidth / 2f + 14, y0 + 2, guiWidth / 2f - 22, 2);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // home score
        int f0 = match.stats[Match.HOME].goals % 10;
        int f1 = ((match.stats[Match.HOME].goals - f0) / 10) % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2 - 15 - 48, y0 - 40);
        }
        batch.draw(Assets.score[f0], guiWidth / 2 - 15 - 24, y0 - 40);

        // "-"
        batch.draw(Assets.score[10], guiWidth / 2f - 9, y0 - 40);

        // away score
        f0 = match.stats[Match.AWAY].goals % 10;
        f1 = (match.stats[Match.AWAY].goals - f0) / 10 % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2f + 17, y0 - 40);
            batch.draw(Assets.score[f0], guiWidth / 2f + 17 + 24, y0 - 40);
        } else {
            batch.draw(Assets.score[f0], guiWidth / 2f + 17, y0 - 40);
        }

        // scorers
        for (int t = HOME; t <= AWAY; t++) {
            int y = y0 + 4;
            for (String row : match.scorers.rows[t]) {
                int x = guiWidth / 2 + (t == HOME ? -12 : +14);
                Font.Align align = t == HOME ? Font.Align.RIGHT : Font.Align.LEFT;
                Assets.font10.draw(batch, row, x, y, align);
                y += 14;
            }
        }
    }

    private void drawPenaltiesScore() {
        // default values
        int h0 = 0;
        int w0 = 0;
        int w1 = 0;
        int h1 = 0;
        float imageScale0 = 1f;
        float imageScale1 = 1f;

        // max rows of rows
        int rows = Math.max(match.penalties[HOME].size(), match.penalties[AWAY].size());

        // size of club logos / national flags
        if (match.team[HOME].image != null) {
            w0 = match.team[HOME].image.getRegionWidth();
            h0 = match.team[HOME].image.getRegionHeight();
            if (h0 > 70) {
                imageScale0 = 70f / h0;
            }
        }
        if (match.team[AWAY].image != null) {
            w1 = match.team[AWAY].image.getRegionWidth();
            h1 = match.team[AWAY].image.getRegionHeight();
            if (h1 > 70) {
                imageScale1 = 70f / h1;
            }
        }

        int hMax = Math.max((int) (imageScale0 * h0), (int) (imageScale1 * h1));
        int y0 = guiHeight - 16 - Math.max(hMax, 14 * rows);

        // club logos / national flags
        if (match.team[HOME].image != null) {
            int x = 12;
            int y = y0 + 8 + (hMax - (int) (imageScale0 * h0)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[HOME].image, x + 2, y + 2, 0, 0, w0, h0, imageScale0, imageScale0, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[HOME].image, x, y, 0, 0, w0, h0, imageScale0, imageScale0, 0);
        }
        if (match.team[AWAY].image != null) {
            int x = guiWidth - (int) (imageScale1 * w1) - 12;
            int y = y0 + 8 + (hMax - (int) (imageScale1 * h1)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[AWAY].image, x + 2, y + 2, 0, 0, w1, h1, imageScale1, imageScale1, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[AWAY].image, x, y, 0, 0, w1, h1, imageScale1, imageScale1, 0);
        }

        // teams
        Assets.font14.draw(batch, match.team[HOME].name, +12, y0 - 22, Font.Align.LEFT);
        Assets.font14.draw(batch, match.team[AWAY].name, guiWidth - 10, y0 - 22, Font.Align.RIGHT);

        // bars
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);

        shapeRenderer.rect(10, y0, guiWidth / 2f - 22, 2);
        shapeRenderer.rect(guiWidth / 2f + 12, y0, guiWidth / 2f - 22, 2);

        shapeRenderer.setColor(0x242424, guiAlpha);
        shapeRenderer.rect(12, y0 + 2, guiWidth / 2f - 22, 2);
        shapeRenderer.rect(guiWidth / 2f + 14, y0 + 2, guiWidth / 2f - 22, 2);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // home score
        int homeScore = match.penaltyGoals(HOME);
        int f0 = homeScore % 10;
        int f1 = ((homeScore - f0) / 10) % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2 - 15 - 48, y0 - 40);
        }
        batch.draw(Assets.score[f0], guiWidth / 2 - 15 - 24, y0 - 40);

        // "-"
        batch.draw(Assets.score[10], guiWidth / 2f - 9, y0 - 40);

        // away score
        int awayScore = match.penaltyGoals(AWAY);
        f0 = awayScore % 10;
        f1 = (awayScore - f0) / 10 % 10;

        if (f1 > 0) {
            batch.draw(Assets.score[f1], guiWidth / 2f + 17, y0 - 40);
            batch.draw(Assets.score[f0], guiWidth / 2f + 17 + 24, y0 - 40);
        } else {
            batch.draw(Assets.score[f0], guiWidth / 2f + 17, y0 - 40);
        }

        // scorers
        for (int t = HOME; t <= AWAY; t++) {
            int y = y0 + 4;
            for (Match.Penalty penalty : match.penalties[t]) {
                int x = guiWidth / 2 + (t == HOME ? -12 : +14);
                String text = "";
                switch (penalty.state) {
                    case TO_KICK:
                        text = " ";
                        break;
                    case SCORED:
                        text = t == HOME ? penalty.kicker.shirtName + " " + (char) 25 : (char) 25 + " " + penalty.kicker.shirtName;
                        break;
                    case MISSED:
                        text = t == HOME ? penalty.kicker.shirtName + " " + (char) 26 : (char) 26 + " " + penalty.kicker.shirtName;
                        break;
                }

                Font.Align align = t == HOME ? Font.Align.RIGHT : Font.Align.LEFT;
                Assets.font10.draw(batch, text, x, y, align);
                y += 14;
            }
        }
    }

    private void drawStatistics() {

        int l = 13 + (guiWidth - 360) / 5 + 2;
        int r = guiWidth - l + 2;
        int w = r - l;
        int t = guiHeight / 2 - 270 + 2;
        int b = guiHeight / 2 + 270 + 2;
        int h = b - t;
        int hw = guiWidth / 2;

        // fading
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // top strip
        fadeRect(l + 2, t + 2, r - 2, t + h / 10 + 1, 0.35f, 0x000000);

        // middle strips
        int i = t + h / 10 + 2;
        for (int j = 1; j < 9; j++) {
            fadeRect(l + 2, i + 1, r - 2, i + h / 10 - 1, 0.35f, 0x000000);
            i = i + h / 10;
        }

        // bottom strip
        fadeRect(l + 2, i + 1, r - 2, b - 2, 0.35f, 0x000000);

        // frame shadow
        shapeRenderer.setColor(0x242424, guiAlpha);
        drawFrame(l, t, r - l, b - t);

        l = l - 2;
        r = r - 2;
        t = t - 2;
        b = b - 2;

        // frame
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);
        drawFrame(l, t, r - l, b - t);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        MatchStats homeStats = match.stats[Match.HOME];
        MatchStats awayStats = match.stats[Match.AWAY];

        int possHome = Math.round(100 * (1f + match.stats[Match.HOME].ballPossession) / (2f + homeStats.ballPossession + awayStats.ballPossession));
        int possAway = 100 - possHome;

        // text
        int lc = l + w / 5;
        int rc = r - w / 5;
        i = t + h / 20 - 8;
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS"), hw, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, match.team[Match.HOME].name, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, match.team[Match.AWAY].name, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        int homeGoals = (match.penalties[HOME].size() > 0) ? match.penaltiesScore(HOME) : homeStats.goals;
        int awayGoals = (match.penalties[AWAY].size() > 0) ? match.penaltiesScore(AWAY) : awayStats.goals;
        Assets.font14.draw(batch, homeGoals, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.GOALS"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayGoals, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, possHome, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.POSSESSION"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, possAway, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, homeStats.overallShots, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.GOAL ATTEMPTS"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayStats.overallShots, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, homeStats.centeredShots, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.ON TARGET"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayStats.centeredShots, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, homeStats.cornersWon, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.CORNERS WON"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayStats.cornersWon, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, homeStats.foulsConceded, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.FOULS CONCEDED"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayStats.foulsConceded, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, homeStats.yellowCards, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.BOOKINGS"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayStats.yellowCards, rc, i, Font.Align.CENTER);

        i = i + h / 10;
        Assets.font14.draw(batch, homeStats.redCards, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.SENDINGS OFF"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayStats.redCards, rc, i, Font.Align.CENTER);

        batch.setColor(0xFFFFFF, 1f);
    }

    private void fadeRect(int x0, int y0, int x1, int y1, float alpha, int color) {
        shapeRenderer.setColor(color, alpha);
        shapeRenderer.rect(x0, y0, x1 - x0, y1 - y0);
    }

    private void drawFrame(int x, int y, int w, int h) {
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

    private void drawBenchPlayers() {
        int w = 250;
        int h = 18;

        int x = guiWidth / 3 + 2;
        int y = guiHeight / 2 - 100 + 2;

        // objects' shadows //
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0x242424, guiAlpha);

        // title
        drawFrame(x, y, w, h + 2);

        // image
        shapeRenderer.rect(x + w / 2 - 41, y + 41, 82, 66);

        // list
        drawFrame(x, y + 125, w, match.settings.benchSize * h + 6);

        // slots
        int color = 0x242424;

        if (match.fsm.benchStatus.selectedPosition == -1) {
            color = GLColor.sweepColor(color, 0xFFDD33);
        }

        fadeRect(x, y + 2, x + w - 2, y + h, 0.6f, color);

        for (int pos = 0; pos < match.settings.benchSize; pos++) {
            color = 0x242424;

            if (match.fsm.benchStatus.selectedPosition == pos) {
                color = GLColor.sweepColor(color, 0xFFDD33);
            }
            fadeRect(x, y + 125 + 4 + pos * h, x + w - 2, y + 125 + 2 + (pos + 1) * h, 0.6f, color);
        }

        x = x - 2;
        y = y - 2;

        // objects //
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);

        // title
        drawFrame(x, y, w, h + 2);

        // list
        drawFrame(x, y + 125, w, match.settings.benchSize * h + 6);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // image
        batch.draw(Assets.bench[0], x + w / 2 - 41, y + 41);

        Assets.font10.draw(batch, Assets.strings.get("BENCH"), x + w / 2, y + 3, Font.Align.CENTER);

        int benchSize = min(match.settings.benchSize, match.fsm.benchStatus.team.lineup.size() - TEAM_SIZE);
        for (int pos = 0; pos < benchSize; pos++) {
            Player player = match.fsm.benchStatus.team.lineupAtPosition(TEAM_SIZE + pos);

            if (!player.getState().checkId(STATE_OUTSIDE)) {
                Assets.font10.draw(batch, player.number, x + 25, y + 5 + 125 + pos * h, Font.Align.CENTER);
                Assets.font10.draw(batch, player.shirtName, x + 45, y + 5 + 125 + pos * h, Font.Align.LEFT);
                Assets.font10.draw(batch, Assets.strings.get(player.getRoleLabel()), x + w - 20, y + 5 + 125 + pos * h, Font.Align.CENTER);
            }
        }
    }

    private void drawBenchFormation() {
        int w = 250;
        int h = 18;

        int x = guiWidth / 3 + 2;
        int y = guiHeight / 2 - 150 + 2;

        // objects' shadows //
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0x242424, guiAlpha);

        // title
        drawFrame(x, y, w, h + 2);

        // image
        shapeRenderer.rect(x + w / 2 - 41, y + 41, 82, 66);

        // list
        drawFrame(x, y + 125, w, TEAM_SIZE * h + 6);

        // slots
        int color = 0x242424;
        if (match.fsm.benchStatus.selectedPosition == -1) {
            // substitution - yellow
            if (match.fsm.benchStatus.substPosition != -1) {
                color = GLColor.sweepColor(color, 0xFFFF33);
            }
            // swap - blue
            else {
                color = GLColor.sweepColor(color, 0x33DDFF);
            }
        }
        fadeRect(x, y + 2, x + w - 2, y + h, 0.6f, color);

        for (int pos = 0; pos < TEAM_SIZE; pos++) {
            color = 0x242424;
            if (pos == match.fsm.benchStatus.swapPosition) {
                color = 0x33DDFF;
            }

            if (pos == match.fsm.benchStatus.selectedPosition) {
                // substitution - yellow
                if (match.fsm.benchStatus.substPosition != -1) {
                    color = GLColor.sweepColor(0x242424, 0xFFFF33);
                }
                // swap - blue
                else {
                    color = GLColor.sweepColor(color, 0x33DDFF);
                }
            }
            fadeRect(x, y + 125 + 4 + pos * h, x + w - 2, y + 125 + 2 + (pos + 1) * h, 0.6f, color);
        }

        x = x - 2;
        y = y - 2;

        // objects //
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);

        // title
        drawFrame(x, y, w, h + 2);

        // list
        drawFrame(x, y + 125, w, TEAM_SIZE * h + 6);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        // image
        batch.draw(Assets.bench[1], x + w / 2 - 41, y + 41);

        Assets.font10.draw(batch, Assets.strings.get("FORMATION"), x + w / 2, y + 3, Font.Align.CENTER);

        for (int pos = 0; pos < TEAM_SIZE; pos++) {

            Player ply = match.fsm.benchStatus.team.lineupAtPosition(pos);

            Assets.font10.draw(batch, ply.number, x + 25, y + 5 + 125 + pos * h, Font.Align.CENTER);
            Assets.font10.draw(batch, ply.shirtName, x + 45, y + 5 + 125 + pos * h, Font.Align.LEFT);
            Assets.font10.draw(batch, Assets.strings.get(ply.getRoleLabel()), x + w - 20, y + 5 + 125 + pos * h, Font.Align.CENTER);
        }
    }

    private void drawTacticsSwitch() {
        int w = 180;
        int h = 18;

        int x = guiWidth / 3 + 35 + 2;
        int y = guiHeight / 2 - 186 + 2;

        // objects' shadows //
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0x242424, guiAlpha);

        // image
        shapeRenderer.rect(x + w / 2 - 41, y, 82, 66);

        // frame
        drawFrame(x, y + 80, w, 18 * h + 6);

        // slots
        for (int i = 0; i < 18; i++) {
            int color = 0x242424;
            if (i == match.fsm.benchStatus.team.tactics) {
                color = 0xFFAA33;
            }
            if (i == match.fsm.benchStatus.selectedTactics) {
                color = GLColor.sweepColor(color, 0xFFAA33);
            }
            fadeRect(x, y + 84 + h * i, x + w - 2, y + 82 + h * (i + 1), 0.6f, color);
        }

        x = x - 2;
        y = y - 2;

        // objects //
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);

        drawFrame(x, y + 80, w, 18 * h + 6);

        shapeRenderer.end();
        batch.begin();
        batch.setColor(0xFFFFFF, guiAlpha);

        batch.draw(Assets.bench[1], x + w / 2 - 41, y);

        for (int i = 0; i < 18; i++) {
            Assets.font10.draw(batch, Tactics.codes[i], x + w / 2, y + 85 + h * i, Font.Align.CENTER);
        }
    }

    void updateCamera(ActionCamera.Mode mode) {
        actionCamera.update(mode);
    }

    void save() {
        ball.save(match.subframe);
        match.team[HOME].save(match.subframe);
        match.team[AWAY].save(match.subframe);
        vcameraX[match.subframe] = Math.round(actionCamera.x);
        vcameraY[match.subframe] = Math.round(actionCamera.y);
    }

    private void drawPlayerNumberAndName(Player player) {
        Assets.font10.draw(batch, player.number + " " + player.shirtName, 10, 2, Font.Align.LEFT);
    }
}
