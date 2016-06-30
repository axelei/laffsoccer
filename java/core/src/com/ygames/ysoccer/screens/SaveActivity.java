package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

public class SaveActivity extends GlScreen {

    public SaveActivity(GlGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

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
            setText(s.replace("%s", game.activity.longName.toUpperCase()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class FilenameLabel extends Label {

        public FilenameLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 160, 500, 160, 36);
            setColors(0x9C522A, 0xBB5A25, 0x69381D);
            setText(Assets.strings.get("FILENAME") + ":", Font.Align.RIGHT, Assets.font14);
        }
    }

    class FilenameButton extends InputButton {

        public FilenameButton() {
            setGeometry(game.settings.GUI_WIDTH / 2, 500, 160, 36);
            setColors(0x1769BD, 0x3A90E8, 0x10447A);
            setText(game.activity.filename.toUpperCase(), Font.Align.CENTER, Assets.font14);
            setEntryLimit(8);
        }

        @Override
        public void onUpdate() {
            if (getText().toLowerCase().compareTo(game.activity.filename) != 0) {
                game.activity.filename = getText().toLowerCase();
                game.activity.save();
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
