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
            if (teamFile.exists()) {
                Json json = new Json();
                Team team = json.fromJson(Team.class, teamFile.readString());
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

        Team team;

        public TeamButton(Team team) {
            this.team = team;
            setSize(270, 30);
            updateColors();
            setText(team.name.toUpperCase(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            if (game.teamList.contains(team)) {
                switch (team.controlMode) {
                    case COMPUTER:
                        team.controlMode = Team.ControlMode.PLAYER;
                        break;
                    case PLAYER:
                        team.controlMode = Team.ControlMode.COACH;
                        break;
                    case COACH:
                        team.controlMode = Team.ControlMode.UNDEFINED;
                        game.teamList.remove(team);
                        break;
                }
            } else {
                team.controlMode = Team.ControlMode.COMPUTER;
                game.teamList.add(team);
            }
            updateColors();
        }

        private void updateColors() {
            switch (team.controlMode) {
                case UNDEFINED:
                    setColors(0x98691E, 0xC88B28, 0x3E2600);
                    break;
                case COMPUTER:
                    setColors(0x981E1E, 0xC72929, 0x640000);
                    break;
                case PLAYER:
                    setColors(0x0000C8, 0x1919FF, 0x000078);
                    break;
                case COACH:
                    setColors(0x009BDC, 0x19BBFF, 0x0071A0);
                    break;
            }
        }
    }
}
