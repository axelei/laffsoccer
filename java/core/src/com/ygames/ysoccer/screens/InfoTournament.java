package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.tournament.Tournament;
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

class InfoTournament extends GLScreen {

    private Tournament tournament;

    InfoTournament(GLGame game) {
        super(game);

        background = game.stateBackground;

        tournament = (Tournament) game.competition;
        Widget w;

        w = new TitleBar(tournament.name, game.stateColor.body);
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

        w = new AwayGoalsLabel();
        widgets.add(w);

        w = new AwayGoalsButton();
        widgets.add(w);

        w = new TeamsLabel();
        widgets.add(w);

        w = new SeededLabel();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setGeometry(game.gui.WIDTH / 2 - 470, 145, 236, 36);
            setColors(0x666666);
            setText(Assets.strings.get(tournament.getWeatherLabel()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SeasonStartButton extends Button {

        SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 145, 176, 36);
            setColors(0x666666);
            setText(Assets.strings.get(Month.getLabel(tournament.seasonStart)), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class SeasonSeparatorButton extends Button {

        SeasonSeparatorButton() {
            setGeometry(game.gui.WIDTH / 2 - 54, 145, 36, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class SeasonEndButton extends Button {

        SeasonEndButton() {
            setGeometry(game.gui.WIDTH / 2 - 16, 145, 176, 36);
            setColors(0x666666);
            setText(Assets.strings.get(Month.getLabel(tournament.seasonEnd)), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 145, 392, 36);
            setColors(0x666666);
            setText(Assets.strings.get(Pitch.names[tournament.pitchType.ordinal()]), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.weather == Competition.Weather.BY_PITCH_TYPE);
            setActive(false);
        }
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 170, 145, 140, 36);
            setColors(0x666666);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 145, 158, 36);
            setColors(0x666666);
            setText(Assets.strings.get(MatchSettings.getTimeLabel(tournament.time)), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 190, 244, 36);
            setColors(0x666666);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 224, 190, 52, 36);
            setColors(0x666666);
            setText(tournament.substitutions, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 170, 190, 94, 36);
            setColors(0x666666);
            setText(Assets.strings.get("SUBSTITUTES.FROM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 - 74, 190, 52, 36);
            setColors(0x666666);
            setText(tournament.benchSize, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AwayGoalsLabel extends Button {

        AwayGoalsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 12, 190, 206, 36);
            setColors(0x666666);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.hasTwoLegsRound());
            setActive(false);
        }
    }

    private class AwayGoalsButton extends Button {

        AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 196, 190, 274, 36);
            setColors(0x666666);
            setText(Assets.strings.get(tournament.getAwayGoalsLabel(tournament.awayGoals)), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.hasTwoLegsRound());
            setActive(false);
        }
    }

    private class TeamsLabel extends Label {

        TeamsLabel() {
            setText(Assets.strings.get("TEAMS"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 446, 260);
        }
    }

    private class SeededLabel extends Label {

        SeededLabel() {
            setText(Assets.strings.get("SEEDED"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 149, 260);
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
