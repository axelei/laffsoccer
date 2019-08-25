package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.gui.Widget.widgetComparatorByText;

class CompetitionViewTeams extends GLScreen {

    CompetitionViewTeams(GLGame game) {
        super(game);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(gettext("SELECT SQUAD TO VIEW"), game.stateColor.body);
        widgets.add(w);

        List<Widget> list = new ArrayList<>();
        for (Team team : game.competition.teams) {
            w = new TeamButton(team);
            list.add(w);
            widgets.add(w);

        }

        if (list.size() > 0) {
            Collections.sort(list, widgetComparatorByText);
            Widget.arrange(game.gui.WIDTH, 350, 32, 20, list);
            setSelectedWidget(list.get(0));
        }

        w = new ExitButton();
        widgets.add(w);
    }

    private class TeamButton extends Button {

        Team team;

        TeamButton(Team team) {
            this.team = team;
            setSize(270, 30);
            setColors(0x98691E, 0xC88B28, 0x3E2600);
            setText(team.name, CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewTeam(game, team, game.competition));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(gettext("EXIT"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }
}
