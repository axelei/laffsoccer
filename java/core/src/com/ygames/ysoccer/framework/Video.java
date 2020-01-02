package com.ygames.ysoccer.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Video {

    public static final String JAVAFX_VIDEO_PLAYER = "com.ygames.ysoccer.desktop.JavaFXVideoPlayer";

    public static void playVideo(Settings settings, String...media) {

        Class<?> videoPlayerClass = null;

        try {
            videoPlayerClass = Class.forName(JAVAFX_VIDEO_PLAYER);
        } catch(ClassNotFoundException e) {
            return;
        }

        try {
            Method callMethod = videoPlayerClass.getMethod("playMedia", Settings.class, String[].class);
            callMethod.invoke(null, new Object[] {settings, media});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

}
