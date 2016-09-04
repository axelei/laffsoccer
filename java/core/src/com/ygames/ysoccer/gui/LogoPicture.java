package com.ygames.ysoccer.gui;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.match.Team;

public class LogoPicture extends Picture {

    Team team;
    public boolean isCustom;

    public LogoPicture(Team team) {
        super(null);
        this.team = team;
    }

    @Override
    public void onUpdate() {
        String logoPath = team.path.replaceFirst("/TEAM.", "/LOGO.").replaceFirst(".JSON", ".PNG");
        FileHandle customLogo = Assets.teamsFolder.child(logoPath);
        if (customLogo.exists()) {
            isCustom = true;
            setImage(new Image(customLogo.path()));
        } else {
            setImage(team.kits.get(0).loadLogo());
        }
        setGeometry(135 - image.getRegionWidth() / 2, 50 - image.getRegionHeight() / 2, image.getRegionWidth(), image.getRegionHeight());
    }
}
