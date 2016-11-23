package com.ygames.ysoccer.framework;

public class RgbPair {

    GlColor rgbOld;
    GlColor rgbNew;

    public RgbPair(GlColor rgbOld, GlColor rgbNew) {
        this.rgbOld = rgbOld;
        this.rgbNew = rgbNew;
    }

    public RgbPair(int rgbOld, GlColor rgbNew) {
        this.rgbOld = new GlColor(rgbOld);
        this.rgbNew = rgbNew;
    }

    public RgbPair(int rgbOld, int rgbNew) {
        this.rgbOld = new GlColor(rgbOld);
        this.rgbNew = new GlColor(rgbNew);
    }
}
