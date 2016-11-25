package com.ygames.ysoccer.framework;

public class RgbPair {

    int rgbOld;
    int rgbNew;

    public RgbPair(int rgbOld, GlColor rgbNew) {
        this.rgbOld = rgbOld;
        this.rgbNew = rgbNew.getRGB();
    }

    public RgbPair(int rgbOld, int rgbNew) {
        this.rgbOld = rgbOld;
        this.rgbNew = rgbNew;
    }
}
