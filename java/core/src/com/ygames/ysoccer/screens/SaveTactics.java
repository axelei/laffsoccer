package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Tactics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.framework.GLGame.State.NONE;

class SaveTactics extends GLScreen {

    String filename;

    SaveTactics(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        filename = Tactics.fileNames[game.tacticsToEdit];

        Widget w;

        w = new TitleBar(Assets.strings.get("SAVE TACTICS"), 0xBA9206);
        widgets.add(w);

        // read tactics files
        List<FileHandle> files = Arrays.asList(Assets.tacticsFolder.list(".TAC"));
        Collections.sort(files, Assets.fileComparatorByName);

        List<Widget> list = new ArrayList<Widget>();
        for (FileHandle file : files) {
            w = new TacticsButton(file.nameWithoutExtension());
            list.add(w);
            widgets.add(w);
        }

        Widget.arrange(game.gui.WIDTH, 280, 42, list);

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

    private void save(String name) {
        if (name.isEmpty()) {
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
            // TODO
//            game.setScreen(new SetTeam(game));
        }
    }
}
