package com.ygames.ysoccer.framework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;

public class Video {

    public static final String JAVAFX_VIDEO_PLAYER = "com.ygames.ysoccer.desktop.JavaFXVideoPlayer";

    private static Class<?> videoPlayerClass;

    private static Method callMethod = null;
    private static Method readyMethod = null;

    static {
        try {
            videoPlayerClass = Class.forName(JAVAFX_VIDEO_PLAYER);
        } catch(ClassNotFoundException e) {
            videoPlayerClass = null;
        }

        try {
            callMethod = videoPlayerClass.getMethod("playMedia", Settings.class, String[].class);
            readyMethod = videoPlayerClass.getMethod("isReady");
        } catch (NoSuchMethodException e) {
            videoPlayerClass = null;
            e.printStackTrace();
        }
    }

    public static boolean isReady() {
        try {
            return (boolean) readyMethod.invoke(null, new Object[]{});
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void playVideo(Settings settings, String...media) {

        if (videoPlayerClass == null) {
            return;
        }

        try {
            callMethod.invoke(null, new Object[] {settings, media});
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
