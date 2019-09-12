package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.tournament.Round;
import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.competitions.tournament.groups.Groups;
import com.ygames.ysoccer.competitions.tournament.knockout.Knockout;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Picture;
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

        w = new DescriptionLabel();
        widgets.add(w);

        // rounds
        for (int i = 0; i < tournament.rounds.size(); i++) {
            w = new RoundNumberLabel(i);
            widgets.add(w);

            w = new RoundTeamsButton(i);
            widgets.add(w);

            w = new RoundGroupsButton(i);
            widgets.add(w);

            w = new RoundSeededButton(i);
            widgets.add(w);

            Round round = tournament.rounds.get(i);
            switch (round.type) {
                case KNOCKOUT:
                    Knockout knockout = (Knockout) round;

                    w = new RoundLegsButton(i, knockout);
                    widgets.add(w);

                    w = new RoundExtraTimeButton(i, knockout);
                    widgets.add(w);

                    w = new RoundPenaltiesButton(i, knockout);
                    widgets.add(w);
                    break;

                case GROUPS:
                    Groups groups = (Groups) round;

                    w = new RoundPointsForAWinButton(i, groups);
                    widgets.add(w);

                    w = new RoundPlayEachTeamButton(i, groups);
                    widgets.add(w);
                    break;
            }
            if (i < tournament.rounds.size() - 1) {
                w = new ShortArrowPicture(i);
                widgets.add(w);
            }

            w = new RoundQualificationLabel(i);
            widgets.add(w);
        }

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setGeometry(game.gui.WIDTH / 2 - 470, 145, 236, 36);
            setColor(0x666666);
            setText(Assets.strings.get(tournament.getWeatherLabel()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SeasonStartButton extends Button {

        SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 145, 176, 36);
            setColor(0x666666);
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
            setColor(0x666666);
            setText(Assets.strings.get(Month.getLabel(tournament.seasonEnd)), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 145, 392, 36);
            setColor(0x666666);
            setText(Assets.strings.get(Pitch.names[tournament.pitchType.ordinal()]), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.weather == Competition.Weather.BY_PITCH_TYPE);
            setActive(false);
        }
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 170, 145, 140, 36);
            setColor(0x666666);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 145, 158, 36);
            setColor(0x666666);
            setText(Assets.strings.get(MatchSettings.getTimeLabel(tournament.time)), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 190, 244, 36);
            setColor(0x666666);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 224, 190, 52, 36);
            setColor(0x666666);
            setText(tournament.substitutions, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 170, 190, 94, 36);
            setColor(0x666666);
            setText(Assets.strings.get("SUBSTITUTES.FROM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 - 74, 190, 52, 36);
            setColor(0x666666);
            setText(tournament.benchSize, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AwayGoalsLabel extends Button {

        AwayGoalsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 12, 190, 206, 36);
            setColor(0x666666);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setVisible(tournament.hasTwoLegsRound());
            setActive(false);
        }
    }

    private class AwayGoalsButton extends Button {

        AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 196, 190, 274, 36);
            setColor(0x666666);
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

    private class DescriptionLabel extends Label {

        DescriptionLabel() {
            setText(Assets.strings.get("DESCRIPTION"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 + 182, 260);
        }
    }

    private class RoundNumberLabel extends Label {

        RoundNumberLabel(int round) {
            setGeometry(game.gui.WIDTH / 2 - 512, 280 + 62 * round, 40, 32);
            setText(round + 1, Font.Align.CENTER, Assets.font14);
        }
    }

    private class RoundTeamsButton extends Button {

        RoundTeamsButton(int round) {
            setGeometry(game.gui.WIDTH / 2 - 470, 280 + 62 * round, 48, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(tournament.rounds.get(round).numberOfTeams, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundGroupsButton extends Button {

        RoundGroupsButton(int round) {
            setGeometry(game.gui.WIDTH / 2 - 420, 280 + 62 * round, 248, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText("", Font.Align.CENTER, Assets.font14);
            int teams = tournament.rounds.get(round).numberOfTeams;
            String key;
            switch (tournament.rounds.get(round).type) {
                case KNOCKOUT:
                    switch (teams) {
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
                    setText(Assets.strings.get(key));
                    break;

                case GROUPS:
                    int groups = ((Groups) tournament.rounds.get(round)).groups.size();
                    if (groups == 1) {
                        key = "%n GROUP OF %m";
                    } else {
                        key = "%n GROUPS OF %m";
                    }
                    setText(Assets.strings.get(key)
                            .replaceFirst("%n", "" + groups)
                            .replaceFirst("%m", "" + (teams / groups))
                    );
                    break;
            }
            setActive(false);
        }
    }

    private class RoundSeededButton extends Button {

        RoundSeededButton(int round) {
            setGeometry(game.gui.WIDTH / 2 - 170, 280 + 62 * round, 42, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(tournament.rounds.get(round).seeded ? "*" : "-", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundLegsButton extends Button {

        RoundLegsButton(int round, Knockout knockout) {
            setGeometry(game.gui.WIDTH / 2 - 126, 280 + 62 * round, 138, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(Assets.strings.get(knockout.numberOfLegs == 1 ? "ONE LEG" : "TWO LEGS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundExtraTimeButton extends Button {

        RoundExtraTimeButton(int round, Knockout knockout) {
            setGeometry(game.gui.WIDTH / 2 + 14, 280 + 62 * round, 240, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(Assets.strings.get(Round.getExtraTimeLabel(knockout.extraTime)), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundPenaltiesButton extends Button {

        RoundPenaltiesButton(int round, Knockout knockout) {
            setGeometry(game.gui.WIDTH / 2 + 256, 280 + 62 * round, 240, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(Assets.strings.get(Round.getPenaltiesLabel(knockout.penalties)), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundPointsForAWinButton extends Button {

        RoundPointsForAWinButton(int round, Groups groups) {
            setGeometry(game.gui.WIDTH / 2 - 126, 280 + 62 * round, 310, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(Assets.strings.get("%n POINTS FOR A WIN").replaceFirst("%n", "" + groups.pointsForAWin), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundPlayEachTeamButton extends Button {

        RoundPlayEachTeamButton(int round, Groups groups) {
            setGeometry(game.gui.WIDTH / 2 + 186, 280 + 62 * round, 310, 32);
            if (round == tournament.currentRound) {
                setColor(0x444444);
            } else {
                setColor(0x666666);
            }
            setText(Assets.strings.get("PLAY EACH TEAM ×%n").replaceFirst("%n", "" + groups.rounds), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ShortArrowPicture extends Picture {

        ShortArrowPicture(int round) {
            setTextureRegion(Assets.shortArrow);
            setPosition(game.gui.WIDTH / 2 - 446, 326 + 62 * round);
        }
    }

    private class RoundQualificationLabel extends Label {

        RoundQualificationLabel(int round) {
            setPosition(game.gui.WIDTH / 2, 326 + 62 * round);
            int teams = tournament.rounds.get(round).numberOfTeams;

            String label = "";
            switch (tournament.rounds.get(round).type) {
                case KNOCKOUT:
                    Knockout knockout = (Knockout) tournament.rounds.get(round);
                    if (teams == 2) {
                        if (knockout.numberOfLegs == 1) {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNER WINS TOURNAMENT");
                        } else {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNER ON AGGREGATE WINS TOURNAMENT");
                        }
                    } else {
                        if (knockout.numberOfLegs == 1) {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNERS QUALIFY");
                        } else {
                            label = Assets.strings.get("TOURNAMENT.MATCH WINNERS ON AGGREGATE QUALIFY");
                        }
                    }
                    break;

                case GROUPS:
                    int groups = ((Groups) tournament.rounds.get(round)).groups.size();
                    if (groups == 1) {
                        if (round == tournament.rounds.size() - 1) {
                            label = Assets.strings.get("TOURNAMENT.GROUP WINNER WINS TOURNAMENT");
                        } else {
                            label = Assets.strings.get("TOURNAMENT.TOP %n IN GROUP QUALIFY")
                                    .replaceFirst("%n", "" + tournament.rounds.get(round + 1).numberOfTeams);
                        }
                    } else {
                        int nextRoundTeams = tournament.rounds.get(round + 1).numberOfTeams;
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
                    }
                    break;
            }
            setText("(" + label + ")", Font.Align.CENTER, Assets.font10);
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
