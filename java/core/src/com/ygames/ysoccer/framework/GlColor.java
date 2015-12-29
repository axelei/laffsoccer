package com.ygames.ysoccer.framework;

public class GlColor {

    public static int rgb(int r, int g, int b) {
        return ((r << 16) | (g << 8) | b);
    }

    public static int red(int rgb) {
        return ((rgb >> 16) & 0xFF);
    }

    public static int green(int rgb) {
        return ((rgb >> 8) & 0xFF);
    }

    public static int blue(int rgb) {
        return (rgb & 0xFF);
    }
}
