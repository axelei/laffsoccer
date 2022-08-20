package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;

import static com.ygames.ysoccer.competitions.Competition.Type.FRIENDLY;
import static com.ygames.ysoccer.competitions.Competition.Type.TEST_MATCH;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchSetup extends GLScreen {

    private final MatchSettings matchSettings;
    private final TimePicture timePicture;
    private final PitchTypePicture pitchTypePicture;
    private final WeatherButton weatherButton;
    private final WeatherPicture weatherPicture;
    private final KitPicture[] kitPictures = new KitPicture[2];
    private final ArrayList<KitButton>[] kitButtons = new ArrayList[2];
    private final Widget playMatchButton;

    MatchSetup(GLGame game) {
        super(game);
        playMenuMusic = false;

        Match match = navigation.competition.getMatch();
        Team homeTeam = match.team[HOME];
        Team awayTeam = match.team[AWAY];

        Team.kitAutoSelection(homeTeam, awayTeam);

        matchSettings = new MatchSettings(navigation.competition, game.settings);

        Assets.Sounds.volume = game.settings.soundVolume;

        background = new Texture("images/backgrounds/menu_match_presentation.jpg");

        Widget w;

        w = new TitleBar(navigation.competition.name, game.stateColor.body);
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

        for (int t = HOME; t <= AWAY; t++) {
            Team team = (t == HOME) ? homeTeam : awayTeam;

            w = new TeamNameButton(team, t);
            widgets.add(w);

            kitPictures[t] = new KitPicture(team, t);
            widgets.add(kitPictures[t]);

            kitButtons[t] = new ArrayList<>();
            for (int i = 0; i < team.kits.size(); i++) {
                KitButton kitButton = new KitButton(team, t, i);
                kitButtons[t].add(kitButton);
                widgets.add(kitButton);
            }
        }

        playMatchButton = new PlayMatchButton();
        widgets.add(playMatchButton);

        setSelectedWidget(playMatchButton);

        w = new ExitButton();
        widgets.add(w);
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 300 + 25, 130 - 40 / 2, 300, 40);
            setText(gettext("TIME"), CENTER, font14);
            setActive(false);
        }
    }

    private class TimePicture extends Button {

        TimePicture() {
            setColor(0x666666);
            setGeometry(game.gui.WIDTH / 2 - 300 - 65, 130 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = gui.lightIcons[matchSettings.time.ordinal()];
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            if (navigation.competition.type == FRIENDLY ||
                    navigation.competition.type == TEST_MATCH) {
                setColor(0x1F1F95);
            } else {
                setColor(0x666666);
                setActive(false);
            }
            setGeometry(game.gui.WIDTH / 2 + 65, 130 - 40 / 2, 300, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext(MatchSettings.getTimeLabel(matchSettings.time)));
        }

        @Override
        public void onFire1Down() {
            rotateTime(1);
        }

        @Override
        public void onFire2Down() {
            rotateTime(-1);
        }

        private void rotateTime(int n) {
            matchSettings.rotateTime(n);
            setDirty(true);
            timePicture.setDirty(true);
        }
    }

    private class PitchTypeLabel extends Button {

        PitchTypeLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 300 + 25, 200 - 40 / 2, 300, 40);
            setText(gettext("PITCH TYPE"), CENTER, font14);
            setActive(false);
        }
    }

    private class PitchTypePicture extends Button {

        PitchTypePicture() {
            setColor(0x666666);
            setGeometry(game.gui.WIDTH / 2 - 300 - 65, 200 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = gui.pitchIcons[matchSettings.pitchType.ordinal()];
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            if (navigation.competition.type == FRIENDLY ||
                    navigation.competition.type == TEST_MATCH) {
                setColor(0x1F1F95);
            } else {
                setColor(0x666666);
                setActive(false);
            }
            setGeometry(game.gui.WIDTH / 2 + 65, 200 - 40 / 2, 300, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext(Pitch.names[matchSettings.pitchType.ordinal()]));
        }

        @Override
        public void onFire1Down() {
            rotatePitchType(1);
        }

        @Override
        public void onFire2Down() {
            rotatePitchType(-1);
        }

        private void rotatePitchType(int n) {
            matchSettings.rotatePitchType(n);
            setDirty(true);
            pitchTypePicture.setDirty(true);
            weatherPicture.setDirty(true);
            weatherButton.setDirty(true);
        }
    }

    private class WeatherLabel extends Button {

        WeatherLabel() {
            setColor(0x800000);
            setGeometry(game.gui.WIDTH / 2 - 300 + 25, 270 - 40 / 2, 300, 40);
            setText(gettext("WEATHER"), CENTER, font14);
            setActive(false);
        }
    }

    private class WeatherPicture extends Button {

        WeatherPicture() {
            setColor(0x666666);
            setGeometry(game.gui.WIDTH / 2 - 300 - 65, 270 - 50 / 2, 50, 50);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = gui.weatherIcons[matchSettings.weatherOffset()];
        }
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setGeometry(game.gui.WIDTH / 2 + 65, 270 - 40 / 2, 300, 40);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            if (navigation.competition.type == FRIENDLY ||
                    navigation.competition.type == TEST_MATCH) {
                setColor(0x1F1F95);
                setActive(true);
            } else {
                setColor(0x666666);
                setActive(false);
            }
            setText(gettext(matchSettings.getWeatherLabel()));
        }

        @Override
        public void onFire1Down() {
            matchSettings.rotateWeather();
            setDirty(true);
            weatherPicture.setDirty(true);
        }
    }

    private class TeamNameButton extends Button {

        TeamNameButton(Team team, int teamIndex) {
            int sign = teamIndex == 0 ? -1 : 1;
            setGeometry((game.gui.WIDTH - 500) / 2 + (500 / 2 + 20) * sign, 326, 500, 42);
            setColor(0x1F1F95);
            setText(team.name, CENTER, font14);
            setActive(false);
        }
    }

    private class KitPicture extends Button {

        Team team;

        KitPicture(Team team, int teamIndex) {
            this.team = team;
            int sign = teamIndex == 0 ? -1 : 1;
            setGeometry(game.gui.WIDTH / 2 - 83 + 270 * sign, 390, 167, 304);
            setActive(false);
            setAddShadow(true);
        }

        @Override
        public void refresh() {
            textureRegion = team.loadKit(team.kitIndex);
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
            setImageScale(1 / 3f, 1 / 3f);
            setAddShadow(true);
        }

        @Override
        public void refresh() {
            textureRegion = team.loadKit(kitIndex);
            if (team.kitIndex == kitIndex) {
                setVisible(false);
            } else {
                setVisible(true);
                int position = kitIndex - (team.kitIndex < kitIndex ? 1 : 0);
                int sign = teamIndex == 0 ? -1 : 1;
                int x = game.gui.WIDTH / 2 + sign * (340 + 68 * (1 + position / 2)) - 34;
                int y = 430 + 114 * (position % 2);
                setPosition(x, y);
            }
        }

        @Override
        public void onFire1Down() {
            team.kitIndex = kitIndex;
            for (KitButton kitButton : kitButtons[teamIndex]) {
                kitButton.setDirty(true);
            }
            kitPictures[teamIndex].setDirty(true);
            setSelectedWidget(playMatchButton);
        }
    }

    private class PlayMatchButton extends Button {

        PlayMatchButton() {
            setGeometry((game.gui.WIDTH - 240) / 2, 510, 240, 50);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(gettext("PLAY MATCH"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MatchLoading(game, matchSettings, navigation.competition));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(gettext("EXIT"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            switch (navigation.competition.type) {
                case FRIENDLY:
                    if (navigation.folder.equals(Assets.favouritesFile)) {
                        game.setScreen(new SelectFavourites(game));
                    } else {
                        FileHandle[] teamFileHandles = navigation.folder.list(Assets.teamFilenameFilter);
                        if (teamFileHandles.length > 0) {
                            game.setScreen(new SelectTeams(game));
                        } else {
                            game.setScreen(new SelectFolder(game));
                        }
                    }
                    break;

                case LEAGUE:
                    game.setScreen(new PlayLeague(game));
                    break;

                case CUP:
                    game.setScreen(new PlayCup(game));
                    break;

                case TOURNAMENT:
                    game.setScreen(new PlayTournament(game));
                    break;

                case TEST_MATCH:
                    game.setScreen(new DeveloperTools(game));
                    break;
            }
        }
    }
}
