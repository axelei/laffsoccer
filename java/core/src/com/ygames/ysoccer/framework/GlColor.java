package com.ygames.ysoccer.framework;

import java.awt.Color;

public class GlColor extends Color {

    public enum Component {ALPHA, RED, GREEN, BLUE}

    public GlColor(int rgb) {
        super(rgb);
    }

    // "#ABCDEF" format
    public GlColor(String hexString) {
        super(Integer.parseInt(hexString.substring(1), 16));
    }

    public GlColor(int red, int green, int blue) {
        super(red, green, blue);
    }

    public String toHexString() {
        return String.format("#%02x%02x%02x", getRed(), getGreen(), getBlue());
    }

    public int getComponentValue(Component component) {
        if (component == Component.ALPHA) {
            return getAlpha();
        } else if (component == Component.RED) {
            return getRed();
        } else if (component == Component.GREEN) {
            return getGreen();
        } else if (component == Component.BLUE) {
            return getBlue();
        } else {
            return -1;
        }
    }

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
