package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
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
}
