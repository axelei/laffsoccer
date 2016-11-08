package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Time;

import java.util.ArrayList;
import java.util.List;

class MatchSetup extends GLScreen {

    private FileHandle fileHandle;
    private League league;
    private Competition competition;
    private Team homeTeam;
    private Team awayTeam;
    private MatchSettings matchSettings;
    private TimePicture timePicture;
    private PitchTypePicture pitchTypePicture;
    private WeatherButton weatherButton;
    private WeatherPicture weatherPicture;
    private KitPicture[] kitPictures = new KitPicture[2];
    private List<KitButton>[] kitButtons = (ArrayList<KitButton>[]) new ArrayList[2];
    private Widget playMatchButton;

    MatchSetup(GLGame game, FileHandle fileHandle, League league, Competition competition, Team homeTeam, Team awayTeam) {
        super(game);

        this.fileHandle = fileHandle;
        this.league = league;
        this.competition = competition;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        Team.kitAutoSelection(homeTeam, awayTeam);

        matchSettings = new MatchSettings(competition, game.settings.weatherMaxStrength);

        background = new Image("images/backgrounds/menu_match_presentation.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        w = new TimeLabel();
        widgets.add(w);

        timePicture = new TimePicture();
        widgets.add(timePicture);

        w = new TimeButton();
        widgets.add(w);

        w = new PitchTypeLabel();
        widgets.add(w);

        pitchTypePicture = new PitchTypePicture();
        widgets.add(pitchTypePicture);

        w = new PitchTypeButton();
        widgets.add(w);

        w = new WeatherLabel();
        widgets.add(w);

        weatherPicture = new WeatherPicture();
        widgets.add(weatherPicture);

        weatherButton = new WeatherButton();
        widgets.add(weatherButton);

        for (int t = Match.HOME; t <= Match.AWAY; t++) {
            Team team = (t == Match.HOME) ? homeTeam : awayTeam;

            w = new TeamNameButton(team, t);
            widgets.add(w);

            kitPictures[t] = new KitPicture(team, t);
            widgets.add(kitPictures[t]);

            kitButtons[t] = new ArrayList<KitButton>();
            for (int i = 0; i < team.kits.size(); i++) {
                KitButton kitButton = new KitButton(team, t, i);
                kitButtons[t].add(kitButton);
                widgets.add(kitButton);
            }
        }

        playMatchButton = new PlayMatchButton();
        widgets.add(playMatchButton);

        selectedWidget = playMatchButton;

        w = new ExitButton();
        widgets.add(w);
    }

    private class TitleButton extends Button {

        TitleButton() {
            setGeometry((game.settings.GUI_WIDTH - 840) / 2, 30, 840, 44);
            setColors(0x008080, 0x00B2B4, 0x004040);
            setText(competition.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setColors(0x800000);
            setGeometry(game.settings.GUI_WIDTH / 2 - 300 - 65, 130 - 40 / 2, 300, 40);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimePicture extends Button {

        TimePicture() {
            setColors(0x666666);
            setGeometry((game.settings.GUI_WIDTH - 50) / 2, 130 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            image = Assets.lightIcons[matchSettings.time];
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            if (competition.getType() == Competition.Type.FRIENDLY) {
                setColors(0x1F1F95);
            } else {
                setColors(0x666666);
                setActive(false);
            }
            setGeometry(game.settings.GUI_WIDTH / 2 + 65, 130 - 40 / 2, 300, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Time.names[matchSettings.time]));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotateTime(1);
            setChanged(true);
            timePicture.setChanged(true);
        }
    }

    private class PitchTypeLabel extends Button {

        PitchTypeLabel() {
            setColors(0x800000);
            setGeometry(game.settings.GUI_WIDTH / 2 - 300 - 65, 200 - 40 / 2, 300, 40);
            setText(Assets.strings.get("PITCH TYPE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PitchTypePicture extends Button {

        PitchTypePicture() {
            setColors(0x666666);
            setGeometry((game.settings.GUI_WIDTH - 50) / 2, 200 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            image = Assets.pitchIcons[matchSettings.pitchType.ordinal()];
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            if (competition.getType() == Competition.Type.FRIENDLY) {
                setColors(0x1F1F95);
            } else {
                setColors(0x666666);
                setActive(false);
            }
            setGeometry(game.settings.GUI_WIDTH / 2 + 65, 200 - 40 / 2, 300, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Pitch.names[matchSettings.pitchType.ordinal()]));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotatePitchType(1);
            setChanged(true);
            pitchTypePicture.setChanged(true);
            weatherPicture.setChanged(true);
            weatherButton.setChanged(true);
        }
    }

    private class WeatherLabel extends Button {

        WeatherLabel() {
            setColors(0x800000);
            setGeometry(game.settings.GUI_WIDTH / 2 - 300 - 65, 270 - 40 / 2, 300, 40);
            setText(Assets.strings.get("WEATHER"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class WeatherPicture extends Button {

        WeatherPicture() {
            setColors(0x666666);
            setGeometry((game.settings.GUI_WIDTH - 50) / 2, 270 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            image = Assets.weatherIcons[matchSettings.weatherOffset()];
        }
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(game.settings.GUI_WIDTH / 2 + 65, 270 - 40 / 2, 300, 40);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            if (competition.getType() == Competition.Type.FRIENDLY && matchSettings.pitchType != Pitch.Type.RANDOM) {
                setColors(0x1F1F95);
                setActive(true);
            } else {
                setColors(0x666666);
                setActive(false);
            }
            setText(Assets.strings.get(matchSettings.getWeatherLabel()));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotateWeather(true);
            setChanged(true);
            weatherPicture.setChanged(true);
        }
    }

    private class TeamNameButton extends Button {

        TeamNameButton(Team team, int teamIndex) {
            int sign = teamIndex == 0 ? -1 : 1;
            setGeometry((game.settings.GUI_WIDTH - 520) / 2 + 280 * sign, 330, 520, 34);
            setColors(0x1F1F95);
            setText(team.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class KitPicture extends Button {

        Team team;

        KitPicture(Team team, int teamIndex) {
            this.team = team;
            int sign = teamIndex == 0 ? -1 : 1;
            setGeometry(game.settings.GUI_WIDTH / 2 - 83 + 280 * sign, 390, 167, 304);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            image = team.kits.get(team.kitIndex).loadImage();
        }
    }

    private class KitButton extends Button {

        Team team;
        int teamIndex;
        int kitIndex;

        KitButton(Team team, int teamIndex, int kitIndex) {
            this.team = team;
            this.teamIndex = teamIndex;
            this.kitIndex = kitIndex;
            setSize(58, 104);
            setImageScale(0.33333334f, 0.33333334f);
        }

        @Override
        public void onUpdate() {
            image = team.kits.get(kitIndex).loadImage();
            if (team.kitIndex == kitIndex) {
                setVisible(false);
            } else {
                setVisible(true);
                int position = kitIndex - (team.kitIndex < kitIndex ? 1 : 0);
                int sign = teamIndex == 0 ? -1 : 1;
                int x = (1 + 2 * teamIndex) * (game.settings.GUI_WIDTH) / 4 + sign * (50 + 68 * (1 + position / 2)) - 34;
                int y = 430 + 114 * (position % 2);
                setPosition(x, y);
            }
        }

        @Override
        public void onFire1Down() {
            team.kitIndex = kitIndex;
            for (KitButton kitButton : kitButtons[teamIndex]) {
                kitButton.setChanged(true);
            }
            kitPictures[teamIndex].setChanged(true);
            selectedWidget = playMatchButton;
        }
    }

    private class PlayMatchButton extends Button {

        PlayMatchButton() {
            setGeometry((game.settings.GUI_WIDTH - 240) / 2, 510, 240, 50);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(Assets.strings.get("PLAY MATCH"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MatchLoading(game, homeTeam, awayTeam, matchSettings, competition));
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (competition.getType()) {
                case FRIENDLY:
                    game.setScreen(new SelectTeams(game, fileHandle, league, competition));
                    break;
                case LEAGUE:
                    game.setScreen(new PlayLeague(game));
                    break;
                case CUP:
                    game.setScreen(new PlayCup(game));
                    break;
            }
        }
    }
}
