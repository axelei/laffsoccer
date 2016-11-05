package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

public class ViewTeam extends GLScreen {

    Font font10yellow;

    public ViewTeam(GLGame game, Team team, Competition competition) {
        super(game);

        background = game.stateBackground;

        font10yellow = new Font(10, new RgbPair(0xFCFCFC, 0xFCFC00));
        font10yellow.load();

        Widget w;

        // players
        int x = 0;
        for (int p = 0; p < Const.FULL_TEAM; p++) {
            Player player = team.playerAtPosition(p);

            w = new PlayerNumberLabel(p, player);
            widgets.add(w);

            w = new PlayerNameButton(p, player, team, competition);
            widgets.add(w);

            x = 448;
            if (team.type == Team.Type.CLUB) {
                w = new PlayerNationalityLabel(x, p, player);
                widgets.add(w);
                x = x + 57;
            }

            w = new PlayerRoleLabel(x, p, player);
            widgets.add(w);
            x = x + 31;

            Player.Skill[] orderedSkills = null;
            if (player != null) {
                orderedSkills = player.getOrderedSkills();
            }
            for (int j = 0; j < 3; j++) {
                w = new PlayerSkillLabel(orderedSkills, j, x, p, player);
                widgets.add(w);
                x = x + 12;
            }
            x = x + 31;

            w = new PlayerGoalsLabel(x, p, player);
            widgets.add(w);
        }

        w = new Label();
        w.setText(Assets.strings.get("GOALS"), Font.Align.CENTER, Assets.font10);
        w.setPosition(x + 15, 116);
        widgets.add(w);

        w = new TitleBar(team);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        selectedWidget = w;
    }

    class PlayerNumberLabel extends Label {

        public PlayerNumberLabel(int p, Player player) {
            setGeometry(54, 126 + 18 * p, 30, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText(player.number);
            }
        }
    }

    class PlayerNameButton extends Button {

        public PlayerNameButton(int p, Player player, Team team, Competition competition) {
            setGeometry(84, 126 + 18 * p, 364, 17);
            setText("", Font.Align.LEFT, Assets.font10);
            setPlayerWidgetColor(this, p, team, competition);
            if (player != null) {
                setText(player.name);
            }
            setActive(false);
        }
    }

    class PlayerNationalityLabel extends Label {

        public PlayerNationalityLabel(int x, int p, Player player) {
            setGeometry(x + 3, 126 + 18 * p, 54, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText("(" + player.nationality + ")");
            }
        }
    }

    class PlayerRoleLabel extends Label {
        public PlayerRoleLabel(int x, int p, Player player) {
            setGeometry(x, 126 + 18 * p, 30, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText(Assets.strings.get(player.getRoleLabel()));
            }
        }
    }

    class PlayerSkillLabel extends Label {

        public PlayerSkillLabel(Player.Skill[] skills, int j, int x, int p, Player player) {
            setGeometry(x, 126 + 18 * p, 12, 17);
            setText("", Font.Align.CENTER, font10yellow);
            if (player != null && skills != null) {
                setText(Assets.strings.get(Player.getSkillLabel(skills[j])));
            }
        }
    }

    class PlayerGoalsLabel extends Label {

        public PlayerGoalsLabel(int x, int p, Player player) {
            setGeometry(x, 126 + 18 * p, 30, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText(player.goals);
            }
        }
    }

    class TitleBar extends Button {

        public TitleBar(Team team) {
            setGeometry((game.settings.GUI_WIDTH - 600) / 2, 45, 601, 41);
            setColors(0x6A5ACD, 0x8F83D7, 0x372989);
            setText(team.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setGeometry(game.settings.GUI_WIDTH - 185 - 30, 660, 145, 40);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new CompetitionViewTeams(game));
        }
    }

    static void setPlayerWidgetColor(Widget w, int p, Team team, Competition competition) {
        // goalkeeper
        if (p == 0) {
            w.setColors(0x00A7DE, 0x33CCFF, 0x005F7E);
        }
        // other player
        else if (p < Const.TEAM_SIZE) {
            w.setColors(0x003FDE, 0x255EFF, 0x00247E);
        }
        // bench
        else if (p < Const.TEAM_SIZE + competition.benchSize) {
            w.setColors(0x111188, 0x2D2DB3, 0x001140);
        }
        // reserve
        else if (p < team.players.size()) {
            w.setColors(0x404040, 0x606060, 0x202020);
        }
        // empty
        else {
            w.setColors(0x202020, 0x404040, 0x101010);
        }
    }

}
