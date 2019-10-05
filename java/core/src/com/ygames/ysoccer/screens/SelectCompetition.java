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
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.ygames.ysoccer.framework.Assets.gettext;

class SelectCompetition extends GLScreen {

    private FileHandle currentFolder;

    SelectCompetition(GLGame game, FileHandle folder) {
        super(game);
        this.currentFolder = folder;

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(gettext("CHOOSE PRESET COMPETITION"), game.stateColor.body);
        widgets.add(w);

        // Breadcrumb
        ArrayList<Widget> breadcrumb = new ArrayList<>();

        FileHandle fh = currentFolder;
        boolean isDataRoot;
        do {
            isDataRoot = fh.equals(Assets.competitionsRootFolder);
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

        // Competitions buttons
        ArrayList<Widget> competitionsList = new ArrayList<>();

        FileHandle tournamentsFile = currentFolder.child("tournaments.json");
        if (tournamentsFile.exists()) {
            Tournament[] tournaments = Assets.json.fromJson(Tournament[].class, tournamentsFile.readString("UTF-8"));
            for (Tournament tournament : tournaments) {
                tournament.category = Competition.Category.PRESET_COMPETITION;
                w = new CompetitionButton(tournament);
                competitionsList.add(w);
                widgets.add(w);
            }
        }

        FileHandle leaguesFile = currentFolder.child("leagues.json");
        if (leaguesFile.exists()) {
            League[] leagues = Assets.json.fromJson(League[].class, leaguesFile.readString("UTF-8"));
            for (League league : leagues) {
                league.category = Competition.Category.PRESET_COMPETITION;
                w = new CompetitionButton(league);
                competitionsList.add(w);
                widgets.add(w);
            }
        }

        FileHandle cupsFile = currentFolder.child("cups.json");
        if (cupsFile.exists()) {
            Cup[] cups = Assets.json.fromJson(Cup[].class, cupsFile.readString("UTF-8"));
            for (Cup cup : cups) {
                cup.category = Competition.Category.PRESET_COMPETITION;
                w = new CompetitionButton(cup);
                competitionsList.add(w);
                widgets.add(w);
            }
        }

        // Folders buttons
        ArrayList<Widget> foldersList = new ArrayList<>();
        ArrayList<FileHandle> files = new ArrayList<>(Arrays.asList(currentFolder.list()));
        Collections.sort(files, Assets.fileComparatorByName);
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                foldersList.add(w);
                widgets.add(w);
            }
        }

        int topY = 380 - 28 * (competitionsList.size() + Widget.getRows(foldersList)) / 2;
        int centerY = topY + 28 * competitionsList.size() / 2;
        if (competitionsList.size() > 0) {
            Widget.arrange(game.gui.WIDTH, centerY, 28, 20, competitionsList);
            setSelectedWidget(competitionsList.get(0));
        }
        centerY += 28 * (competitionsList.size() + Widget.getRows(foldersList)) / 2 + 6;
        if (foldersList.size() > 0) {
            Widget.arrange(game.gui.WIDTH, centerY, 28, 20, foldersList);
            setSelectedWidget(foldersList.get(0));
        }

        w = new AbortButton();
        widgets.add(w);
        if (getSelectedWidget() == null) {
            setSelectedWidget(w);
        }
    }

    private class BreadCrumbButton extends Button {

        FileHandle folder;

        BreadCrumbButton(FileHandle folder, boolean isDataRoot) {
            this.folder = folder;
            setSize(0, 32);
            if (folder == currentFolder) {
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
            game.setScreen(new SelectCompetition(game, folder));
        }
    }

    private class FolderButton extends Button {

        FileHandle folder;

        FolderButton(FileHandle folder) {
            this.folder = folder;
            setSize(340, 28);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(folder.name().replace('_', ' '), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectCompetition(game, folder));
        }
    }

    private class CompetitionButton extends Button {

        Competition competition;

        CompetitionButton(Competition competition) {
            this.competition = competition;
            boolean dataFolderExists = Assets.teamsRootFolder.child(competition.files.folder).exists();
            setSize(480, 28);
            setColor(dataFolderExists ? 0x1B4D85 : 0x666666);
            setText(competition.name, Font.Align.CENTER, Assets.font14);
            setActive(dataFolderExists);
        }

        @Override
        public void onFire1Down() {
            game.teamList = Team.loadTeamList(competition.files.teams);
            navigation.folder = Assets.teamsRootFolder.child(competition.files.folder);
            navigation.league = competition.files.league;
            navigation.competition = competition;
            game.setScreen(new AllSelectedTeams(game));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColor(0xC8000E);
            setText(gettext("ABORT"), Font.Align.CENTER, Assets.font14);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}