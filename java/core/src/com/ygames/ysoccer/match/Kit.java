package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GlColor;
import com.ygames.ysoccer.framework.RgbPair;

import java.util.ArrayList;
import java.util.List;

public class Kit implements Json.Serializable {

    public enum Field {SHIRT1, SHIRT2, SHORTS, SOCKS}

    public String style;
    public int shirt1;
    public int shirt2;
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

    public void write(Json json) {
        json.writeValue("style", style);
        json.writeValue("shirt1", GlColor.toHexString(shirt1));
        json.writeValue("shirt2", GlColor.toHexString(shirt2));
        json.writeValue("shorts", GlColor.toHexString(shorts));
        json.writeValue("socks", GlColor.toHexString(socks));
    }

    public void read(Json json, JsonValue jsonMap) {
        style = jsonMap.getString("style");
        shirt1 = GlColor.valueOf(jsonMap.getString("shirt1"));
        shirt2 = GlColor.valueOf(jsonMap.getString("shirt2"));
        shorts = GlColor.valueOf(jsonMap.getString("shorts"));
        socks = GlColor.valueOf(jsonMap.getString("socks"));
    }

    public TextureRegion loadImage() {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        addKitColors(rgbPairs);
        return Assets.loadTextureRegion("images/kit/" + style + ".PNG", rgbPairs);
    }

    public TextureRegion loadLogo() {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        addKitColors(rgbPairs);
        return Assets.loadTextureRegion("images/logo/" + style + ".PNG", rgbPairs);
    }

    public void addKitColors(List<RgbPair> rgbPairs) {

        // shirt1
        rgbPairs.add(new RgbPair(0xE20020, shirt1));
        rgbPairs.add(new RgbPair(0xBF001B, GlColor.darker(shirt1, 0.8D)));
        rgbPairs.add(new RgbPair(0x9C0016, GlColor.darker(shirt1, 0.6D)));
        rgbPairs.add(new RgbPair(0x790011, GlColor.darker(shirt1, 0.4D)));

        // shirt2
        rgbPairs.add(new RgbPair(0x01FFC6, shirt2));
        rgbPairs.add(new RgbPair(0x00C79B, GlColor.darker(shirt2, 0.8D)));
        rgbPairs.add(new RgbPair(0x008B6C, GlColor.darker(shirt2, 0.6D)));
        rgbPairs.add(new RgbPair(0x006A52, GlColor.darker(shirt2, 0.4D)));

        // shorts
        rgbPairs.add(new RgbPair(0xF6FF00, shorts));
        rgbPairs.add(new RgbPair(0xCDD400, GlColor.darker(shorts, 0.8D)));
        rgbPairs.add(new RgbPair(0xA3A900, GlColor.darker(shorts, 0.6D)));
        rgbPairs.add(new RgbPair(0x7A7E00, GlColor.darker(shorts, 0.4D)));

        // socks
        rgbPairs.add(new RgbPair(0x0097EE, socks));
        rgbPairs.add(new RgbPair(0x0088D6, GlColor.darker(socks, 0.8D)));
        rgbPairs.add(new RgbPair(0x0079BF, GlColor.darker(socks, 0.6D)));
        rgbPairs.add(new RgbPair(0x006AA7, GlColor.darker(socks, 0.4D)));
    }

    public static float colorDifference(Kit homeKit, Kit awayKit) {
        int homeColor1 = homeKit.shirt1;
        int homeColor2 = homeKit.shirt2;
        int awayColor1 = awayKit.shirt1;
        int awayColor2 = awayKit.shirt2;
        float differencePair = GlColor.difference(homeColor1, awayColor1) + GlColor.difference(homeColor2, awayColor2);
        float differenceSwap = GlColor.difference(homeColor1, awayColor2) + GlColor.difference(homeColor2, awayColor1);
        return Math.min(differencePair, differenceSwap);
    }
}
