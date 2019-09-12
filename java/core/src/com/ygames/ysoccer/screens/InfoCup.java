package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;

class InfoCup extends GLScreen {

    private Cup cup;

    InfoCup(GLGame game) {
        super(game);

        background = game.stateBackground;

        cup = (Cup) game.competition;

        Widget w;

        w = new TitleBar(cup.name, game.stateColor.body);
        widgets.add(w);

        w = new WeatherButton();
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

    private class WeatherButton extends Button {

        WeatherButton() {
            setGeometry(game.gui.WIDTH / 2 - 470, 165, 236, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(cup.getWeatherLabel()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SeasonStartButton extends Button {

        SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 176, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Month.getLabel(cup.seasonStart)), Font.Align.CENTER, Assets.font14);
            setVisible(cup.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class SeasonSeparatorButton extends Button {

        SeasonSeparatorButton() {
            setGeometry(game.gui.WIDTH / 2 - 54, 165, 36, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setVisible(cup.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class SeasonEndButton extends Button {

        SeasonEndButton() {
            setGeometry(game.gui.WIDTH / 2 - 16, 165, 176, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Month.getLabel(cup.seasonEnd)), Font.Align.CENTER, Assets.font14);
            setVisible(cup.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 392, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Pitch.names[cup.pitchType.ordinal()]), Font.Align.CENTER, Assets.font14);
            setVisible(cup.weather == Competition.Weather.BY_PITCH_TYPE);
            setActive(false);
        }
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 170, 165, 140, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 165, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(MatchSettings.getTimeLabel(cup.time)), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 210, 305, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 163, 210, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.substitutions, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 5, 210, 305, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 210, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.benchSize, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundsLabel extends Button {

        RoundsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 255, 236, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("ROUNDS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundsButton extends Button {

        RoundsButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 255, 158, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(cup.rounds.size(), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AwayGoalsLabel extends Button {

        AwayGoalsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 64, 255, 228, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AwayGoalsButton extends Button {

        AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 166, 255, 304, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(cup.getAwayGoalsLabel(cup.awayGoals)), Font.Align.CENTER, Assets.font14);
            setVisible(cup.hasTwoLegsRound());
            setActive(false);
        }
    }

    private class TeamsLabel extends Label {

        TeamsLabel() {
            setText(Assets.strings.get("TEAMS"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 186, 325);
        }
    }

    private class DescriptionLabel extends Label {

        DescriptionLabel() {
            setText(Assets.strings.get("DESCRIPTION"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 + 115, 325);
        }
    }

    private class RoundNameLabel extends Button {

        RoundNameLabel(int round) {
            setGeometry(game.gui.WIDTH / 2 - 470, 344 + 34 * round, 248, 32);
            if (round == cup.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText("", Font.Align.LEFT, Assets.font14);
            setActive(false);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.gettext(cup.rounds.get(round).name));
            }
        }
    }

    private class RoundTeamsLabel extends Button {

        RoundTeamsLabel(int round) {
            setGeometry(game.gui.WIDTH / 2 - 212, 344 + 34 * round, 50, 32);
            if (round == cup.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(cup.getRoundTeams(round), Font.Align.CENTER, Assets.font14);
            setActive(false);
            setVisible(round < cup.rounds.size());
        }
    }

    private class RoundLegsButton extends Button {

        RoundLegsButton(int round) {
            setGeometry(game.gui.WIDTH / 2 - 152, 344 + 34 * round, 138, 32);
            if (round == cup.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText("", Font.Align.CENTER, Assets.font14);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getLegsLabel()));
            }
            setActive(false);
        }
    }

    private class RoundExtraTimeButton extends Button {

        RoundExtraTimeButton(int round) {
            setGeometry(game.gui.WIDTH / 2 - 12, 344 + 34 * round, 240, 32);
            if (round == cup.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText("", Font.Align.CENTER, Assets.font14);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getExtraTimeLabel()));
            }
            setActive(false);
        }
    }

    private class RoundPenaltiesButton extends Button {

        RoundPenaltiesButton(int round) {
            setGeometry(game.gui.WIDTH / 2 + 230, 344 + 34 * round, 240, 32);
            if (round == cup.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText("", Font.Align.CENTER, Assets.font14);
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getPenaltiesLabel()));
            }
            setActive(false);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
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
