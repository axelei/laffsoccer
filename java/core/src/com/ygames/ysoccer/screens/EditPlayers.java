package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

public class EditPlayers extends GlScreen {

    Team team;
    boolean modified;

    public EditPlayers(GlGame game, Team team, Boolean modified) {
        super(game);
        this.team = team;
        this.modified = modified;

        background = game.stateBackground;

        Widget w;

        w = new TeamNameButton();
        widgets.add(w);

        selectedWidget = w;
    }

    class TeamNameButton extends InputButton {

        public TeamNameButton() {
            setGeometry(60, 30, 520, 40);
            setColors(0x9C522A, 0xBB5A25, 0x69381D);
            setText(team.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(16);
        }

        @Override
        public void onUpdate() {
            team.name = text;
            setModified();
        }
    }

    void setModified() {
        modified = true;
    }
}
