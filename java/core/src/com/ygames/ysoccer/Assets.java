package com.ygames.ysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.ygames.ysoccer.framework.Font;

import java.util.Locale;

public class Assets {

    public static I18NBundle strings;
    public static Font font14;

    public static void load() {
        strings = I18NBundle.createBundle(Gdx.files.internal("i18n/strings"), new Locale("en"));
        font14 = new Font(14);
        font14.load();
    }
}
