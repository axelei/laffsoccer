package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

class DiyCompetition extends GLScreen {

    DiyCompetition(GLGame game) {
        super(game);

        game.setState(GLGame.State.COMPETITION, Competition.Category.DIY_COMPETITION);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar();
        widgets.add(w);

        w = new LeagueButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new CupButton();
        widgets.add(w);

        w = new TournamentButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.gui.WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x376E2F, 0x4E983F, 0x214014);
            setText(Assets.strings.get("DIY COMPETITION"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class LeagueButton extends Button {

        LeagueButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 270, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("LEAGUE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DiyLeague(game));
        }
    }

    private class CupButton extends Button {

        CupButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 350, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("CUP"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DiyCup(game));
        }
    }

    private class TournamentButton extends Button {

        TournamentButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 430, 340, 40);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TOURNAMENT"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
