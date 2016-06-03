package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Assets {

    public static I18NBundle strings;
    public static List<String> locales;
    public static Font font14;
    public static Font font10;
    public static FileHandle teamsFolder;
    public static FileHandle competitionsFolder;
    public static Json json;
    public static int[] calendars = new int[4600];

    public static void load(Settings settings) {
        loadLocales();
        loadStrings(settings);
        font14 = new Font(14);
        font14.load();
        font10 = new Font(10);
        font10.load();
        teamsFolder = Gdx.files.local("data/teams");
        competitionsFolder = Gdx.files.local("data/competitions");
        json = new Json();
        loadCalendars();
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
}
