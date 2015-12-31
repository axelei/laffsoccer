package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class Friendly extends GlScreen {

    private FileHandle fileHandle;
    private boolean isDataRoot;

    public Friendly(GlGame game, FileHandle fileHandle) {
        super(game);
        this.fileHandle = fileHandle;
        isDataRoot = (fileHandle.path().equals(Assets.dataFolder.path()));

        background = new Image("images/backgrounds/menu_friendly.jpg");

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        FileHandle[] files = fileHandle.list();
        int i = 0;
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                w.setPosition((game.settings.GUI_WIDTH - 340) / 2, 300 + 60 * i);
                widgets.add(w);
                if (i == 0) {
                    selectedWidget = w;
                }
                i++;
            }
        }

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            selectedWidget = w;
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            String title = Assets.strings.get("FRIENDLY");
            if (!isDataRoot) {
                title += " - " + fileHandle.name().toUpperCase();
            }
            int w = Math.max(400, 80 + 16 * title.length());
            setGeometry((game.settings.GUI_WIDTH - w) / 2, 30, w, 40);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class FolderButton extends Button {

        FileHandle fileHandle;

        public FolderButton(FileHandle fileHandle) {
            this.fileHandle = fileHandle;
            setSize(340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(fileHandle.name().toUpperCase(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Friendly(game, fileHandle));
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 708, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            if (isDataRoot) {
                game.setScreen(new Main(game));
            } else {
                game.setScreen(new Friendly(game, fileHandle.parent()));
            }
        }
    }
}