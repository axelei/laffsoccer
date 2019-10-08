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
import java.util.Arrays;
import java.util.Collections;

import static com.ygames.ysoccer.framework.Assets.fileComparatorByName;
import static com.ygames.ysoccer.framework.Assets.font10;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class SelectFolder extends GLScreen {

    SelectFolder(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(getTitle(), game.stateColor.body);
        widgets.add(w);

        // Breadcrumb
        ArrayList<Widget> breadcrumb = new ArrayList<>();

        FileHandle fh = navigation.folder;
        boolean isDataRoot;
        do {
            isDataRoot = fh.equals(Assets.teamsRootFolder);
            w = new BreadCrumbButton(fh, isDataRoot);
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

        // Folders buttons
        ArrayList<Widget> list = new ArrayList<>();

        ArrayList<FileHandle> files = new ArrayList<>(Arrays.asList(navigation.folder.list()));
        Collections.sort(files, fileComparatorByName);
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                list.add(w);
                widgets.add(w);
            }
        }

        if (navigation.folder.equals(Assets.teamsRootFolder) && Assets.favourites.size() > 0) {
            w = new FavouritesButton();
            list.add(w);
            widgets.add(w);
        }

        if (list.size() > 0) {
            Widget.arrange(game.gui.WIDTH, 380, 34, 20, list);
            setSelectedWidget(list.get(0));
        }

        switch (game.getState()) {
            case EDIT:
            case TRAINING:
                w = new SearchTeamButton();
                widgets.add(w);
                break;

            case FRIENDLY:
            case COMPETITION:
                w = new ViewSelectedTeamsButton();
                widgets.add(w);
                break;
        }


        w = new AbortButton();
        widgets.add(w);
        if (getSelectedWidget() == null) {
            setSelectedWidget(w);
        }

        switch (game.getState()) {
            case EDIT:
                w = new SearchPlayerButton();
                widgets.add(w);
                break;

            case FRIENDLY:
            case COMPETITION:
                w = new PlayButton();
                widgets.add(w);
                break;
        }
    }

    private String getTitle() {
        String title = "";
        switch (game.getState()) {
            case COMPETITION:
            case FRIENDLY:
                int diff = navigation.competition.numberOfTeams - game.teamList.numberOfTeams();
                title = gettext((diff == 0) ? "CHANGE TEAMS FOR" : "CHOOSE TEAMS FOR")
                        + " " + navigation.competition.name;
                break;

            case EDIT:
                title = gettext("EDIT TEAMS");
                break;

            case TRAINING:
                title = gettext("TRAINING");
                break;
        }
        return title;
    }

    private class BreadCrumbButton extends Button {

        private FileHandle folder;

        BreadCrumbButton(FileHandle folder, boolean isDataRoot) {
            this.folder = folder;
            setSize(0, 32);
            if (folder == navigation.folder) {
                setColors(game.stateColor.darker());
                setActive(false);
            } else {
                setColors(game.stateColor);
            }
            setText(isDataRoot ? "" + (char) 20 : folder.name().replace('_', ' '), CENTER, font10);
            autoWidth();
        }

        @Override
        public void onFire1Down() {
            navigation.folder = folder;
            navigation.league = null;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class FolderButton extends Button {

        private FileHandle fileHandle;

        FolderButton(FileHandle fileHandle) {
            this.fileHandle = fileHandle;
            setSize(340, 32);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(fileHandle.name().replace('_', ' '), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            FileHandle[] teamFileHandles = fileHandle.list(Assets.teamFilenameFilter);
            if (teamFileHandles.length > 0) {
                switch (game.getState()) {
                    case COMPETITION:
                    case FRIENDLY:
                        navigation.folder = fileHandle;
                        navigation.league = null;
                        game.setScreen(new SelectTeams(game));
                        break;

                    case EDIT:
                    case TRAINING:
                        navigation.folder = fileHandle;
                        navigation.league = null;
                        game.setScreen(new SelectTeam(game));
                        break;
                }
            } else {
                navigation.folder = fileHandle;
                game.setScreen(new SelectFolder(game));
            }
        }
    }

    private class FavouritesButton extends Button {

        FavouritesButton() {
            setSize(340, 32);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(gettext("FAVOURITES"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case COMPETITION:
                case FRIENDLY:
                    navigation.folder = Assets.favouritesFile;
                    game.setScreen(new SelectFavourites(game));
                    break;

                case EDIT:
                case TRAINING:
                    navigation.folder = Assets.favouritesFile;
                    game.setScreen(new SelectFavourite(game));
                    break;
            }
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

    private class SearchTeamButton extends Button {

        SearchTeamButton() {
            setColor(0x4444AA);
            setText(gettext("SEARCH.SEARCH TEAMS"), CENTER, font14);
            setGeometry((game.gui.WIDTH - 180) / 2 - 360 - 20, 660, 360, 36);
        }

        @Override
        public void refresh() {
            setVisible(!navigation.folder.equals(Assets.teamsRootFolder));
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SearchTeams(game));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColor(0xC8000E);
            setText(gettext("ABORT"), CENTER, font14);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
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
            setVisible(game.teamList.numberOfTeams() > 0);
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

    private class SearchPlayerButton extends Button {

        SearchPlayerButton() {
            setColor(0x4444AA);
            setText(gettext("SEARCH.SEARCH PLAYERS"), CENTER, font14);
            setGeometry((game.gui.WIDTH + 180) / 2 + 20, 660, 360, 36);
        }

        @Override
        public void refresh() {
            setVisible(!navigation.folder.equals(Assets.teamsRootFolder));
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SearchPlayers(game));
        }
    }
}
