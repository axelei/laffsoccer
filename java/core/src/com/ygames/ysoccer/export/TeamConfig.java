package com.ygames.ysoccer.export;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class TeamConfig implements Json.Serializable {

    public FileHandle sourceFile;
    public String path;
    public int country;
    public int gtn;
    public int division;

    public TeamConfig() {
        sourceFile = null;
        path = null;
        country = 0;
        gtn = 0;
        division = -1;
    }

    public TeamConfig(String path, int country, int gtn, int division) {
        this.sourceFile = null;
        this.path = path;
        this.country = country;
        this.gtn = gtn;
        this.division = division;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.readFields(this, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeValue("path", path);
        json.writeValue("country", country);
        json.writeValue("gtn", gtn);
        json.writeValue("division", division);
    }
}
