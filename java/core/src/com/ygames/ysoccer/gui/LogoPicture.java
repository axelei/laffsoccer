package com.ygames.ysoccer.gui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Team;

public class LogoPicture extends Picture {

    private Team team;
    public boolean isCustom;
    private int centerX, centerY;

    public LogoPicture(Team team, int centerX, int centerY) {
        super(null);
        this.team = team;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public void onUpdate() {
        String logoPath = team.path.replaceFirst("/team.", "/logo.").replaceFirst(".json", ".png");
        FileHandle customLogo = Assets.teamsFolder.child(logoPath);
        if (customLogo.exists()) {
            isCustom = true;
            Texture texture = new Texture(customLogo.path());
            TextureRegion textureRegion = new TextureRegion(texture);
            textureRegion.flip(false, true);
            setTextureRegion(textureRegion);
        } else {
            setTextureRegion(team.kits.get(0).loadLogo());
        }
        setGeometry(centerX - textureRegion.getRegionWidth() / 2, centerY - textureRegion.getRegionHeight() / 2, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
