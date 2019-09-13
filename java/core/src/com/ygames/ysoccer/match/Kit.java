package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLColor;
import com.ygames.ysoccer.framework.RgbPair;

import java.util.ArrayList;
import java.util.List;

public class Kit implements Json.Serializable {

    public enum Field {SHIRT1, SHIRT2, SHIRT3, SHORTS, SOCKS}

    public String style;
    public int shirt1;
    public int shirt2;
    public int shirt3;
    public int shorts;
    public int socks;

    public static int[] colors = new int[]{
            0xAFB2B9, // lgray
            0xDFE8EB, // white
            0x1E1E1E, // black
            0xF5732C, // orange
            0xF61C1C, // red
            0x3033C1, // blue
            0x993333, // brown
            0x47BFEB, // lblue
            0x30C52C, // green
            0xE8EF2F, // yellow
            0x5D2399, // violet
            0xF59EE1, // pink
            0x0B7DD5, // kombat blue
            0x5083B8, // steel
            0x417039, // mil.green
            0x2E247A, // dark blue
            0xA31212, // garnet
            0xD000EF, // light violet
            0x7BE2E3, // sky blue
            0x81FF11, // bright green
            0xFFD200, // dark yellow
            0x949494, // dark gray
            0xCAC47D, // gold
    };

    public Kit() {
    }

    public Kit(String style, int shirt1, int shirt2, int shirt3, int shorts, int socks) {
        this.style = style;
        this.shirt1 = shirt1;
        this.shirt2 = shirt2;
        this.shirt3 = shirt3;
        this.shorts = shorts;
        this.socks = socks;
    }

    @Override
    public void write(Json json) {
        json.writeValue("style", style);
        json.writeValue("shirt1", GLColor.toHexString(shirt1));
        json.writeValue("shirt2", GLColor.toHexString(shirt2));
        json.writeValue("shirt3", GLColor.toHexString(shirt3));
        json.writeValue("shorts", GLColor.toHexString(shorts));
        json.writeValue("socks", GLColor.toHexString(socks));
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        style = jsonMap.getString("style");
        shirt1 = GLColor.valueOf(jsonMap.getString("shirt1"));
        shirt2 = GLColor.valueOf(jsonMap.getString("shirt2"));
        shirt3 = GLColor.valueOf(jsonMap.getString("shirt3", "#000000"));
        shorts = GLColor.valueOf(jsonMap.getString("shorts"));
        socks = GLColor.valueOf(jsonMap.getString("socks"));
    }

    public TextureRegion loadImage() {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        addKitColors(rgbPairs);
        return Assets.loadTextureRegion("images/kit/" + style + ".png", rgbPairs);
    }

    public TextureRegion loadLogo() {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        addKitColors(rgbPairs);
        return Assets.loadTextureRegion("images/logo/" + style + ".png", rgbPairs);
    }

    public void addKitColors(List<RgbPair> rgbPairs) {

        // shirt1
        rgbPairs.add(new RgbPair(0xE20020, shirt1));
        rgbPairs.add(new RgbPair(0xBF001B, GLColor.darker(shirt1, 0.9)));
        rgbPairs.add(new RgbPair(0x9C0016, GLColor.darker(shirt1, 0.8)));
        rgbPairs.add(new RgbPair(0x790011, GLColor.darker(shirt1, 0.7)));

        // shirt2
        rgbPairs.add(new RgbPair(0x01FFC6, shirt2));
        rgbPairs.add(new RgbPair(0x00C79B, GLColor.darker(shirt2, 0.9)));
        rgbPairs.add(new RgbPair(0x008B6C, GLColor.darker(shirt2, 0.8)));
        rgbPairs.add(new RgbPair(0x006A52, GLColor.darker(shirt2, 0.7)));

        // shirt3
        rgbPairs.add(new RgbPair(0xCC00FF, shirt3));
        rgbPairs.add(new RgbPair(0x8600A7, GLColor.darker(shirt3, 0.9)));
        rgbPairs.add(new RgbPair(0x610079, GLColor.darker(shirt3, 0.8)));
        rgbPairs.add(new RgbPair(0x420052, GLColor.darker(shirt3, 0.7)));

        // shorts
        rgbPairs.add(new RgbPair(0xF6FF00, shorts));
        rgbPairs.add(new RgbPair(0xCDD400, GLColor.darker(shorts, 0.9)));
        rgbPairs.add(new RgbPair(0xA3A900, GLColor.darker(shorts, 0.8)));
        rgbPairs.add(new RgbPair(0x7A7E00, GLColor.darker(shorts, 0.7)));

        // socks
        rgbPairs.add(new RgbPair(0x0097EE, socks));
        rgbPairs.add(new RgbPair(0x0088D6, GLColor.darker(socks, 0.9)));
        rgbPairs.add(new RgbPair(0x0079BF, GLColor.darker(socks, 0.8)));
        rgbPairs.add(new RgbPair(0x006AA7, GLColor.darker(socks, 0.7)));
    }

    public static float colorDifference(Kit homeKit, Kit awayKit) {
        int homeColor1 = homeKit.shirt1;
        int homeColor2 = homeKit.shirt2;
        int awayColor1 = awayKit.shirt1;
        int awayColor2 = awayKit.shirt2;
        float differencePair = GLColor.difference(homeColor1, awayColor1) + GLColor.difference(homeColor2, awayColor2);
        float differenceSwap = GLColor.difference(homeColor1, awayColor2) + GLColor.difference(homeColor2, awayColor1);
        return Math.min(differencePair, differenceSwap);
    }
}
