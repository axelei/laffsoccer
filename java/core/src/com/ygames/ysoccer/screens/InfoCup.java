package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class InfoCup extends GlScreen {

    Cup cup;

    public InfoCup(GlGame game) {
        super(game);

        background = game.stateBackground;

        cup = (Cup) game.competition;

        Widget w;

        w = new TitleBar();
        widgets.add(w);

        w = new SeasonPitchTypeButton();
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 680) / 2, 30, 680, 40);
            setColors(game.stateColor);
            setText(cup.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SeasonPitchTypeButton extends Button {

        public SeasonPitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 165, 236, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(cup.getBySeasonLabel()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
