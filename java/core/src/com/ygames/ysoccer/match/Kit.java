package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.GlColor;

public class Kit implements Json.Serializable {

    public String style;
    GlColor shirt1;
    GlColor shirt2;
    GlColor shorts;
    GlColor socks;

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
}
