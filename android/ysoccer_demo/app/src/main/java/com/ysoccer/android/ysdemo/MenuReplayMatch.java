package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Widget;
import com.ysoccer.android.ysdemo.match.MatchSettings;
import com.ysoccer.android.ysdemo.match.Team;

public class MenuReplayMatch extends GLScreen {

    private final Team[] teams;
    private final MatchSettings matchSettings;

    class ReplayButton extends Button {

        ReplayButton() {
            setColors(0x2D855D);
            setGeometry((Settings.GUI_WIDTH - 300) / 2, 250, 300, 40);
            setText(gettext(R.string.REPLAY_MATCH), 0, 14);
        }

        @Override
        public void onFire1Down() {
            Team[] newTeams = new Team[2];
            for (int t = 0; t <= 1; t++) {
                newTeams[t] = new Team(teams[t].nameKey, teams[t].ext,
                        teams[t].number);
                newTeams[t].loadFromFile(glGame);
                newTeams[t].index = t;
                newTeams[t].setControlMode(teams[t].getControlMode());
                newTeams[t].kitIndex = teams[t].kitIndex;
            }
            game.setScreen(new MatchLoading(game, newTeams, matchSettings));
        }
    }

    class BackButton extends Button {

        BackButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((Settings.GUI_WIDTH - 300) / 2, 310, 300, 40);
            setText(gettext(R.string.EXIT), 0, 14);
        }

        @Override
        public void onFire1Down() {
            onKeyBackHw();
        }
    }

    MenuReplayMatch(Game game, Team[] teams, MatchSettings matchSettings) {
        super(game);
        this.teams = teams;
        this.matchSettings = matchSettings;

        setBackground(Assets.settingsMenuBackground);

        Widget w;

        w = new Button();
        w.setColors(0x536B90, 0x7090C2, 0x7090C2);
        String title = teams[0].name + "  -  " + teams[1].name;
        int titleWidth = Math.max(340, 40 + 16 * title.length());
        w.setGeometry((Settings.GUI_WIDTH - titleWidth) / 2, 20, titleWidth, 40);
        w.setText(title, 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        w = new ReplayButton();
        getWidgets().add(w);

        setSelectedWidget(w);

        w = new BackButton();
        getWidgets().add(w);

    }

    @Override
    public void resume() {
        super.resume();
        Assets.settingsMenuBackground.reload();
    }

    @Override
    public void onKeyBackHw() {
        game.setScreen(new MenuMain(game));
    }

}
