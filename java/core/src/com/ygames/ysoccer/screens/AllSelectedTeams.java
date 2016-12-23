package com.ygames.ysoccer.screens;

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
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class AllSelectedTeams extends GLScreen {

    private FileHandle currentFolder;
    private String league;
    private Competition competition;

    private Widget playButton;
    private Widget changeTeamsButton;

    AllSelectedTeams(GLGame game, FileHandle folder, String league, Competition competition) {
        super(game);
        this.currentFolder = folder;
        this.league = league;
        this.competition = competition;

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(Assets.strings.get("ALL SELECTED TEAMS FOR") + " " + competition.name, game.stateColor.body);
        widgets.add(w);

        w = new ComputerButton();
        widgets.add(w);

        w = new ComputerLabel();
        widgets.add(w);

        w = new PlayerCoachButton();
        widgets.add(w);

        w = new PlayerCoachLabel();
        widgets.add(w);

        w = new CoachButton();
        widgets.add(w);

        w = new CoachLabel();
        widgets.add(w);

        List<Widget> list = new ArrayList<Widget>();
        for (Team team : game.teamList) {
            w = new TeamButton(team);
            list.add(w);
            widgets.add(w);
        }
        Collections.sort(list, Widget.widgetComparatorByText);
        Widget.arrange(game.gui.WIDTH, 392, 29, list);

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

    private class ComputerButton extends Button {

        ComputerButton() {
            setGeometry((game.gui.WIDTH - 3 * 260) / 2 - 20, 112, 60, 26);
            setColors(0x981E1E, 0xC72929, 0x640000);
            setActive(false);
        }
    }

    private class ComputerLabel extends Button {

        ComputerLabel() {
            setGeometry((game.gui.WIDTH - 3 * 260) / 2 - 20 + 80, 112, 180, 26);
            setText(Assets.strings.get("CONTROL MODE.COMPUTER"), Font.Align.LEFT, Assets.font10);
            setActive(false);
        }
    }

    private class PlayerCoachButton extends Button {

        PlayerCoachButton() {
            setGeometry((game.gui.WIDTH - 260) / 2, 112, 60, 26);
            setColors(0x0000C8, 0x1919FF, 0x000078);
            setActive(false);
        }
    }

    private class PlayerCoachLabel extends Button {

        PlayerCoachLabel() {
            setGeometry((game.gui.WIDTH - 260) / 2 + 80, 112, 180, 26);
            setText(Assets.strings.get("CONTROL MODE.PLAYER-COACH"), Font.Align.LEFT, Assets.font10);
            setActive(false);
        }
    }

    private class CoachButton extends Button {

        CoachButton() {
            setGeometry((game.gui.WIDTH + 260) / 2 + 20, 112, 60, 26);
            setColors(0x009BDC, 0x19BBFF, 0x0071A0);
            setActive(false);
        }
    }

    private class CoachLabel extends Button {

        CoachLabel() {
            setGeometry((game.gui.WIDTH + 260) / 2 + 20 + 80, 112, 180, 26);
            setText(Assets.strings.get("CONTROL MODE.COACH"), Font.Align.LEFT, Assets.font10);
            setActive(false);
        }
    }

    private class TeamButton extends Button {

        private Team team;

        TeamButton(Team team) {
            this.team = team;
            setSize(300, 28);
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
            FileHandle[] teamFileHandles = currentFolder.list(Assets.teamFilenameFilter);
            if (teamFileHandles.length > 0) {
                game.setScreen(new SelectTeams(game, currentFolder, league, competition));
            } else {
                game.setScreen(new SelectFolder(game, competition));
            }
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC8000E);
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
                switch (competition.type) {
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
                setColors(null);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            switch (competition.type) {
                case FRIENDLY:
                    Team homeTeam = game.teamList.get(HOME);
                    Team awayTeam = game.teamList.get(AWAY);

                    // reset input devices
                    game.inputDevices.setAvailability(true);
                    homeTeam.setInputDevice(null);
                    homeTeam.releaseNonAiInputDevices();
                    awayTeam.setInputDevice(null);
                    awayTeam.releaseNonAiInputDevices();

                    // choose the menu to set
                    if (homeTeam.controlMode != Team.ControlMode.COMPUTER) {
                        if (lastFireInputDevice != null) {
                            homeTeam.setInputDevice(lastFireInputDevice);
                        }
                        game.setScreen(new SetTeam(game, currentFolder, league, competition, homeTeam, awayTeam, HOME));
                    } else if (awayTeam.controlMode != Team.ControlMode.COMPUTER) {
                        if (lastFireInputDevice != null) {
                            awayTeam.setInputDevice(lastFireInputDevice);
                        }
                        game.setScreen(new SetTeam(game, currentFolder, league, competition, homeTeam, awayTeam, AWAY));
                    } else {
                        game.setScreen(new MatchSetup(game, currentFolder, league, competition, homeTeam, awayTeam));
                    }
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
