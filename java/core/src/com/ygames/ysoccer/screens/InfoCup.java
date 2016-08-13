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

        w = new SubstitutesLabel();
        widgets.add(w);

        w = new SubstitutesButton();
        widgets.add(w);

        w = new BenchSizeLabel();
        widgets.add(w);

        w = new BenchSizeButton();
        widgets.add(w);

        w = new RoundsLabel();
        widgets.add(w);

        w = new RoundsButton();
        widgets.add(w);

        w = new AwayGoalsLabel();
        widgets.add(w);

        w = new AwayGoalsButton();
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

    class SubstitutesLabel extends Button {

        public SubstitutesLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 210, 305, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesButton extends Button {

        public SubstitutesButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 163, 210, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.substitutions, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeLabel extends Button {

        public BenchSizeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 5, 210, 305, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeButton extends Button {

        public BenchSizeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 312, 210, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.benchSize, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class RoundsLabel extends Button {

        public RoundsLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 255, 236, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("ROUNDS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class RoundsButton extends Button {

        public RoundsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 232, 255, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.rounds.size(), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class AwayGoalsLabel extends Button {

        public AwayGoalsLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 64, 255, 228, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class AwayGoalsButton extends Button {

        public AwayGoalsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 166, 255, 304, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(cup.getAwayGoalsLabel()), Font.Align.CENTER, Assets.font14);
            setVisible(cup.hasTwoLegsRound());
            setActive(false);
        }
    }
}