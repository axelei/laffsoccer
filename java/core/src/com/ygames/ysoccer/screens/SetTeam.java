package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.TacticsBoard;
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

    private FileHandle fileHandle;
    private League league;
    private Competition competition;
    private Team homeTeam;
    private Team awayTeam;
    private int teamToSet;

    Team ownTeam;
    Team opponentTeam;
    Team shownTeam;
    int selectedPos;
    boolean compareTactics;
    Font font10yellow;

    List<Widget> playerButtons = new ArrayList<Widget>();
    TacticsBoard tacticsBoard;
    Widget[] tacticsButtons = new Widget[18];

    public SetTeam(GlGame game, FileHandle fileHandle, League league, Competition competition, Team homeTeam, Team awayTeam, int teamToSet) {
        super(game);

        this.fileHandle = fileHandle;
        this.league = league;
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
        compareTactics = false;

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

            int x = 528;
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

        tacticsBoard = new TacticsBoard(ownTeam, opponentTeam);
        tacticsBoard.setPosition(game.settings.GUI_WIDTH / 2 + 115, 126);
        widgets.add(tacticsBoard);

        for (int t = 0; t < 18; t++) {
            w = new TacticsButton(t);
            tacticsButtons[t] = w;
            widgets.add(w);
        }

        w = new TacticsComparisonButton();
        widgets.add(w);

        w = new OpponentTeamButton();
        widgets.add(w);

        w = new ControlModeButton();
        widgets.add(w);

        w = new TeamNameButton();
        widgets.add(w);

        w = new PlayMatchButton();
        widgets.add(w);

        selectedWidget = w;

        w = new ExitButton();
        widgets.add(w);
    }

    class PlayerFaceButton extends Button {

        int pos;

        public PlayerFaceButton(int pos) {
            this.pos = pos;
            setGeometry(100, 126 + 20 * pos, 24, 18);
            setImagePosition(2, -2);
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
            setGeometry(126, 126 + 20 * pos, 34, 18);
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
            setGeometry(162, 126 + 20 * pos, 364, 18);
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
            setGeometry(528, 126 + 20 * pos, 24, 18);
            setImagePosition(0, 2);
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
            setGeometry(528, 126 + 20 * pos, 56, 18);
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
            setGeometry(x, 126 + 20 * pos, 30, 18);
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
            setGeometry(x, 126 + 20 * pos, 12, 18);
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
            setGeometry(x, 126 + 20 * pos, 64, 18);
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
            setGeometry(game.settings.GUI_WIDTH - 100 - 90, 126 + 20 * t, 90, 18);
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

    class TacticsComparisonButton extends Button {
        public TacticsComparisonButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 115, 450, 264, 34);
            setColors(0x824200, 0xB46A00, 0x4C2600);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onUpdate() {
            setVisible(shownTeam == ownTeam);
            if (compareTactics) {
                setText(Assets.strings.get("TEAM TACTICS"));
            } else {
                setText(Assets.strings.get("TACTICS COMPARISON"));
            }
        }

        @Override
        public void onFire1Down() {
            compareTactics = !compareTactics;
            tacticsBoard.setCompareTactics(compareTactics);
            setChanged(true);
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
                tacticsBoard.setViewOpponent(true);
            } else {
                shownTeam = ownTeam;
                tacticsBoard.setViewOpponent(false);
            }
            for (Widget w : widgets) {
                w.setChanged(true);
            }
        }
    }

    class ControlModeButton extends Button {
        public ControlModeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 115, 562, 175, 40);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onUpdate() {
            switch (shownTeam.controlMode) {
                case COMPUTER:
                    setText(Assets.strings.get("CONTROL MODE.COMPUTER") + ":");
                    setColors(0x981E1E, 0xC72929, 0x640000);
                    break;
                case PLAYER:
                    setText(Assets.strings.get("CONTROL MODE.PLAYER-COACH") + ":");
                    setColors(0x0000C8, 0x1919FF, 0x000078);
                    break;
                case COACH:
                    setText(Assets.strings.get("CONTROL MODE.COACH") + ":");
                    setColors(0x009BDC, 0x19BBFF, 0x0071A0);
                    break;
            }
            setActive(shownTeam == ownTeam);
        }

        @Override
        public void onFire1Down() {
            switch (ownTeam.controlMode) {
                case PLAYER:
                    ownTeam.controlMode = Team.ControlMode.COACH;
                    // TODO
//                    if (ownTeam.inputDevice == null) {
//                        ownTeam.releaseNonAiInputDevices();
//                        ownTeam.setInputDevice(inputDevices.assignFirstAvailable());
//                    }
                    break;
                case COACH:
                    ownTeam.controlMode = Team.ControlMode.PLAYER;
                    break;
            }
            setChanged(true);
            updatePlayerButtons();
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
                game.setScreen(new SetTeam(game, fileHandle, league, competition, homeTeam, awayTeam, Match.AWAY));
            } else {
                // TODO: game.setScreen(new MatchPresentation(game, competition, homeTeam, awayTeam));
            }
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setGeometry(game.settings.GUI_WIDTH - 145 - 100, game.settings.GUI_HEIGHT - 40 / 2 - 60, 145, 40);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            setVisible(shownTeam == ownTeam);
        }

        @Override
        public void onFire1Down() {
            switch (competition.getType()) {
                case FRIENDLY:
                    game.setScreen(new SelectTeams(game, fileHandle, league, competition));
                    break;
                case LEAGUE:
                    game.setScreen(new PlayLeague(game));
                    break;
                case CUP:
                    game.setScreen(new PlayCup(game));
                    break;
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
