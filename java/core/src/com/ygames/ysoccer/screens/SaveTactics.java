package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Tactics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.GLGame.State.NONE;

class SaveTactics extends GLScreen {

    private String filename;

    SaveTactics(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        filename = Tactics.codes[game.tacticsToEdit];

        Widget w;

        w = new TitleBar(gettext("SAVE TACTICS"), 0xBA9206);
        widgets.add(w);

        // read tactics files
        List<FileHandle> files = Arrays.asList(Assets.tacticsFolder.list(".TAC"));
        Collections.sort(files, Assets.fileComparatorByName);

        List<Widget> list = new ArrayList<>();
        for (FileHandle file : files) {
            w = new TacticsButton(file.nameWithoutExtension());
            list.add(w);
            widgets.add(w);
        }

        Widget.arrange(game.gui.WIDTH, 280, 42, 20, list);

        for (Widget widget : list) {
            widget.w = 160;
            w = new Button();
            w.setSize(80, 36);
            w.setPosition(widget.x + 165, widget.y);
            w.setColors(0x98691E, 0xC88B28, 0x3E2600);
            w.setText("TACT", Font.Align.CENTER, Assets.font14);
            w.setActive(false);
            widgets.add(w);
        }

        w = new FilenameLabel();
        widgets.add(w);

        w = new FilenameButton();
        widgets.add(w);

        w = new SaveButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class TacticsButton extends Button {

        String name;

        TacticsButton(String name) {
            this.name = name;
            setSize(245, 36);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            save(name);
        }
    }

    private class FilenameLabel extends Label {

        FilenameLabel() {
            setGeometry(game.gui.WIDTH / 2 - 180, 500, 180, 36);
            setText(gettext("FILENAME") + ":", Font.Align.RIGHT, Assets.font14);
        }
    }

    private class FilenameButton extends InputButton {

        FilenameButton() {
            setGeometry(game.gui.WIDTH / 2, 500, 160, 36);
            setColors(0x1769BD, 0x3A90E8, 0x10447A);
            setText(filename, Font.Align.CENTER, Assets.font14);
            setEntryLimit(8);
        }

        @Override
        public void onChanged() {
            filename = text;
        }
    }

    private class SaveButton extends Button {

        SaveButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 590, 180, 36);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(gettext("SAVE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Up() {
            save(null);
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(gettext("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Up() {
            game.setScreen(new EditTactics(game));
        }
    }

    private void save(String name) {
        if (name == null) {
            name = filename;
        }

        // rename
        game.editedTactics.name = name;

        // save
        game.editedTactics.saveFile(name + ".TAC");

        // copy edited tactics back
        Assets.tactics[game.tacticsToEdit].copyFrom(game.editedTactics);
        Tactics.codes[game.tacticsToEdit] = game.editedTactics.name;

        if (game.getState() == NONE) {
            game.setScreen(new Main(game));
        } else {
            game.setScreen(new SetTeam(game));
        }
    }
}
