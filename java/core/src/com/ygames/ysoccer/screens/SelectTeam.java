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
import java.util.List;

import static com.ygames.ysoccer.match.Team.Type.CLUB;

class SelectTeam extends GLScreen {

    SelectTeam(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(getTitle(), game.stateColor.body);
        widgets.add(w);

        List<Widget> list = new ArrayList<Widget>();

        List<String> leagues = new ArrayList<String>();
        FileHandle leaguesFile = navigation.folder.child("leagues.json");
        if (navigation.league == null && leaguesFile.exists()) {
            leagues = Assets.json.fromJson(ArrayList.class, String.class, leaguesFile.readString("UTF-8"));
        }

        List<Team> teamList = new ArrayList<Team>();
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
        if (navigation.league != null) {
            w = new BreadCrumbLeagueLabel();
            breadcrumb.add(w);
        }
        FileHandle fh = navigation.folder;
        boolean isDataRoot;
        do {
            isDataRoot = fh.equals(Assets.teamsRootFolder);
            boolean disabled = (navigation.league == null && fh == navigation.folder);
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

    private String getTitle() {
        String title = "";
        switch (game.getState()) {
            case EDIT:
                title = Assets.strings.get("EDIT TEAMS");
                break;

            case TRAINING:
                title = Assets.strings.get("TRAINING");
                break;
        }
        return title;
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
            if (folder == navigation.folder && navigation.league != null) {
                navigation.league = null;
                game.setScreen(new SelectTeam(game));
            } else {
                navigation.folder = folder;
                game.setScreen(new SelectFolder(game));
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
            navigation.league = text;
            game.setScreen(new SelectTeam(game));
        }
    }

    private class TeamButton extends Button {

        private Team team;

        TeamButton(Team team) {
            this.team = team;
            setSize(270, 28);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            if (game.settings.development) {
                int v = 0;
                for (int i = 0; i < 11; i++) v += team.playerAtPosition(i).getValue();
                setText(team.name + " " + v, Font.Align.CENTER, Assets.font14);
            } else {
                setText(team.name, Font.Align.CENTER, Assets.font14);
            }
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
