package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

public class PlayLeague extends GlScreen {

    private String[] headers = {
            "GRID HEADER.PLAYED MATCHES",
            "GRID HEADER.WON MATCHES",
            "GRID HEADER.DRAWN MATCHES",
            "GRID HEADER.LOST MATCHES",
            "GRID HEADER.GOALS FOR",
            "GRID HEADER.GOALS AGAINST",
            "GRID HEADER.POINTS"
    };

    public PlayLeague(GlGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        // table headers
        int dx = 460;
        int dy = 100 + 11 * (24 - game.competition.numberOfTeams);
        for (String header : headers) {
            w = new Label();
            w.setGeometry(dx, dy, 62, 21);
            w.setText(Assets.strings.get(header), Font.Align.CENTER, Assets.font10);
            widgets.add(w);
            dx += 60;
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 520) / 2, 30, 520, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(game.competition.getMenuTitle().toUpperCase(), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
