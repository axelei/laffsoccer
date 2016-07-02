package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SaveCompetition extends GlScreen {

    public SaveCompetition(GlGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        // Competitions buttons
        List<Widget> competitionButtonsList = new ArrayList<Widget>();
        List<Widget> categoryLabelsList = new ArrayList<Widget>();

        FileHandle folder = Assets.savesFolder.child(game.competition.getCategoryFolder());
        ArrayList<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(folder.list()));
        Collections.sort(files, new Assets.CompareFileHandlesByName());
        for (FileHandle file : files) {
            if (!file.isDirectory() && file.extension().equals("json")) {
                w = new CompetitionButton(file.nameWithoutExtension());
                competitionButtonsList.add(w);
                widgets.add(w);

                w = new CategoryLabel(game.competition.getCategoryFolder());
                categoryLabelsList.add(w);
                widgets.add(w);
            }
        }

        if (competitionButtonsList.size() > 0) {
            int len = competitionButtonsList.size();
            for (int i = 0; i < len; i++) {
                w = competitionButtonsList.get(i);
                w.x = (game.settings.GUI_WIDTH) / 2 - w.w + 180;
                w.y = 320 + 34 * (i - len / 2);
                w = categoryLabelsList.get(i);
                w.x = (game.settings.GUI_WIDTH) / 2 + 180;
                w.y = 320 + 34 * (i - len / 2);
            }
        }

        w = new FilenameLabel();
        widgets.add(w);

        w = new FilenameButton();
        widgets.add(w);
        selectedWidget = w;

        w = new AbortButton();
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 520) / 2, 30, 520, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            String s = Assets.strings.get("SAVE %s");
            setText(s.replace("%s", game.competition.longName.toUpperCase()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    public class CompetitionButton extends Button {

        private String filename;

        public CompetitionButton(String filename) {
            this.filename = filename;
            setSize(540, 30);
            setColors(0x1B4D85, 0x256AB7, 0x001D3E);
            setText(filename.toUpperCase(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.competition.filename = filename;
            game.competition.save();
            game.setScreen(new Main(game));
        }
    }

    public class CategoryLabel extends Button {

        public CategoryLabel(String category) {
            setSize(180, 30);
            setText(category.toUpperCase(), Font.Align.CENTER, Assets.font14);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setActive(false);
        }
    }

    class FilenameLabel extends Label {

        public FilenameLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 360, 500, 180, 36);
            setColors(0x9C522A, 0xBB5A25, 0x69381D);
            setText(Assets.strings.get("FILENAME") + ":", Font.Align.RIGHT, Assets.font14);
        }
    }

    class FilenameButton extends InputButton {

        public FilenameButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 180, 500, 540, 36);
            setColors(0x1769BD, 0x3A90E8, 0x10447A);
            setText(game.competition.filename.toUpperCase(), Font.Align.CENTER, Assets.font14);
            setEntryLimit(30);
        }

        @Override
        public void onUpdate() {
            if (getText().toLowerCase().compareTo(game.competition.filename) != 0) {
                game.competition.filename = getText().toLowerCase();
                game.competition.save();
                game.setScreen(new Main(game));
            }
        }
    }

    class AbortButton extends Button {

        public AbortButton() {
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC8000E, 0xFF1929, 0x74040C);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
