package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;

import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.match.Team.Type.CLUB;

class SearchTeams extends GLScreen {

    private enum State {START, SEARCHING, FINISHED}

    private final int MAX_RESULTS = 20;

    private State state = State.START;

    private Button searchInputButton;

    private int fileIndex;
    private ArrayList<String> teamFiles;
    private ArrayList<TeamButton> teamList;

    private boolean reachedMaxResults = false;

    SearchTeams(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(gettext("SEARCH.SEARCH TEAMS"), game.stateColor.body);
        widgets.add(w);

        // Breadcrumb
        ArrayList<Widget> breadcrumb = new ArrayList<>();
        if (navigation.league != null) {
            w = new BreadCrumbLeagueLabel();
            breadcrumb.add(w);
        }
        FileHandle fh = navigation.folder;
        boolean isDataRoot;
        do {
            isDataRoot = fh.equals(Assets.teamsRootFolder);
            boolean isCurrent = (navigation.league == null && fh == navigation.folder);
            w = new BreadCrumbButton(fh, isDataRoot, isCurrent);
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

        teamFiles = new ArrayList<>();
        teamList = new ArrayList<>();
        recursiveTeamSearch(navigation.folder);

        w = new InfoLabel();
        widgets.add(w);

        searchInputButton = new SearchInputButton();
        widgets.add(searchInputButton);
        setSelectedWidget(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private void recursiveTeamSearch(FileHandle folder) {
        FileHandle[] teamFileHandles = folder.list(Assets.teamFilenameFilter);
        if (teamFileHandles.length > 0) {
            for (FileHandle teamFileHandle : teamFileHandles) {
                Team team = Assets.json.fromJson(Team.class, teamFileHandle.readString("UTF-8"));
                team.path = Assets.getRelativeTeamPath(teamFileHandle);
                if ((navigation.league == null) || ((team.type == CLUB) && team.league.equals(navigation.league))) {
                    teamFiles.add(Assets.getRelativeTeamPath(teamFileHandle));
                }
            }
        } else {
            FileHandle[] fileHandles = folder.list();
            for (FileHandle teamFileHandle : fileHandles) {
                if (teamFileHandle.isDirectory()) {
                    recursiveTeamSearch(teamFileHandle);
                }
            }
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

        private FileHandle folder;

        BreadCrumbButton(FileHandle folder, boolean isDataRoot, boolean isCurrent) {
            this.folder = folder;
            setSize(0, 32);
            if (isCurrent) {
                setColors(game.stateColor.darker());
                setActive(false);
            } else {
                setColors(game.stateColor);
                if (isDataRoot) setActive(false);
            }
            setText(isDataRoot ? "" + (char) 20 : folder.name().replace('_', ' '), Font.Align.CENTER, Assets.font10);
            autoWidth();
        }

        @Override
        public void onFire1Down() {
            navigation.folder = folder;
            navigation.league = null;
            game.setScreen(new SearchTeams(game));
        }
    }

    private class InfoLabel extends Label {

        InfoLabel() {
            setGeometry((game.gui.WIDTH - 400) / 2, 110, 400, 40);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            switch (state) {
                case START:
                    setText("(" + teamFiles.size() + " " + gettext("TEAMS") + ")");
                    break;

                case SEARCHING:
                    setText((fileIndex + 1) + " / " + teamFiles.size());
                    break;

                case FINISHED:
                    if (reachedMaxResults) {
                        setText(gettext("SEARCH.FIRST %n TEAMS FOUND").replaceFirst("%n", MAX_RESULTS + ""));
                    } else {
                        setText(gettext("SEARCH.%n TEAMS FOUND").replaceFirst("%n", teamList.size() + ""));
                    }
                    break;
            }
        }
    }

    private class SearchInputButton extends InputButton {

        SearchInputButton() {
            setGeometry((game.gui.WIDTH - 440) / 2, 150, 440, 36);
            setColors(0x1769BD, 0x3A90E8, 0x10447A);
            setText("", Font.Align.CENTER, Assets.font14);
            setEntryLimit(20);
        }

        @Override
        public void onChanged() {
            if (text.length() > 0) {
                fileIndex = 0;
                widgets.removeAll(teamList);
                teamList.clear();
                reachedMaxResults = false;
                state = State.SEARCHING;
            }
            refreshAllWidgets();
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(gettext("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            FileHandle[] teamFileHandles = navigation.folder.list(Assets.teamFilenameFilter);
            if (teamFileHandles.length > 0) {
                game.setScreen(new SelectTeam(game));
            } else {
                game.setScreen(new SelectFolder(game));
            }
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        switch (state) {
            case START:
                break;

            case SEARCHING:
                if (reachedMaxResults || fileIndex == teamFiles.size()) {
                    state = State.FINISHED;
                } else {
                    searchTeam(teamFiles.get(fileIndex));
                    fileIndex++;
                }
                refreshAllWidgets();
                break;

            case FINISHED:
                break;
        }
    }

    private void searchTeam(String teamFile) {
        FileHandle fh = Assets.teamsRootFolder.child(teamFile);
        Team team = Assets.json.fromJson(Team.class, fh.readString("UTF-8"));
        team.path = Assets.getRelativeTeamPath(fh);
        String searchTerm = searchInputButton.getText();
        if (accentInsensitiveContains(team.name, searchTerm)) {
            if (teamList.size() < MAX_RESULTS) {
                TeamButton teamButton = new TeamButton(team, fh.parent());
                teamButton.setPosition((game.gui.WIDTH - 604) / 2, 210 + 21 * teamList.size());
                teamList.add(teamButton);
                widgets.add(teamButton);
            } else {
                reachedMaxResults = true;
            }
        }
    }

    private boolean accentInsensitiveContains(String string, String searchTerm) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .contains(searchTerm);
    }

    private class TeamButton extends Button {

        Team team;
        FileHandle folder;

        TeamButton(Team team, FileHandle folder) {
            this.team = team;
            this.folder = folder;
            setColor(0x308C3B);
            setSize(604, 19);
            String t = "";
            switch (team.type) {
                case CLUB:
                case CUSTOM:
                    t = team.name + " (" + team.country + ")";
                    break;
                case NATIONAL:
                    t = team.name + " (" + folder.name() + ")";
                    break;
            }
            setText(t, Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            navigation.folder = folder;
            navigation.league = team.league;

            switch (game.getState()) {
                case EDIT:
                    game.setScreen(new EditTeam(game, team, false));
                    break;

                case TRAINING:
                    navigation.team = team;
                    game.setScreen(new SetupTraining(game));
                    break;
            }

        }
    }
}
