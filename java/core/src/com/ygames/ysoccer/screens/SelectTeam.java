package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.framework.Assets.favourites;
import static com.ygames.ysoccer.framework.Assets.font10;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.font6;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Assets.saveFavourites;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.framework.Font.Align.LEFT;
import static com.ygames.ysoccer.framework.GLGame.State.EDIT;
import static com.ygames.ysoccer.match.Team.Type.CLUB;

class SelectTeam extends GLScreen {

    SelectTeam(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(getTitle(), game.stateColor.body);
        widgets.add(w);

        List<Widget> list = new ArrayList<>();

        ArrayList<String> leagues = new ArrayList<>();
        boolean singleLeague = false;
        FileHandle leaguesFile = navigation.folder.child("leagues.json");
        if (leaguesFile.exists()) {
            Collections.addAll(leagues, Assets.json.fromJson(String[].class, leaguesFile.readString("UTF-8")));
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
            if (list.size() > 0) {
                Widget.arrange(game.gui.WIDTH, 380, 34, 20, list);
                setSelectedWidget(list.get(0));
            }
        }

        // teams
        else {
            for (Team team : teamList) {
                w = new TeamButton(team);
                list.add(w);
                widgets.add(w);
            }
            if (list.size() > 0) {
                Collections.sort(list, Widget.widgetComparatorByText);
                Widget.arrange(game.gui.WIDTH, 380, 30, game.getState() == EDIT ? 40 : 20, list);
                setSelectedWidget(list.get(0));

                for (Widget teamButton : list) {
                    if (game.getState() == EDIT) {
                        w = new FavouriteToggleButton(teamButton);
                        widgets.add(w);
                        if (Settings.development && Settings.showTeamValues) {
                            w = new PriceLabel(teamButton);
                            widgets.add(w);
                        }
                    }
                }

            }
        }


        // Breadcrumb
        List<Widget> breadcrumb = new ArrayList<>();
        if (navigation.league != null) {
            w = new BreadCrumbLeagueLabel();
            breadcrumb.add(w);
        }
        FileHandle fh = navigation.folder;
        boolean isDataRoot;
        do {
            isDataRoot = fh.equals(Assets.teamsRootFolder);
            boolean disabled = (fh == navigation.folder) &&
                    (navigation.league == null || singleLeague);
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

        if (leagues.size() > 1 && game.getState() == EDIT) {
            w = new SearchTeamButton();
            widgets.add(w);
        }

        w = new AbortButton();
        widgets.add(w);
        if (getSelectedWidget() == null) {
            setSelectedWidget(w);
        }

        if (game.getState() == EDIT &&
                !navigation.folder.equals(Assets.teamsRootFolder)) {
            w = new SearchPlayerButton();
            widgets.add(w);
        }
    }

    private String getTitle() {
        String title = "";
        switch (game.getState()) {
            case EDIT:
                title = gettext("EDIT TEAMS");
                break;

            case TRAINING:
                title = gettext("TRAINING");
                break;
        }
        return title;
    }

    private class BreadCrumbLeagueLabel extends Button {

        BreadCrumbLeagueLabel() {
            setSize(0, 32);
            setColors(game.stateColor.darker());
            setActive(false);
            setText(navigation.league, CENTER, font10);
            autoWidth();
        }
    }

    private class BreadCrumbButton extends Button {

        private final FileHandle folder;

        BreadCrumbButton(FileHandle folder, boolean isDataRoot, boolean disabled) {
            this.folder = folder;
            setSize(0, 32);
            if (disabled) {
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
            if (folder == navigation.folder && navigation.league != null) {
                navigation.league = null;
                game.setScreen(new SelectTeam(game));
            } else {
                navigation.folder = folder;
                navigation.league = null;
                game.setScreen(new SelectFolder(game));
            }
        }
    }

    private class LeagueButton extends Button {

        LeagueButton(String name) {
            setSize(300, 32);
            setColor(0x1B4D85);
            setText(name, CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            navigation.league = text;
            game.setScreen(new SelectTeam(game));
        }
    }

    private class TeamButton extends Button {

        private final Team team;

        TeamButton(Team team) {
            this.team = team;
            setSize(270, 28);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(team.name, CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case EDIT:
                    game.setScreen(new EditPlayers(game, team, false));
                    break;

                case TRAINING:
                    navigation.team = team;
                    game.setScreen(new SetupTraining(game));
                    break;
            }
        }
    }

    private class FavouriteToggleButton extends Button {

        private final String teamPath;
        private boolean isFavourite;

        FavouriteToggleButton(Widget teamButton) {
            teamPath = ((TeamButton) teamButton).team.path;
            setGeometry(teamButton.x + teamButton.w, teamButton.y, 26, 28);
            setText("", CENTER, font14);
            isFavourite = favourites.contains(teamPath);
        }

        @Override
        public void refresh() {
            setText(isFavourite ? "" + (char) 22 : "" + (char) 23);
        }

        @Override
        public void onFire1Down() {
            if (favourites.contains(teamPath)) {
                favourites.remove(teamPath);
                isFavourite = false;
            } else {
                favourites.add(teamPath);
                isFavourite = true;
            }
            saveFavourites();
            setDirty(true);
        }
    }

    private class PriceLabel extends Label {

        PriceLabel(Widget teamButton) {
            Team team = ((TeamButton) teamButton).team;
            int v = 0;
            for (int i = 0; i < 11; i++) {
                v += team.playerAtPosition(i).getValue();
            }
            setGeometry(teamButton.x, teamButton.y, 60, 19);
            setText("" + v, LEFT, font6);
        }
    }

    private class SearchTeamButton extends Button {

        SearchTeamButton() {
            setColor(0x4444AA);
            setText(gettext("SEARCH.SEARCH TEAMS"), CENTER, font14);
            setGeometry((game.gui.WIDTH - 180) / 2 - 360 - 20, 660, 360, 36);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SearchTeams(game));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColor(0xC8000E);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(gettext("ABORT"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    private class SearchPlayerButton extends Button {

        SearchPlayerButton() {
            setColor(0x4444AA);
            setText(gettext("SEARCH.SEARCH PLAYERS"), CENTER, font14);
            setGeometry((game.gui.WIDTH + 180) / 2 + 20, 660, 360, 36);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SearchPlayers(game));
        }
    }
}