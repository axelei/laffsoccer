package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;

import static com.ygames.ysoccer.competitions.Competition.Category.DIY_COMPETITION;
import static com.ygames.ysoccer.competitions.Competition.Type.FRIENDLY;
import static com.ygames.ysoccer.framework.Assets.font10;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.framework.Font.Align.LEFT;
import static com.ygames.ysoccer.gui.Widget.widgetComparatorByText;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Team.ControlMode.COACH;
import static com.ygames.ysoccer.match.Team.ControlMode.COMPUTER;
import static com.ygames.ysoccer.match.Team.ControlMode.PLAYER;
import static com.ygames.ysoccer.match.Team.ControlMode.UNDEFINED;

class SelectFavourites extends GLScreen {

    private Widget titleButton;
    private Widget viewSelectedTeamsButton;
    private Widget playButton;

    SelectFavourites(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar();
        widgets.add(w);
        titleButton = w;

        ArrayList<Widget> teamButtons = new ArrayList<>();

        ArrayList<Team> teamList = new ArrayList<>();
        for (String teamPath : Assets.favourites) {
            FileHandle file = Assets.teamsRootFolder.child(teamPath);
            if (file.exists()) {
                Team team = Assets.json.fromJson(Team.class, file.readString("UTF-8"));
                team.path = Assets.getRelativeTeamPath(file);
                teamList.add(team);
            }
        }

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
            teamButtons.add(w);
            widgets.add(w);
        }

        if (teamButtons.size() > 0) {
            Collections.sort(teamButtons, widgetComparatorByText);
            Widget.arrange(game.gui.WIDTH, 392, 29, 20, teamButtons);
            setSelectedWidget(teamButtons.get(0));

            for (Widget teamButton : teamButtons) {
                w = new FavouriteFolderButton(teamButton);
                widgets.add(w);
            }
        }

        w = new PlayButton();
        widgets.add(w);
        playButton = w;

        // Breadcrumb
        ArrayList<Widget> breadcrumb = new ArrayList<>();

        w = new BreadCrumbRootButton();
        breadcrumb.add(w);

        w = new BreadCrumbFavouritesLabel();
        breadcrumb.add(w);

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
            String title = gettext((diff == 0) ? "CHANGE TEAMS FOR" : "CHOOSE TEAMS FOR") + " " + navigation.competition.name;
            setText(title, CENTER, font14);
        }
    }

    private class BreadCrumbRootButton extends Button {

        BreadCrumbRootButton() {
            setSize(0, 32);
            setColors(game.stateColor);
            setText("" + (char) 20, CENTER, font10);
            autoWidth();
        }

        @Override
        public void onFire1Down() {
            navigation.folder = Assets.teamsRootFolder;
            navigation.league = null;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class BreadCrumbFavouritesLabel extends Button {

        BreadCrumbFavouritesLabel() {
            setSize(0, 32);
            setColors(game.stateColor.darker());
            setActive(false);
            setText(gettext("FAVOURITES"), CENTER, font10);
            autoWidth();
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
            setText(gettext("CONTROL MODE.COMPUTER"), LEFT, font10);
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
            setText(gettext("CONTROL MODE.PLAYER-COACH"), LEFT, font10);
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
            setText(gettext("CONTROL MODE.COACH"), LEFT, font10);
            setActive(false);
        }
    }

    private class TeamButton extends Button {

        private Team team;
        FileHandle folder;

        TeamButton(Team team) {
            this.team = team;
            setSize(382, 28);
            updateColors();
            FileHandle teamFolder = Assets.teamsRootFolder.child(team.path);
            String mainFolder = Assets.getTeamFirstFolder(teamFolder).name();
            setText(team.name + ", " + mainFolder, CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            if (game.teamList.contains(team)) {
                switch (team.controlMode) {
                    case COMPUTER:
                        team.controlMode = PLAYER;
                        break;

                    case PLAYER:
                        team.controlMode = COACH;
                        break;

                    case COACH:
                        team.controlMode = UNDEFINED;
                        game.teamList.removeTeam(team);
                        break;
                }
            } else {
                team.controlMode = COMPUTER;
                game.teamList.addTeam(team);
            }
            updateColors();
            viewSelectedTeamsButton.setDirty(true);
            playButton.setDirty(true);
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

    private class FavouriteFolderButton extends Button {

        private TeamButton teamButton;

        FavouriteFolderButton(Widget teamButton) {
            this.teamButton = (TeamButton) teamButton;
            setGeometry(teamButton.x + teamButton.w, teamButton.y, 26, 28);
            setText("" + (char) 20, CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            navigation.folder = Assets.teamsRootFolder.child(teamButton.team.path).parent();
            navigation.league = teamButton.team.league;
            game.setScreen(new SelectTeams(game));
        }
    }

    private class ViewSelectedTeamsButton extends Button {

        ViewSelectedTeamsButton() {
            setGeometry((game.gui.WIDTH - 180) / 2 - 360 - 20, 660, 360, 36);
            setColors(0x9A6C9C, 0xBA99BB, 0x4F294F);
            setText(gettext("VIEW SELECTED TEAMS"), CENTER, font14);
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
            setText(gettext("ABORT"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    private class PlayButton extends Button {

        PlayButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 660, 360, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            int diff = navigation.competition.numberOfTeams - game.teamList.numberOfTeams();
            if (diff == 0) {
                switch (navigation.competition.type) {
                    case FRIENDLY:
                        setText(gettext("PLAY FRIENDLY"));
                        break;

                    case LEAGUE:
                        setText(gettext("PLAY LEAGUE"));
                        break;

                    case CUP:
                        setText(gettext("PLAY CUP"));
                        break;

                    case TOURNAMENT:
                        setText(gettext("PLAY TOURNAMENT"));
                        break;
                }
                setColors(0x138B21, 0x1BC12F, 0x004814);
                setActive(true);
            } else {
                if (diff > 1) {
                    setText(gettext("SELECT %n MORE TEAMS").replace("%n", "" + diff));
                } else if (diff == 1) {
                    setText(gettext("SELECT 1 MORE TEAM"));
                } else if (diff == -1) {
                    setText(gettext("SELECT 1 LESS TEAM"));
                } else {
                    setText(gettext("SELECT %n LESS TEAMS").replace("%n", "" + (-diff)));
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
                    if (homeTeam.controlMode != COMPUTER) {
                        if (lastFireInputDevice != null) {
                            homeTeam.setInputDevice(lastFireInputDevice);
                        }
                        navigation.team = homeTeam;
                        game.setScreen(new SetTeam(game));
                    } else if (awayTeam.controlMode != COMPUTER) {
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
}
