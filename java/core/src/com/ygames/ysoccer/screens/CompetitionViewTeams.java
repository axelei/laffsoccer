package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

public class CompetitionViewTeams extends GlScreen {

    public CompetitionViewTeams(GlGame game) {
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
                selectedWidget = w;
            }
        }
        Widget.arrange(game.settings, 350, 32, list);
    }

    public class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 660) / 2, 30, 660, 40);
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
            setText(team.name.toUpperCase(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            // TODO
        }
    }
}
