package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.GlColor;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.framework.RgbPair;

import java.util.ArrayList;
import java.util.List;

public class Kit implements Json.Serializable {

    public enum Field {SHIRT1, SHIRT2, SHORTS, SOCKS}

    public String style;
    public GlColor shirt1;
    public GlColor shirt2;
    public GlColor shorts;
    public GlColor socks;

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
        json.writeValue("shirt1", shirt1.toHexString());
        json.writeValue("shirt2", shirt2.toHexString());
        json.writeValue("shorts", shorts.toHexString());
        json.writeValue("socks", socks.toHexString());
    }

    public void read(Json json, JsonValue jsonMap) {
        style = jsonMap.getString("style");
        shirt1 = new GlColor(jsonMap.getString("shirt1"));
        shirt2 = new GlColor(jsonMap.getString("shirt2"));
        shorts = new GlColor(jsonMap.getString("shorts"));
        socks = new GlColor(jsonMap.getString("socks"));
    }

    public Image loadImage() {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        addKitColors(rgbPairs);
        return Image.loadImage("images/kit/" + style + ".PNG", rgbPairs);
    }

    public Image loadLogo() {
        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
        addKitColors(rgbPairs);
        return Image.loadImage("images/logo/" + style + ".PNG", rgbPairs);
    }

    public void addKitColors(List<RgbPair> rgbPairs) {

        // shirt1
        rgbPairs.add(new RgbPair(0xE20020, shirt1));
        rgbPairs.add(new RgbPair(0xBF001B, shirt1.darker(0.8)));
        rgbPairs.add(new RgbPair(0x9C0016, shirt1.darker(0.6)));
        rgbPairs.add(new RgbPair(0x790011, shirt1.darker(0.4)));

        // shirt2
        rgbPairs.add(new RgbPair(0x01FFC6, shirt2));
        rgbPairs.add(new RgbPair(0x00C79B, shirt2.darker(0.8)));
        rgbPairs.add(new RgbPair(0x008B6C, shirt2.darker(0.6)));
        rgbPairs.add(new RgbPair(0x006A52, shirt2.darker(0.4)));

        // shorts
        rgbPairs.add(new RgbPair(0xF6FF00, shorts));
        rgbPairs.add(new RgbPair(0xCDD400, shorts.darker(0.8)));
        rgbPairs.add(new RgbPair(0xA3A900, shorts.darker(0.6)));
        rgbPairs.add(new RgbPair(0x7A7E00, shorts.darker(0.4)));

        // socks
        rgbPairs.add(new RgbPair(0x0097EE, socks));
        rgbPairs.add(new RgbPair(0x0088D6, socks.darker(0.8)));
        rgbPairs.add(new RgbPair(0x0079BF, socks.darker(0.6)));
        rgbPairs.add(new RgbPair(0x006AA7, socks.darker(0.4)));
    }

    public static float colorDifference(Kit homeKit, Kit awayKit) {
        int homeColor1 = homeKit.shirt1.getRGB();
        int homeColor2 = homeKit.shirt2.getRGB();
        int awayColor1 = awayKit.shirt1.getRGB();
        int awayColor2 = awayKit.shirt2.getRGB();
        float differencePair = GlColor.difference(homeColor1, awayColor1) + GlColor.difference(homeColor2, awayColor2);
        float differenceSwap = GlColor.difference(homeColor1, awayColor2) + GlColor.difference(homeColor2, awayColor1);
        return Math.min(differencePair, differenceSwap);
    }
}
