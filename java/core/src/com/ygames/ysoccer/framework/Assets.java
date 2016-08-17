package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Tactics;

import java.io.IOException;
import java.io.InputStream;
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
}
