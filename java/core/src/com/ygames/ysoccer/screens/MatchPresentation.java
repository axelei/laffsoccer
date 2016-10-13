package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Time;

class MatchPresentation extends GlScreen {

    private FileHandle fileHandle;
    private League league;
    private Competition competition;
    private Team homeTeam;
    private Team awayTeam;
    private MatchSettings matchSettings;
    private TimePicture timePicture;
    private PitchTypePicture pitchTypePicture;

    MatchPresentation(GlGame game, FileHandle fileHandle, League league, Competition competition, Team homeTeam, Team awayTeam) {
        super(game);

        this.fileHandle = fileHandle;
        this.league = league;
        this.competition = competition;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        matchSettings = new MatchSettings(competition);

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

        w = new PlayMatchButton();
        widgets.add(w);

        selectedWidget = w;

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
            image = Assets.pitchIcons[matchSettings.pitchType];
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
            setText(Assets.strings.get(Pitch.names[matchSettings.pitchType]));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotatePitchType(1);
            setChanged(true);
            pitchTypePicture.setChanged(true);
            // TODO: weatherPicture.setChanged(true);
            // TODO: weatherButton.setChanged(true);
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

    private class PlayMatchButton extends Button {

        PlayMatchButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 375, 340, 40);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(Assets.strings.get("PLAY MATCH"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            // TODO: game.setScreen(new MatchLoading(game, homeTeam, awayTeam, matchSettings));
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
