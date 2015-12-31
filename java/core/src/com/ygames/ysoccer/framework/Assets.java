package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class Assets {

    public static I18NBundle strings;
    public static Font font14;

    public static void load(Settings settings) {
        strings = I18NBundle.createBundle(Gdx.files.internal("i18n/strings"), new Locale(settings.locale));
        font14 = new Font(14);
        font14.load();
    }
}
