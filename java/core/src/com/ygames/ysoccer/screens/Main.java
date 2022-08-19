package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Picture;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import static com.ygames.ysoccer.competitions.Competition.Category.DIY_COMPETITION;
import static com.ygames.ysoccer.competitions.Competition.Category.PRESET_COMPETITION;
import static com.ygames.ysoccer.framework.Assets.competitionsRootFolder;
import static com.ygames.ysoccer.framework.Assets.font10;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.framework.Font.Align.LEFT;
import static com.ygames.ysoccer.framework.GLGame.State.COMPETITION;
import static com.ygames.ysoccer.framework.GLGame.State.NONE;
import static com.ygames.ysoccer.framework.GLGame.State.TRAINING;

public class Main extends GLScreen {

    public Main(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_main.jpg");

        game.teamList.clear();
        game.setState(NONE, null);
        navigation.folder = Assets.teamsRootFolder;
        navigation.league = null;
        navigation.team = null;

        Widget w;

        w = new LogoPicture();
        widgets.add(w);

        w = new GameOptionsButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new MatchOptionsButton();
        widgets.add(w);

        w = new ControlsButton();
        widgets.add(w);

        w = new EditTacticsButton();
        widgets.add(w);

        w = new EditTeamsButton();
        widgets.add(w);

        w = new FriendlyButton();
        widgets.add(w);

        w = new DiyCompetitionButton();
        widgets.add(w);

        w = new PresetCompetitionButton();
        widgets.add(w);

        w = new TrainingButton();
        widgets.add(w);

        int y = 510;
        if (game.hasCompetition()) {
            w = new ReplayContinueCompetitionButton(y);
            widgets.add(w);

            w = new SaveCompetitionButton(y);
            widgets.add(w);

            y += 80;
        }

        w = new LoadOldCompetitionButton(y);
        widgets.add(w);

        // release date
        w = new Label();
        w.setText("", LEFT, font10);
        w.setPosition(20, game.gui.HEIGHT - 29);
        widgets.add(w);

        w = new HomePageButton();
        widgets.add(w);

        if (Settings.development) {
            w = new DeveloperToolsButton();
            widgets.add(w);
        }
    }

    private class LogoPicture extends Picture {

        LogoPicture() {
            setPosition(game.gui.WIDTH / 2, 163);
        }

        @Override
        public void refresh() {
            setTextureRegion(gui.logo);
        }
    }

    private class GameOptionsButton extends Button {

        GameOptionsButton() {
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 270, 350, 36);
            setColors(0x536B90, 0x7090C2, 0x263142);
            setText(gettext("GAME OPTIONS"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new GameOptions(game));
        }
    }

    private class MatchOptionsButton extends Button {

        MatchOptionsButton() {
            setColor(0x3847A3);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 315, 350, 36);
            setText(gettext("MATCH OPTIONS"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MatchOptions(game));
        }
    }

    private class ControlsButton extends Button {

        ControlsButton() {
            setColor(0x83079C);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 360, 350, 36);
            setText(gettext("CONTROLS"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SetupControls(game));
        }
    }

    private class EditTacticsButton extends Button {

        EditTacticsButton() {
            setColor(0xBA9206);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 405, 350, 36);
            setText(gettext("EDIT TACTICS"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            FileHandle teamFileHandle = Assets.teamsRootFolder.child("CUSTOM/team.electronics.json");
            game.tacticsTeam = Assets.json.fromJson(Team.class, teamFileHandle.readString("UTF-8"));
            game.setScreen(new SelectTactics(game));
        }
    }

    private class EditTeamsButton extends Button {

        EditTeamsButton() {
            setColors(0x89421B, 0xBB5A25, 0x3D1E0D);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 450, 350, 36);
            setText(gettext("EDIT TEAMS"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setState(GLGame.State.EDIT, null);
            navigation.competition = null;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class FriendlyButton extends Button {

        FriendlyButton() {
            setGeometry(game.gui.WIDTH / 2 + 45, 270, 350, 36);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(gettext("FRIENDLY"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DesignFriendly(game));
        }
    }

    private class DiyCompetitionButton extends Button {

        DiyCompetitionButton() {
            setColors(0x376E2F, 0x4E983F, 0x214014);
            setGeometry(game.gui.WIDTH / 2 + 45, 315, 350, 36);
            setText(gettext("DIY COMPETITION"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            if (game.hasCompetition()) {
                game.setScreen(new CreateCompetitionWarning(game, DIY_COMPETITION));
            } else {
                game.setScreen(new DiyCompetition(game));
            }
        }
    }

    private class PresetCompetitionButton extends Button {

        PresetCompetitionButton() {
            setColors(0x415600, 0x5E7D00, 0x243000);
            setGeometry(game.gui.WIDTH / 2 + 45, 360, 350, 36);
            setText(gettext("PRESET COMPETITION"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            if (game.hasCompetition()) {
                game.setScreen(new CreateCompetitionWarning(game, PRESET_COMPETITION));
            } else {
                game.setState(COMPETITION, PRESET_COMPETITION);
                game.setScreen(new SelectCompetition(game, competitionsRootFolder));
            }
        }
    }

    private class TrainingButton extends Button {

        TrainingButton() {
            setColors(0x1B8A7F, 0x25BDAE, 0x115750);
            setGeometry(game.gui.WIDTH / 2 + 45, 405, 350, 36);
            setText(gettext("TRAINING"), CENTER, font14);
        }

        @Override
        protected void onFire1Down() {
            game.setState(TRAINING, null);
            navigation.competition = null;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class ReplayContinueCompetitionButton extends Button {

        ReplayContinueCompetitionButton(int y) {
            setColors(0x568200, 0x77B400, 0x243E00);
            setGeometry((game.gui.WIDTH - 600) / 2, y, 600, 32);
            String s = gettext(game.competition.isEnded() ? "REPLAY %s" : "CONTINUE %s");
            setText(s.replace("%s", game.competition.name), CENTER, font10);
        }

        @Override
        public void onFire1Up() {
            if (game.competition.isEnded()) {
                game.competition.restart();
            }

            switch (game.competition.category) {
                case DIY_COMPETITION:
                case PRESET_COMPETITION:
                    game.setState(COMPETITION, game.competition.category);
                    switch (game.competition.type) {
                        case LEAGUE:
                            game.setScreen(new PlayLeague(game));
                            break;

                        case CUP:
                            game.setScreen(new PlayCup(game));
                            break;

                        case TOURNAMENT:
                            game.setScreen(new PlayTournament(game));
                            break;
                    }
                    break;
            }
        }
    }

    private class SaveCompetitionButton extends Button {

        SaveCompetitionButton(int y) {
            setColors(0xC8000E, 0xFF1929, 0x74040C);
            setGeometry((game.gui.WIDTH - 600) / 2, y + 40, 600, 32);
            String s = gettext("SAVE %s");
            setText(s.replace("%s", game.competition.name), CENTER, font10);
        }

        @Override
        public void onFire1Up() {
            game.setScreen(new SaveCompetition(game));
        }
    }

    private class LoadOldCompetitionButton extends Button {

        LoadOldCompetitionButton(int y) {
            setColor(0x2898c7);
            setGeometry((game.gui.WIDTH - 600) / 2, y, 600, 32);
            setText(gettext("LOAD OLD COMPETITION"), CENTER, font10);
        }

        @Override
        public void onFire1Up() {
            if (game.hasCompetition()) {
                game.setScreen(new LoadCompetitionWarning(game));
            } else {
                game.setScreen(new LoadCompetition(game));
            }
        }
    }

    private class HomePageButton extends Button {

        HomePageButton() {
            setGeometry(game.gui.WIDTH - 172, game.gui.HEIGHT - 20, 172, 20);
            setText("YSOCCER.SF.NET", LEFT, font10);
        }

        @Override
        public void onFire1Down() {
            Gdx.net.openURI("http://ysoccer.sf.net");
        }
    }

    private class DeveloperToolsButton extends Button {

        DeveloperToolsButton() {
            setColor(0x191FB0);
            setGeometry((game.gui.WIDTH - 300) / 2, 675, 300, 32);
            setText("DEVELOPER TOOLS", CENTER, font10);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DeveloperTools(game));
        }
    }
}
