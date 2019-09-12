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
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;

import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.match.Team.Type.CLUB;

class SearchPlayers extends GLScreen {

    private enum State {START, SEARCHING, FINISHED}

    private final int MAX_RESULTS = 20;

    private State state = State.START;

    private Button searchInputButton;

    private int fileIndex;
    private ArrayList<String> teamFiles;
    private ArrayList<PlayerButton> playerList;

    private boolean reachedMaxResults = false;

    SearchPlayers(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(gettext("SEARCH.SEARCH PLAYERS"), game.stateColor.body);
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
        playerList = new ArrayList<>();
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
            game.setScreen(new SearchPlayers(game));
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
                        setText(gettext("SEARCH.FIRST %n PLAYERS FOUND").replaceFirst("%n", MAX_RESULTS + ""));
                    } else {
                        setText(gettext("SEARCH.%n PLAYERS FOUND").replaceFirst("%n", playerList.size() + ""));
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
                widgets.removeAll(playerList);
                playerList.clear();
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
                    searchPlayer(teamFiles.get(fileIndex));
                    fileIndex++;
                }
                refreshAllWidgets();
                break;

            case FINISHED:
                break;
        }
    }

    private void searchPlayer(String teamFile) {
        FileHandle fh = Assets.teamsRootFolder.child(teamFile);
        Team team = Assets.json.fromJson(Team.class, fh.readString("UTF-8"));
        team.path = Assets.getRelativeTeamPath(fh);
        String searchTerm = searchInputButton.getText();
        for (Player player : team.players) {
            if (accentInsensitiveContains(player.name, searchTerm) ||
                    accentInsensitiveContains(player.shirtName, searchTerm)) {
                if (playerList.size() < MAX_RESULTS) {
                    PlayerButton playerButton = new PlayerButton(player, fh.parent());
                    playerButton.setPosition((game.gui.WIDTH - 604) / 2, 210 + 21 * playerList.size());
                    playerList.add(playerButton);
                    widgets.add(playerButton);
                } else {
                    reachedMaxResults = true;
                    return;
                }
            }
        }
    }

    private boolean accentInsensitiveContains(String string, String searchTerm) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .contains(searchTerm);
    }

    private class PlayerButton extends Button {

        Player player;
        FileHandle folder;

        PlayerButton(Player player, FileHandle folder) {
            this.player = player;
            this.folder = folder;
            setColor(0x308C3B);
            setSize(604, 19);
            setText(player.name + " (" + player.team.name + ")", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            navigation.folder = folder;
            navigation.league = player.team.league;
            game.setScreen(new EditPlayers(game, player.team, false));
        }
    }
}
