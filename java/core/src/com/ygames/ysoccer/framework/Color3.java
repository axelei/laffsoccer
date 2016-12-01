package com.ygames.ysoccer.framework;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Color3 implements Json.Serializable {

    public int color1;
    public int color2;
    public int color3;

    public Color3() {
    }

    public Color3(int color1, int color2, int color3) {
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    @Override
    public void write(Json json) {
        json.writeValue("color1", GLColor.toHexString(color1));
        json.writeValue("color2", GLColor.toHexString(color2));
        json.writeValue("color3", GLColor.toHexString(color3));
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        color1 = GLColor.valueOf(jsonMap.getString("color1"));
        color2 = GLColor.valueOf(jsonMap.getString("color2"));
        color3 = GLColor.valueOf(jsonMap.getString("color3"));
    }
}
