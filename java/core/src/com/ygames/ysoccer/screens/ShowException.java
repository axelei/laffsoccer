package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.TextBox;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.List;

public class ShowException extends GLScreen {

    public ShowException(GLGame game, Exception e) {
        super(game);

        clearColor = 0x001E4E;

        BitmapFont font = new BitmapFont(true);

        List<String> lines = new ArrayList<String>();
        lines.add("An unexpected error has occurred :(");
        lines.add("");
        lines.add(e.getMessage());
        lines.add("");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            lines.add(stackTraceElement.toString());
        }
        Widget w = new TextBox(font, lines, game.gui.WIDTH / 2, game.gui.HEIGHT / 2 - 22 * lines.size() / 2);
        widgets.add(w);
    }
}
