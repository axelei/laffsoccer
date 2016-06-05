package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

public class AllSelectedTeams extends GlScreen {

    private FileHandle fileHandle;
    private Widget playButton;
    private Widget changeTeamsButton;

    public AllSelectedTeams(GlGame game, FileHandle fileHandle) {
        super(game);
        this.fileHandle = fileHandle;

        background = game.stateBackground;

        Widget w;

        w = new TitleButton();
        widgets.add(w);

        w = new ComputerButton();
        widgets.add(w);

        w = new PlayerCoachButton();
        widgets.add(w);

        w = new CoachButton();
        widgets.add(w);

        List<Widget> list = new ArrayList<Widget>();
        for (Team team : game.teamList) {
            w = new TeamButton(team);
            list.add(w);
            widgets.add(w);
        }
        Widget.arrange(game.settings, 350, 32, list);

        w = new ChangeTeamsButton();
        widgets.add(w);
        changeTeamsButton = w;
        selectedWidget = w;

        w = new AbortButton();
        widgets.add(w);

        w = new PlayButton();
        widgets.add(w);
        playButton = w;
        int diff = game.competition.numberOfTeams - game.teamList.size();
        if (diff == 0) {
            selectedWidget = w;
        }
    }

    class TitleButton extends Button {
        public TitleButton() {
            String title = Assets.strings.get("ALL SELECTED TEAMS FOR")
                    + " " + game.competition.name.toUpperCase()
                    + " - " + fileHandle.name().toUpperCase();
            int w = Math.max(960, 80 + 16 * title.length());
            setGeometry((game.settings.GUI_WIDTH - w) / 2, 30, w, 40);
            setColors(game.stateColor);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class ComputerButton extends Button {
        public ComputerButton() {
            setGeometry((game.settings.GUI_WIDTH - 3 * 300) / 2 - 20, 86, 300, 30);
            setColors(0x981E1E, 0xC72929, 0x640000);
            setText(Assets.strings.get("COMPUTER"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PlayerCoachButton extends Button {
        public PlayerCoachButton() {
            setGeometry((game.settings.GUI_WIDTH - 300) / 2, 86, 300, 30);
            setColors(0x0000C8, 0x1919FF, 0x000078);
            setText(Assets.strings.get("PLAYER") + "-" + Assets.strings.get("COACH"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class CoachButton extends Button {
        public CoachButton() {
            setGeometry((game.settings.GUI_WIDTH + 300) / 2 + 20, 86, 300, 30);
            setColors(0x009BDC, 0x19BBFF, 0x0071A0);
            setText(Assets.strings.get("COACH"), Font.Align.CENTER, Assets.font14);
            setActive(false);
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
            playButton.setChanged(true);
            changeTeamsButton.setChanged(true);
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

    class ChangeTeamsButton extends Button {
        public ChangeTeamsButton() {
            setGeometry((game.settings.GUI_WIDTH - 180) / 2 - 360 - 20, 660, 360, 36);
            setColors(0x9A6C9C, 0xBA99BB, 0x4F294F);
        }

        @Override
        public void onUpdate() {
            int diff = game.competition.numberOfTeams - game.teamList.size();
            setText(Assets.strings.get((diff == 0) ? "CHANGE TEAMS" : "CHOOSE TEAMS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            FileHandle fileHandle = Gdx.files.local(game.competition.absolutePath);
            game.setScreen(new SelectFolder(game, fileHandle));
        }
    }

    class AbortButton extends Button {
        public AbortButton() {
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC8000E, 0xFF1929, 0x74040C);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.competition = null;
            game.setScreen(new Main(game));
        }
    }

    class PlayButton extends Button {
        public PlayButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 660, 360, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            int diff = game.competition.numberOfTeams - game.teamList.size();
            if (diff == 0) {
                switch (game.competition.type) {
                    case FRIENDLY:
                        setText(Assets.strings.get("PLAY FRIENDLY"));
                        break;
                    case LEAGUE:
                        setText(Assets.strings.get("PLAY LEAGUE"));
                        break;
                }
                setColors(0x138B21, 0x1BC12F, 0x004814);
                setActive(true);
            } else {
                if (diff > 1) {
                    setText(Assets.strings.get("SELECT %n MORE TEAMS").replace("%n", "" + diff));
                } else if (diff == 1) {
                    setText(Assets.strings.get("SELECT 1 MORE TEAM"));
                } else if (diff == -1) {
                    setText(Assets.strings.get("SELECT 1 LESS TEAM"));
                } else {
                    setText(Assets.strings.get("SELECT %n LESS TEAMS").replace("%n", "" + (-diff)));
                }
                setColors(0x000000, 0x000000, 0x000000);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            switch (game.competition.type) {
                case FRIENDLY:
                    // TODO
                    break;
                case LEAGUE:
                    game.competition.start(game.teamList);
                    game.setScreen(new PlayLeague(game));
                    break;
                case CUP:
                    // TODO
                    break;
            }
        }
    }
}
