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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class LoadTactics extends GLScreen {

    LoadTactics(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("LOAD EDITED TACTICS"), 0xBA9206);
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
            Tactics tactics = new Tactics();
            tactics.copyFrom(game.editedTactics);
            game.tacticsUndo.push(tactics);

            InputStream in = Assets.tacticsFolder.child(name + ".TAC").read();
            try {
                game.editedTactics.loadFile(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Tactics.codes[game.tacticsToEdit] = name;

            game.setScreen(new EditTactics(game));
        }
    }
}
