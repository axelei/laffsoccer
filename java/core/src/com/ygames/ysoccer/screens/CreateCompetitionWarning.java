package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

class CreateCompetitionWarning extends GLScreen {

    private Competition.Category createCategory;

    CreateCompetitionWarning(GLGame game, Competition.Category createCategory) {
        super(game);
        this.createCategory = createCategory;

        background = new Texture("images/backgrounds/menu_competition.jpg");

        Widget w;

        String titleLabel = Competition.getCategoryLabel(createCategory);
        // TODO refactor into Competition.getCategoryColor method
        int titleColor = 0;
        switch (createCategory) {
            case DIY_COMPETITION:
                titleColor = 0x376E2F;
                break;

            case PRESET_COMPETITION:
                titleColor = 0x415600;
                break;
        }
        w = new TitleBar(Assets.strings.get(titleLabel), titleColor);
        widgets.add(w);

        // warning
        w = new Button();
        w.setGeometry((game.gui.WIDTH - 580) / 2, 270, 580, 180);
        w.setColors(0xDC0000, 0xFF4141, 0x8C0000);
        w.setActive(false);
        widgets.add(w);

        String msg = Assets.strings.get(Competition.getWarningLabel(game.competition.category));
        int cut = msg.indexOf(" ", msg.length() / 2);

        w = new Label();
        w.setText(msg.substring(0, cut), Font.Align.CENTER, Assets.font14);
        w.setPosition(game.gui.WIDTH / 2, 340);
        widgets.add(w);

        w = new Label();
        w.setText(msg.substring(cut + 1), Font.Align.CENTER, Assets.font14);
        w.setPosition(game.gui.WIDTH / 2, 380);
        widgets.add(w);

        w = new ContinueButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class ContinueButton extends Button {

        ContinueButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 605, 180, 36);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("CONTINUE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.clearCompetition();
            switch (createCategory) {
                case DIY_COMPETITION:
                    game.setScreen(new DiyCompetition(game));
                    break;

                case PRESET_COMPETITION:
                    game.setState(GLGame.State.COMPETITION, Competition.Category.PRESET_COMPETITION);
                    game.setScreen(new SelectCompetition(game, Assets.competitionsRootFolder));
                    break;
            }
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
