package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

class AllSelectedTeams extends GLScreen {

    private FileHandle fileHandle;
    private Widget playButton;
    private Widget changeTeamsButton;
    private Competition competition;

    AllSelectedTeams(GLGame game, FileHandle fileHandle, Competition competition) {
        super(game);
        this.fileHandle = fileHandle;
        this.competition = competition;

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
        Widget.arrange(game.gui.WIDTH, 350, 32, list);

        w = new ChangeTeamsButton();
        widgets.add(w);
        changeTeamsButton = w;
        setSelectedWidget(w);

        w = new AbortButton();
        widgets.add(w);

        w = new PlayButton();
        widgets.add(w);
        playButton = w;
        int diff = competition.numberOfTeams - game.teamList.size();
        if (diff == 0) {
            setSelectedWidget(w);
        }
    }

    class TitleButton extends Button {

        public TitleButton() {
            String title = Assets.strings.get("ALL SELECTED TEAMS FOR")
                    + " " + competition.name
                    + " - " + fileHandle.name();
            int w = Math.max(960, 80 + 16 * title.length());
            setGeometry((game.gui.WIDTH - w) / 2, 30, w, 40);
            setColors(game.stateColor);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ComputerButton extends Button {

        ComputerButton() {
            setGeometry((game.gui.WIDTH - 3 * 300) / 2 - 20, 86, 300, 30);
            setColors(0x981E1E, 0xC72929, 0x640000);
            setText(Assets.strings.get("CONTROL MODE.COMPUTER"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PlayerCoachButton extends Button {

        PlayerCoachButton() {
            setGeometry((game.gui.WIDTH - 300) / 2, 86, 300, 30);
            setColors(0x0000C8, 0x1919FF, 0x000078);
            setText(Assets.strings.get("CONTROL MODE.PLAYER-COACH"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class CoachButton extends Button {

        CoachButton() {
            setGeometry((game.gui.WIDTH + 300) / 2 + 20, 86, 300, 30);
            setColors(0x009BDC, 0x19BBFF, 0x0071A0);
            setText(Assets.strings.get("CONTROL MODE.COACH"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TeamButton extends Button {

        Team team;

        TeamButton(Team team) {
            this.team = team;
            setSize(270, 30);
            updateColors();
            setText(team.name, Font.Align.CENTER, Assets.font14);
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
            playButton.setDirty(true);
            changeTeamsButton.setDirty(true);
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

    private class ChangeTeamsButton extends Button {

        ChangeTeamsButton() {
            setGeometry((game.gui.WIDTH - 180) / 2 - 360 - 20, 660, 360, 36);
            setColors(0x9A6C9C, 0xBA99BB, 0x4F294F);
        }

        @Override
        public void refresh() {
            int diff = competition.numberOfTeams - game.teamList.size();
            setText(Assets.strings.get((diff == 0) ? "CHANGE TEAMS" : "CHOOSE TEAMS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            FileHandle fileHandle = Gdx.files.local(competition.absolutePath);
            FileHandle[] teamFileHandles = fileHandle.list(Assets.teamFilenameFilter);
            if (teamFileHandles.length > 0) {
                game.setScreen(new SelectTeams(game, fileHandle, null, competition));
            } else {
                game.setScreen(new SelectFolder(game, fileHandle, competition));
            }
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC8000E, 0xFF1929, 0x74040C);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    private class PlayButton extends Button {

        PlayButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 660, 360, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            int diff = competition.numberOfTeams - game.teamList.size();
            if (diff == 0) {
                switch (competition.getType()) {
                    case FRIENDLY:
                        setText(Assets.strings.get("PLAY FRIENDLY"));
                        break;

                    case LEAGUE:
                        setText(Assets.strings.get("PLAY LEAGUE"));
                        break;

                    case CUP:
                        setText(Assets.strings.get("PLAY CUP"));
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
            switch (competition.getType()) {
                case FRIENDLY:
                    // TODO
                    break;

                case LEAGUE:
                    competition.start(game.teamList);
                    game.setCompetition(competition);
                    game.setScreen(new PlayLeague(game));
                    break;

                case CUP:
                    competition.start(game.teamList);
                    game.setCompetition(competition);
                    game.setScreen(new PlayCup(game));
                    break;
            }
        }
    }
}
