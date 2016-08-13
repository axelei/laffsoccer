package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Time;

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

        w = new SeasonStartButton();
        widgets.add(w);

        w = new SeasonSeparatorButton();
        widgets.add(w);

        w = new SeasonEndButton();
        widgets.add(w);

        w = new PitchTypeButton();
        widgets.add(w);

        w = new TimeLabel();
        widgets.add(w);

        w = new TimeButton();
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

    class SeasonStartButton extends Button {

        public SeasonStartButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 232, 165, 176, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.monthNames[cup.seasonStart]), Font.Align.CENTER, Assets.font14);
            setVisible(cup.bySeason);
            setActive(false);
        }
    }

    class SeasonSeparatorButton extends Button {

        public SeasonSeparatorButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 54, 165, 36, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setVisible(cup.bySeason);
            setActive(false);
        }
    }

    class SeasonEndButton extends Button {

        public SeasonEndButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 16, 165, 176, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.monthNames[cup.seasonEnd]), Font.Align.CENTER, Assets.font14);
            setVisible(cup.bySeason);
            setActive(false);
        }
    }

    class PitchTypeButton extends Button {

        public PitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 232, 165, 392, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Pitch.names[cup.pitchType]), Font.Align.CENTER, Assets.font14);
            setVisible(!cup.bySeason);
            setActive(false);
        }
    }

    class TimeLabel extends Button {

        public TimeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 170, 165, 140, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TimeButton extends Button {

        public TimeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 312, 165, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.names[cup.time]), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
