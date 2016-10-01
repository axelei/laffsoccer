package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetTeam extends GlScreen {

    Competition competition;
    Team homeTeam;
    Team awayTeam;
    int teamToSet;

    Team ownTeam;
    Team opponentTeam;
    Team shownTeam;
    int selectedPos;
    Font font10yellow;

    List<Widget> playerButtons = new ArrayList<Widget>();
    Widget[] tacticsButtons = new Widget[18];

    public SetTeam(GlGame game, Competition competition, Team homeTeam, Team awayTeam, int teamToSet) {
        super(game);

        this.competition = competition;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.teamToSet = teamToSet;
        if (teamToSet == Match.HOME) {
            ownTeam = homeTeam;
            opponentTeam = awayTeam;
        } else {
            ownTeam = awayTeam;
            opponentTeam = homeTeam;
        }
        shownTeam = ownTeam;
        selectedPos = -1;

        background = new Image("images/backgrounds/menu_set_team.jpg");

        font10yellow = new Font(10, new RgbPair(0xFCFCFC, 0xFCFC00));
        font10yellow.load();

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

            int x = 458;
            if (shownTeam.type == Team.Type.CLUB) {
                if (game.settings.useFlags) {
                    w = new PlayerNationalityFlagButton(pos);
                    playerButtons.add(w);
                    widgets.add(w);
                    x += 26;
                } else {
                    w = new PlayerNationalityCodeButton(pos);
                    playerButtons.add(w);
                    widgets.add(w);
                    x += 58;
                }
            }

            w = new PlayerRoleButton(x, pos);
            playerButtons.add(w);
            widgets.add(w);
            x += 32;

            for (int skillIndex = 0; skillIndex < 3; skillIndex++) {
                w = new PlayerSkillButton(pos, skillIndex, x);
                playerButtons.add(w);
                widgets.add(w);
                x += 14;
            }

            w = new PlayerStarsButton(pos, x);
            playerButtons.add(w);
            widgets.add(w);
        }

        for (int t = 0; t < 18; t++) {
            w = new TacticsButton(t);
            tacticsButtons[t] = w;
            widgets.add(w);
        }

        w = new OpponentTeamButton();
        widgets.add(w);

        // team name
        w = new TeamNameButton();
        widgets.add(w);

        w = new PlayMatchButton();
        widgets.add(w);

        selectedWidget = w;
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
            Player player = shownTeam.playerAtPosition(pos);
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
            Player player = shownTeam.playerAtPosition(pos);
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
            Player player = shownTeam.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.name);
                setActive(shownTeam == ownTeam);
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
                int ply1 = ownTeam.playerIndexAtPosition(selectedPos);
                int ply2 = ownTeam.playerIndexAtPosition(pos);

                Collections.swap(ownTeam.players, ply1, ply2);

                selectedPos = -1;
            }
            updatePlayerButtons();
        }
    }

    class PlayerNationalityFlagButton extends Button {

        int pos;

        public PlayerNationalityFlagButton(int pos) {
            this.pos = pos;
            setGeometry(458, 126 + 19 * pos, 24, 17);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = shownTeam.playerAtPosition(pos);
            if (player == null) {
                image = null;
            } else {
                image = Assets.getNationalityFlag(player.nationality);
            }
        }
    }

    class PlayerNationalityCodeButton extends Button {

        int pos;

        public PlayerNationalityCodeButton(int pos) {
            this.pos = pos;
            setGeometry(458, 126 + 19 * pos, 56, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = shownTeam.playerAtPosition(pos);
            if (player == null) {
                setText("");
            } else {
                setText("(" + player.nationality + ")");
            }
        }
    }

    class PlayerRoleButton extends Button {

        int pos;

        public PlayerRoleButton(int x, int pos) {
            this.pos = pos;
            setGeometry(x, 126 + 19 * pos, 30, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = shownTeam.playerAtPosition(pos);
            if (player == null) {
                setText("");
            } else {
                setText(Assets.strings.get(player.getRoleLabel()));
            }
        }
    }

    class PlayerSkillButton extends Button {

        int pos;
        int skillIndex;

        public PlayerSkillButton(int pos, int skillIndex, int x) {
            this.pos = pos;
            this.skillIndex = skillIndex;
            setGeometry(x, 126 + 19 * pos, 12, 17);
            setText("", Font.Align.CENTER, font10yellow);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = shownTeam.playerAtPosition(pos);
            if (player == null) {
                setText("");
            } else {
                Player.Skill[] skills = player.getOrderedSkills();
                if (skills == null) {
                    setText("");
                } else {
                    setText(Assets.strings.get(Player.getSkillLabel(skills[skillIndex])));
                }
            }
        }
    }

    class PlayerStarsButton extends Button {

        int pos;

        public PlayerStarsButton(int pos, int x) {
            this.pos = pos;
            setGeometry(x, 126 + 19 * pos, 64, 16);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = shownTeam.playerAtPosition(pos);
            if (player == null) {
                image = null;
            } else {
                image = Assets.stars[Emath.floor((player.getValue() + 3) / 5.5)];
            }
        }
    }

    class TacticsButton extends Button {

        int t;

        public TacticsButton(int t) {
            this.t = t;
            setGeometry(game.settings.GUI_WIDTH - 30 - 90, 126 + 20 * t, 90, 18);
            setText(Tactics.codes[t], Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onUpdate() {
            if (shownTeam.getTacticsIndex() == t) {
                setColors(0x9D7B03, 0xE2B004, 0x675103);
            } else {
                setColors(0xE2B004, 0xFCCE30, 0x9D7B03);
            }
            setActive(shownTeam == ownTeam);
        }

        @Override
        public void onFire1Down() {
            if (shownTeam.getTacticsIndex() != t) {
                shownTeam.tactics = Tactics.codes[t];
                updateTacticsButtons();
                updatePlayerButtons();
            }
        }
    }

    class OpponentTeamButton extends Button {

        public OpponentTeamButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 115, 500, 175, 34);
            setColors(0x8B2323, 0xBF4531, 0x571717);
            setText(Assets.strings.get("OPPONENT TEAM"), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onUpdate() {
            setVisible(shownTeam == ownTeam);
        }

        @Override
        public void onFire1Down() {
            if (shownTeam == ownTeam) {
                shownTeam = opponentTeam;
            } else {
                shownTeam = ownTeam;
            }
            for (Widget w : widgets) {
                w.setChanged(true);
            }
        }
    }

    class TeamNameButton extends Button {

        public TeamNameButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 300, 45, 601, 41);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(shownTeam.name);
            if (shownTeam == ownTeam) {
                setColors(0x6A5ACD, 0x8F83D7, 0x372989);
            } else {
                setColors(0xC14531, 0xDF897B, 0x8E3324);
            }
        }
    }

    class PlayMatchButton extends Button {

        public PlayMatchButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 115, game.settings.GUI_HEIGHT - 44 / 2 - 60, 200, 44);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(Assets.strings.get("PLAY MATCH"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setVisible(shownTeam == ownTeam);
        }

        @Override
        public void onFire1Down() {
            if (teamToSet == Match.HOME && opponentTeam.controlMode != Team.ControlMode.COMPUTER) {
                game.setScreen(new SetTeam(game, competition, homeTeam, awayTeam, Match.AWAY));
            } else {
                // TODO: game.setScreen(new MatchPresentation(game, competition, homeTeam, awayTeam));
            }
        }
    }

    private void updatePlayerButtons() {
        for (Widget w : playerButtons) {
            w.setChanged(true);
        }
    }

    private void updateTacticsButtons() {
        for (Widget w : tacticsButtons) {
            w.setChanged(true);
        }
    }

    private void setPlayerWidgetColor(Widget b, int pos) {
        if (shownTeam == ownTeam) {
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
            else if (pos < shownTeam.players.size()) {
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
            else if (pos < shownTeam.players.size()) {
                b.setColors(0x404040, 0x606060, 0x202020);
            }
            // void
            else {
                b.setColors(0x202020, 0x404040, 0x101010);
            }
        }
    }
}
