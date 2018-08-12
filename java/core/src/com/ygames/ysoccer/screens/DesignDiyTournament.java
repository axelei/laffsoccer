package com.ygames.ysoccer.screens;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Picture;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.math.Emath;

import static java.lang.Math.min;

class DesignDiyTournament extends GLScreen {

    Tournament tournament;
    private Widget seasonStartButton;
    private Widget seasonSeparatorButton;
    private Widget seasonEndButton;
    private Widget pitchTypeButton;
    private Widget substitutesButton;
    private Widget awayGoalsLabel;
    private Widget awayGoalsButton;

    private int[] roundTeams = {24, 16, 8, 4, 2, 0};
    private int[] roundGroups = {6, 0, 0, 0, 0, 0};
    private boolean[] roundSeeded = {false, false, false, false, false, false};
    private int[] roundLegs = {1, 1, 1, 1, 1, 1};
    private Round.ExtraTime[] roundsExtraTime = {Round.ExtraTime.ON, Round.ExtraTime.ON, Round.ExtraTime.ON, Round.ExtraTime.ON, Round.ExtraTime.ON, Round.ExtraTime.ON};
    private Round.Penalties[] roundsPenalties = {Round.Penalties.ON, Round.Penalties.ON, Round.Penalties.ON, Round.Penalties.ON, Round.Penalties.ON, Round.Penalties.ON};
    private int[] roundsPointsFaw = {2, 2, 2, 2, 2, 2};
    private int[] roundsPlayEachTeam = {1, 1, 1, 1, 1, 1};

    private Widget[] roundNumberLabels = new Widget[6];
    private Widget[] roundTeamsButtons = new Widget[6];
    private Widget[] roundGroupsButtons = new Widget[6];
    private Widget[] roundSeededButtons = new Widget[6];
    private Widget[] roundLegsButtons = new Widget[6];
    private Widget[] roundExtraTimeButtons = new Widget[6];
    private Widget[] roundPenaltiesButtons = new Widget[6];
    private Widget[] roundPointsFawButtons = new Widget[6];
    private Widget[] roundPlayEachTeamButtons = new Widget[6];
    private Widget[] roundShortArrowPictures = new Widget[5];

    DesignDiyTournament(GLGame game) {
        super(game);

        background = game.stateBackground;

        tournament = new Tournament();
        tournament.name = Assets.strings.get("DIY TOURNAMENT");
        tournament.category = Competition.Category.DIY_COMPETITION;

        Widget w;

        w = new TitleBar(Assets.strings.get("DESIGN DIY TOURNAMENT"), game.stateColor.body);
        widgets.add(w);

        w = new TournamentNameButton();
        widgets.add(w);

        w = new WeatherButton();
        widgets.add(w);

        w = new SeasonStartButton();
        widgets.add(w);
        seasonStartButton = w;

        w = new SeasonSeparatorButton();
        widgets.add(w);
        seasonSeparatorButton = w;

        w = new SeasonEndButton();
        widgets.add(w);
        seasonEndButton = w;

        w = new PitchTypeButton();
        widgets.add(w);
        pitchTypeButton = w;

        w = new TimeLabel();
        widgets.add(w);

        w = new TimeButton();
        widgets.add(w);

        w = new SubstitutesLabel();
        widgets.add(w);

        w = new SubstitutesButton();
        widgets.add(w);
        substitutesButton = w;

        w = new BenchSizeLabel();
        widgets.add(w);

        w = new BenchSizeButton();
        widgets.add(w);

        w = new AwayGoalsLabel();
        widgets.add(w);
        awayGoalsLabel = w;

        w = new AwayGoalsButton();
        widgets.add(w);
        awayGoalsButton = w;

        w = new TeamsLabel();
        widgets.add(w);

        w = new SeededLabel();
        widgets.add(w);

        w = new DescriptionLabel();
        widgets.add(w);

        // rounds
        for (int i = 0; i < 6; i++) {
            w = new RoundNumberLabel(i);
            widgets.add(w);
            roundNumberLabels[i] = w;

            w = new RoundTeamsButton(i);
            widgets.add(w);
            roundTeamsButtons[i] = w;

            w = new RoundGroupsButton(i);
            widgets.add(w);
            roundGroupsButtons[i] = w;

            w = new RoundSeededButton(i);
            widgets.add(w);
            roundSeededButtons[i] = w;

            w = new RoundLegsButton(i);
            widgets.add(w);
            roundLegsButtons[i] = w;

            w = new RoundExtraTimeButton(i);
            widgets.add(w);
            roundExtraTimeButtons[i] = w;

            w = new RoundPenaltiesButton(i);
            widgets.add(w);
            roundPenaltiesButtons[i] = w;

            w = new RoundPointsFawButton(i);
            widgets.add(w);
            roundPointsFawButtons[i] = w;

            w = new RoundPlayEachTeamButton(i);
            widgets.add(w);
            roundPlayEachTeamButtons[i] = w;

            if (i < 5) {
                w = new ShortArrowPicture(i);
                widgets.add(w);
                roundShortArrowPictures[i] = w;
            }
        }

        w = new OkButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class TournamentNameButton extends InputButton {

        TournamentNameButton() {
            setGeometry((game.gui.WIDTH - 940) / 2, 120, 940, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText(tournament.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onChanged() {
            tournament.name = text;
        }
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setGeometry(game.gui.WIDTH / 2 - 470, 165, 236, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            tournament.weather = Competition.Weather.values()[Emath.rotate(tournament.weather, Competition.Weather.BY_SEASON, Competition.Weather.BY_PITCH_TYPE, 1)];
            setDirty(true);
            seasonStartButton.setDirty(true);
            seasonSeparatorButton.setDirty(true);
            seasonEndButton.setDirty(true);
            pitchTypeButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(tournament.getWeatherLabel()));
        }
    }

    private class SeasonStartButton extends Button {

        SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 176, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateSeasonStart(1);
        }

        @Override
        public void onFire1Hold() {
            updateSeasonStart(1);
        }

        @Override
        public void onFire2Down() {
            updateSeasonStart(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSeasonStart(-1);
        }

        private void updateSeasonStart(int n) {
            tournament.seasonStart = Month.values()[Emath.rotate(tournament.seasonStart, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(tournament.seasonStart)));
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class SeasonSeparatorButton extends Button {

        SeasonSeparatorButton() {
            setGeometry(game.gui.WIDTH / 2 - 54, 165, 36, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class SeasonEndButton extends Button {

        SeasonEndButton() {
            setGeometry(game.gui.WIDTH / 2 - 16, 165, 176, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateSeasonEnd(1);
        }

        @Override
        public void onFire1Hold() {
            updateSeasonEnd(1);
        }

        @Override
        public void onFire2Down() {
            updateSeasonEnd(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSeasonEnd(-1);
        }

        private void updateSeasonEnd(int n) {
            tournament.seasonEnd = Month.values()[Emath.rotate(tournament.seasonEnd, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(tournament.seasonEnd)));
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 392, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updatePitchType(1);
        }

        @Override
        public void onFire1Hold() {
            updatePitchType(1);
        }

        @Override
        public void onFire2Down() {
            updatePitchType(-1);
        }

        @Override
        public void onFire2Hold() {
            updatePitchType(-1);
        }

        private void updatePitchType(int n) {
            tournament.pitchType = Pitch.Type.values()[Emath.rotate(tournament.pitchType.ordinal(), 0, Pitch.Type.RANDOM.ordinal(), n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Pitch.names[tournament.pitchType.ordinal()]));
            setVisible(tournament.weather == Competition.Weather.BY_PITCH_TYPE);
        }
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 170, 165, 140, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 165, 158, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateTime(1);
        }

        @Override
        public void onFire2Down() {
            updateTime(-1);
        }

        private void updateTime(int n) {
            tournament.time = MatchSettings.Time.values()[Emath.rotate(tournament.time, MatchSettings.Time.DAY, MatchSettings.Time.NIGHT, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(MatchSettings.getTimeLabel(tournament.time)));
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 210, 244, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 224, 210, 52, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateSubstitutes(1);
        }

        @Override
        public void onFire1Hold() {
            updateSubstitutes(1);
        }

        @Override
        public void onFire2Down() {
            updateSubstitutes(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSubstitutes(-1);
        }

        private void updateSubstitutes(int n) {
            tournament.substitutions = Emath.slide(tournament.substitutions, 2, tournament.benchSize, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(tournament.substitutions);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 170, 210, 94, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES.FROM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 - 74, 210, 52, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateBenchSize(1);
        }

        @Override
        public void onFire1Hold() {
            updateBenchSize(1);
        }

        @Override
        public void onFire2Down() {
            updateBenchSize(-1);
        }

        @Override
        public void onFire2Hold() {
            updateBenchSize(-1);
        }

        private void updateBenchSize(int n) {
            tournament.benchSize = Emath.slide(tournament.benchSize, 2, 12, n);
            tournament.substitutions = min(tournament.substitutions, tournament.benchSize);
            setDirty(true);
            substitutesButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(tournament.benchSize);
        }
    }

    private class AwayGoalsLabel extends Button {

        AwayGoalsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 12, 210, 206, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(false);
            for (int i = 0; i < 6; i++) {
                if (roundLegs[i] == 2) {
                    setVisible(true);
                    break;
                }
            }
        }
    }

    private class AwayGoalsButton extends Button {

        AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 196, 210, 274, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateAwayGoals(1);
        }

        @Override
        public void onFire2Down() {
            updateAwayGoals(-1);
        }

        private void updateAwayGoals(int n) {
            tournament.awayGoals = Competition.AwayGoals.values()[Emath.rotate(tournament.awayGoals.ordinal(), 0, 2, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(tournament.getAwayGoalsLabel(tournament.awayGoals)));
            setVisible(false);
            for (int i = 0; i < 6; i++) {
                if (roundLegs[i] == 2) {
                    setVisible(true);
                    break;
                }
            }
        }
    }

    private class TeamsLabel extends Label {

        TeamsLabel() {
            setText(Assets.strings.get("TEAMS"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 446, 280);
        }
    }

    private class SeededLabel extends Label {

        SeededLabel() {
            setText(Assets.strings.get("SEEDED"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 149, 280);
        }
    }

    private class DescriptionLabel extends Label {

        DescriptionLabel() {
            setText(Assets.strings.get("DESCRIPTION"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 + 182, 280);
        }
    }

    private class RoundNumberLabel extends Button {

        private int round;

        RoundNumberLabel(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 512, 299 + 58 * round, 40, 32);
            setText(round + 1, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1);
        }
    }

    private class RoundTeamsButton extends Button {

        private int round;

        RoundTeamsButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 470, 299 + 58 * round, 48, 32);
            setColors(0x90217a);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            incrementTeams();
        }

        @Override
        public void onFire1Hold() {
            incrementTeams();
        }

        private void incrementTeams() {
            int t = roundTeams[round];
            boolean found = false;
            while (!found) {
                t++;

                int maxTeams = 64;
                if (round == 5) {
                    maxTeams = 24;
                } else {
                    if (roundTeams[round + 1] > 0) {
                        maxTeams = min(maxTeams, 24 * roundTeams[round + 1]);
                    }
                }
                if (t > maxTeams) return;

                // should be smaller than previous round
                if (round > 0 && t >= roundTeams[round - 1]) return;

                // should be divisible in groups, each up to 24 teams
                for (int d = 1; d <= 8; d++) {
                    if (t % d == 0 && t / d <= 24) {
                        found = true;
                        break;
                    }
                }
            }

            // set new value
            roundTeams[round] = t;
            resetRoundGroups(round);
            setRoundButtonsDirty(round);

            // possibily reset previous round
            if (round > 0 && 2 * roundTeams[round] != roundTeams[round - 1]) {
                resetRoundGroups(round - 1);
                setRoundButtonsDirty(round - 1);
            }

            // possibly activate next round
            if (t > 2 && round < 5 && roundTeams[round + 1] == 0) {
                roundTeams[round + 1] = 1;
                setRoundButtonsDirty(round + 1);
            }
        }

        @Override
        public void onFire2Down() {
            decrementTeams();
        }

        @Override
        public void onFire2Hold() {
            decrementTeams();
        }

        private void decrementTeams() {
            int t = roundTeams[round];
            boolean found = false;
            while (!found) {
                t--;
                // at least 1 team
                if (t == 0) return;

                // should be greater than next round
                if (round < 5 && t <= roundTeams[round + 1]) return;

                // should be greater then groups of previous round
                if (round > 0 && t < roundGroups[round - 1]) return;

                // should be divisible in groups, each up to 24 teams
                for (int d = 1; d <= 8; d++) {
                    if (t % d == 0 && t / d <= 24) {
                        found = true;
                        break;
                    }
                }
            }

            // set new value
            roundTeams[round] = t;
            resetRoundGroups(round);
            setRoundButtonsDirty(round);

            // possibily reset previous round
            if (round > 0 && 2 * roundTeams[round] != roundTeams[round - 1]) {
                resetRoundGroups(round - 1);
                setRoundButtonsDirty(round - 1);
            }

            // possibly deactivate next round
            if (t == 2 && round < 5 && roundTeams[round + 1] == 1) {
                roundTeams[round + 1] = 0;
                setRoundButtonsDirty(round + 1);
            }
        }

        @Override
        public void refresh() {
            setText(roundTeams[round]);
            setVisible(roundTeams[round] > 0);
        }
    }

    private class RoundGroupsButton extends Button {

        private int round;

        RoundGroupsButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 420, 299 + 58 * round, 248, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            rotateGroups();
        }

        @Override
        public void onFire1Hold() {
            rotateGroups();
        }

        private void rotateGroups() {
            int groups = roundGroups[round];

            // search next value
            boolean found = false;
            do {
                int maxGroups;
                if (round == 5) {
                    maxGroups = 1;
                } else {
                    maxGroups = min(8, roundTeams[round + 1]);
                }
                groups = Emath.rotate(groups, 0, maxGroups, 1);

                if (groups == 0) {
                    // final
                    if (roundTeams[round] == 2) {
                        found = true;
                        break;
                    }
                    // knockout
                    if (round < 5) {
                        if (roundTeams[round] == 2 * roundTeams[round + 1]) {
                            found = true;
                            break;
                        }
                    }
                } else {
                    // round teams are divisible in groups from 2 to 24 teams
                    if (roundTeams[round] % groups == 0
                            && Emath.isIn(roundTeams[round] / groups, 2, 24)) {
                        found = true;
                        break;
                    }
                }

            } while (groups != roundGroups[round]);

            if (!found) return;

            // set new value
            roundGroups[round] = groups;
            setRoundButtonsDirty(round);
        }

        @Override
        public void refresh() {
            String key;
            if (roundGroups[round] == 0) {
                switch (roundTeams[round]) {
                    case 2:
                        key = "FINAL";
                        break;
                    case 4:
                        key = "SEMI-FINAL";
                        break;
                    case 8:
                        key = "QUARTER-FINAL";
                        break;
                    default:
                        key = "KNOCKOUT";
                }
            } else if (roundGroups[round] == 1) {
                key = "%n GROUP OF %m";
            } else {
                key = "%n GROUPS OF %m";
            }
            String label = Assets.strings.get(key);
            if (roundGroups[round] == 0) {
                setText(label);
            } else {
                setText(label
                        .replaceFirst("%n", "" + roundGroups[round])
                        .replaceFirst("%m", "" + (roundTeams[round] / roundGroups[round]))
                );
            }
            setVisible(roundTeams[round] > 1);
        }
    }

    private class RoundSeededButton extends Button {

        private int round;

        RoundSeededButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 170, 299 + 58 * round, 42, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            toggleSeeded();
        }

        @Override
        public void onFire1Hold() {
            toggleSeeded();
        }

        private void toggleSeeded() {
            roundSeeded[round] = !roundSeeded[round];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(roundSeeded[round] ? "*" : "-");
            setVisible(roundTeams[round] > 1);
        }
    }

    private class RoundLegsButton extends Button {

        private int round;

        RoundLegsButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 126, 299 + 58 * round, 138, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            rotateLegs();
        }

        @Override
        public void onFire2Down() {
            rotateLegs();
        }

        private void rotateLegs() {
            roundLegs[round] = Emath.rotate(roundLegs[round], 1, 2, 1);
            setDirty(true);
            awayGoalsLabel.setDirty(true);
            awayGoalsButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] == 0);
            if (visible) {
                setText(Assets.strings.get(roundLegs[round] == 1 ? "ONE LEG" : "TWO LEGS"));
            }
        }
    }

    private class RoundExtraTimeButton extends Button {

        private int round;

        RoundExtraTimeButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 + 14, 299 + 58 * round, 240, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            rotateExtraTime();
        }

        @Override
        public void onFire1Hold() {
            rotateExtraTime();
        }

        private void rotateExtraTime() {
            roundsExtraTime[round] = Round.ExtraTime.values()[Emath.rotate(roundsExtraTime[round].ordinal(), 0, 2, 1)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] == 0);
            if (visible) {
                setText(Assets.strings.get(Round.getExtraTimeLabel(roundsExtraTime[round])));
            }
        }
    }

    private class RoundPenaltiesButton extends Button {

        private int round;

        RoundPenaltiesButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 + 256, 299 + 58 * round, 240, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            rotatePenalties();
        }

        @Override
        public void onFire1Hold() {
            rotatePenalties();
        }

        private void rotatePenalties() {
            roundsPenalties[round] = Round.Penalties.values()[Emath.rotate(roundsPenalties[round].ordinal(), 0, 2, 1)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] == 0);
            if (visible) {
                setText(Assets.strings.get(Round.getPenaltiesLabel(roundsPenalties[round])));
            }
        }
    }

    private class RoundPointsFawButton extends Button {

        private int round;

        RoundPointsFawButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 126, 299 + 58 * round, 320, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            rotatePointsFaw();
        }

        @Override
        public void onFire1Hold() {
            rotatePointsFaw();
        }

        private void rotatePointsFaw() {
            roundsPointsFaw[round] = Emath.rotate(roundsPointsFaw[round], 2, 3, 1);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] > 0);
            if (visible) {
                setText(Assets.strings.get("%n POINTS FOR A WIN").replaceFirst("%n", "" + roundsPointsFaw[round]));
            }
        }
    }

    private class RoundPlayEachTeamButton extends Button {

        private int round;

        RoundPlayEachTeamButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 + 196, 299 + 58 * round, 300, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            rotatePlayEachTeam();
        }

        @Override
        public void onFire1Hold() {
            rotatePlayEachTeam();
        }

        private void rotatePlayEachTeam() {
            roundsPlayEachTeam[round] = Emath.rotate(roundsPlayEachTeam[round], 1, 4, 1);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] > 0);
            if (visible) {
                setText(Assets.strings.get("PLAY EACH TEAM ×%n").replaceFirst("%n", "" + roundsPlayEachTeam[round]));
            }
        }
    }

    private class ShortArrowPicture extends Picture {

        private int round;

        ShortArrowPicture(int round) {
            this.round = round;
            setTextureRegion(Assets.shortArrow);
            setPosition(game.gui.WIDTH / 2 - 446, 343 + 58 * round);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 2);
        }
    }

    private void resetRoundGroups(int round) {

        // final
        if (roundTeams[round] == 2) {
            roundGroups[round] = 0;
            return;
        }

        // knockout
        if (round < 5 && roundTeams[round] == 2 * roundTeams[round + 1]) {
            roundGroups[round] = 0;
            return;
        }

        for (int groups = 1; groups <= 8; groups++) {
            // round teams are divisible in groups up to 24 teams
            if (roundTeams[round] % groups == 0 && roundTeams[round] / groups <= 24) {
                roundGroups[round] = groups;
                return;
            }
        }
        throw new GdxRuntimeException("Failed to reset groups");
    }

    private void setRoundButtonsDirty(int round) {
        roundNumberLabels[round].setDirty(true);
        roundTeamsButtons[round].setDirty(true);
        roundGroupsButtons[round].setDirty(true);
        roundSeededButtons[round].setDirty(true);
        roundLegsButtons[round].setDirty(true);
        roundExtraTimeButtons[round].setDirty(true);
        roundPenaltiesButtons[round].setDirty(true);
        roundPointsFawButtons[round].setDirty(true);
        roundPlayEachTeamButtons[round].setDirty(true);
        if (round < 5) {
            roundShortArrowPictures[round].setDirty(true);
        }
    }

    private class OkButton extends Button {

        OkButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry(game.gui.WIDTH / 2 - 180 - 2, 660, 180, 38);
            setText(Assets.strings.get("OK"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            navigation.folder = Assets.teamsRootFolder;
            navigation.competition = tournament;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColors(0xC8000E);
            setGeometry(game.gui.WIDTH / 2 + 2, 660, 180, 36);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
