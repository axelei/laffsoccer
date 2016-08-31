package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.GlColor;

public class GlColor3 implements Json.Serializable {

    public String name;
    GlColor color1;
    GlColor color2;
    GlColor color3;

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("color1", color1.toHexString());
        json.writeValue("color2", color2.toHexString());
        json.writeValue("color3", color3.toHexString());
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        name = jsonMap.getString("name");
        color1 = new GlColor(jsonMap.getString("color1"));
        color2 = new GlColor(jsonMap.getString("color2"));
        color3 = new GlColor(jsonMap.getString("color3"));
    }
}
