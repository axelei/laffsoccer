package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Time;

public class InfoCup extends GLScreen {

    Cup cup;

    public InfoCup(GLGame game) {
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

        w = new TeamsLabel();
        widgets.add(w);

        w = new DescriptionLabel();
        widgets.add(w);

        // rounds
        for (int i = 0; i < 6; i++) {
            w = new RoundNameLabel(i);
            widgets.add(w);

            w = new RoundTeamsLabel(i);
            widgets.add(w);

            w = new RoundLegsButton(i);
            widgets.add(w);

            w = new RoundExtraTimeButton(i);
            widgets.add(w);

            w = new RoundPenaltiesButton(i);
            widgets.add(w);
        }

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.gui.WIDTH - 680) / 2, 30, 680, 40);
            setColors(game.stateColor);
            setText(cup.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SeasonPitchTypeButton extends Button {

        public SeasonPitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 470, 165, 236, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(cup.getBySeasonLabel()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SeasonStartButton extends Button {

        public SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 176, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.monthNames[cup.seasonStart]), Font.Align.CENTER, Assets.font14);
            setVisible(cup.bySeason);
            setActive(false);
        }
    }

    class SeasonSeparatorButton extends Button {

        public SeasonSeparatorButton() {
            setGeometry(game.gui.WIDTH / 2 - 54, 165, 36, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setVisible(cup.bySeason);
            setActive(false);
        }
    }

    class SeasonEndButton extends Button {

        public SeasonEndButton() {
            setGeometry(game.gui.WIDTH / 2 - 16, 165, 176, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.monthNames[cup.seasonEnd]), Font.Align.CENTER, Assets.font14);
            setVisible(cup.bySeason);
            setActive(false);
        }
    }

    class PitchTypeButton extends Button {

        public PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 392, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Pitch.names[cup.pitchType.ordinal()]), Font.Align.CENTER, Assets.font14);
            setVisible(!cup.bySeason);
            setActive(false);
        }
    }

    class TimeLabel extends Button {

        public TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 170, 165, 140, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TimeButton extends Button {

        public TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 165, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.names[cup.time]), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesLabel extends Button {

        public SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 210, 305, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesButton extends Button {

        public SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 163, 210, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.substitutions, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeLabel extends Button {

        public BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 5, 210, 305, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeButton extends Button {

        public BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 210, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.benchSize, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class RoundsLabel extends Button {

        public RoundsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 255, 236, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("ROUNDS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class RoundsButton extends Button {

        public RoundsButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 255, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.rounds.size(), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class AwayGoalsLabel extends Button {

        public AwayGoalsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 64, 255, 228, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class AwayGoalsButton extends Button {

        public AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 166, 255, 304, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(cup.getAwayGoalsLabel()), Font.Align.CENTER, Assets.font14);
            setVisible(cup.hasTwoLegsRound());
            setActive(false);
        }
    }

    class TeamsLabel extends Label {

        public TeamsLabel() {
            setText(Assets.strings.get("TEAMS"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 186, 325);
        }
    }

    class DescriptionLabel extends Label {

        public DescriptionLabel() {
            setText(Assets.strings.get("DESCRIPTION"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 + 115, 325);
        }
    }

    class RoundNameLabel extends Button {

        public RoundNameLabel(int round) {
            setGeometry(game.gui.WIDTH / 2 - 470, 344 + 34 * round, 248, 32);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.getRoundName(round)));
            }
        }
    }

    class RoundTeamsLabel extends Button {

        public RoundTeamsLabel(int round) {
            setGeometry(game.gui.WIDTH / 2 - 212, 344 + 34 * round, 50, 32);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.getRoundTeams(round), Font.Align.CENTER, Assets.font14);
            setActive(false);
            setVisible(round < cup.rounds.size());
        }
    }

    class RoundLegsButton extends Button {

        public RoundLegsButton(int round) {
            setGeometry(game.gui.WIDTH / 2 - 152, 344 + 34 * round, 138, 32);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("", Font.Align.CENTER, Assets.font14);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getLegsLabel()));
            }
            setActive(false);
        }
    }

    class RoundExtraTimeButton extends Button {

        public RoundExtraTimeButton(int round) {
            setGeometry(game.gui.WIDTH / 2 - 12, 344 + 34 * round, 240, 32);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("", Font.Align.CENTER, Assets.font14);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getExtraTimeLabel()));
            }
            setActive(false);
        }
    }

    class RoundPenaltiesButton extends Button {

        public RoundPenaltiesButton(int round) {
            setGeometry(game.gui.WIDTH / 2 + 230, 344 + 34 * round, 240, 32);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("", Font.Align.CENTER, Assets.font14);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getPenaltiesLabel()));
            }
            setActive(false);
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }
}
