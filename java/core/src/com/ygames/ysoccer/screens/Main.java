package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Picture;
import com.ygames.ysoccer.gui.Widget;

public class Main extends GLScreen {

    public Main(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_main.jpg");
        TextureRegion logo = Assets.loadTextureRegion("images/logo.png");

        game.teamList.clear();

        Widget w;

        w = new Picture(logo);
        w.setPosition((game.gui.WIDTH - logo.getRegionWidth()) / 2, 100);
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
        w.setText("", Font.Align.LEFT, Assets.font10);
        w.setPosition(20, game.gui.HEIGHT - 29);
        widgets.add(w);

        // homepage
        w = new Label();
        w.setText("YSOCCER.SF.NET", Font.Align.RIGHT, Assets.font10);
        w.setPosition(game.gui.WIDTH - 20, game.gui.HEIGHT - 29);
        widgets.add(w);
    }

    private class GameOptionsButton extends Button {

        GameOptionsButton() {
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 270, 350, 36);
            setColors(0x536B90, 0x7090C2, 0x263142);
            setText(Assets.strings.get("GAME OPTIONS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new GameOptions(game));
        }
    }

    private class MatchOptionsButton extends Button {

        MatchOptionsButton() {
            setColors(0x3847A3);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 315, 350, 36);
            setText(Assets.strings.get("MATCH OPTIONS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new MatchOptions(game));
        }
    }

    private class ControlsButton extends Button {

        ControlsButton() {
            setColors(0x83079C);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 360, 350, 36);
            setText(Assets.strings.get("CONTROLS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SetupControls(game));
        }
    }

    private class EditTacticsButton extends Button {

        EditTacticsButton() {
            setColors(0xBA9206);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 405, 350, 36);
            setText(Assets.strings.get("EDIT TACTICS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectTactics(game));
        }
    }

    private class EditTeamsButton extends Button {

        EditTeamsButton() {
            setColors(0x89421B, 0xBB5A25, 0x3D1E0D);
            setGeometry(game.gui.WIDTH / 2 - 45 - 350, 450, 350, 36);
            setText(Assets.strings.get("EDIT TEAMS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setState(GLGame.State.EDIT, null);
            game.setScreen(new SelectFolder(game, Assets.teamsRootFolder, null));
        }
    }

    private class FriendlyButton extends Button {

        FriendlyButton() {
            setGeometry(game.gui.WIDTH / 2 + 45, 270, 350, 36);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(Assets.strings.get("FRIENDLY"), Font.Align.CENTER, Assets.font14);
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
            setText(Assets.strings.get("DIY COMPETITION"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            if (game.hasCompetition()) {
                game.setScreen(new CreateCompetitionWarning(game, Competition.Category.DIY_COMPETITION));
            } else {
                game.setScreen(new DiyCompetition(game));
            }
        }
    }

    private class PresetCompetitionButton extends Button {

        PresetCompetitionButton() {
            setColors(0x415600, 0x5E7D00, 0x243000);
            setGeometry(game.gui.WIDTH / 2 + 45, 360, 350, 36);
            setText(Assets.strings.get("PRESET COMPETITION"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            if (game.hasCompetition()) {
                game.setScreen(new CreateCompetitionWarning(game, Competition.Category.PRESET_COMPETITION));
            } else {
                game.setState(GLGame.State.COMPETITION, Competition.Category.PRESET_COMPETITION);
                game.setScreen(new SelectCompetition(game, Assets.competitionsFolder));
            }
        }
    }

    private class TrainingButton extends Button {

        TrainingButton() {
            setColors(0x1B8A7F, 0x25BDAE, 0x115750);
            setGeometry(game.gui.WIDTH / 2 + 45, 405, 350, 36);
            setText(Assets.strings.get("TRAINING"), Font.Align.CENTER, Assets.font14);
        }
    }

    private class ReplayContinueCompetitionButton extends Button {

        ReplayContinueCompetitionButton(int y) {
            setColors(0x568200, 0x77B400, 0x243E00);
            setGeometry((game.gui.WIDTH - 600) / 2, y, 600, 32);
            String s = Assets.strings.get(game.competition.isEnded() ? "REPLAY %s" : "CONTINUE %s");
            setText(s.replace("%s", game.competition.name), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Up() {
            if (game.competition.isEnded()) {
                game.competition.restart();
            }

            switch (game.competition.category) {
                case DIY_COMPETITION:
                case PRESET_COMPETITION:
                    game.setState(GLGame.State.COMPETITION, game.competition.category);
                    switch (game.competition.getType()) {
                        case LEAGUE:
                            game.setScreen(new PlayLeague(game));
                            break;

                        case CUP:
                            game.setScreen(new PlayCup(game));
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
            String s = Assets.strings.get("SAVE %s");
            setText(s.replace("%s", game.competition.name), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Up() {
            game.setScreen(new SaveCompetition(game));
        }
    }

    private class LoadOldCompetitionButton extends Button {

        LoadOldCompetitionButton(int y) {
            setColors(0x2898c7);
            setGeometry((game.gui.WIDTH - 600) / 2, y, 600, 32);
            setText(Assets.strings.get("LOAD OLD COMPETITION"), Font.Align.CENTER, Assets.font10);
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
}
