package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.ygames.ysoccer.match.Team.Type.CLUB;

class SelectTeams extends GLScreen {

    private FileHandle fileHandle;
    private String league;

    private Widget titleButton;
    private Widget viewSelectedTeamsButton;
    private Widget playButton;
    private Competition competition;

    SelectTeams(GLGame game, FileHandle fileHandle, String league, Competition competition) {
        super(game);
        this.fileHandle = fileHandle;
        this.league = league;
        this.competition = competition;

        background = game.stateBackground;

        Widget w;

        w = new TitleButton();
        widgets.add(w);
        titleButton = w;

        List<Widget> list = new ArrayList<Widget>();

        HashSet<String> leagues = new HashSet<String>();

        List<Team> teamList = new ArrayList<Team>();
        FileHandle[] teamFileHandles = fileHandle.list(Assets.teamFilenameFilter);
        for (FileHandle teamFileHandle : teamFileHandles) {
            Team team = Assets.json.fromJson(Team.class, teamFileHandle.readString("UTF-8"));
            team.path = Assets.getRelativeTeamPath(teamFileHandle);
            if ((league == null) || ((team.type == CLUB) && team.league.equals(league))) {
                teamList.add(team);
                if (team.type == CLUB) {
                    leagues.add(team.league);
                }
            }
        }

        // leagues
        if (leagues.size() > 1) {
            for (String name : leagues) {
                LeagueButton leagueButton = new LeagueButton(name);
                list.add(leagueButton);
                widgets.add(leagueButton);
            }
            Collections.sort(list, new Widget.CompareByText());
            Widget.arrange(game.gui.WIDTH, 380, 30, list);
            setSelectedWidget(list.get(0));
        }

        // teams
        else {
            w = new ComputerButton();
            widgets.add(w);

            w = new PlayerCoachButton();
            widgets.add(w);

            w = new CoachButton();
            widgets.add(w);

            for (Team team : teamList) {
                if (game.teamList.contains(team)) {
                    w = new TeamButton(game.teamList.get(game.teamList.indexOf(team)));
                } else {
                    w = new TeamButton(team);
                }
                list.add(w);
                widgets.add(w);
            }

            if (list.size() > 0) {
                Collections.sort(list, new Widget.CompareByText());
                Widget.arrange(game.gui.WIDTH, 380, 29, list);
                setSelectedWidget(list.get(0));
            }

            w = new ViewSelectedTeamsButton();
            widgets.add(w);
            viewSelectedTeamsButton = w;

            w = new PlayButton();
            widgets.add(w);
            playButton = w;
        }

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            setSelectedWidget(w);
        }
    }

    class TitleButton extends Button {

        public TitleButton() {
            setColors(game.stateColor);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            int diff = competition.numberOfTeams - game.teamList.size();
            String title = Assets.strings.get((diff == 0) ? "CHANGE TEAMS FOR" : "CHOOSE TEAMS FOR");
            title += " " + competition.name
                    + " - " + fileHandle.name();
            int w = Math.max(960, 80 + 16 * title.length());
            setGeometry((game.gui.WIDTH - w) / 2, 30, w, 40);
            setColors(game.stateColor);
            setText(title, Font.Align.CENTER, Assets.font14);
        }
    }

    private class LeagueButton extends Button {

        LeagueButton(String name) {
            setSize(300, 28);
            setColors(0x1B4D85);
            setText(name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectTeams(game, fileHandle, text, competition));
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
            viewSelectedTeamsButton.setChanged(true);
            playButton.setChanged(true);
            titleButton.setChanged(true);
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

    private class ViewSelectedTeamsButton extends Button {

        ViewSelectedTeamsButton() {
            setGeometry((game.gui.WIDTH - 180) / 2 - 360 - 20, 660, 360, 36);
            setColors(0x9A6C9C, 0xBA99BB, 0x4F294F);
            setText(Assets.strings.get("VIEW SELECTED TEAMS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new AllSelectedTeams(game, fileHandle, competition));
        }

        @Override
        public void onUpdate() {
            setVisible(game.teamList.size() > 0);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            if (league == null) {
                game.setScreen(new SelectFolder(game, fileHandle.parent(), competition));
            } else {
                game.setScreen(new SelectTeams(game, fileHandle, null, competition));
            }
        }
    }

    private class PlayButton extends Button {

        PlayButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 660, 360, 36);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
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
                    // choose the menu to set
                    game.inputDevices.setAvailability(true);
                    game.teamList.get(Match.HOME).setInputDevice(null);
                    game.teamList.get(Match.HOME).releaseNonAiInputDevices();
                    game.teamList.get(Match.AWAY).setInputDevice(null);
                    game.teamList.get(Match.AWAY).releaseNonAiInputDevices();
                    if (game.teamList.get(Match.HOME).controlMode != Team.ControlMode.COMPUTER) {
                        game.setScreen(new SetTeam(game, fileHandle, league, competition, game.teamList.get(Match.HOME), game.teamList.get(Match.AWAY), Match.HOME));
                    } else if (game.teamList.get(Match.AWAY).controlMode != Team.ControlMode.COMPUTER) {
                        game.setScreen(new SetTeam(game, fileHandle, league, competition, game.teamList.get(Match.HOME), game.teamList.get(Match.AWAY), Match.AWAY));
                    } else {
                        game.setScreen(new MatchSetup(game, fileHandle, league, competition, game.teamList.get(Match.HOME), game.teamList.get(Match.AWAY)));
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
