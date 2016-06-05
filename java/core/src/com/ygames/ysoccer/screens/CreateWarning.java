package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

public class CreateWarning extends GlScreen {

    public CreateWarning(GlGame game, Competition.Category createCategory) {
        super(game);

        game.state = GlGame.State.COMPETITION;
        game.stateBackground = new Image("images/backgrounds/menu_competition.jpg");
        game.stateColor.set(0x376E2F, 0x4E983F, 0x214014);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(createCategory);
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

    }

    public class TitleBar extends Button {

        public TitleBar(Competition.Category createCategory) {
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            String label = Competition.getCategoryLabel(createCategory);
            setText(Assets.strings.get(label), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
