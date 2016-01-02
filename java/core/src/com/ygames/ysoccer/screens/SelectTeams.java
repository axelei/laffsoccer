package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

public class SelectTeams extends GlScreen {

    private FileHandle fileHandle;

    public SelectTeams(GlGame game, FileHandle fileHandle, League league) {
        super(game);
        this.fileHandle = fileHandle;

        background = new Image("images/backgrounds/menu_friendly.jpg");

        Widget w;
        List<Widget> list = new ArrayList<Widget>();
        for (Team teamStub : league.teams) {
            FileHandle teamFile = fileHandle.child("team." + teamStub.code + ".json");
            Json json = new Json();
            Team team = json.fromJson(Team.class, teamFile.readString());
            if (teamFile.exists()) {
                w = new TeamButton(team);
                list.add(w);
                widgets.add(w);
            }
        }
        if (list.size() > 0) {
            Widget.arrange(game.settings, 32, list);
            selectedWidget = list.get(0);
        }
    }

    class TeamButton extends Button {
        public TeamButton(Team team) {
            setSize(270, 30);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(team.name.toUpperCase(), Font.Align.CENTER, Assets.font14);
        }
    }
}
