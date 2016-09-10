package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

public class SetTeam extends GlScreen {

    Competition competition;

    Team team;
    Team opponent;
    Team current;
    int selectedPos;

    Widget[] faceButtons = new Widget[Const.FULL_TEAM];

    public SetTeam(GlGame game, Competition competition, Team homeTeam, Team awayTeam, int teamToSet) {
        super(game);

        this.competition = competition;
        if (teamToSet == Match.HOME) {
            team = homeTeam;
            opponent = awayTeam;
        } else {
            team = awayTeam;
            opponent = homeTeam;
        }
        current = team;
        selectedPos = -1;

        background = new Image("images/backgrounds/menu_set_team.jpg");

        Widget w;

        // players
        for (int pos = 0; pos < Const.FULL_TEAM; pos++) {
            w = new PlayerFaceButton(pos);
            faceButtons[pos] = w;
            widgets.add(w);
        }

        // team name
        w = new TeamNameButton();
        widgets.add(w);
    }

    class PlayerFaceButton extends Button {

        int pos;

        public PlayerFaceButton(int pos) {
            this.pos = pos;
            setGeometry(30, 126 + 19 * pos, 24, 17);
            setImagePosition(2, -3);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                image = null;
            } else {
                image = player.createFace();
            }
        }
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

    private void setPlayerWidgetColor(Widget b, int pos) {
        if (current == team) {
            // goalkeeper
            if (pos == 0) {
                b.setColors(0x00A7DE, 0x33CCFF, 0x005F7E);
            }
            // other player
            else if (pos < Const.TEAM_SIZE) {
                b.setColors(0x003FDE, 0x255EFF, 0x00247E);
            }
            // bench
            else if (pos < Const.TEAM_SIZE + competition.benchSize) {
                b.setColors(0x111188, 0x2D2DB3, 0x001140);
            }
            // reserve
            else if (pos < current.players.size()) {
                b.setColors(0x404040, 0x606060, 0x202020);
            }
            // void
            else {
                b.setColors(0x202020, 0x404040, 0x101010);
            }

            // selected
            if (selectedPos == pos) {
                b.setColors(0x993333, 0xC24242, 0x5A1E1E);
            }
        }
        // opponent
        else {
            // goalkeeper
            if (pos == 0) {
                b.setColors(0xE60000, 0xFF4141, 0xB40000);
            }
            // other player
            else if (pos < Const.TEAM_SIZE) {
                b.setColors(0xB40000, 0xE60000, 0x780000);
            }
            // bench
            else if (pos < Const.TEAM_SIZE + competition.benchSize) {
                b.setColors(0x780000, 0xB40000, 0x3C0000);
            }
            // reserve
            else if (pos < current.players.size()) {
                b.setColors(0x404040, 0x606060, 0x202020);
            }
            // void
            else {
                b.setColors(0x202020, 0x404040, 0x101010);
            }
        }
    }
}
