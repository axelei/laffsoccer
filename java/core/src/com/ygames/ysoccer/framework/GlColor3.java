package com.ygames.ysoccer.framework;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class GlColor3 implements Json.Serializable {

    public String name;
    public int color1;
    public int color2;
    public int color3;

    public GlColor3() {
    }

    public GlColor3(String name, int color1, int color2, int color3) {
        this.name = name;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("color1", GlColor.toHexString(color1));
        json.writeValue("color2", GlColor.toHexString(color2));
        json.writeValue("color3", GlColor.toHexString(color3));
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        name = jsonMap.getString("name");
        color1 = GlColor.valueOf(jsonMap.getString("color1"));
        color2 = GlColor.valueOf(jsonMap.getString("color2"));
        color3 = GlColor.valueOf(jsonMap.getString("color3"));
    }
}
