package com.ygames.ysoccer.framework;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class GlColor3 implements Json.Serializable {

    public String name;
    public GlColor color1;
    public GlColor color2;
    public GlColor color3;

    public GlColor3() {
    }

    public GlColor3(String name, int color1, int color2, int color3) {
        this.name = name;
        this.color1 = new GlColor(color1);
        this.color2 = new GlColor(color2);
        this.color3 = new GlColor(color3);
    }

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
