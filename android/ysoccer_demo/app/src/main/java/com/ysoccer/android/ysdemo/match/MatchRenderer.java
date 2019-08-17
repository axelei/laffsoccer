package com.ysoccer.android.ysdemo.match;

import android.annotation.SuppressLint;
import android.opengl.GLU;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.Color;
import com.ysoccer.android.framework.gl.Frame;
import com.ysoccer.android.framework.gl.RgbPair;
import com.ysoccer.android.framework.gl.SpriteBatcher;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.gl.TextureDrawer;
import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.ysdemo.Assets;
import com.ysoccer.android.ysdemo.R;
import com.ysoccer.android.ysdemo.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class MatchRenderer {
    static final float FRUSTUM_WIDTH = 1280;
    static final float FRUSTUM_HEIGHT = 800;
    GLGraphics glGraphics;
    int screenWidth;
    int screenHeight;
    int zoom;
    public ActionCamera actionCamera;
    public int[] vcameraX = new int[Const.REPLAY_SUBFRAMES];
    public int[] vcameraY = new int[Const.REPLAY_SUBFRAMES];

    public Match match;
    SpriteBatcher batcher;
    private List<Sprite> allSprites;
    private List<PlayerSprite> radarPlayers;
    private SpriteComparator spriteComparator;
    TextureDrawer spriteDrawer;
    FlagpostSprite[] flagpostSprite = new FlagpostSprite[4];

    int modW = Const.REPLAY_FRAMES;
    int modH = 2 * Const.REPLAY_FRAMES;
    int modX = (int) Math.ceil(Const.PITCH_W / ((float) modW));
    int modY = (int) Math.ceil(Const.PITCH_H / ((float) modH));

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayGoalScorer;
    boolean displayTime;
    boolean displayWindVane;
    boolean displayScore;
    boolean displayStatistics;
    boolean displayRadar;
    boolean displayControls;

    public MatchRenderer(GLGraphics glGraphics, SpriteBatcher batcher,
                         Match match) {
        this.glGraphics = glGraphics;
        screenWidth = glGraphics.getWidth();
        screenHeight = glGraphics.getHeight();
        zoom = Math.max(100, 20 * (int) (5.0f * glGraphics.getWidth() / 640));
        actionCamera = new ActionCamera(this);
        for (int i = 0; i < Const.REPLAY_SUBFRAMES; i++) {
            vcameraX[i] = Math.round(actionCamera.x);
            vcameraY[i] = Math.round(actionCamera.y);
        }

        this.match = match;
        this.batcher = batcher;
        allSprites = new ArrayList<Sprite>();
        radarPlayers = new ArrayList<PlayerSprite>();
        allSprites.add(new BallSprite(glGraphics, match.ball));
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            int len = match.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                PlayerSprite playerSprite = new PlayerSprite(glGraphics, match.team[t].lineup
                        .get(i));
                allSprites.add(playerSprite);
                if (i < Const.TEAM_SIZE) {
                    radarPlayers.add(playerSprite);
                }
            }
        }

        allSprites.add(flagpostSprite[0] = new FlagpostSprite(glGraphics, match, -1, -1));
        allSprites.add(flagpostSprite[1] = new FlagpostSprite(glGraphics, match, -1, +1));
        allSprites.add(flagpostSprite[2] = new FlagpostSprite(glGraphics, match, +1, -1));
        allSprites.add(flagpostSprite[3] = new FlagpostSprite(glGraphics, match, +1, +1));
        allSprites.add(new GoalTopA(glGraphics));
        allSprites.add(new GoalTopB(glGraphics));
        spriteComparator = new SpriteComparator();

        Assets.crowdRenderer.setMaxRank(match.getRank() + 1);

        //COPY THESE DEFAULTS AND OVERRIDE
        /*
        match.renderer.displayControlledPlayer = true;
		match.renderer.displayBallOwner = false;
		match.renderer.displayGoalScorer = false;
		match.renderer.displayTime = true;
		match.renderer.displayWindVane = true;
		match.renderer.displayScore = false;
		match.renderer.displayStatistics = false;
		match.renderer.displayRadar = true;
		*/
        displayControlledPlayer = false;
        displayBallOwner = false;
        displayGoalScorer = false;
        displayTime = false;
        displayWindVane = false;
        displayScore = false;
        displayStatistics = false;
        displayRadar = false;
    }

    public void loadGraphics(GLGame game) {
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            for (int i = 0; i < 10; i++) {
                Assets.player[t][i] = null;
            }
        }
        Assets.hairs.clear();
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            int len = match.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = match.team[t].lineup.get(i);
                if (player.role != Player.GOALKEEPER) {
                    loadPlayer(game, player);
                }
                loadHair(game, player);
            }
        }
        Assets.loadPlayerShadows(game, match.settings);
        Assets.loadPlayerNumber(game, match.settings);
        Assets.loadBall(game, match.settings);
        Assets.loadFlagposts(game, match.settings);
        loadCrowd(game, match.team[Match.HOME]);
        // Assets.loadStadium(game, match.locationSettings.pitchType);
        loadTeamFlags(game);
    }

    private void loadPlayer(GLGame game, Player player) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        player.team.kits.get(player.team.kitIndex).addKitColors(rgbPairs);
        addSkinColors(player, rgbPairs);

        Assets.loadPlayer(game, player.team, player.skinColor, rgbPairs);
    }

    private void addSkinColors(Player player, List<RgbPair> rgbPairs) {

        rgbPairs.add(new RgbPair(0xFF6300, Skin.colors[player.skinColor][0]));
        rgbPairs.add(new RgbPair(0xB54200, Skin.colors[player.skinColor][1]));
        rgbPairs.add(new RgbPair(0x631800, Skin.colors[player.skinColor][2]));

    }

    private void loadCrowd(Game game, Team team) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        team.kits.get(0).addKitColors(rgbPairs);

        Assets.loadCrowd(game, team, rgbPairs);
    }

    private void loadHair(Game game, Player player) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        addHairColors(player, rgbPairs);
        player.hair = Assets.loadHair(game, Hair.codes[player.hairType],
                rgbPairs);
    }

    private void addHairColors(Player player, List<RgbPair> rgbPairs) {
        // shaved
        if (Hair.codes[player.hairType] == 201) {
            rgbPairs.add(new RgbPair(
                    0x907130,
                    Color.setAlpha(
                            Hair.shavedColor[Hair.shavedTable[player.hairColor][player.skinColor]][0],
                            0xFF)));
            rgbPairs.add(new RgbPair(
                    0x715930,
                    Color.setAlpha(
                            Hair.shavedColor[Hair.shavedTable[player.hairColor][player.skinColor]][1],
                            0xFF)));
            // others
        } else {
            rgbPairs.add(new RgbPair(0x907130, Hair.colors[player.hairColor][0]));
            rgbPairs.add(new RgbPair(0x715930, Hair.colors[player.hairColor][1]));
            rgbPairs.add(new RgbPair(0x514030, Hair.colors[player.hairColor][2]));
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadTeamFlags(GLGame game) {
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            String folder = "images/flags/";
            String country = match.team[t].country;
            if (country.equals("UEF")) {
                folder += "uefa";
            } else if (country.equals("CNC")) {
                folder += "concacaf";
            } else if (country.equals("CNM")) {
                folder += "conmebol";
            } else if (country.equals("CAF")) {
                folder += "caf";
            } else if (country.equals("AFC")) {
                folder += "afc";
            } else if (country.equals("OFC")) {
                folder += "ofc";
            }
            Assets.teamFlags[t] = new Texture(game, folder + "/"
                    + match.team[t].nameKey.toLowerCase() + ".png");
        }
    }

    public void render(GLGame game) {
        GL10 gl = glGraphics.getGL();
        // gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, glGraphics.getWidth() * 100.0f / zoom, glGraphics.getHeight() * 100.0f / zoom, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(Const.CENTER_X - vcameraX[match.subframe], Const.CENTER_Y - vcameraY[match.subframe], 0);

        glGraphics.setColor(0xFFFFFF);
        renderBackground();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        Assets.crowdRenderer.draw(batcher);

        renderSprites(match.subframe);

        //redraw bottom goal
        glGraphics.drawTexture(Assets.goalBottom, Const.GOAL_BTM_X, Const.GOAL_BTM_Y);

        // redraw jumpers
        // top
        if (match.ball.data[match.subframe].y < -Const.JUMPER_Y) {
            glGraphics.drawTexture(Assets.jumper, -Const.JUMPER_X, -Const.JUMPER_Y - 40);
            glGraphics.drawTexture(Assets.jumper, +Const.JUMPER_X, -Const.JUMPER_Y - 40);
        }
        // bottom
        if (match.ball.data[match.subframe].y < +Const.JUMPER_Y) {
            glGraphics.drawTexture(Assets.jumper, -Const.JUMPER_X, +Const.JUMPER_Y - 40);
            glGraphics.drawTexture(Assets.jumper, +Const.JUMPER_X, +Const.JUMPER_Y - 40);
        }

        if (match.settings.weatherStrength != Weather.Strength.NONE) {
            drawWeatherEffects(match.settings, match.subframe);
        }

        if (displayControlledPlayer) {
            drawControlledPlayersNumbers();
        }

        //////// CONTROLS ////////
        if (displayControls && game.gamepadInput == null) {
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluOrtho2D(gl, 0, TouchInput.TOUCHCAM_WIDTH, TouchInput.TOUCHCAM_HEIGHT, 0);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

            drawControls(game.touchInput);
        }
        //////////////////////////


        ////////////////////GUI////////////////////
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, Settings.screenWidth, Settings.screenHeight, 0);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(Settings.guiOriginX, Settings.guiOriginY, 0);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        /////////////////////////////////////////


        // guiCam.setViewportAndMatrices();
//		gl.glMatrixMode(GL10.GL_PROJECTION);
//		gl.glLoadIdentity();
//		GLU.gluOrtho2D(gl, 0, Settings.screenWidth, Settings.screenHeight, 0);
//
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		gl.glLoadIdentity();
//
//		gl.glEnable(GL10.GL_BLEND);
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        //clock
        if (displayTime) {
            drawTime();
        }

        //radar
        if (displayRadar && game.settings.displayRadar) {
            drawRadar(match.subframe);
        }

        //wind vane
        if (displayWindVane && (match.settings.wind.speed > 0)) {
            //set_color($FFFFFF, light)
            glGraphics.drawSubTextureRect(Assets.wind, -Settings.guiOriginX + Settings.screenWidth - 50, -Settings.guiOriginY + 20, 30, 30, 30 * match.settings.wind.direction, 30 * (match.settings.wind.speed - 1), 30, 30);
        }

        //score
        if (displayScore) {
            drawScore();
        }

        //statistics
        if (displayStatistics) {
            drawStatistics();
        }

        // additional state-specific render
        MatchState matchState = match.fsm.getState();
        if (matchState != null) {
            matchState.render();
        }

    }

    private void renderBackground() {
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                glGraphics.drawTexture(Assets.stadium[r][c], -Const.CENTER_X
                        + 512 * c, -Const.CENTER_Y + 512 * r);
            }
        }
    }

    private void renderSprites(int subframe) {

        drawShadows(subframe);

        spriteComparator.subframe = subframe;
        Collections.sort(allSprites, spriteComparator);

        int len = allSprites.size();
        for (int i = 0; i < len; i++) {
            Sprite sprite = allSprites.get(i);
            sprite.draw(subframe);
        }
    }

    private void drawShadows(int subframe) {

        glGraphics.setAlpha(match.settings.shadowAlpha);

        batcher.beginBatch(Assets.flagposts);
        for (int i = 0; i < 4; i++) {
            flagpostSprite[i].drawShadow(subframe, batcher);
        }
        batcher.endBatch();

        Data d = match.ball.data[subframe];
        glGraphics.drawTextureRect(Assets.ball, d.x - 1 + 0.65f * d.z, d.y - 3
                + 0.46f * d.z, 32, 0, 8, 8);
        if (match.settings.time == Time.NIGHT) {
            glGraphics.drawTextureRect(Assets.ball, d.x - 5 - 0.65f * d.z, d.y - 3 + 0.46f * d.z, 32, 0, 8, 8);
            glGraphics.drawTextureRect(Assets.ball, d.x - 5 - 0.65f * d.z, d.y - 3 - 0.46f * d.z, 32, 0, 8, 8);
            glGraphics.drawTextureRect(Assets.ball, d.x - 1 + 0.65f * d.z, d.y - 3 - 0.46f * d.z, 32, 0, 8, 8);
        }

        Frame keyFrame;

        // keepers
        batcher.beginBatch(Assets.keeperShadow);
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            int len = match.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = match.team[t].lineup.get(i);
                if (player.role == Player.GOALKEEPER) {
                    d = player.data[subframe];
                    if (d.isVisible) {
                        keyFrame = Assets.keeperShadowTextureRegions[d.fmx][d.fmy];
                        batcher.drawSprite(d.x - 24 + 0.65f * d.z, d.y - 34 + 0.46f * d.z, 50, 50, keyFrame);
                    }
                }
            }
        }
        batcher.endBatch();

        for (int i = 0; i < (match.settings.time == Time.NIGHT ? 4 : 1); i++) {
            batcher.beginBatch(Assets.playerShadows[i]);
            for (int t = Match.HOME; t <= Match.AWAY; t++) {
                for (Player player : match.team[t].lineup) {
                    if (player.role != Player.GOALKEEPER) {
                        d = player.data[subframe];
                        if (d.isVisible) {
                            float offsetX = PlayerSprite.offsets[d.fmy][d.fmx][0];
                            float offsetY = PlayerSprite.offsets[d.fmy][d.fmx][1];
                            float mX = (i == 0 || i == 3) ? 0.65f : -0.65f;
                            float mY = (i == 0 || i == 1) ? 0.46f : -0.46f;
                            keyFrame = Assets.playerShadowTextureRegions[d.fmx][d.fmy];
                            batcher.drawSprite(d.x - offsetX + mX * d.z, d.y - offsetY + 5 + mY * d.z, 32, 32, keyFrame);
                        }
                    }
                }
            }
            batcher.endBatch();
        }

        glGraphics.setAlpha(1.0f);

    }

    private void drawControlledPlayersNumbers() {
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            if (match.team[t] != null) {
                int len = match.team[t].lineup.size();
                for (int i = 0; i < len; i++) {
                    Player player = match.team[t].lineup.get(i);
                    if (player.inputDevice != player.ai && player.isVisible) {
                        drawPlayerNumber(player, batcher);
                    }
                }
            }
        }
    }

    private void drawPlayerNumber(Player player, SpriteBatcher batcher) {

        int f0 = player.number % 10;
        int f1 = (player.number - f0) / 10 % 10;

        int dx = Math.round(player.x) + 1;
        int dy = Math.round(player.y) - 40 - Math.round(player.z);

        int w0 = 6 - ((f0 == 1) ? 2 : 1);
        int w1 = 6 - ((f1 == 1) ? 2 : 1);

        batcher.beginBatch(Assets.playerNumber);
        if (f1 > 0) {
            dx = dx - (w0 + 2 + w1) / 2;
            batcher.drawSprite(dx, dy, 6, 10, Assets.playerNumberRegions[f1]);
            dx = dx + w1 + 2;
            batcher.drawSprite(dx, dy, 6, 10, Assets.playerNumberRegions[f0]);
        } else {
            batcher.drawSprite(dx - w0 / 2, dy, 6, 10,
                    Assets.playerNumberRegions[f0]);
        }
        batcher.endBatch();
    }

    private void drawWeatherEffects(MatchSettings matchSettings,
                                    int subframe) {
        switch (matchSettings.weatherEffect) {
            case Weather.RAIN:
                glGraphics.setAlpha(0.6f);
                batcher.beginBatch(Assets.rain);

                matchSettings.game.random.setSeed(1);
                // game.random.nextInt(1);
                for (int i = 1; i <= 40 * matchSettings.weatherStrength; i++) {
                    int x = matchSettings.game.random.nextInt(modW);
                    int y = matchSettings.game.random.nextInt(modH);
                    int h = (matchSettings.game.random.nextInt(modH) + subframe)
                            % modH;
                    if (h > 0.3f * modH) {
                        for (int fx = 0; fx <= modX; fx++) {
                            for (int fy = 0; fy <= modY; fy++) {
                                int px = ((x + modW - Math.round(subframe
                                        / ((float) GLGame.SUBFRAMES))) % modW)
                                        + modW * (fx - 1);
                                int py = ((y + 4 * Math.round(subframe
                                        / GLGame.SUBFRAMES)) % modH)
                                        + modH * (fy - 1);
                                int rf = 3 * h / modH;
                                if (h > 0.9f * modH) {
                                    rf = 3;
                                }
                                // glGraphics.drawSubTextureRect(Assets.rain,
                                // -Const.CENTER_X +px , -Const.CENTER_Y +py , 30,
                                // 30, 30*rf, 0, 30, 30);
                                batcher.drawSprite(-Const.CENTER_X + px,
                                        -Const.CENTER_Y + py, 30, 30,
                                        Assets.rainRegions[rf]);
                            }
                        }
                    }
                }

                batcher.endBatch();
                glGraphics.setAlpha(1.0f);
                break;

            case Weather.SNOW:
                glGraphics.setAlpha(0.7f);
                batcher.beginBatch(Assets.snow);

                matchSettings.game.random.setSeed(1);
                // game.random.nextInt(1);
                for (int i = 1; i <= 30 * matchSettings.weatherStrength; i++) {
                    int x = matchSettings.game.random.nextInt(modW);
                    int y = matchSettings.game.random.nextInt(modH);
                    int s = i % 3;
                    int a = matchSettings.game.random.nextInt(360);
                    for (int fx = 0; fx <= modX; fx++) {
                        for (int fy = 0; fy <= modY; fy++) {
                            int px = (int) (((x + modW + 30 * Emath.sin(360
                                    * subframe / ((float) Const.REPLAY_SUBFRAMES) + a)) % modW) + modW
                                    * (fx - 1));
                            int py = ((y + 2 * Math.round(subframe
                                    / GLGame.SUBFRAMES)) % modH)
                                    + modH * (fy - 1);
                            // glGraphics.drawSubTextureRect(Assets.snow,
                            // -Const.CENTER_X +px, -Const.CENTER_Y +py, 3, 3, 3*s,
                            // 0, 3, 3);
                            batcher.drawSprite(-Const.CENTER_X + px,
                                    -Const.CENTER_Y + py, 3, 3,
                                    Assets.snowRegions[s]);
                        }
                    }
                }

                batcher.endBatch();
                glGraphics.setAlpha(1.0f);
                break;

            case Weather.FOG:
                glGraphics.setAlpha(0.25f * matchSettings.weatherStrength);
                batcher.beginBatch(Assets.fog);

                int TILE_WIDTH = 256;
                int fogX = -Const.CENTER_X
                        + vcameraX[subframe]
                        - 2
                        * TILE_WIDTH
                        + ((Const.CENTER_X - vcameraX[subframe]) % TILE_WIDTH + 2 * TILE_WIDTH)
                        % TILE_WIDTH;
                int fogY = -Const.CENTER_Y
                        + vcameraY[subframe]
                        - 2
                        * TILE_WIDTH
                        + ((Const.CENTER_Y - vcameraY[subframe]) % TILE_WIDTH + 2 * TILE_WIDTH)
                        % TILE_WIDTH;
                int x = fogX;
                while (x < (fogX + Settings.screenWidth + 2 * TILE_WIDTH)) {
                    int y = fogY;
                    while (y < (fogY + Settings.screenHeight + 2 * TILE_WIDTH)) {
                        // glGraphics.drawTexture(Assets.fog, x
                        // +((subframe/Const.AI_SUBFRAMES) % TILE_WIDTH), y
                        // +((2*subframe/Const.AI_SUBFRAMES) % TILE_WIDTH));
                        batcher.drawSprite(
                                x + ((subframe / GLGame.SUBFRAMES) % TILE_WIDTH),
                                y
                                        + ((2 * subframe / GLGame.SUBFRAMES) % TILE_WIDTH),
                                256, 256, Assets.fogFrame);
                        y = y + TILE_WIDTH;
                    }
                    x = x + TILE_WIDTH;
                }

                batcher.endBatch();
                glGraphics.setAlpha(1.0f);
                break;
        }
    }

    private void drawRosters() {

        int l = 13 + (Settings.screenWidth - 640) / 5 + 2;
        int r = Settings.screenWidth - l + 2;
        int w = r - l;
        int t = 20 + (Settings.screenHeight - 400) / 5 + 2;
        int b = Settings.screenHeight - t + 8 + 2;
        int h = b - t;
        int m1 = t + h / 8 + 2;
        int m2 = t + h / 3 + 2;
        int hw = Settings.screenWidth / 2 + 2;

        glGraphics.fadeRect(l + 2, t + 2, r - 2, b - 2, 0.35f, 0x000000);

        //frame shadow
        /*
        glGraphics.setColor(0, 24, 0);
		glGraphics.drawFrame(l, t, r -l, b -t);
		
		l = l -2;
		r = r -2;
		t = t -2;
		b = b -2;
		m1 = m1 -2;
		m2 = m2 -2;
		hw = hw -2;
	*/
        //frame
        glGraphics.setColor(0xFFFFFF);

        glGraphics.drawFrame(l, t, r - l, b - t);
        /*
        ''middle
		DrawLine(hw -0.2*w,	m1, 	hw +0.2*w,	m1)
		DrawLine(hw -0.2*w, m1 +1, 	hw +0.2*w,	m1 +1)
		
		''middle left
		DrawLine(l +0.05*w,	m2, 	hw -0.05*w,	m2)
		DrawLine(l +0.05*w, m2 +1, 	hw -0.05*w,	m2 +1)
		
		''middle right
		DrawLine(hw +0.05*w, m2, 	r -0.05*w,	m2)
		DrawLine(hw +0.05*w, m2 +1,	r -0.05*w,	m2 +1)
		
		''title
		Local y:Int = t +0.044*h
		If (menu.status = MS_FRIENDLY)
			text14u(dictionary.gettext("FRIENDLY"), 0.5*game_settings.screen_width, y, img_ucode14g, 0) 
		Else
			text14u(competition.get_menu_title(), 0.5*game_settings.screen_width, y, img_ucode14g, 0) 
		EndIf
		
		''city & stadium
		If (team[HOME].city <> "")
			If (team[HOME].stadium <> "")
				y = t +0.163*h 
				text10u(team[HOME].stadium + ", " + team[HOME].city, 0.5*game_settings.screen_width, y, img_ucode10g, 0)
			EndIf
		EndIf
		
		''club logos / national flags
		y = t + 0.13*h
		
		Local w0:Int = 0
		Local h0:Int = 0
		Local w1:Int = 0
		Local h1:Int = 0
		Local lm:Int = 84 ''left margin
		Local rm:Int = 84 ''right margin
		
		''size of club logos / national flags
		If (team[HOME].clnf <> Null)
			w0 = ImageWidth(team[HOME].clnf)
			h0 = ImageHeight(team[HOME].clnf)
			lm = w0 +18
		EndIf
		If (team[AWAY].clnf <> Null)
			w1 = ImageWidth(team[AWAY].clnf)
			h1 = ImageHeight(team[AWAY].clnf)
			rm = w1 +18
		EndIf
		
		''draw club logos / national flags
		If (team[HOME].clnf <> Null)
			If (team[HOME].national = False)
				DrawImage team[HOME].clnf_sh, l +0.044*w, y -0.5*h0 +2
			EndIf
			SetColor light, light, light
			DrawImage team[HOME].clnf, l +0.044*w -2, y -0.5*h0
		EndIf
		If (team[AWAY].clnf <> Null)
			If (team[AWAY].national = False)
				DrawImage team[AWAY].clnf_sh, r -0.044*w -w1, y -0.5*h0 +2
			EndIf
			SetColor light, light, light
			DrawImage team[AWAY].clnf, r -0.044*w -w1 -2,	y -0.5*h0
		EndIf
		
		''team name
		y = t +0.25*h
		text14u(team[HOME].name, l +0.24*w, y, img_ucode14g, 0)
		text14u(team[AWAY].name, l +0.72*w, y, img_ucode14g, 0)
		
		''players
		For Local tm:Int = HOME To AWAY
			
			y = t +0.38*h
			
			For Local pos:Int = 1 To TEAM_SIZE
				
				Local player:t_player = team[tm].player_at_position(pos -1)
				
				''number
				text10u(player.number, l +(0.10 +0.48*tm)*w, y, img_ucode10g, 0)
				
				''name
				If (player.surname <> "")
					text10u(player.surname, l +(0.14 +0.48*tm)*w, y, img_ucode10g, 1)
				Else
					text10u(player.name, l +(0.14 +0.48*tm)*w, y, img_ucode10g, 1)
				EndIf
				
				y = y +0.044*h
				
			Next
			
		Next
		
		''coach
		y = t +0.875*h
		text10u(dictionary.gettext("COACH") + ":", l +0.08*w, y, img_ucode10g, 1)
		text10u(dictionary.gettext("COACH") + ":", l +0.56*w, y, img_ucode10g, 1)
		
		y = t +0.925*h
		text10u(team[HOME].coach_name, l +0.24*w, y, img_ucode10g, 0)
		text10u(team[AWAY].coach_name, l +0.72*w, y, img_ucode10g, 0)
		
		*/

    }

    private void drawControls(TouchInput touchInput) {
        batcher.beginBatch(Assets.joystick);
        batcher.drawSprite(
                TouchInput.joystickCurrentPos.x - TouchInput.JOYSTICK_R,
                TouchInput.joystickCurrentPos.y - TouchInput.JOYSTICK_R,
                128, 128, Assets.joystickRegions[0]
        );
        batcher.drawSprite(
                TouchInput.joystickCurrentPos.x + ((touchInput.value ? Emath.cos(touchInput.angle) : 0) - 2) * TouchInput.JOYSTICK_R / 2,
                TouchInput.joystickCurrentPos.y + ((touchInput.value ? Emath.sin(touchInput.angle) : 0) - 2) * TouchInput.JOYSTICK_R / 2,
                128, 128, Assets.joystickRegions[1]
        );
        batcher.drawSprite(
                TouchInput.BUTTON_X - TouchInput.BUTTON_R,
                TouchInput.BUTTON_Y - TouchInput.BUTTON_R,
                128, 128, Assets.joystickRegions[2]
        );
        batcher.drawSprite(
                TouchInput.BUTTON_X - TouchInput.BUTTON_R,
                TouchInput.BUTTON_Y - TouchInput.BUTTON_R,
                128, 128, Assets.joystickRegions[3]
        );
        batcher.endBatch();
    }

    private void drawTime() {

        int minute = match.getMinute();

        //TODO
        //batcher.beginBatch(Assets.time);

        //"mins"
        glGraphics.drawSubTextureRect(Assets.time, -Settings.guiOriginX + 46, -Settings.guiOriginY + 22, 48, 20, 120, 0, 48, 20);

        //units
        int digit = minute % 10;
        glGraphics.drawSubTextureRect(Assets.time, -Settings.guiOriginX + 34, -Settings.guiOriginY + 22, 12, 20, digit * 12, 0, 12, 20);

        //tens
        minute = (minute - digit) / 10;
        digit = minute % 10;
        if (minute > 0) {
            glGraphics.drawSubTextureRect(Assets.time, -Settings.guiOriginX + 22, -Settings.guiOriginY + 22, 12, 20, digit * 12, 0, 12, 20);
        }

        //hundreds
        minute = (minute - digit) / 10;
        digit = minute % 10;
        if (digit > 0) {
            glGraphics.drawSubTextureRect(Assets.time, -Settings.guiOriginX + 10, -Settings.guiOriginY + 22, 12, 20, digit * 12, 0, 12, 20);
        }

    }

    private void drawRadar(int subframe) {
        GL10 gl = glGraphics.getGL();

        final int RX = -Settings.guiOriginX + 10;
        final int RY = -Settings.guiOriginY + 60;
        final int RW = 132;
        final int RH = 166;

        glGraphics.fadeRect(RX, RY, RX + RW, RY + RH, 0.6f, match.settings.grass.darkShadow);

        gl.glDisable(GL10.GL_BLEND);
        glGraphics.setColor(0x000000);
        glGraphics.drawRect(RX, RY, RW, 1);
        glGraphics.drawRect(RX + RW, RY, 1, RH);
        glGraphics.drawRect(RX, RY + RH / 2, RW, 1);
        glGraphics.drawRect(RX, RY + RH, RW, 1);
        glGraphics.drawRect(RX, RY, 1, RH);

        //prepare y-sorted list
        spriteComparator.subframe = subframe;
        Collections.sort(radarPlayers, spriteComparator);

        //get shirt colors
        int[] shirt1 = new int[2];
        int[] shirt2 = new int[2];
        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            Kit kit = match.team[t].kits.get(match.team[t].kitIndex);
            shirt1[t] = Kit.colors[kit.shirt1][0];
            shirt2[t] = Kit.colors[kit.shirt2][0];
        }

        //draw placeholders
        int len = radarPlayers.size();
        for (int i = 0; i < len; i++) {
            Player player = radarPlayers.get(i).player;
            Data d = player.data[subframe];
            if (d.isVisible) {
                int dx = RX + RW / 2 + d.x / 8;
                int dy = RY + RH / 2 + d.y / 8;

                glGraphics.setColor(0x242424);
                glGraphics.drawRect(dx - 3, dy - 3, 5, 1);
                glGraphics.drawRect(dx - 4, dy - 2, 1, 3);
                glGraphics.drawRect(dx - 3, dy + 2, 5, 1);
                glGraphics.drawRect(dx + 3, dy - 2, 1, 3);

                glGraphics.setColor(shirt1[player.team.index]);
                glGraphics.drawRect(dx - 3, dy - 2, 3, 4);

                glGraphics.setColor(shirt2[player.team.index]);
                glGraphics.drawRect(dx, dy - 2, 3, 4);
            }
        }

        glGraphics.setColor(0xFFFFFF);

        //draw controlled players numbers
        if (displayControlledPlayer) {
            for (int i = 0; i < len; i++) {
                Player player = radarPlayers.get(i).player;
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
                        glGraphics.drawSubTextureRect(Assets.playerTinyNumbers, dx, dy, w1, 6, (f1 + 1) * 4 - w1, 0, w1, 6);
                        dx = dx + w1;
                        glGraphics.drawSubTextureRect(Assets.playerTinyNumbers, dx, dy, w0, 6, (f0 + 1) * 4 - w0, 0, w0, 6);
                    } else {
                        w0 = 4 - (f0 == 1 ? 2 : 0);
                        dx = dx - w0 / 2;
                        glGraphics.drawSubTextureRect(Assets.playerTinyNumbers, dx, dy, w0, 6, (f0 + 1) * 4 - w0, 0, w0, 6);
                    }
                }
            }
        }
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void drawScore() {

        //default values
        int w0 = 0;
        int h0 = 0;
        int w1 = 0;
        int h1 = 0;
        int lm = 84;    //left margin
        int rm = 84;    //right margin

        //max rows of scorers list
        //TODO
        int rows = 0;//Math.max(rows_counter(score[HOME]),rows_counter(score[AWAY]));

        //size of flags
        if (Assets.teamFlags[Match.HOME] != null) {
            w0 = Assets.teamFlags[Match.HOME].width;
            h0 = Assets.teamFlags[Match.HOME].height;
            lm = w0 + 18;
        }
        if (Assets.teamFlags[Match.AWAY] != null) {
            w1 = Assets.teamFlags[Match.AWAY].width;
            h1 = Assets.teamFlags[Match.AWAY].height;
            rm = w1 + 18;
        }

        int y0 = Math.max(h0, h1);
        y0 = Math.max(y0, 14 * rows);
        y0 = -Settings.guiOriginY + Settings.screenHeight - 16 - y0;

        //flags
        if (Assets.teamFlags[Match.HOME] != null) {
//			if (match.team[Match.HOME].isNational == false) {
//				glGraphics.drawTexture(Assets.teamFlagsShadows[Match.HOME], 12, y0 +10);
        }
        //SetColor(light, light, light)
        glGraphics.drawTexture(Assets.teamFlags[Match.HOME], -Settings.guiOriginX + 210, y0 + 8);
//		}
        if (Assets.teamFlags[Match.AWAY] != null) {
//			if (match.team[Match.AWAY].isNational == false) {
//				glGraphics.drawTexture(Assets.teamFlagsShadows[Match.AWAY], Settings.screenWidth -w1 -8, y0 +10);
//			}
            //SetColor(light, light, light)
            glGraphics.drawTexture(Assets.teamFlags[Match.AWAY], -Settings.guiOriginX + Settings.screenWidth - w1 - 210, y0 + 8);
        }

        //teams
        glGraphics.text14u(match.team[Match.HOME].name, -Settings.guiOriginX + 210, y0 - 22, Assets.ucode14, +1);
        glGraphics.text14u(match.team[Match.AWAY].name, -Settings.guiOriginX + Settings.screenWidth - 208, y0 - 22, Assets.ucode14, -1);

        //bars
        glGraphics.setColor(0xFFFFFF);
        glGraphics.drawRect(-Settings.guiOriginX + 210, y0, Settings.screenWidth / 2 - 222, 2);
        glGraphics.drawRect(-Settings.guiOriginX + Settings.screenWidth / 2 + 12, y0, Settings.screenWidth / 2 - 222, 2);

        glGraphics.setColor(0x242424);
        glGraphics.drawRect(-Settings.guiOriginX + 212, y0 + 2, Settings.screenWidth / 2 - 222, 2);
        glGraphics.drawRect(-Settings.guiOriginX + Settings.screenWidth / 2 + 14, y0 + 2, Settings.screenWidth / 2 - 222, 2);

        //TODO: usare sprite batcher;
        //home score
        int f0 = match.stats[Match.HOME].goals % 10;
        int f1 = ((match.stats[Match.HOME].goals - f0) / 10) % 10;

        glGraphics.setColor(0xFFFFFF);
        if (f1 > 0) {
            glGraphics.drawSubTextureRect(Assets.score, -Settings.guiOriginX + Settings.screenWidth / 2 - 15 - 48, y0 - 40, 24, 38, f1 * 24, 0, 24, 38);
        }
        glGraphics.drawSubTextureRect(Assets.score, -Settings.guiOriginX + Settings.screenWidth / 2 - 15 - 24, y0 - 40, 24, 38, f0 * 24, 0, 24, 38);

        //"-"
        glGraphics.drawSubTextureRect(Assets.score, -Settings.guiOriginX + Settings.screenWidth / 2 - 9, y0 - 40, 24, 38, 10 * 24, 0, 24, 38);

        //away score
        f0 = match.stats[Match.AWAY].goals % 10;
        f1 = (match.stats[Match.AWAY].goals - f0) / 10 % 10;

        if (f1 > 0) {
            glGraphics.drawSubTextureRect(Assets.score, -Settings.guiOriginX + Settings.screenWidth / 2 + 17, y0 - 40, 24, 38, f1 * 24, 0, 24, 38);
            glGraphics.drawSubTextureRect(Assets.score, -Settings.guiOriginX + Settings.screenWidth / 2 + 17 + 24, y0 - 40, 24, 38, f0 * 24, 0, 24, 38);
        } else {
            glGraphics.drawSubTextureRect(Assets.score, -Settings.guiOriginX + Settings.screenWidth / 2 + 17, y0 - 40, 24, 38, f0 * 24, 0, 24, 38);
        }

        //scorers
//		text10u(score[HOME], 0.5*game_settings.screen_width -12, y0 +4, img_ucode10g, -1)
//		text10u(score[AWAY], 0.5*game_settings.screen_width +14, y0 +4, img_ucode10g, 1)

    }

    private void drawStatistics() {

        int l = 13 + (Settings.screenWidth - 100) / 5 + 2;
        int r = Settings.screenWidth - l + 2;
        int w = r - l;
        int t = 20 + (Settings.screenHeight - 400) / 5 + 2;
        int b = Settings.screenHeight - t + 8 + 2;
        int h = b - t;
        int hw = Settings.screenWidth / 2;

        // fading
        // glGraphics.setColor(0xFFFFFF);
        glGraphics.fadeRect(l + 2, t + 2, r - 2, t + h / 10 + 1, 0.35f, 0x000000);

        int i = t + h / 10 + 2;
        for (int j = 1; j < 9; j++) {
            glGraphics.fadeRect(l + 2, i + 1, r - 2, i + h / 10 - 1, 0.35f, 0x000000);
            i = i + h / 10;
        }
        glGraphics.fadeRect(l + 2, i + 1, r - 2, b - 2, 0.35f, 0x000000);

        // frame shadow
        glGraphics.setColor(0x242424);
        glGraphics.drawFrame(l, t, r - l, b - t);

        l = l - 2;
        r = r - 2;
        t = t - 2;
        b = b - 2;

        // frame
        glGraphics.setColor(0xFFFFFF);
        glGraphics.drawFrame(l, t, r - l, b - t);
        glGraphics.setAlpha(1.0f);

        MatchStats homeStats = match.stats[Match.HOME];
        MatchStats awayStats = match.stats[Match.AWAY];

        int possHome = Math.round(100 * (1 + match.stats[Match.HOME].ballPossession)
                / (2 + homeStats.ballPossession + awayStats.ballPossession));
        int possAway = 100 - possHome;

        // text
        int lc = l + w / 5;
        int rc = r - w / 5;
        i = t + h / 20 - 8;
        glGraphics.text14u(gettext(R.string.MATCH_STATS), hw, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u(match.team[Match.HOME].name, lc, i, Assets.ucode14,
                0);
        glGraphics.text14u(match.team[Match.AWAY].name, rc, i, Assets.ucode14,
                0);

        i = i + h / 10;
        glGraphics.text14u("" + homeStats.goals, lc, i, Assets.ucode14, 0);
        glGraphics.text14u(gettext(R.string.GOALS), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + awayStats.goals, rc, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u("" + possHome, lc, i, Assets.ucode14, 0);
        glGraphics.text14u(gettext(R.string.POSSESSION), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + possAway, rc, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u("" + homeStats.overallShots, lc, i, Assets.ucode14, 0);
        glGraphics.text14u(gettext(R.string.GOAL_ATTEMPTS), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + awayStats.overallShots, rc, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u("" + homeStats.centeredShots, lc, i, Assets.ucode14, 0);
        glGraphics.text14u(gettext(R.string.ON_TARGET), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + awayStats.centeredShots, rc, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u("" + homeStats.cornersWon, lc, i, Assets.ucode14, 0);
        glGraphics.text14u(gettext(R.string.CORNERS_WON), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + awayStats.cornersWon, rc, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u("" + homeStats.foulsConceded, lc, i, Assets.ucode14, 0);
        glGraphics
                .text14u(gettext(R.string.FOULS_CONCEDED), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + awayStats.foulsConceded, rc, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u("" + homeStats.yellowCards, lc, i, Assets.ucode14, 0);
        glGraphics.text14u(gettext(R.string.BOOKINGS), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + awayStats.yellowCards, rc, i, Assets.ucode14, 0);

        i = i + h / 10;
        glGraphics.text14u("" + homeStats.redCards, lc, i, Assets.ucode14, 0);
        glGraphics.text14u(gettext(R.string.SENDINGS_OFF), hw, i, Assets.ucode14, 0);
        glGraphics.text14u("" + awayStats.redCards, rc, i, Assets.ucode14, 0);

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

    void updateCameraY(int follow) {
        updateCameraY(follow, ActionCamera.CS_NORMAL, 0);
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

    String gettext(int id) {
        return match.glGame.getResources().getString(id);
    }

}
