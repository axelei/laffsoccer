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

    public void addKitColors(List<RgbPair> rgbPairs) {

        // shirt1
        rgbPairs.add(new RgbPair(0xE20020, shirt1));
        rgbPairs.add(new RgbPair(0xBF001B, shirt1.darker()));
        rgbPairs.add(new RgbPair(0x9C0016, shirt1.darker().darker()));
        rgbPairs.add(new RgbPair(0x790011, shirt1.darker().darker().darker()));

        // shirt2
        rgbPairs.add(new RgbPair(0x01FFC6, shirt2));
        rgbPairs.add(new RgbPair(0x00C79B, shirt2.darker()));
        rgbPairs.add(new RgbPair(0x008B6C, shirt2.darker().darker()));
        rgbPairs.add(new RgbPair(0x006A52, shirt2.darker().darker().darker()));

        // shorts
        rgbPairs.add(new RgbPair(0xF6FF00, shorts));
        rgbPairs.add(new RgbPair(0xCDD400, shorts.darker()));
        rgbPairs.add(new RgbPair(0xA3A900, shorts.darker().darker()));
        rgbPairs.add(new RgbPair(0x7A7E00, shorts.darker().darker().darker()));

        // socks
        rgbPairs.add(new RgbPair(0x0097EE, socks));
        rgbPairs.add(new RgbPair(0x0088D6, socks.darker()));
        rgbPairs.add(new RgbPair(0x0079BF, socks.darker().darker()));
        rgbPairs.add(new RgbPair(0x006AA7, socks.darker().darker().darker()));
    }
}
