package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Sky;
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.math.Emath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Assets {

    public static Random random;
    public static Cursor customCursor;
    public static Cursor hiddenCursor;
    public static I18NBundle strings;
    public static List<String> locales;
    public static Font font14;
    public static Font font10;
    public static FileHandle teamsRootFolder;
    public static FileHandle competitionsRootFolder;
    public static FileHandle savesFolder;
    public static FileHandle saveGame;
    public static Json json;
    public static int[] calendars = new int[4600];
    public static List<String> associations;
    public static Tactics[] tactics = new Tactics[18];
    public static List<String> kits;
    public static List<String> hairStyles;
    public static List<String> currencies;
    public static TextureRegion[] stars = new TextureRegion[10];
    public static TextureRegion[][] controls = new TextureRegion[2][3];
    public static TextureRegion[][] pieces = new TextureRegion[2][2];
    public static TextureRegion[] lightIcons = new TextureRegion[3];
    public static TextureRegion[] pitchIcons = new TextureRegion[10];
    public static TextureRegion[] weatherIcons = new TextureRegion[11];
    public static TextureRegion[][] stadium = new TextureRegion[4][4];
    public static TextureRegion[] ball = new TextureRegion[5];
    public static TextureRegion[][] cornerFlags = new TextureRegion[6][3];
    public static TextureRegion[][][] cornerFlagsShadows = new TextureRegion[6][3][4];
    public static Texture goalTopA;
    public static Texture goalBottom;
    public static TextureRegion keeper[][][][] = new TextureRegion[2][10][8][19];
    public static TextureRegion[][][] keeperShadow = new TextureRegion[8][19][4];
    public static TextureRegion[][][][] player = new TextureRegion[2][10][8][16];
    public static Map<Hair, TextureRegion[][]> hairs = new HashMap<Hair, TextureRegion[][]>();
    public static TextureRegion[][][] playerShadow = new TextureRegion[8][16][4];
    public static Pixmap keeperCollisionDetection;
    public static TextureRegion[][] playerNumbers = new TextureRegion[10][2];
    public static TextureRegion time[] = new TextureRegion[11];
    public static TextureRegion score[] = new TextureRegion[11];
    public static TextureRegion replay[][] = new TextureRegion[16][2];
    public static TextureRegion replaySpeed[][] = new TextureRegion[3][3];
    public static TextureRegion[] rain = new TextureRegion[4];
    public static TextureRegion[] snow = new TextureRegion[3];
    public static Texture fog;
    public static TextureRegion[][] wind = new TextureRegion[8][2];

    public static void load(Settings settings) {
        random = new Random(System.currentTimeMillis());
        customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("images/arrow.png")), 0, 0);
        hiddenCursor = Gdx.graphics.newCursor(new Pixmap(1, 1, Pixmap.Format.RGBA8888), 0, 0);
        loadLocales();
        loadStrings(settings);
        font14 = new Font(14);
        font14.load();
        font10 = new Font(10);
        font10.load();

        // first-time setup of local data for android
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            FileHandle localDataFolder = Gdx.files.local("data");
            if (localDataFolder.list().length == 0) {
                Gdx.files.internal("data").copyTo(localDataFolder);
            }
        }

        teamsRootFolder = Gdx.files.local("data/teams/");
        competitionsRootFolder = Gdx.files.local("data/competitions/");
        savesFolder = Gdx.files.local("data/saves/competitions/");
        saveGame = savesFolder.child("savegame.json");
        json = new Json();
        json.addClassTag("CUP", Cup.class);
        json.addClassTag("LEAGUE", League.class);
        json.setOutputType(JsonWriter.OutputType.json);
        loadCalendars();
        associations = new ArrayList<String>(Arrays.asList(Const.associations));
        loadTactics();
        loadKits();
        hairStyles = loadHairStyles();
        currencies = new ArrayList<String>(Arrays.asList(loadJsonFile(String[].class, "currencies.json")));
        loadStars();
        loadControls();
        loadPieces();
        loadLightIcons();
        loadPitchIcons();
        loadWeatherIcons();
        goalTopA = new Texture("images/stadium/goal_top_a.png");
        goalBottom = new Texture("images/stadium/goal_bottom.png");
        loadKeeperShadow();
        loadPlayerShadow();
        keeperCollisionDetection = new Pixmap(Gdx.files.internal("images/keeper_cd.png"));
        loadPlayerNumbers();
        loadTime();
        loadScore();
        loadReplay();
        loadReplaySpeed();
        loadRain();
        loadSnow();
        fog = new Texture("images/fog.png");
        loadWind();
    }

    private static void loadLocales() {
        locales = new ArrayList<String>();
        FileHandle[] files = Gdx.files.local("i18n/").list(".properties");
        for (FileHandle file : files) {
            String[] parts = file.nameWithoutExtension().split("_");
            if (parts.length > 1) {
                locales.add(parts[1]);
            } else {
                locales.add("en");
            }
        }
        Collections.sort(locales);
    }

    public static void loadStrings(Settings settings) {
        strings = I18NBundle.createBundle(Gdx.files.internal("i18n/strings"), new Locale(settings.locale));
    }

    private static void loadCalendars() {
        InputStream in = null;
        try {
            in = Gdx.files.internal("calendars.bin").read();
            byte[] buffer = new byte[1];
            for (int i = 0; i < calendars.length; i++) {
                in.read(buffer);
                calendars[i] = buffer[0] & 0xFF;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while reading calendars " + e.getMessage());
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }
    }

    public static Comparator<FileHandle> fileComparatorByName = new CompareFileHandlesByName();

    private static class CompareFileHandlesByName implements Comparator<FileHandle> {

        @Override
        public int compare(FileHandle o1, FileHandle o2) {
            return o1.nameWithoutExtension().compareTo(o2.nameWithoutExtension());
        }
    }

    private static void loadTactics() {
        InputStream in = null;
        for (int i = 0; i < tactics.length; i++) {
            try {
                tactics[i] = new Tactics();
                in = Gdx.files.internal("data/tactics/preset/" + Tactics.fileNames[i] + ".TAC").read();
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
    }

    private static void loadKits() {
        kits = new ArrayList<String>();
        FileHandle fileHandle = Gdx.files.internal("images/kit");
        for (FileHandle kitFile : fileHandle.list()) {
            kits.add(kitFile.nameWithoutExtension());
        }
        Collections.sort(kits);
    }

    private static <T> T loadJsonFile(Class<T> type, String filename) {
        return Assets.json.fromJson(type, Gdx.files.internal(filename).readString("UTF-8"));
    }

    private static List<String> loadHairStyles() {
        List<String> hairStyles = new ArrayList<String>();
        FileHandle folder = Gdx.files.internal("images/player/hairstyles");
        for (FileHandle file : folder.list(".png")) {
            hairStyles.add(file.nameWithoutExtension());
        }
        Collections.sort(hairStyles);
        return hairStyles;
    }

    public static String moneyFormat(double p) {
        String suffix = "";
        int e3 = Emath.floor(Math.log10(p) / 3);
        switch (e3) {
            case 0:
                // no suffix
                break;

            case 1:
                suffix = strings.get("MONEY.THOUSANDS");
                break;

            case 2:
                suffix = strings.get("MONEY.MILLIONS");
                break;

            case 3:
                suffix = strings.get("MONEY.BILLIONS");
                break;
        }

        int e = Emath.floor(Math.log10(p));
        double div = Math.pow(10, e - 1);
        p = Emath.floor(p / div);

        int mul = e - 1 - 3 * e3;
        if (mul >= 0) {
            p *= (int) (Math.pow(10, mul));
        } else if (mul < 0) {
            p /= (Math.pow(10, -mul));
        }

        DecimalFormat df = new DecimalFormat("#,###,###,##0.##");
        return df.format(p) + suffix;
    }

    public static TextureRegion getNationalityFlag(String nationality) {
        try {
            Texture texture = new Texture("images/flags/tiny/" + nationality + ".png");
            TextureRegion textureRegion = new TextureRegion(texture);
            textureRegion.flip(false, true);
            return textureRegion;
        } catch (Exception e) {
            Gdx.app.log("Warning", e.getMessage());
            return null;
        }
    }

    private static void loadStars() {
        Texture texture = new Texture("images/stars.png");
        for (int i = 0; i < 10; i++) {
            stars[i] = new TextureRegion(texture, 0, 16 * i, 64, 16);
            stars[i].flip(false, true);
        }
    }

    private static void loadControls() {
        Texture texture = new Texture("images/controls.png");
        for (int i = 0; i < 3; i++) {
            controls[0][i] = new TextureRegion(texture, 36 * i, 0, 36, 36);
            controls[0][i].flip(false, true);
            controls[1][i] = new TextureRegion(texture, 18 * i, 36, 18, 18);
            controls[1][i].flip(false, true);
        }
    }

    private static void loadPieces() {
        Texture texture = new Texture("images/pieces.png");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                pieces[i][j] = new TextureRegion(texture, 20 * i, 14 * j, 20, 14);
                pieces[i][j].flip(false, true);
            }
        }
    }

    private static void loadLightIcons() {
        Texture texture = new Texture("images/light.png");
        for (int i = 0; i < 3; i++) {
            lightIcons[i] = new TextureRegion(texture, 47 * i, 0, 46, 46);
            lightIcons[i].flip(false, true);
        }
    }

    private static void loadPitchIcons() {
        Texture texture = new Texture("images/pitches.png");
        for (int i = 0; i < 10; i++) {
            pitchIcons[i] = new TextureRegion(texture, 47 * i, 0, 46, 46);
            pitchIcons[i].flip(false, true);
        }
    }

    private static void loadWeatherIcons() {
        Texture texture = new Texture("images/weather.png");
        for (int i = 0; i < 11; i++) {
            weatherIcons[i] = new TextureRegion(texture, 47 * i, 0, 46, 46);
            weatherIcons[i].flip(false, true);
        }
    }

    private static void loadTime() {
        Texture texture = new Texture("images/time.png");
        for (int i = 0; i < 11; i++) {
            time[i] = new TextureRegion(texture, 12 * i, 0, i < 10 ? 12 : 48, 20);
            time[i].flip(false, true);
        }
    }

    private static void loadScore() {
        Texture texture = new Texture("images/score.png");
        for (int i = 0; i < 11; i++) {
            score[i] = new TextureRegion(texture, 24 * i, 0, 24, 38);
            score[i].flip(false, true);
        }
    }

    private static void loadReplay() {
        Texture texture = new Texture("images/replay.png");
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 2; j++) {
                replay[i][j] = new TextureRegion(texture, 40 * i, 46 * j, 40, 46);
                replay[i][j].flip(false, true);
            }
        }
    }

    private static void loadReplaySpeed() {
        Texture texture = new Texture("images/replay_speed.png");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                replaySpeed[i][j] = new TextureRegion(texture, 29 * i, 29 * j, 29, 29);
                replaySpeed[i][j].flip(false, true);
            }
        }
    }

    private static void loadRain() {
        Texture texture = new Texture("images/rain.png");
        for (int i = 0; i < 4; i++) {
            rain[i] = new TextureRegion(texture, 30 * i, 0, 30, 30);
            rain[i].flip(false, true);
        }
    }

    private static void loadSnow() {
        Texture texture = new Texture("images/snow.png");
        for (int i = 0; i < 3; i++) {
            snow[i] = new TextureRegion(texture, 3 * i, 0, 3, 3);
            snow[i].flip(false, true);
        }
    }

    private static void loadWind() {
        Texture texture = new Texture("images/wind.png");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 2; j++) {
                wind[i][j] = new TextureRegion(texture, 30 * i, 30 * j, 30, 30);
                wind[i][j].flip(false, true);
            }
        }
    }

    public static void loadStadium(MatchSettings matchSettings) {

        String paletteName = matchSettings.pitchType.toString().toLowerCase();
        switch (matchSettings.time) {
            case DAY:
                switch (matchSettings.sky) {
                    case Sky.CLEAR:
                        paletteName += "_sunny.pal";
                        break;

                    case Sky.CLOUDY:
                        paletteName += "_cloudy.pal";
                        break;
                }
                break;

            case NIGHT:
                paletteName += "_night.pal";
                break;
        }

        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                stadium[r][c] = loadTextureRegion("images/stadium/generic_" + c + "" + r + ".png", "images/stadium/palettes/" + paletteName);
            }
        }
    }

    public static void loadBall(MatchSettings matchSettings) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        switch (matchSettings.time) {
            case DAY:
                rgbPairs.add(new RgbPair(0x005200, matchSettings.grass.lightShadow));
                rgbPairs.add(new RgbPair(0x001800, matchSettings.grass.darkShadow));
                break;

            case NIGHT:
                rgbPairs.add(new RgbPair(0x005200, matchSettings.grass.lightShadow));
                rgbPairs.add(new RgbPair(0x001800, matchSettings.grass.lightShadow));
                break;
        }

        Texture ballTexture = loadTexture("images/" + (matchSettings.isSnowing() ? "ballsnow.png" : "ball.png"), rgbPairs);
        for (int r = 0; r < 5; r++) {
            ball[r] = new TextureRegion(ballTexture, r * 8, 0, 8, 8);
            ball[r].flip(false, true);
        }
    }

    public static void loadCornerFlags(MatchSettings matchSettings) {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        switch (matchSettings.time) {
            case DAY:
                rgbPairs.add(new RgbPair(0x291000, matchSettings.grass.darkShadow));
                break;

            case NIGHT:
                rgbPairs.add(new RgbPair(0x291000, matchSettings.grass.lightShadow));
                break;
        }

        Texture cornerFlags = loadTexture("images/corner_flags.png", rgbPairs);
        for (int frameX = 0; frameX < 6; frameX++) {
            for (int frameY = 0; frameY < 3; frameY++) {
                Assets.cornerFlags[frameX][frameY] = new TextureRegion(cornerFlags, 42 * frameX, 84 * frameY, 42, 36);
                Assets.cornerFlags[frameX][frameY].flip(false, true);
                for (int i = 0; i < 4; i++) {
                    Assets.cornerFlagsShadows[frameX][frameY][i] = new TextureRegion(cornerFlags, 42 * frameX, 84 * frameY + 36 + 12 * i, 42, 12);
                    Assets.cornerFlagsShadows[frameX][frameY][i].flip(false, true);
                }
            }
        }
    }

    public static void loadKeeper(Player p) {
        if (keeper[p.team.index][p.skinColor.ordinal()][0][0] == null) {
            List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
            p.addSkinColors(rgbPairs);
            Texture keeper = loadTexture("images/player/keeper.png", rgbPairs);
            for (int frameX = 0; frameX < 8; frameX++) {
                for (int frameY = 0; frameY < 19; frameY++) {
                    Assets.keeper[p.team.index][p.skinColor.ordinal()][frameX][frameY] = new TextureRegion(keeper, 50 * frameX, 50 * frameY, 50, 50);
                    Assets.keeper[p.team.index][p.skinColor.ordinal()][frameX][frameY].flip(false, true);
                }
            }
        }
    }

    private static void loadKeeperShadow() {
        for (int i = 0; i < 4; i++) {
            Texture textureShadow = new Texture("images/player/shadows/keeper_" + i + ".png");
            for (int frameX = 0; frameX < 8; frameX++) {
                for (int frameY = 0; frameY < 19; frameY++) {
                    Assets.keeperShadow[frameX][frameY][i] = new TextureRegion(textureShadow, 50 * frameX, 50 * frameY, 50, 50);
                    Assets.keeperShadow[frameX][frameY][i].flip(false, true);
                }
            }
        }
    }

    private static void loadPlayerShadow() {
        for (int i = 0; i < 4; i++) {
            Texture textureShadow = new Texture("images/player/shadows/player_" + i + ".png");
            for (int frameX = 0; frameX < 8; frameX++) {
                for (int frameY = 0; frameY < 16; frameY++) {
                    Assets.playerShadow[frameX][frameY][i] = new TextureRegion(textureShadow, 32 * frameX, 32 * frameY, 32, 32);
                    Assets.playerShadow[frameX][frameY][i].flip(false, true);
                }
            }
        }
    }

    private static void loadPlayerNumbers() {
        Texture texture = new Texture("images/player/numbers.png");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 2; j++) {
                Assets.playerNumbers[i][j] = new TextureRegion(texture, 6 * i, 16 * j, 6, 10);
                Assets.playerNumbers[i][j].flip(false, true);
            }
        }
    }

    public static void loadPlayer(Player p) {
        if (player[p.team.index][p.skinColor.ordinal()][0][0] == null) {
            List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
            p.team.kits.get(p.team.kitIndex).addKitColors(rgbPairs);
            p.addSkinColors(rgbPairs);
            Texture playerTexture = loadTexture("images/player/" + p.team.kits.get(p.team.kitIndex).style + ".png", rgbPairs);
            for (int frameX = 0; frameX < 8; frameX++) {
                for (int frameY = 0; frameY < 16; frameY++) {
                    player[p.team.index][p.skinColor.ordinal()][frameX][frameY] = new TextureRegion(playerTexture, 32 * frameX, 32 * frameY, 32, 32);
                    player[p.team.index][p.skinColor.ordinal()][frameX][frameY].flip(false, true);
                }
            }
        }
    }

    public static void loadHair(Player player) {
        player.hair = new Hair(player.hairColor, player.hairStyle);
        if (hairs.get(player.hair) == null) {
            List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
            player.addHairColors(rgbPairs);
            Texture texture = loadTexture("images/player/hairstyles/" + player.hairStyle + ".png", rgbPairs);
            TextureRegion textureRegion[][] = new TextureRegion[8][10];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 10; j++) {
                    textureRegion[i][j] = new TextureRegion(texture, 21 * i, 21 * j, 20, 20);
                    textureRegion[i][j].flip(false, true);
                }
            }
            hairs.put(player.hair, textureRegion);
        }
    }

    public static TextureRegion loadTextureRegion(String internalPath) {
        Texture texture = new Texture(internalPath);
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.flip(false, true);
        return textureRegion;
    }

    public static TextureRegion loadTextureRegion(String internalPath, List<RgbPair> rgbPairs) {
        Texture texture = Assets.loadTexture(internalPath, rgbPairs);
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.flip(false, true);
        return textureRegion;
    }

    private static TextureRegion loadTextureRegion(String internalPath, String paletteFile) {
        Texture texture = loadTexture(internalPath, paletteFile);
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.flip(false, true);
        return textureRegion;
    }

    public static Texture loadTexture(String internalPath, List<RgbPair> rgbPairs) {
        InputStream in = null;
        try {
            in = Gdx.files.internal(internalPath).read();

            ByteArrayInputStream inputStream = PngEditor.editPalette(in, rgbPairs);

            byte[] bytes = FileUtils.inputStreamToBytes(inputStream);

            Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
            return new Texture(pixmap);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load texture", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    Gdx.app.error("loadTexture", e.toString());
                }
        }
    }

    private static Texture loadTexture(String internalPath, String paletteFile) {
        InputStream in = null;
        try {
            in = Gdx.files.internal(internalPath).read();
            InputStream palette = Gdx.files.internal(paletteFile).read();

            ByteArrayInputStream inputStream = PngEditor.swapPalette(in, palette);

            byte[] bytes = FileUtils.inputStreamToBytes(inputStream);

            Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
            return new Texture(pixmap);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load texture", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    Gdx.app.error("loadTexture", e.toString());
                }
        }
    }

    public static String getRelativeTeamPath(FileHandle fileHandle) {
        return fileHandle.path().replaceFirst(teamsRootFolder.path(), "");
    }

    public static FilenameFilter teamFilenameFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.startsWith("team.");
        }
    };
}
