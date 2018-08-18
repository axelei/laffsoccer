package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

class ViewStatistics extends GLScreen {

    ViewStatistics(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(Assets.strings.get("STATISTICS"), game.stateColor.body);
        widgets.add(w);

        w = new HighestScorerButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new CompetitionInfoButton();
        widgets.add(w);

        w = new ViewSquadsButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class HighestScorerButton extends Button {

        HighestScorerButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 270, 340, 44);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("HIGHEST SCORER LIST"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new TopScorers(game));
        }
    }

    private class CompetitionInfoButton extends Button {

        CompetitionInfoButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 350, 340, 44);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("COMPETITION INFO"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.competition.type) {
                case LEAGUE:
                    game.setScreen(new InfoLeague(game));
                    break;

                case CUP:
                    game.setScreen(new InfoCup(game));
                    break;

                case TOURNAMENT:
                    game.setScreen(new InfoTournament(game));
                    break;
            }
        }
    }

    private class ViewSquadsButton extends Button {

        ViewSquadsButton() {
            setGeometry((game.gui.WIDTH - 340) / 2, 430, 340, 44);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("VIEW SQUADS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new CompetitionViewTeams(game));
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
        }
    }
}
