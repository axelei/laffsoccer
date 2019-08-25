package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;

import static com.ygames.ysoccer.framework.Assets.favourites;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Assets.saveFavourites;
import static com.ygames.ysoccer.framework.GLGame.State.EDIT;

class SelectFavourite extends GLScreen {

    SelectFavourite(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(getTitle(), game.stateColor.body);
        widgets.add(w);

        ArrayList<Widget> teamButtons = new ArrayList<>();

        for (String teamPath : favourites) {
            FileHandle file = Assets.teamsRootFolder.child(teamPath);
            if (file.exists()) {
                Team team = Assets.json.fromJson(Team.class, file.readString("UTF-8"));
                team.path = Assets.getRelativeTeamPath(file);
                w = new TeamButton(team, file.parent());
                teamButtons.add(w);
                widgets.add(w);
            }
        }

        if (teamButtons.size() > 0) {
            Collections.sort(teamButtons, Widget.widgetComparatorByText);
            Widget.arrange(game.gui.WIDTH, 380, 30, game.getState() == EDIT ? 40 : 20, teamButtons);
            setSelectedWidget(teamButtons.get(0));

            if (game.getState() == EDIT) {
                for (Widget teamButton : teamButtons) {
                    w = new FavouriteToggleButton(teamButton);
                    widgets.add(w);
                }
            }
        }

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

        w = new AbortButton();
        widgets.add(w);
        if (selectedWidget == null) {
            setSelectedWidget(w);
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

    private class BreadCrumbRootButton extends Button {

        BreadCrumbRootButton() {
            setSize(0, 32);
            setColors(game.stateColor);
            setText("" + (char) 20, Font.Align.CENTER, Assets.font10);
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
            setText(gettext("FAVOURITES"), Font.Align.CENTER, Assets.font10);
            autoWidth();
        }
    }

    private class TeamButton extends Button {

        private Team team;
        FileHandle folder;

        TeamButton(Team team, FileHandle folder) {
            this.team = team;
            this.folder = folder;
            setSize(382, 28);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            String mainFolder = Assets.getTeamFirstFolder(folder).name();
            setText(team.name + ", " + mainFolder, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case EDIT:
                    navigation.folder = folder;
                    navigation.league = team.league;
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

        private Team team;
        private boolean isFavourite;

        FavouriteToggleButton(Widget teamButton) {
            this.team = ((TeamButton) teamButton).team;
            setGeometry(teamButton.x - 26, teamButton.y, 26, 28);
            setText("", Font.Align.CENTER, Assets.font14);
            isFavourite = favourites.contains(team.path);
        }

        @Override
        public void refresh() {
            setText(isFavourite ? "" + (char) 22 : "" + (char) 23);
        }

        @Override
        public void onFire1Down() {
            if (favourites.contains(team.path)) {
                favourites.remove(team.path);
                isFavourite = false;
            } else {
                favourites.add(team.path);
                isFavourite = true;
            }
            saveFavourites();
            setDirty(true);
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColors(0xC8000E);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(gettext("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
