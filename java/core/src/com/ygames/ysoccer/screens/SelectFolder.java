package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
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

class SelectFolder extends GLScreen {

    SelectFolder(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(getTitle(), game.stateColor.body);
        widgets.add(w);

        // Breadcrumb
        List<Widget> breadcrumb = new ArrayList<Widget>();

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
        List<Widget> list = new ArrayList<Widget>();
        ArrayList<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(navigation.folder.list()));
        Collections.sort(files, Assets.fileComparatorByName);
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                list.add(w);
                widgets.add(w);
            }
        }

        if (list.size() > 0) {
            Widget.arrange(game.gui.WIDTH, 380, 34, list);
            setSelectedWidget(list.get(0));
        }

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            setSelectedWidget(w);
        }
    }

    private String getTitle() {
        String title = "";
        switch (game.getState()) {
            case COMPETITION:
            case FRIENDLY:
                int diff = navigation.competition.numberOfTeams - game.teamList.size();
                title = Assets.strings.get((diff == 0) ? "CHANGE TEAMS FOR" : "CHOOSE TEAMS FOR")
                        + " " + navigation.competition.name;
                break;

            case EDIT:
                title = Assets.strings.get("EDIT TEAMS");
                break;

            case TRAINING:
                title = Assets.strings.get("TRAINING");
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
            setText(isDataRoot ? "" + (char) 20 : folder.name().replace('_', ' '), Font.Align.CENTER, Assets.font10);
            autoWidth();
        }

        @Override
        public void onFire1Down() {
            navigation.folder = folder;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class FolderButton extends Button {

        private FileHandle fileHandle;

        FolderButton(FileHandle fileHandle) {
            this.fileHandle = fileHandle;
            setSize(340, 32);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(fileHandle.name().replace('_', ' '), Font.Align.CENTER, Assets.font14);
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
                        game.setScreen(new SelectTeams(game, navigation.competition));
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

    private class ExitButton extends Button {

        ExitButton() {
            if (game.getState() == GLGame.State.EDIT || game.getState() == GLGame.State.TRAINING) {
                setColors(0xC84200);
                setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
            } else {
                setColors(0xC8000E);
                setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
            }
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}