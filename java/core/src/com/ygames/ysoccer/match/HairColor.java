package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.GlColor;

public class HairColor implements Json.Serializable {

    public String name;
    GlColor colors[];

    @Override
    public void write(Json json) {
        json.writeValue(name, new String[]{colors[0].toHexString(), colors[1].toHexString(), colors[2].toHexString()});
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.child().name();
        String cs[] = jsonData.child().asStringArray();
        colors = new GlColor[]{new GlColor(cs[0]), new GlColor(cs[1]), new GlColor(cs[2])};
    }
}
