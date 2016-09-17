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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetTeam extends GlScreen {

    Competition competition;

    Team team;
    Team opponent;
    Team current;
    int selectedPos;

    List<Widget> playerButtons = new ArrayList<Widget>();

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
            playerButtons.add(w);
            widgets.add(w);

            w = new PlayerNumberButton(pos);
            playerButtons.add(w);
            widgets.add(w);

            w = new PlayerNameButton(pos);
            playerButtons.add(w);
            widgets.add(w);

            if (current.type == Team.Type.CLUB) {
                w = new PlayerNationalityButton(pos);
                playerButtons.add(w);
                widgets.add(w);
            }

            selectedWidget = w;
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
            Player player = current.playerAtPosition(pos);
            if (player == null) {
                image = null;
            } else {
                image = player.createFace();
            }
        }
    }

    class PlayerNumberButton extends Button {

        int pos;

        public PlayerNumberButton(int pos) {
            this.pos = pos;
            setGeometry(56, 126 + 19 * pos, 34, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = current.playerAtPosition(pos);
            if (player == null) {
                setText("");
            } else {
                setText(player.number);
            }
        }
    }


    class PlayerNameButton extends Button {

        int pos;

        public PlayerNameButton(int pos) {
            this.pos = pos;
            setGeometry(92, 126 + 19 * pos, 364, 17);
            setText("", Font.Align.LEFT, Assets.font10);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            Player player = current.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.name);
                setActive(current == team);
            }
        }

        @Override
        public void onFire1Down() {
            // select
            if (selectedPos == -1) {
                selectedPos = pos;
            }
            // deselect
            else if (selectedPos == pos) {
                selectedPos = -1;
            }
            // swap
            else {
                int ply1 = team.playerIndexAtPosition(selectedPos);
                int ply2 = team.playerIndexAtPosition(pos);

                Collections.swap(team.players, ply1, ply2);

                selectedPos = -1;
            }
            updatePlayerButtons();
        }
    }

    class PlayerNationalityButton extends Button {

        int pos;

        public PlayerNationalityButton(int pos) {
            this.pos = pos;
            setGeometry(458, 126 + 19 * pos, 56, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = current.playerAtPosition(pos);
            if (player == null) {
                setText("");
            } else {
                setText("(" + player.nationality + ")");
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

    private void updatePlayerButtons() {
        for (Widget w : playerButtons) {
            w.setChanged(true);
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
