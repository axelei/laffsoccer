package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

class MatchOptions extends GlScreen {

    MatchOptions(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_match_options.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        w = new WeatherEffectsLabel();
        widgets.add(w);
    }

    private class TitleButton extends Button {

        TitleButton() {
            setColors(0x536B90);
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get("MATCH OPTIONS"));
        }
    }

    private class WeatherEffectsLabel extends Button {

        WeatherEffectsLabel() {
            setColors(0x76683C);
            setGeometry(110, 190, 470, 36);
            setText(Assets.strings.get("WEATHER EFFECTS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
