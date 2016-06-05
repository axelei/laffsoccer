package com.ygames.ysoccer.screens;

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

        if (game.competition != null) {
            w = new ReplayContinueCompetitionButton();
            widgets.add(w);
        }
    }

    class GameOptionsButton extends Button {

        public GameOptionsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 320, 290, 320, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 320, 340, 320, 36);
            setText(Assets.strings.get("MATCH OPTIONS"), Font.Align.CENTER, Assets.font14);
        }
    }

    class ControlButton extends Button {

        public ControlButton() {
            setColors(0xA905A3, 0xE808E0, 0x5A0259);
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 320, 390, 320, 36);
            setText(Assets.strings.get("CONTROL"), Font.Align.CENTER, Assets.font14);
        }
    }

    class EditTacticsButton extends Button {

        public EditTacticsButton() {
            setColors(0xBA9206, 0xE9B607, 0x6A5304);
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 320, 440, 320, 36);
            setText(Assets.strings.get("EDIT TACTICS"), Font.Align.CENTER, Assets.font14);
        }
    }

    class EditTeamsButton extends Button {

        public EditTeamsButton() {
            setColors(0x89421B, 0xBB5A25, 0x3D1E0D);
            setGeometry(game.settings.GUI_WIDTH / 2 - 40 - 320, 490, 320, 36);
            setText(Assets.strings.get("EDIT TEAMS"), Font.Align.CENTER, Assets.font14);
        }
    }

    class FriendlyButton extends Button {

        public FriendlyButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 290, 320, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 340, 320, 36);
            setText(Assets.strings.get("DIY COMPETITION"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DiyCompetition(game));
        }
    }

    class PresetCompetitionButton extends Button {

        public PresetCompetitionButton() {
            setColors(0x415600, 0x5E7D00, 0x243000);
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 390, 320, 36);
            setText(Assets.strings.get("PRESET COMPETITION"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.state = GlGame.State.COMPETITION;
            game.stateBackground = new Image("images/backgrounds/menu_competition.jpg");
            game.stateColor.set(0x415600, 0x5E7D00, 0x243000);
            game.setScreen(new SelectCompetition(game, Assets.competitionsFolder));
        }
    }

    class TrainingButton extends Button {

        public TrainingButton() {
            setColors(0x1B8A7F, 0x25BDAE, 0x115750);
            setGeometry(game.settings.GUI_WIDTH / 2 + 40, 440, 320, 36);
            setText(Assets.strings.get("TRAINING"), Font.Align.CENTER, Assets.font14);
        }
    }

    class ReplayContinueCompetitionButton extends Button {

        public ReplayContinueCompetitionButton() {
            setColors(0x568200, 0x77B400, 0x243E00);
            setGeometry(game.settings.GUI_WIDTH / 2 - 260, 560, 520, 36);
            String s = Assets.strings.get(game.competition.isEnded() ? "REPLAY %s" : "CONTINUE %s");
            setText(s.replace("%s", game.competition.longName.toUpperCase()), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            // TODO: set competition menu
        }
    }
}
