package com.ygames.ysoccer.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ygames.ysoccer.YSoccer;

public class DesktopLauncher {

    public static final String ICON_128 = "images/icon_128.png";
    public static final String ICON_32 = "images/icon_128.png";

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.addIcon(ICON_128, Files.FileType.Internal);
        config.addIcon(ICON_32, Files.FileType.Internal);
        config.title = "Charnego Internatiolaff Soccer";
        config.forceExit = false;
        new LwjglApplication(new YSoccer(), config);
    }
}
