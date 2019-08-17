package com.ysoccer.android.ysdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.Music;
import com.ysoccer.android.framework.Sound;
import com.ysoccer.android.framework.gl.Font;
import com.ysoccer.android.framework.gl.Frame;
import com.ysoccer.android.framework.gl.RgbPair;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.ysdemo.match.CrowdRenderer;
import com.ysoccer.android.ysdemo.match.Kit;
import com.ysoccer.android.ysdemo.match.MatchSettings;
import com.ysoccer.android.ysdemo.match.Pitch;
import com.ysoccer.android.ysdemo.match.Sky;
import com.ysoccer.android.ysdemo.match.Tactics;
import com.ysoccer.android.ysdemo.match.Team;
import com.ysoccer.android.ysdemo.match.Time;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Assets {

    static class Backgrounds {
        static Texture menuMatch;
    }

    public static Texture mainMenuBackground;
    public static Texture helpMenuBackground;
    public static Texture settingsMenuBackground;
    public static Texture[][] stadium = new Texture[4][4];
    public static Texture crowd;
    public static Frame[] crowdFrames = new Frame[5];
    public static CrowdRenderer crowdRenderer;

    public static Texture ucode14;
    public static int[] ucode14w = new int[1088];
    public static int[] ucode10w = new int[1088];
    public static Texture[][] player = new Texture[2][10];
    public static List<Texture> hairs = new ArrayList<Texture>();
    public static Texture[] playerShadows = new Texture[4];
    public static Frame[][] playerShadowTextureRegions = new Frame[8][16];
    public static Texture keeper;
    public static Texture keeperShadow;
    public static Frame[][] keeperShadowTextureRegions = new Frame[8][19];
    public static Texture playerTinyNumbers;
    public static Texture[] kit = new Texture[2];

    public static Texture ball;
    public static Texture flagposts;
    public static Frame[][][] flagpostShadowFrames = new Frame[6][3][4];
    public static Texture goalTopA;
    public static Texture goalTopB;
    public static Texture goalBottom;
    public static Texture jumper;
    public static Texture playerNumber;
    public static Frame[] playerNumberRegions = new Frame[10];
    public static Texture time;
    public static Texture score;
    public static Texture replay;
    public static Texture teamFlags[] = new Texture[2];
    public static Texture light;
    public static Texture pitches;

    public static Texture weather;
    public static Texture wind;
    public static Texture rain;
    public static Frame[] rainRegions = new Frame[4];
    public static Texture snow;
    public static Frame[] snowRegions = new Frame[3];
    public static Texture fog;
    public static Frame fogFrame;
    public static Texture logo;

    public static Texture joystick;
    public static Frame[] joystickRegions = new Frame[4];

    public static Font font;
    public static int GLYPHWIDTH = 16;
    public static int GLYPHHEIGHT = 23;

    public static Bitmap keeperCollisionDetection;

    public static Tactics[] tactics = new Tactics[18];

    public static Music music;

    protected static Sound bounceSound;
    protected static Sound chantSound;
    protected static Sound deflectSound;
    protected static Sound endGameSound;
    protected static Sound holdSound;
    protected static Sound homeGoalSound;
    protected static Sound introSound;
    protected static Sound postSound;
    protected static Sound kickSound;
    protected static Sound netSound;
    protected static Sound whistleSound;
    protected static Music crowdSound;
    protected static Sound clickSound;

    public static void load(GLGame glGame) {
        settingsMenuBackground = new Texture(glGame,
                "images/backgrounds/menu_game_options.jpg");

        keeper = new Texture(glGame, "images/player/keeper.png");

        ucode14 = new Texture(glGame, "images/ucode_14.png");
        loadUcodeTable(glGame, 14);

        font = new Font(ucode14, 0, 0, 64, GLYPHWIDTH, GLYPHHEIGHT);

        InputStream in = null;
        try {
            in = glGame.getFileIO().readAsset("images/keeper_cd.png");
            keeperCollisionDetection = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Couldn't load collision detection bitmap ", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }

        try {
            in = glGame.getFileIO().readAsset("images/stadium/crowd.txt");
            crowdRenderer = new CrowdRenderer(in);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load crowd list ", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }

        for (int i = 0; i < tactics.length; i++) {
            try {
                tactics[i] = new Tactics();
                in = glGame.getFileIO().readAsset(
                        "tactics/preset/" + Tactics.fileNames[i] + ".TAC");
                tactics[i].loadFile(in);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't load tactics", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
            }
        }

        music = glGame.getAudio().newMusic("music/menu.ogg");
        music.setLooping(true);
        music.setVolume(0.0f);
        if (glGame.settings.musicVolume > 0) {
            music.play();
        }
        bounceSound = glGame.getAudio().newSound("sfx/bounce.ogg");
        chantSound = glGame.getAudio().newSound("sfx/chant.ogg");
        deflectSound = glGame.getAudio().newSound("sfx/deflect.ogg");
        endGameSound = glGame.getAudio().newSound("sfx/endgame.ogg");
        holdSound = glGame.getAudio().newSound("sfx/hold.ogg");
        homeGoalSound = glGame.getAudio().newSound("sfx/homegoal.ogg");
        introSound = glGame.getAudio().newSound("sfx/intro.ogg");
        postSound = glGame.getAudio().newSound("sfx/post.ogg");
        kickSound = glGame.getAudio().newSound("sfx/kick.ogg");
        netSound = glGame.getAudio().newSound("sfx/net.ogg");
        whistleSound = glGame.getAudio().newSound("sfx/whistle.ogg");
        clickSound = glGame.getAudio().newSound("click.ogg");
    }

    public static void loadPlayer(GLGame game, Team team, int skinColor, List<RgbPair> rgbPairs) {
        if (player[team.index][skinColor] == null) {
            player[team.index][skinColor] = new Texture((GLGame) game,
                    "images/player/"
                            + Kit.styles[team.kits.get(team.kitIndex).style]
                            + ".png", rgbPairs);
        }
    }

    public static void loadPlayerShadows(GLGame game,
                                         MatchSettings matchSettings) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        switch (matchSettings.time) {
            case Time.DAY:
                rgbPairs.add(new RgbPair(0x404040, matchSettings.grass.darkShadow));
                playerShadows[0] = new Texture(game,
                        "images/player/shadows/player_0.png", rgbPairs);
                break;

            case Time.NIGHT:
                rgbPairs.add(new RgbPair(0x404040, matchSettings.grass.lightShadow));
                for (int i = 0; i < 4; i++) {
                    playerShadows[i] = new Texture(game,
                            "images/player/shadows/player_" + i + ".png", rgbPairs);
                }
        }

        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 16; r++) {
                playerShadowTextureRegions[c][r] = new Frame(playerShadows[0],
                        c * 32, r * 32, 32, 32);
            }
        }

        // keeper
        keeperShadow = new Texture(game, "images/player/shadows/keeper_0.png",
                rgbPairs);
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 19; r++) {
                keeperShadowTextureRegions[c][r] = new Frame(keeperShadow,
                        c * 50, r * 50, 50, 50);
            }
        }

    }

    public static void loadPlayerNumber(GLGame game, MatchSettings matchSettings) {
        playerNumber = new Texture(game, "images/number.png");
        for (int i = 0; i < 10; i++) {
            playerNumberRegions[i] = new Frame(playerNumber, i * 6,
                    matchSettings.pitchType == Pitch.WHITE ? 16 : 0, 6, 10);
        }
    }

    public static void loadBall(GLGame game, MatchSettings matchSettings) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        switch (matchSettings.time) {
            case Time.DAY:
                rgbPairs.add(new RgbPair(0x005200, matchSettings.grass.lightShadow));
                rgbPairs.add(new RgbPair(0x001800, matchSettings.grass.darkShadow));
                break;

            case Time.NIGHT:
                rgbPairs.add(new RgbPair(0x005200, matchSettings.grass.lightShadow));
                rgbPairs.add(new RgbPair(0x001800, matchSettings.grass.lightShadow));
        }

        Assets.ball = new Texture(game, "images/"
                + (matchSettings.isSnowing() ? "ballsnow.png" : "ball.png"),
                rgbPairs);
    }

    public static void loadFlagposts(GLGame game, MatchSettings matchSettings) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        switch (matchSettings.time) {
            case Time.DAY:
                rgbPairs.add(new RgbPair(0x291000, matchSettings.grass.darkShadow));
                break;

            case Time.NIGHT:
                rgbPairs.add(new RgbPair(0x291000, matchSettings.grass.lightShadow));
        }

        Assets.flagposts = new Texture(game, "images/flagpost.png", rgbPairs);
        for (int frameX = 0; frameX < 6; frameX++) {
            for (int frameY = 0; frameY < 3; frameY++) {
                for (int i = 0; i < 4; i++) {
                    Assets.flagpostShadowFrames[frameX][frameY][i] = new Frame(
                            Assets.flagposts, 42 * frameX, 84 * frameY + 36
                            + 12 * i, 42, 12);
                }
            }
        }
    }

    public static void loadCrowd(Game game, Team team, List<RgbPair> rgbPairs) {
        crowd = new Texture((GLGame) game, "images/stadium/crowd.png", rgbPairs);
        crowdFrames[0] = new Frame(Assets.crowd, 0, 0, 47, 35);
        crowdFrames[1] = new Frame(Assets.crowd, 70, 0, 47, 35);
        crowdFrames[2] = new Frame(Assets.crowd, 0, 38, 47, 26);
        crowdFrames[3] = new Frame(Assets.crowd, 49, 29, 23, 35);
        crowdFrames[4] = new Frame(Assets.crowd, 105, 29, 23, 35);
    }

    public static void loadKit(Game game, Team team) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        Kit teamKit = team.kits.get(team.kitIndex);
        teamKit.addKitColors(rgbPairs);
        kit[team.index] = new Texture((GLGame) game, "images/kits/"
                + Kit.styles[teamKit.style] + ".png", rgbPairs);
    }

    public static int loadHair(Game game, int hairCut, List<RgbPair> rgbPairs) {
        Texture hair = new Texture((GLGame) game, "images/player/hair/"
                + hairCut + ".png", rgbPairs);
        hairs.add(hair);
        return hairs.size() - 1;
    }

    public static void reload(GLGame glGame) {
        for (int i = 0; i < hairs.size(); i++) {
            hairs.get(i).reload();
        }
        keeper.reload();
        ucode14.reload();
    }

    public static void loadStadium(GLGame game, MatchSettings matchSettings) {

        String paletteName = "time:" + matchSettings.time + " sky:"
                + matchSettings.sky;
        switch (matchSettings.time) {
            case Time.DAY:
                switch (matchSettings.sky) {
                    case Sky.CLEAR:
                        paletteName = Pitch.names[matchSettings.pitchType]
                                + "_sunny.pal";
                        break;

                    case Sky.CLOUDY:
                        paletteName = Pitch.names[matchSettings.pitchType]
                                + "_cloudy.pal";
                        break;
                }
                break;

            case Time.NIGHT:
                paletteName = Pitch.names[matchSettings.pitchType] + "_night.pal";
                break;
        }

        if (stadium[0][0] == null) {
            for (int c = 0; c < 4; c++) {
                for (int r = 0; r < 4; r++) {
                    stadium[r][c] = new Texture(game, "images/stadium/generic_"
                            + c + "" + r + ".png", "images/stadium/palettes/"
                            + paletteName);
                }
            }
        } else {
            for (int c = 0; c < 4; c++) {
                for (int r = 0; r < 4; r++) {
                    stadium[r][c].setPaletteFile("images/stadium/palettes/"
                            + paletteName);
                    stadium[r][c].reload();
                }
            }
        }
    }

    public static void playSound(GLGame glGame, Sound sound, float volume) {
        if (volume > 0) {
            sound.play(volume);
        }
    }

    private static void loadUcodeTable(Game game, int size) {
        InputStream in = null;
        try {
            in = game.getFileIO().readAsset("ucode_" + size + ".txt");

            byte[] buffer = new byte[1];
            int s = 0;

            // row
            for (int r = 0; r < 17; r++) {

                // block
                for (int b = 0; b < 8; b++) {

                    // column
                    for (int c = 0; c < 8; c++) {

                        in.read(buffer);
                        s = buffer[0] & 0xFF;

                        if (s >= 48 && s <= 57) {
                            s = s - 48;
                        } else if (s >= 65 && s <= 78) {
                            s = s - 55;
                        } else {
                            s = 30;
                        }

                        switch (size) {
                            case 10:
                                ucode10w[r * 64 + 8 * b + c] = s;
                                break;
                            case 14:
                                ucode14w[r * 64 + 8 * b + c] = s;
                                break;
                        }

                    }

                    // skip ':' or CR
                    in.read(buffer);
                    s = buffer[0] & 0xFF;

                }

                // if s = CR then skip LF
                if (s == 0x0D) {
                    in.read(buffer);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load ucode width table "
                    + e.getMessage());
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }

    }

}
