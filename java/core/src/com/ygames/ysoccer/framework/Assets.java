package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.math.Emath;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Assets {

    public static I18NBundle strings;
    public static List<String> locales;
    public static Font font14;
    public static Font font10;
    public static FileHandle teamsFolder;
    public static FileHandle competitionsFolder;
    public static FileHandle savesFolder;
    public static Json json;
    public static int[] calendars = new int[4600];
    public static List<String> associations;
    public static Tactics[] tactics = new Tactics[18];
    public static List<String> kits;
    public static List<GlColor3> hairColors;
    public static List<String> hairStyles;
    public static List<GlColor3> skinColors;
    public static List<GlColor2> shavedColors;
    public static List<String> currencies;
    public static Image[] stars = new Image[10];

    public static void load(Settings settings) {
        loadLocales();
        loadStrings(settings);
        font14 = new Font(14);
        font14.load();
        font10 = new Font(10);
        font10.load();
        teamsFolder = Gdx.files.local("data/teams");
        competitionsFolder = Gdx.files.local("data/competitions");
        savesFolder = Gdx.files.local("data/saves/competitions");
        json = new Json();
        json.addClassTag("CUP", Cup.class);
        json.addClassTag("LEAGUE", League.class);
        json.setOutputType(JsonWriter.OutputType.json);
        loadCalendars();
        associations = new ArrayList<String>(Arrays.asList(Const.associations));
        loadTactics();
        loadKits();
        hairColors = loadColors("player/haircolors");
        hairStyles = loadHairStyles();
        skinColors = loadColors("player/skincolors");
        shavedColors = new ArrayList<GlColor2>(Arrays.asList(loadJsonFile(GlColor2[].class, "player/shaved_colors.json")));
        currencies = new ArrayList<String>(Arrays.asList(loadJsonFile(String[].class, "currencies.json")));
        loadStars();
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
        Collections.sort(locales, new Assets.CompareStringsByName());
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

    public static class CompareFileHandlesByName implements Comparator<FileHandle> {

        @Override
        public int compare(FileHandle o1, FileHandle o2) {
            return o1.name().compareTo(o2.name());
        }
    }

    public static class CompareStringsByName implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
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

    private static List<GlColor3> loadColors(String path) {
        List<GlColor3> colors = new ArrayList<GlColor3>();
        FileHandle folder = Gdx.files.internal(path);
        List<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(folder.list(".json")));
        Collections.sort(files, new CompareFileHandlesByName());
        for (FileHandle file : files) {
            GlColor3 color = Assets.json.fromJson(GlColor3.class, file.readString());
            colors.add(color);
        }
        return colors;
    }

    private static <T> T loadJsonFile(Class<T> type, String filename) {
        return Assets.json.fromJson(type, Gdx.files.internal(filename).readString());
    }

    private static List<String> loadHairStyles() {
        List<String> hairStyles = new ArrayList<String>();
        FileHandle folder = Gdx.files.internal("images/player/hairstyles");
        for (FileHandle file : folder.list(".PNG")) {
            hairStyles.add(file.nameWithoutExtension());
        }
        Collections.sort(hairStyles);
        return hairStyles;
    }

    public static GlColor3 getHairColorByName(String name) {
        for (GlColor3 hairColor : Assets.hairColors) {
            if (hairColor.name.equals(name)) {
                return hairColor;
            }
        }
        return Assets.hairColors.get(0);
    }

    public static GlColor3 getSkinColorByName(String name) {
        for (GlColor3 skinColor : Assets.skinColors) {
            if (skinColor.name.equals(name)) {
                return skinColor;
            }
        }
        return Assets.skinColors.get(0);
    }

    public static GlColor2 getShavedColor(String skinColor, String hairColor) {
        for (GlColor2 shavedColor : Assets.shavedColors) {
            if (shavedColor.name.equals(skinColor + "-" + hairColor)) {
                return shavedColor;
            }
        }
        return null;
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

    public static Image getNationalityFlag(String nationality) {
        String filename = "images/flags/tiny/" + nationality + ".png";
        try {
            return new Image(filename);
        } catch (Exception e) {
            Gdx.app.log("Warning", e.getMessage());
            return null;
        }
    }

    private static void loadStars() {
        Texture texture = new Texture("images/stars.png");
        for (int i = 0; i < 10; i++) {
            stars[i] = new Image(texture, 0, 16 * i, 64, 16);
        }
    }
}
