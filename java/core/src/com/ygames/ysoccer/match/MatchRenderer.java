package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.math.Emath;

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

    GLSpriteBatch batch;
    private GLShapeRenderer shapeRenderer;
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
    private MatchState matchState;
    private List<Sprite> allSprites;
    private List<PlayerSprite> radarPlayers;
    private Sprite.SpriteComparator spriteComparator;
    private CornerFlagSprite[] cornerFlagSprites;

    private final int modW = Const.REPLAY_FRAMES;
    private final int modH = 2 * Const.REPLAY_FRAMES;
    private final int modX = (int) Math.ceil(Const.PITCH_W / ((float) modW));
    private final int modY = (int) Math.ceil(Const.PITCH_H / ((float) modH));

    public MatchRenderer(GLGraphics glGraphics, Match match) {
        this.batch = glGraphics.batch;
        this.shapeRenderer = glGraphics.shapeRenderer;
        this.camera = glGraphics.camera;
        this.match = match;

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), match.game.settings);

        actionCamera = new ActionCamera(this);
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        allSprites = new ArrayList<Sprite>();
        radarPlayers = new ArrayList<PlayerSprite>();
        allSprites.add(new BallSprite(glGraphics, match.ball));
        for (int t = HOME; t <= AWAY; t++) {
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

        spriteComparator = new Sprite.SpriteComparator();

        Assets.crowdRenderer.setMaxRank(match.getRank());
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
        matchState = match.fsm.getState();

        gl.glEnable(GL20.GL_BLEND);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(true, Gdx.graphics.getWidth() * 100.0f / zoom, Gdx.graphics.getHeight() * 100.0f / zoom);
        camera.translate(-Const.CENTER_X + vcameraX[match.subframe], -Const.CENTER_Y + vcameraY[match.subframe], 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground();

        Assets.crowdRenderer.draw(batch);

        renderSprites(match.subframe);

        // redraw bottom goal
        batch.draw(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y, 146, 56, 0, 0, 146, 56, false, true);

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
        if (matchState.displayRadar && match.game.settings.radar) {
            drawRadar(match.subframe);
        }

        // wind vane
        if (matchState.displayWindVane && (match.settings.wind.speed > 0)) {
            batch.draw(Assets.wind[match.settings.wind.direction][match.settings.wind.speed - 1], guiWidth - 50, 20);
        }

        // score
        if (matchState.displayScore) {
            drawScore();
        }

        // statistics
        if (matchState.displayStatistics) {
            drawStatistics();
        }

        // goal scorer
        if (matchState.displayGoalScorer && (match.subframe % 160 > 80)) {
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

    private void drawRain(MatchSettings matchSettings, int subframe) {
        batch.setColor(0xFFFFFF, 0.6f);
        Assets.random.setSeed(1);
        for (int i = 1; i <= 40 * matchSettings.weatherStrength; i++) {
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

    private void drawSnow(MatchSettings matchSettings, int subframe) {
        batch.setColor(0xFFFFFF, 0.7f);

        Assets.random.setSeed(1);
        for (int i = 1; i <= 30 * matchSettings.weatherStrength; i++) {
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

    private void drawFog(MatchSettings matchSettings, int subframe) {
        batch.setColor(0xFFFFFF, 0.25f * matchSettings.weatherStrength);

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

    void drawRosters() {

        int l = 13 + (guiWidth - 640) / 5 + 2;
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

        // TODO: club logos/national flags

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
            int x = 10;
            int y = y0 + 8 + (hMax - (int) (imageScale0 * h0)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[HOME].image, x + 2, y + 2, 0, 0, w0, h0, imageScale0, imageScale0, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[HOME].image, x, y, 0, 0, w0, h0, imageScale0, imageScale0, 0);
        }
        if (match.team[AWAY].image != null) {
            int x = guiWidth - (int) (imageScale1 * w1) - 10;
            int y = y0 + 8 + (hMax - (int) (imageScale1 * h1)) / 2;
            batch.setColor(0x242424, guiAlpha);
            batch.draw(match.team[AWAY].image, x + 2, y + 2, 0, 0, w1, h1, imageScale1, imageScale1, 0);
            batch.setColor(0xFFFFFF, guiAlpha);
            batch.draw(match.team[AWAY].image, x, y, 0, 0, w1, h1, imageScale1, imageScale1, 0);
        }

        // teams
        Assets.font14.draw(batch, match.team[HOME].name, +10, y0 - 22, Font.Align.LEFT);
        Assets.font14.draw(batch, match.team[AWAY].name, guiWidth - 8, y0 - 22, Font.Align.RIGHT);

        // bars
        batch.end();
        gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0xFFFFFF, guiAlpha);

        shapeRenderer.rect(10, y0, guiWidth / 2 - 22, 2);
        shapeRenderer.rect(guiWidth / 2 + 12, y0, guiWidth / 2 - 22, 2);

        shapeRenderer.setColor(0x242424, guiAlpha);
        shapeRenderer.rect(12, y0 + 2, guiWidth / 2 - 22, 2);
        shapeRenderer.rect(guiWidth / 2 + 14, y0 + 2, guiWidth / 2 - 22, 2);

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

    private void drawStatistics() {

        int l = 13 + (guiWidth - 640) / 5 + 2;
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

        int possHome = Math.round(100 * (1 + match.stats[Match.HOME].ballPossession) / (2 + homeStats.ballPossession + awayStats.ballPossession));
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
        Assets.font14.draw(batch, homeStats.goals, lc, i, Font.Align.CENTER);
        Assets.font14.draw(batch, Assets.strings.get("MATCH STATISTICS.GOALS"), hw, i, Font.Align.CENTER);
        Assets.font14.draw(batch, awayStats.goals, rc, i, Font.Align.CENTER);

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
