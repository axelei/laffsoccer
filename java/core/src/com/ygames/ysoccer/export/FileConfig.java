package com.ygames.ysoccer.export;

import java.util.ArrayList;
import java.util.List;

public class FileConfig {

    public String filename;
    public List<TeamConfig> teams = new ArrayList<TeamConfig>();

    public FileConfig() {
    }

    public FileConfig(String filename) {
        this.filename = filename;
        teams = new ArrayList<TeamConfig>();
    }
}
