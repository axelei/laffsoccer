package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SelectCompetition extends GLScreen {

    private FileHandle currentFolder;

    SelectCompetition(GLGame game, FileHandle folder) {
        super(game);
        this.currentFolder = folder;

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(Assets.strings.get("CHOOSE PRESET COMPETITION"), game.stateColor.body);
        widgets.add(w);

        // Breadcrumb
        List<Widget> breadcrumb = new ArrayList<Widget>();

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
        List<Widget> competitionsList = new ArrayList<Widget>();

        FileHandle leaguesFile = currentFolder.child("leagues.json");
        if (leaguesFile.exists()) {
            League[] leagues = Assets.json.fromJson(League[].class, leaguesFile.readString("UTF-8"));
            for (League league : leagues) {
                league.init();
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
                cup.init();
                cup.category = Competition.Category.PRESET_COMPETITION;
                w = new CompetitionButton(cup);
                competitionsList.add(w);
                widgets.add(w);
            }
        }

        // Folders buttons
        List<Widget> foldersList = new ArrayList<Widget>();
        ArrayList<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(currentFolder.list()));
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
            Widget.arrange(game.gui.WIDTH, centerY, 28, competitionsList);
            setSelectedWidget(competitionsList.get(0));
        }
        centerY += 28 * (competitionsList.size() + Widget.getRows(foldersList)) / 2 + 6;
        if (foldersList.size() > 0) {
            Widget.arrange(game.gui.WIDTH, centerY, 28, foldersList);
            setSelectedWidget(foldersList.get(0));
        }

        w = new AbortButton();
        widgets.add(w);
        if (selectedWidget == null) {
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
            setSize(480, 28);
            setColors(0x1B4D85, 0x256AB7, 0x001D3E);
            setText(competition.name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.teamList = competition.loadTeams();
            game.setScreen(new AllSelectedTeams(game, Assets.teamsRootFolder.child(competition.teamsFolder), null, competition));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColors(0xC8000E);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}