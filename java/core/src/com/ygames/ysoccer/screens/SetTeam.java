package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

public class SetTeam extends GlScreen {

    Team team;
    Team opponent;
    Team current;

    public SetTeam(GlGame game, Team homeTeam, Team awayTeam, int teamToSet) {
        super(game);

        background = new Image("images/backgrounds/menu_set_team.jpg");

        if (teamToSet == Match.HOME) {
            team = homeTeam;
            opponent = awayTeam;
        } else {
            team = awayTeam;
            opponent = homeTeam;
        }

        current = team;

        Widget w;

        // team name
        w = new TeamNameButton();
        widgets.add(w);
    }

    class TeamNameButton extends Button {

        public TeamNameButton() {
            setGeometry(640 - 300, 45, 601, 41);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(current.name);
            if (current == team) {
                setColors(0x6A5ACD, 0x8F83D7, 0x372989);
            } else {
                setColors(0xC14531, 0xDF897B, 0x8E3324);
            }
        }
    }
}
