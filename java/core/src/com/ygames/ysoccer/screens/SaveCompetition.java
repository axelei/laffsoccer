package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SaveCompetition extends GLScreen {

    private Button fileNameButton;

    SaveCompetition(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(Assets.strings.get("SAVE %s").replace("%s", game.competition.name), game.stateColor.body);
        widgets.add(w);

        // Competitions buttons
        List<Widget> competitionButtonsList = new ArrayList<Widget>();
        List<Widget> categoryLabelsList = new ArrayList<Widget>();

        FileHandle folder = Assets.savesFolder.child(game.competition.getCategoryFolder());
        ArrayList<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(folder.list()));
        Collections.sort(files, Assets.fileComparatorByName);
        for (FileHandle file : files) {
            if (!file.isDirectory() && file.extension().equals("json")) {
                w = new CompetitionButton(file.nameWithoutExtension());
                competitionButtonsList.add(w);
                widgets.add(w);

                w = new CategoryLabel(game.competition);
                categoryLabelsList.add(w);
                widgets.add(w);
            }
        }

        if (competitionButtonsList.size() > 0) {
            int len = competitionButtonsList.size();
            for (int i = 0; i < len; i++) {
                Widget b = competitionButtonsList.get(i);
                Widget l = categoryLabelsList.get(i);
                b.x = (game.gui.WIDTH - b.w - l.w - 4) / 2;
                b.y = 300 + 34 * (i - len / 2);
                l.x = (game.gui.WIDTH + b.w - l.w + 4) / 2;
                l.y = 300 + 34 * (i - len / 2);
            }
        }

        w = new FilenameLabel();
        widgets.add(w);

        fileNameButton = new FilenameButton();
        widgets.add(fileNameButton);

        w = new SaveButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class CompetitionButton extends Button {

        private String filename;

        CompetitionButton(String filename) {
            this.filename = filename;
            setSize(540, 30);
            setColors(0x1B4D85, 0x256AB7, 0x001D3E);
            setText(filename, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.competition.saveAndSetFilename(filename);
            game.setScreen(new Main(game));
        }
    }

    private class CategoryLabel extends Button {

        CategoryLabel(Competition competition) {
            setSize(180, 30);
            setText(competition.getCategoryFolder(), Font.Align.CENTER, Assets.font14);
            switch (competition.category) {
                case DIY_COMPETITION:
                    setColor(0x376E2F);
                    break;
                case PRESET_COMPETITION:
                    setColor(0x415600);
                    break;
            }
            setActive(false);
        }
    }

    private class FilenameLabel extends Label {

        FilenameLabel() {
            setGeometry(game.gui.WIDTH / 2 - 360 - 2, 550, 180, 36);
            setColors(0x9C522A, 0xBB5A25, 0x69381D);
            setText(Assets.strings.get("FILENAME") + ":", Font.Align.RIGHT, Assets.font14);
        }
    }

    private class FilenameButton extends InputButton {

        FilenameButton() {
            String filename = game.competition.filename;
            if (filename.length() == 0) {
                filename = game.competition.getNewFilename();
            }
            setGeometry(game.gui.WIDTH / 2 - 180 + 2, 550, 540, 36);
            setColors(0x1769BD, 0x3A90E8, 0x10447A);
            setText(filename, Font.Align.CENTER, Assets.font14);
            setEntryLimit(30);
        }

        @Override
        public void onChanged() {
            game.competition.saveAndSetFilename(text);
            game.setScreen(new Main(game));
        }
    }

    private class SaveButton extends Button {

        SaveButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 605, 180, 36);
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setText(Assets.strings.get("SAVE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Up() {
            game.competition.saveAndSetFilename(fileNameButton.getText());
            game.setScreen(new Main(game));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC8000E, 0xFF1929, 0x74040C);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
