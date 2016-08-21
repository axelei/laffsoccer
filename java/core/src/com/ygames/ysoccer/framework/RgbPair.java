package com.ygames.ysoccer.framework;

import java.awt.Color;

public class RgbPair {

    Color rgbOld;
    Color rgbNew;

    public RgbPair(Color rgbOld, Color rgbNew) {
        this.rgbOld = rgbOld;
        this.rgbNew = rgbNew;
    }

    public RgbPair(int rgbOld, Color rgbNew) {
        this.rgbOld = new Color(rgbOld);
        this.rgbNew = rgbNew;
    }
}
