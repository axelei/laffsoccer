package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

public class SelectTeam extends GlScreen {

    private FileHandle fileHandle;
    private League league;

    public SelectTeam(GlGame game, FileHandle fileHandle, League league) {
        super(game);
        this.fileHandle = fileHandle;
        this.league = league;

        background = game.stateBackground;

        Widget w;

        w = new TitleButton();
        widgets.add(w);

        List<Widget> list = new ArrayList<Widget>();
        for (Team teamStub : league.teams) {
            if (game.teamList.contains(teamStub)) {
                w = new TeamButton(game.teamList.get(game.teamList.indexOf(teamStub)));
                list.add(w);
                widgets.add(w);
            } else {
                FileHandle teamFile = Assets.teamsFolder.child(teamStub.path);
                if (teamFile.exists()) {
                    Team team = Assets.json.fromJson(Team.class, teamFile.readString());
                    team.path = teamStub.path;
                    w = new TeamButton(team);
                    list.add(w);
                    widgets.add(w);
                }
            }
        }
        if (list.size() > 0) {
            Widget.arrange(game.settings, 350, 32, list);
            selectedWidget = list.get(0);
        }

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            selectedWidget = w;
        }
    }

    class TitleButton extends Button {

        public TitleButton() {
            String title = "";
            switch (game.getState()) {
                case EDIT:
                    title = Assets.strings.get("EDIT TEAMS") + " - " + league.name;
                    break;
                case TRAINING:
                    // TODO
                    break;
            }
            int w = Math.max(960, 80 + 16 * title.length());
            setGeometry((game.settings.GUI_WIDTH - w) / 2, 30, w, 40);
            setColors(game.stateColor);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TeamButton extends Button {

        Team team;

        public TeamButton(Team team) {
            this.team = team;
            setSize(270, 30);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(team.name.toUpperCase(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case EDIT:
                    // TODO set EditPlayers screen
                    break;
                case TRAINING:
                    // TODO
                    break;
            }
        }
    }

    class ExitButton extends Button {
        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectFolder(game, fileHandle, null));
        }
    }
}
