package com.ygames.ysoccer.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ygames.ysoccer.YSoccer;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.forceExit = false;
        config.addIcon("images/icon_128.png", Files.FileType.Internal);
        config.addIcon("images/icon_32.png", Files.FileType.Internal);
        new LwjglApplication(new YSoccer(), config);
    }
}
