package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

class AllSelectedTeams extends GLScreen {

    private Widget playButton;
    private Widget changeTeamsButton;

    AllSelectedTeams(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(gettext("ALL SELECTED TEAMS FOR") + " " + navigation.competition.name, game.stateColor.body);
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

        List<Widget> list = new ArrayList<>();
        for (Team team : game.teamList) {
            if (team == null) continue;
            w = new TeamButton(team);
            list.add(w);
            widgets.add(w);
        }
        Collections.sort(list, widgetComparatorByText);
        Widget.arrange(game.gui.WIDTH, 392, 29, 20, list);

        w = new ChangeTeamsButton();
        widgets.add(w);
        changeTeamsButton = w;
        setSelectedWidget(w);

        w = new AbortButton();
        widgets.add(w);

        w = new PlayButton();
        widgets.add(w);
        playButton = w;
        int diff = navigation.competition.numberOfTeams - game.teamList.numberOfTeams();
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

        TeamButton(Team team) {
            this.team = team;
            setSize(300, 28);
            updateColors();
            setText(team.name, CENTER, font14);
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
            int diff = navigation.competition.numberOfTeams - game.teamList.numberOfTeams();
            setText(gettext((diff == 0) ? "CHANGE TEAMS" : "CHOOSE TEAMS"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            if (navigation.folder.equals(Assets.favouritesFile)) {
                game.setScreen(new SelectFavourites(game));
            } else {
                FileHandle[] teamFileHandles = navigation.folder.list(Assets.teamFilenameFilter);
                if (teamFileHandles.length > 0) {
                    game.setScreen(new SelectTeams(game));
                } else {
                    game.setScreen(new SelectFolder(game));
                }
            }
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
