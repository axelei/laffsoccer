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
import java.util.HashSet;
import java.util.List;

import static com.ygames.ysoccer.match.Team.Type.CLUB;

class SelectTeam extends GLScreen {

    private FileHandle currentFolder;
    private String league;

    SelectTeam(GLGame game, FileHandle folder, String league) {
        super(game);
        this.currentFolder = folder;
        this.league = league;

        background = game.stateBackground;

        Widget w;

        w = new TitleButton();
        widgets.add(w);

        List<Widget> list = new ArrayList<Widget>();

        HashSet<String> leagues = new HashSet<String>();

        List<Team> teamList = new ArrayList<Team>();
        FileHandle[] teamFileHandles = currentFolder.list(Assets.teamFilenameFilter);
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
            if (list.size() > 0) {
                Collections.sort(list, Widget.widgetComparatorByText);
                Widget.arrange(game.gui.WIDTH, 380, 34, list);
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
                Widget.arrange(game.gui.WIDTH, 380, 30, list);
                setSelectedWidget(list.get(0));
            }
        }


        // Breadcrumb
        List<Widget> breadcrumb = new ArrayList<Widget>();
        if (league != null) {
            w = new BreadCrumbLeagueLabel();
            breadcrumb.add(w);
        }
        FileHandle fh = currentFolder;
        boolean isDataRoot;
        do {
            isDataRoot = fh.path().equals(Assets.teamsFolder.path());
            boolean disabled = (league == null && fh == currentFolder);
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

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            setSelectedWidget(w);
        }
    }

    class TitleButton extends Button {

        public TitleButton() {
            setGeometry((game.gui.WIDTH - 960) / 2, 30, 960, 40);
            setColors(game.stateColor);
            String title = "";
            switch (game.getState()) {
                case EDIT:
                    title = Assets.strings.get("EDIT TEAMS");
                    break;

                case TRAINING:
                    // TODO
                    break;
            }
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BreadCrumbLeagueLabel extends Button {

        BreadCrumbLeagueLabel() {
            setSize(0, 32);
            setColors(game.stateColor.darker());
            setActive(false);
            setText(league, Font.Align.CENTER, Assets.font10);
            autoWidth();
        }
    }

    private class BreadCrumbButton extends Button {

        private FileHandle folder;

        BreadCrumbButton(FileHandle folder, boolean isDataRoot, boolean disabled) {
            this.folder = folder;
            setSize(0, 32);
            if (disabled) {
                setColors(game.stateColor.darker());
                setActive(false);
            } else {
                setColors(game.stateColor);
            }
            setText(isDataRoot ? "" + (char) 20 : folder.name().replace('_', ' '), Font.Align.CENTER, Assets.font10);
            autoWidth();
        }

        @Override
        public void onFire1Down() {
            if (folder == currentFolder && league != null) {
                game.setScreen(new SelectTeam(game, folder, null));
            } else {
                game.setScreen(new SelectFolder(game, folder, null));
            }
        }
    }

    private class LeagueButton extends Button {

        LeagueButton(String name) {
            setSize(300, 32);
            setColors(0x1B4D85);
            setText(name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case EDIT:
                    game.setScreen(new SelectTeam(game, currentFolder, text));
                    break;

                case TRAINING:
                    // TODO
                    break;
            }
        }
    }

    private class TeamButton extends Button {

        private Team team;

        TeamButton(Team team) {
            this.team = team;
            setSize(270, 28);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(team.name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case EDIT:
                    game.setScreen(new EditPlayers(game, currentFolder, league, team, false));
                    break;

                case TRAINING:
                    // TODO
                    break;
            }
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
