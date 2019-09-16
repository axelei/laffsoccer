package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class ReplayMatch extends GLScreen {

    private Match match;

    ReplayMatch(GLGame game, Match match) {
        super(game);
        this.match = match;

        background = new Texture("images/backgrounds/menu_match_presentation.jpg");

        Widget w;

        w = new TitleBar(match.competition.name, game.stateColor.body);
        widgets.add(w);

        w = new ReplayButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class ReplayButton extends Button {

        ReplayButton() {
            setGeometry((game.gui.WIDTH - 240) / 2, 330, 240, 50);
            setColor(0xDC0000);
            setText(Assets.strings.get("REPLAY"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            Friendly friendly = new Friendly();
            friendly.getMatch().setTeam(HOME, match.team[HOME]);
            friendly.getMatch().setTeam(AWAY, match.team[AWAY]);

            game.setScreen(new MatchLoading(game, match.getSettings(), friendly));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
