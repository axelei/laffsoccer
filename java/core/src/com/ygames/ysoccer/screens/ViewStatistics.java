package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class ViewStatistics extends GlScreen {

    public ViewStatistics(GlGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        w = new HighestScorerButton();
        widgets.add(w);

        selectedWidget = w;

        w = new CompetitionInfoButton();
        widgets.add(w);

        w = new ViewSquadsButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    public class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(Assets.strings.get("STATISTICS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    public class HighestScorerButton extends Button {

        public HighestScorerButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 270, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("HIGHEST SCORER LIST"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new TopScorers(game));
        }
    }

    public class CompetitionInfoButton extends Button {

        public CompetitionInfoButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 350, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("COMPETITION INFO"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.competition.getType()) {
                case LEAGUE:
                    game.setScreen(new InfoLeague(game));
                    break;
                case CUP:
                    game.setScreen(new InfoCup(game));
                    break;
            }
        }
    }

    public class ViewSquadsButton extends Button {

        public ViewSquadsButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 430, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("VIEW SQUADS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new CompetitionViewTeams(game));
        }
    }

    public class ExitButton extends Button {

        public ExitButton() {
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.competition.getType()) {
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
