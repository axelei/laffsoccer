package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;
import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchStats;

import java.util.Locale;

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

        widgets.add(new StatsLabel(game.gui.WIDTH / 2 - 60, 120, match.team[Match.HOME].name, Font.Align.RIGHT, Assets.font14));
        widgets.add(new StatsLabel(game.gui.WIDTH / 2 + 60, 120, match.team[Match.AWAY].name, Font.Align.LEFT, Assets.font14));

        //match.team[Match.HOME].name
        //match.team[Match.AWAY].name

        w = new ExitButton();
        widgets.add(w);
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);
        batch.begin();

        int homeGoals = match.stats[HOME].goals;
        int awayGoals = match.stats[AWAY].goals;

        drawNumber(homeGoals, game.gui.WIDTH / 2 - 110, 160);
        int offset = 0;
        if (awayGoals > 9) {
            offset +=12;
        }
        if (awayGoals > 99) {
            offset +=24;
        }
        drawNumber(awayGoals, game.gui.WIDTH / 2 + 110 - 48 + offset, 160);

        batch.end();
    }

    private void drawNumber(int number, int x, int y) {

        // units
        int digit = number % 10;
        batch.draw(Assets.time[digit], x + 24, y);
        // tens
        number = (number - digit) / 10;
        digit = number % 10;
        if (number > 0) {
            batch.draw(Assets.time[digit], x + 12, y);
        }
        // hundreds
        number = (number - digit) / 10;
        digit = number % 10;
        if (digit > 0) {
            batch.draw(Assets.time[digit], x , y);
        }
    }

    private class StatsLabel extends Label {

        StatsLabel(int x, int y, String text, Font.Align align, Font font) {
            setPosition(x, y);
            setText(text, align, font);
        }
    }

    private class ReplayButton extends Button {

        ReplayButton() {
            setGeometry((game.gui.WIDTH - 240) / 2, 600, 240, 50);
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
