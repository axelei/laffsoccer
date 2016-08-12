package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class Main extends GlScreen {

    public Main(GlGame game) {
        super(game);
        background = new Image("images/backgrounds/menu_main.jpg");

        game.teamList.clear();

        Widget w;
        w = new GameOptionsButton();
        widgets.add(w);
        selectedWidget = w;

        w = new MatchOptionsButton();
        widgets.add(w);

        w = new ControlButton();
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
    }

    class GameOptionsButton extends Button {

        public GameOptionsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 340, 270, 340, 36);
            setColors(0x536B90, 0x7090C2, 0x263142);
            setText(Assets.strings.get("GAME OPTIONS"), Font.Align.CENTER, Assets.font14);
            setSelected(true);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new GameOptions(game));
        }
    }

    class MatchOptionsButton extends Button {

        public MatchOptionsButton() {
            setColors(0x6101D7, 0x7D1DFF, 0x3A0181);
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 340, 315, 340, 36);
            setText(Assets.strings.get("MATCH OPTIONS"), Font.Align.CENTER, Assets.font14);
        }
    }

    class ControlButton extends Button {

        public ControlButton() {
            setColors(0xA905A3, 0xE808E0, 0x5A0259);
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 340, 360, 340, 36);
            setText(Assets.strings.get("CONTROL"), Font.Align.CENTER, Assets.font14);
        }
    }

    class EditTacticsButton extends Button {

        public EditTacticsButton() {
            setColors(0xBA9206, 0xE9B607, 0x6A5304);
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 340, 405, 340, 36);
            setText(Assets.strings.get("EDIT TACTICS"), Font.Align.CENTER, Assets.font14);
        }
    }

    class EditTeamsButton extends Button {

        public EditTeamsButton() {
            setColors(0x89421B, 0xBB5A25, 0x3D1E0D);
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 340, 450, 340, 36);
            setText(Assets.strings.get("EDIT TEAMS"), Font.Align.CENTER, Assets.font14);
        }
    }

    class FriendlyButton extends Button {

        public FriendlyButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 270, 340, 36);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(Assets.strings.get("FRIENDLY"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DesignFriendly(game));
        }
    }

    class DiyCompetitionButton extends Button {

        public DiyCompetitionButton() {
            setColors(0x376E2F, 0x4E983F, 0x214014);
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 315, 340, 36);
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

    class PresetCompetitionButton extends Button {

        public PresetCompetitionButton() {
            setColors(0x415600, 0x5E7D00, 0x243000);
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 360, 340, 36);
            setText(Assets.strings.get("PRESET COMPETITION"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            if (game.hasCompetition()) {
                game.setScreen(new CreateCompetitionWarning(game, Competition.Category.PRESET_COMPETITION));
            } else {
                game.setState(GlGame.State.COMPETITION, Competition.Category.PRESET_COMPETITION);
                game.setScreen(new SelectCompetition(game, Assets.competitionsFolder));
            }
        }
    }

    class TrainingButton extends Button {

        public TrainingButton() {
            setColors(0x1B8A7F, 0x25BDAE, 0x115750);
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 405, 340, 36);
            setText(Assets.strings.get("TRAINING"), Font.Align.CENTER, Assets.font14);
        }
    }

    class ReplayContinueCompetitionButton extends Button {

        public ReplayContinueCompetitionButton(int y) {
            setColors(0x568200, 0x77B400, 0x243E00);
            setGeometry((game.settings.GUI_WIDTH - 600) / 2, y, 600, 32);
            String s = Assets.strings.get(game.competition.isEnded() ? "REPLAY %s" : "CONTINUE %s");
            setText(s.replace("%s", game.competition.longName.toUpperCase()), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Up() {
            if (game.competition.isEnded()) {
                game.competition.restart();
            }

            switch (game.competition.category) {
                case DIY_COMPETITION:
                case PRESET_COMPETITION:
                    game.setState(GlGame.State.COMPETITION, game.competition.category);
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

    class SaveCompetitionButton extends Button {

        public SaveCompetitionButton(int y) {
            setColors(0xC8000E, 0xFF1929, 0x74040C);
            setGeometry((game.settings.GUI_WIDTH - 600) / 2, y + 40, 600, 32);
            String s = Assets.strings.get("SAVE %s");
            setText(s.replace("%s", game.competition.longName.toUpperCase()), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Up() {
            game.setScreen(new SaveCompetition(game));
        }
    }

    class LoadOldCompetitionButton extends Button {

        public LoadOldCompetitionButton(int y) {
            setColors(0x2898c7, 0x32bffa, 0x1e7194);
            setGeometry((game.settings.GUI_WIDTH - 600) / 2, y, 600, 32);
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
