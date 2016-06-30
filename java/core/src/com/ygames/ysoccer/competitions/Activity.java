package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;

public abstract class Activity {

    public String longName;
    public String filename;

    public enum Category {
        DIY_COMPETITION,
        PRESET_COMPETITION
    }

    public Category category;

    public Activity() {
        filename = "";
    }

    public boolean isEnded() {
        return true;
    }

    public void restart() {
    }

    public static String getCategoryLabel(Category category) {
        String label = "";
        switch (category) {
            case DIY_COMPETITION:
                label = "DIY COMPETITION";
                break;
            case PRESET_COMPETITION:
                label = "PRESET COMPETITION";
                break;
        }
        return label;
    }

    public static String getWarningLabel(Category category) {
        String label = "";
        switch (category) {
            case DIY_COMPETITION:
                label = "YOU ARE ABOUT TO LOSE CURRENT DIY COMPETITION";
                break;
            case PRESET_COMPETITION:
                label = "YOU ARE ABOUT TO LOSE CURRENT PRESET COMPETITION";
                break;
        }
        return label;
    }

    public void save() {
        String saveName = filename;
        switch (category) {
            case DIY_COMPETITION:
                saveName += ".diy.json";
                break;
            case PRESET_COMPETITION:
                saveName += ".preset.json";
                break;
        }
        FileHandle fileHandle = Assets.savesFolder.child(saveName);
        Assets.json.toJson(this, Activity.class, fileHandle);
    }
}
