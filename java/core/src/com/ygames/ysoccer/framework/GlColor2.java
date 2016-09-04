package com.ygames.ysoccer.framework;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class GlColor2 implements Json.Serializable {

    public String name;
    public GlColor color1;
    public GlColor color2;

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("color1", color1.toHexString());
        json.writeValue("color2", color2.toHexString());
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        name = jsonMap.getString("name");
        color1 = new GlColor(jsonMap.getString("color1"));
        color2 = new GlColor(jsonMap.getString("color2"));
    }
}
