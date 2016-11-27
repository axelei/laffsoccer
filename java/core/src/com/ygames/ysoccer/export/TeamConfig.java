package com.ygames.ysoccer.export;

import com.badlogic.gdx.files.FileHandle;

public class TeamConfig {

    public FileHandle sourceFile;
    public String path;
    public int gtn;
    public int division;

    public TeamConfig() {
        sourceFile = null;
        path = null;
        gtn = 0;
        division = -1;
    }

    public TeamConfig(String path, int gtn, int division) {
        this.sourceFile = null;
        this.path = path;
        this.gtn = gtn;
        this.division = division;
    }
}
