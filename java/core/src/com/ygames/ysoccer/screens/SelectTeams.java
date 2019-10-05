package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Team.Type.CLUB;

class SelectTeams extends GLScreen {

    private Widget titleButton;
    private Widget viewSelectedTeamsButton;
    private Widget playButton;
    private Widget calendarButton;

    SelectTeams(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar();
        widgets.add(w);
        titleButton = w;

        List<Widget> list = new ArrayList<>();

        List<String> leagues = new ArrayList<>();
        boolean singleLeague = false;
        FileHandle leaguesFile = navigation.folder.child("leagues.json");
        if (leaguesFile.exists()) {
            leagues = Assets.json.fromJson(ArrayList.class, String.class, leaguesFile.readString("UTF-8"));
            if (leagues.size() == 1) {
                singleLeague = true;
            }
            if (navigation.league == null) {
                if (singleLeague) {
                    navigation.league = leagues.get(0);
                }
            } else {
                leagues.clear();
            }
        }

        ArrayList<Team> teamList = new ArrayList<>();
        FileHandle[] teamFileHandles = navigation.folder.list(Assets.teamFilenameFilter);
        for (FileHandle teamFileHandle : teamFileHandles) {
            Team team = Assets.json.fromJson(Team.class, teamFileHandle.readString("UTF-8"));
            team.path = Assets.getRelativeTeamPath(teamFileHandle);
            if ((navigation.league == null) || ((team.type == CLUB) && team.league.equals(navigation.league))) {
                teamList.add(team);
                if (team.type == CLUB && !leagues.contains(team.league)) {
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
            Widget.arrange(game.gui.WIDTH, 392, 34, 20, list);
            setSelectedWidget(list.get(0));
        }

        // teams
        else {
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
                Collections.sort(list, Widget.widgetComparatorByText);
                Widget.arrange(game.gui.WIDTH, 392, 29, 20, list);
                setSelectedWidget(list.get(0));
            }

            w = new CalendarButton();
            widgets.add(w);
            calendarButton = w;
        }

        // Breadcrumb
        List<Widget> breadcrumb = new ArrayList<Widget>();
        if (navigation.league != null) {
            w = new BreadCrumbLeagueLabel();
            breadcrumb.add(w);
        }
        FileHandle fh = navigation.folder;
        boolean isDataRoot;
        do {
            isDataRoot = fh.equals(Assets.teamsRootFolder);
            boolean disabled = (fh == navigation.folder) && (navigation.league == null || singleLeague);
            w = new BreadCrumbButton(fh, isDataRoot, disabled);
            breadcrumb.add(w);
            fh = fh.parent();
        } while (!isDataRoot);

        Collections.reverse(breadcrumb);
        int x = (game.gui.WIDTH - 960) / 2;
        for (Widget b : breadcrumb) {
            b.setPosition(x, 72);
            x += b.w + 2;
        }
        widgets.addAll(breadcrumb);

        w = new ViewSelectedTeamsButton();
        widgets.add(w);
        viewSelectedTeamsButton = w;

        w = new AbortButton();
        widgets.add(w);
        if (getSelectedWidget() == null) {
            setSelectedWidget(w);
        }

        w = new PlayButton();
        widgets.add(w);
        playButton = w;
    }

    private class TitleBar extends Button {

        TitleBar() {
            setColors(game.stateColor);
            setActive(false);
        }

        @Override
        public void refresh() {
            setGeometry((game.gui.WIDTH - 960) / 2, 30, 960, 40);
            setColors(game.stateColor);
            int diff = navigation.competition.numberOfTeams - game.teamList.numberOfTeams();
            String title = Assets.strings.get((diff == 0) ? "CHANGE TEAMS FOR" : "CHOOSE TEAMS FOR") + " " + navigation.competition.name;
            setText(title, Font.Align.CENTER, Assets.font14);
        }
    }

    private class BreadCrumbLeagueLabel extends Button {

        BreadCrumbLeagueLabel() {
            setSize(0, 32);
            setColors(game.stateColor.darker());
            setActive(false);
            setText(navigation.league, Font.Align.CENTER, Assets.font10);
            autoWidth();
        }
    }

    private class BreadCrumbButton extends Button {

        private FileHandle fh;

        BreadCrumbButton(FileHandle folder, boolean isDataRoot, boolean disabled) {
            this.fh = folder;
            setSize(0, 32);
            if (disabled) {
                setColors(game.stateColor.darker());
                setActive(false);
            } else {
                setColors(game.stateColor);
            }
            setText(isDataRoot ? "" + (char) 20 : fh.name().replace('_', ' '), Font.Align.CENTER, Assets.font10);
            autoWidth();
        }

        @Override
        public void onFire1Down() {
            if (fh == navigation.folder && navigation.league != null) {
                navigation.folder = fh;
                navigation.league = null;
                game.setScreen(new SelectTeams(game));
            } else {
                navigation.folder = fh;
                navigation.league = null;
                game.setScreen(new SelectFolder(game));
            }
        }
    }

    private class LeagueButton extends Button {

        LeagueButton(String name) {
            setSize(300, 32);
            setColor(0x1B4D85);
            setText(name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            navigation.league = text;
            game.setScreen(new SelectTeams(game));
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
                        game.teamList.removeTeam(team);
                        break;
                }
            } else {
                team.controlMode = Team.ControlMode.COMPUTER;
                game.teamList.addTeam(team);
            }
            updateColors();
            viewSelectedTeamsButton.setDirty(true);
            playButton.setDirty(true);
            calendarButton.setDirty(true);
            titleButton.setDirty(true);
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
            game.setScreen(new AllSelectedTeams(game));
        }

        @Override
        public void refresh() {
            setVisible(game.teamList.numberOfTeams() > 0);
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColor(0xC8000E);
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
            setVisible(game.teamList.numberOfTeams() > 0);
            int diff = navigation.competition.numberOfTeams - game.teamList.numberOfTeams();
            if (diff == 0) {
                switch (navigation.competition.type) {
                    case FRIENDLY:
                        setText(Assets.strings.get("PLAY FRIENDLY"));
                        break;

                    case LEAGUE:
                        setText(Assets.strings.get("PLAY LEAGUE"));
                        break;

                    case CUP:
                        setText(Assets.strings.get("PLAY CUP"));
                        break;

                    case TOURNAMENT:
                        setText(Assets.strings.get("PLAY TOURNAMENT"));
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
            game.teamList.removeNullValues();
            switch (navigation.competition.type) {
                case FRIENDLY:
                    Team homeTeam = game.teamList.get(HOME);
                    Team awayTeam = game.teamList.get(AWAY);

                    Match match = navigation.competition.getMatch();
                    match.setTeam(HOME, homeTeam);
                    match.setTeam(AWAY, awayTeam);

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
                        navigation.team = homeTeam;
                        game.setScreen(new SetTeam(game));
                    } else if (awayTeam.controlMode != Team.ControlMode.COMPUTER) {
                        if (lastFireInputDevice != null) {
                            awayTeam.setInputDevice(lastFireInputDevice);
                        }
                        navigation.team = awayTeam;
                        game.setScreen(new SetTeam(game));
                    } else {
                        game.setScreen(new MatchSetup(game));
                    }
                    break;

                case LEAGUE:
                    navigation.competition.start(game.teamList);
                    game.setCompetition(navigation.competition);
                    game.setScreen(new PlayLeague(game));
                    break;

                case CUP:
                    navigation.competition.start(game.teamList);
                    game.setCompetition(navigation.competition);
                    game.setScreen(new PlayCup(game));
                    break;

                case TOURNAMENT:
                    navigation.competition.start(game.teamList);
                    game.setCompetition(navigation.competition);
                    game.setScreen(new PlayTournament(game));
                    break;
            }
        }
    }

    private class CalendarButton extends Button {

        CalendarButton() {
            setGeometry(game.gui.WIDTH / 2 + 490, 660, 40, 36);
            setText("" + (char) 21, Font.Align.CENTER, Assets.font14);
            setColor(0x138B21);
        }

        @Override
        public void refresh() {
            setVisible(Settings.development
                    && navigation.competition.type != Competition.Type.FRIENDLY
                    && navigation.competition.category == Competition.Category.DIY_COMPETITION
                    && navigation.competition.numberOfTeams == game.teamList.numberOfTeams());
        }

        @Override
        public void onFire1Down() {
            game.teamList.removeNullValues();
            switch (navigation.competition.type) {
                case CUP:
                    game.setScreen(new DiyCupCalendar(game, (Cup) navigation.competition));
                    break;
                case LEAGUE:
                    game.setScreen(new DiyLeagueCalendar(game, (League) navigation.competition));
                    break;
                case TOURNAMENT:
                    game.setScreen(new DiyTournamentCalendar(game, (Tournament) navigation.competition));
            }
        }
    }
}
