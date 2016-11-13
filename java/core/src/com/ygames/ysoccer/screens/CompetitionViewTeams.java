package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

public class CompetitionViewTeams extends GLScreen {

    public CompetitionViewTeams(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar();
        widgets.add(w);

        List<Widget> list = new ArrayList<Widget>();
        for (Team team : game.competition.teams) {
            w = new TeamButton(team);
            list.add(w);
            widgets.add(w);

            if (selectedWidget == null) {
                setSelectedWidget(w);
            }
        }
        Widget.arrange(game.gui.WIDTH, 350, 32, list);

        w = new ExitButton();
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.gui.WIDTH - 660) / 2, 30, 660, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(Assets.strings.get("SELECT SQUAD TO VIEW"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TeamButton extends Button {

        Team team;

        public TeamButton(Team team) {
            this.team = team;
            setSize(270, 30);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(team.name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewTeam(game, team, game.competition));
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }
}
