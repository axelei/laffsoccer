package com.ygames.ysoccer.screens;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.tournament.groups.Groups;
import com.ygames.ysoccer.competitions.tournament.knockout.Knockout;
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
import com.ygames.ysoccer.framework.EMath;

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

    private int[] roundTeams = {24, 16, 8, 4, 2, 1, 0};
    private int[] roundGroups = {6, 0, 0, 0, 0, 0};
    private boolean[] roundSeeded = {false, false, false, false, false, false};

    private Knockout[] knockouts = {new Knockout(), new Knockout(), new Knockout(), new Knockout(), new Knockout(), new Knockout()};
    private Groups[] groups = {new Groups(), new Groups(), new Groups(), new Groups(), new Groups(), new Groups()};

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
    private Widget[] roundQualificationLabels = new Widget[6];

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
            if (i == 0) setSelectedWidget(w);

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

            w = new RoundPointsForAWinButton(i);
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

            w = new RoundQualificationLabel(i);
            widgets.add(w);
            roundQualificationLabels[i] = w;
        }

        w = new OkButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class TournamentNameButton extends InputButton {

        TournamentNameButton() {
            setGeometry((game.gui.WIDTH - 940) / 2, 100, 940, 36);
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
            setGeometry(game.gui.WIDTH / 2 - 470, 145, 236, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            tournament.weather = Competition.Weather.values()[EMath.rotate(tournament.weather, Competition.Weather.BY_SEASON, Competition.Weather.BY_PITCH_TYPE, 1)];
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
            setGeometry(game.gui.WIDTH / 2 - 232, 145, 176, 36);
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
            tournament.seasonStart = Month.values()[EMath.rotate(tournament.seasonStart, Month.JANUARY, Month.DECEMBER, n)];
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
            setGeometry(game.gui.WIDTH / 2 - 54, 145, 36, 36);
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
            setGeometry(game.gui.WIDTH / 2 - 16, 145, 176, 36);
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
            tournament.seasonEnd = Month.values()[EMath.rotate(tournament.seasonEnd, Month.JANUARY, Month.DECEMBER, n)];
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
            setGeometry(game.gui.WIDTH / 2 - 232, 145, 392, 36);
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
            tournament.pitchType = Pitch.Type.values()[EMath.rotate(tournament.pitchType, Pitch.Type.FROZEN, Pitch.Type.RANDOM, n)];
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
            setGeometry(game.gui.WIDTH / 2 + 170, 145, 140, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 145, 158, 36);
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
            tournament.time = MatchSettings.Time.values()[EMath.rotate(tournament.time, MatchSettings.Time.DAY, MatchSettings.Time.NIGHT, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(MatchSettings.getTimeLabel(tournament.time)));
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 190, 244, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 224, 190, 52, 36);
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
            tournament.substitutions = EMath.slide(tournament.substitutions, 2, tournament.benchSize, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(tournament.substitutions);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 170, 190, 94, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES.FROM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 - 74, 190, 52, 36);
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
            tournament.benchSize = EMath.slide(tournament.benchSize, 2, 12, n);
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
            setGeometry(game.gui.WIDTH / 2 - 12, 190, 206, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(false);
            for (int i = 0; i < 6; i++) {
                if (roundGroups[i] == 0 && knockouts[i].numberOfLegs == 2) {
                    setVisible(true);
                    break;
                }
            }
        }
    }

    private class AwayGoalsButton extends Button {

        AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 196, 190, 274, 36);
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
            tournament.awayGoals = Competition.AwayGoals.values()[EMath.rotate(tournament.awayGoals.ordinal(), 0, 2, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(tournament.getAwayGoalsLabel(tournament.awayGoals)));
            setVisible(false);
            for (int i = 0; i < 6; i++) {
                if (roundGroups[i] == 0 && knockouts[i].numberOfLegs == 2) {
                    setVisible(true);
                    break;
                }
            }
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

    private class DescriptionLabel extends Label {

        DescriptionLabel() {
            setText(Assets.strings.get("DESCRIPTION"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 + 182, 260);
        }
    }

    private class RoundNumberLabel extends Label {

        private int round;

        RoundNumberLabel(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 512, 280 + 62 * round, 40, 32);
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
            setGeometry(game.gui.WIDTH / 2 - 470, 280 + 62 * round, 48, 32);
            setColor(0x90217a);
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
            int teams = roundTeams[round];
            boolean found = false;
            while (!found) {
                teams++;

                int maxTeams = 64;
                if (round == 5) {
                    maxTeams = 24;
                } else {
                    if (roundTeams[round + 1] > 0) {
                        maxTeams = min(maxTeams, 24 * roundTeams[round + 1]);
                    }
                }
                if (teams > maxTeams) return;

                // should be smaller than previous round
                if (round > 0 && teams >= roundTeams[round - 1]) return;

                // should be divisible in groups, each up to 24 teams
                int maxGroups = 8;
                if (roundTeams[round + 1] > 0) {
                    maxGroups = min(maxGroups, roundTeams[round + 1]);
                }
                for (int d = 1; d <= maxGroups; d++) {
                    if (teams % d == 0 && teams / d <= 24) {
                        found = true;
                        break;
                    }
                }
            }

            // set new value
            roundTeams[round] = teams;

            // possibly activate next round
            if (teams > 1 && roundTeams[round + 1] == 0) {
                roundTeams[round + 1] = 1;
            }

            resetRoundGroups(round);

            // possibly reset previous round
            if (invalidKnockout(round)) {
                resetRoundGroups(round - 1);
            }

            setRoundButtonsDirty(round);
            if (round > 0) {
                setRoundButtonsDirty(round - 1);
            }
            if (round < 5) {
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
            int teams = roundTeams[round];
            boolean found = false;
            while (!found) {
                teams--;
                // min 2 teams in the first round
                if (round == 0 && teams < 2) return;

                // min 1 team
                if (teams == 0) return;

                // should be greater than next round
                if (teams > 1 && teams <= roundTeams[round + 1]) return;

                // should be greater then groups of previous round
                if (round > 0 && teams < roundGroups[round - 1]) return;

                // should be divisible in groups, each up to 24 teams
                int maxGroups = 8;
                if (roundTeams[round + 1] > 0) {
                    maxGroups = min(maxGroups, roundTeams[round + 1]);
                }
                for (int d = 1; d <= maxGroups; d++) {
                    if (teams % d == 0 && teams / d <= 24) {
                        found = true;
                        break;
                    }
                }
            }

            // set new value
            roundTeams[round] = teams;

            // possibly reset previous round
            if (invalidKnockout(round)) {
                resetRoundGroups(round - 1);
            }

            resetRoundGroups(round);

            // possibly deactivate next round
            if (teams == 1 && roundTeams[round + 1] == 1) {
                roundTeams[round + 1] = 0;
            }

            setRoundButtonsDirty(round);
            if (round > 0) {
                setRoundButtonsDirty(round - 1);
            }
            if (round < 5) {
                setRoundButtonsDirty(round + 1);
            }
        }

        @Override
        public void refresh() {
            setText(roundTeams[round]);
            setVisible(round == 0 || roundTeams[round - 1] > 2);
        }
    }

    private class RoundGroupsButton extends Button {

        private int round;

        RoundGroupsButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 420, 280 + 62 * round, 248, 32);
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
            int maxGroups = min(roundTeams[round + 1], 8);

            // search next value
            boolean found = false;
            do {
                groups = EMath.rotate(groups, 0, maxGroups, 1);

                if (groups == 0) {
                    // knockouts
                    if (roundTeams[round] == 2 * roundTeams[round + 1]) {
                        found = true;
                        break;
                    }
                } else {
                    // round teams are divisible in groups from 2 to 24 teams
                    if (roundTeams[round] % groups == 0
                            && EMath.isIn(roundTeams[round] / groups, 2, 24)) {
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
            setGeometry(game.gui.WIDTH / 2 - 170, 280 + 62 * round, 42, 32);
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
            setGeometry(game.gui.WIDTH / 2 - 126, 280 + 62 * round, 138, 32);
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
            knockouts[round].numberOfLegs = EMath.rotate(knockouts[round].numberOfLegs, 1, 2, 1);
            setDirty(true);
            awayGoalsLabel.setDirty(true);
            awayGoalsButton.setDirty(true);
            roundQualificationLabels[round].setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] == 0);
            if (visible) {
                setText(Assets.strings.get(knockouts[round].numberOfLegs == 1 ? "ONE LEG" : "TWO LEGS"));
            }
        }
    }

    private class RoundExtraTimeButton extends Button {

        private int round;

        RoundExtraTimeButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 + 14, 280 + 62 * round, 240, 32);
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
            knockouts[round].extraTime = Round.ExtraTime.values()[EMath.rotate(knockouts[round].extraTime.ordinal(), 0, 2, 1)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] == 0);
            if (visible) {
                setText(Assets.strings.get(Round.getExtraTimeLabel(knockouts[round].extraTime)));
            }
        }
    }

    private class RoundPenaltiesButton extends Button {

        private int round;

        RoundPenaltiesButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 + 256, 280 + 62 * round, 240, 32);
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
            knockouts[round].penalties = Round.Penalties.values()[EMath.rotate(knockouts[round].penalties.ordinal(), 0, 2, 1)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] == 0);
            if (visible) {
                setText(Assets.strings.get(Round.getPenaltiesLabel(knockouts[round].penalties)));
            }
        }
    }

    private class RoundPointsForAWinButton extends Button {

        private int round;

        RoundPointsForAWinButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 126, 280 + 62 * round, 310, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            rotatePointsForAWin();
        }

        @Override
        public void onFire1Hold() {
            rotatePointsForAWin();
        }

        private void rotatePointsForAWin() {
            groups[round].pointsForAWin = EMath.rotate(groups[round].pointsForAWin, 2, 3, 1);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] > 0);
            if (visible) {
                setText(Assets.strings.get("%n POINTS FOR A WIN").replaceFirst("%n", "" + groups[round].pointsForAWin));
            }
        }
    }

    private class RoundPlayEachTeamButton extends Button {

        private int round;

        RoundPlayEachTeamButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 + 186, 280 + 62 * round, 310, 32);
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
            groups[round].rounds = EMath.rotate(groups[round].rounds, 1, 4, 1);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 1 && roundGroups[round] > 0);
            if (visible) {
                setText(Assets.strings.get("PLAY EACH TEAM ×%n").replaceFirst("%n", "" + groups[round].rounds));
            }
        }
    }

    private class ShortArrowPicture extends Picture {

        private int round;

        ShortArrowPicture(int round) {
            this.round = round;
            setTextureRegion(Assets.shortArrow);
            setPosition(game.gui.WIDTH / 2 - 446, 326 + 62 * round);
        }

        @Override
        public void refresh() {
            setVisible(roundTeams[round] > 2);
        }
    }

    private class RoundQualificationLabel extends Label {

        private int round;

        RoundQualificationLabel(int round) {
            this.round = round;
            setPosition(game.gui.WIDTH / 2, 326 + 62 * round);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void refresh() {
            int teams = roundTeams[round];
            setVisible(teams > 1);

            String label;
            int groups = roundGroups[round];
            switch (groups) {
                case 0:
                    if (teams == 2) {
                        if (knockouts[round].numberOfLegs == 1) {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNER WINS TOURNAMENT");
                        } else {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNER ON AGGREGATE WINS TOURNAMENT");
                        }
                    } else {
                        if (knockouts[round].numberOfLegs == 1) {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNERS QUALIFY");
                        } else {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNERS ON AGGREGATE QUALIFY");
                        }
                    }
                    break;

                case 1:
                    if (roundTeams[round + 1] == 1) {
                        label = Assets.strings.get("TOURNAMENT.GROUP WINNER WINS TOURNAMENT");
                    } else {
                        label = Assets.strings.get("TOURNAMENT.TOP %n IN GROUP QUALIFY")
                                .replaceFirst("%n", "" + roundTeams[round + 1]);
                    }
                    break;

                default:
                    int nextRoundTeams = roundTeams[round + 1];
                    int runnersUp = nextRoundTeams % groups;
                    switch (runnersUp) {
                        case 0:
                            if (nextRoundTeams / groups == 1) {
                                label = Assets.strings.get("TOURNAMENT.WINNERS OF EACH GROUP QUALIFY");
                            } else {
                                label = Assets.strings.get("TOURNAMENT.TOP %n IN EACH GROUP QUALIFY")
                                        .replaceFirst("%n", "" + nextRoundTeams / groups);
                            }
                            break;

                        case 1:
                            if (nextRoundTeams / groups == 1) {
                                label = Assets.strings.get("TOURNAMENT.WINNERS OF EACH GROUP AND BEST RUNNER-UP QUALIFIES");
                            } else {
                                label = Assets.strings.get("TOURNAMENT.TOP %n IN EACH GROUP AND BEST RUNNER-UP QUALIFY")
                                        .replaceFirst("%n", "" + nextRoundTeams / groups);
                            }
                            break;

                        default:
                            if (nextRoundTeams / groups == 1) {
                                label = Assets.strings.get("TOURNAMENT.WINNERS OF EACH GROUP AND BEST %n RUNNERS-UP QUALIFY")
                                        .replaceFirst("%n", "" + runnersUp);
                            } else {
                                label = Assets.strings.get("TOURNAMENT.TOP %n IN EACH GROUP AND BEST %m RUNNERS-UP QUALIFY")
                                        .replaceFirst("%n", "" + nextRoundTeams / groups)
                                        .replaceFirst("%m", "" + runnersUp);
                            }
                            break;
                    }
                    break;
            }
            setText("(" + label + ")");
        }
    }

    private boolean invalidKnockout(int round) {
        return round > 0
                && roundGroups[round - 1] == 0
                && 2 * roundTeams[round] != roundTeams[round - 1];
    }

    private void resetRoundGroups(int round) {

        // knockouts
        if (roundTeams[round] == 2 * roundTeams[round + 1]) {
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
        roundQualificationLabels[round].setDirty(true);
        awayGoalsLabel.setDirty(true);
        awayGoalsButton.setDirty(true);
    }

    private class OkButton extends Button {

        OkButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry(game.gui.WIDTH / 2 - 180 - 2, 660, 180, 38);
            setText(Assets.strings.get("OK"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            int round = 0;
            while (roundTeams[round] > 1) {
                if (roundGroups[round] == 0) {
                    knockouts[round].numberOfTeams = roundTeams[round];
                    knockouts[round].seeded = roundSeeded[round];
                    tournament.addRound(knockouts[round]);
                } else {
                    groups[round].numberOfTeams = roundTeams[round];
                    groups[round].seeded = roundSeeded[round];
                    groups[round].createGroups(roundGroups[round]);
                    tournament.addRound(groups[round]);
                }
                round++;
            }
            navigation.folder = Assets.teamsRootFolder;
            navigation.competition = tournament;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColor(0xC8000E);
            setGeometry(game.gui.WIDTH / 2 + 2, 660, 180, 36);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
