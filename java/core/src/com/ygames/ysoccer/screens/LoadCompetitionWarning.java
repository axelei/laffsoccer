package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

public class LoadCompetitionWarning extends GLScreen {

    public LoadCompetitionWarning(GLGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_competition.jpg");

        Widget w;

        w = new TitleBar();
        widgets.add(w);

        // warning
        w = new Button();
        w.setGeometry((game.settings.GUI_WIDTH - 580) / 2, 270, 580, 180);
        w.setColors(0xDC0000, 0xFF4141, 0x8C0000);
        w.setActive(false);
        widgets.add(w);

        String msg = Assets.strings.get(Competition.getWarningLabel(game.competition.category));
        int cut = msg.indexOf(" ", msg.length() / 2);

        w = new Label();
        w.setText(msg.substring(0, cut), Font.Align.CENTER, Assets.font14);
        w.setPosition(game.settings.GUI_WIDTH / 2, 340);
        widgets.add(w);

        w = new Label();
        w.setText(msg.substring(cut + 1), Font.Align.CENTER, Assets.font14);
        w.setPosition(game.settings.GUI_WIDTH / 2, 380);
        widgets.add(w);

        w = new ContinueButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);

        selectedWidget = w;
    }

    public class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x2898c7, 0x32bffa, 0x1e7194);
            setText(Assets.strings.get("LOAD OLD COMPETITION"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    public class ContinueButton extends Button {

        public ContinueButton() {
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 590, 180, 36);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("CONTINUE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.clearCompetition();
            game.setScreen(new LoadCompetition(game));
        }
    }

    public class AbortButton extends Button {

        public AbortButton() {
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
