package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

public class ViewTeam extends GlScreen {

    public ViewTeam(GlGame game, Team team, Competition competition) {
        super(game);

        background = game.stateBackground;

        Widget w;

        // players
        for (int p = 0; p < Const.FULL_TEAM; p++) {
            Player player = team.playerAtPosition(p);

            w = new PlayerNumberLabel(p, player);
            widgets.add(w);

            w = new PlayerNameButton(p, player, team, competition);
            widgets.add(w);

            int x = 360;
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
        }

        w = new TitleBar(team);
        widgets.add(w);

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
            setGeometry(84, 126 + 18 * p, 276, 17);
            setText("", Font.Align.LEFT, Assets.font10);
            setPlayerWidgetColor(this, p, team, competition);
            if (player != null) {
                setText(player.name.toUpperCase());
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
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null && skills != null) {
                setText(Assets.strings.get(player.getSkillLabel(skills[j])));
            }
        }
    }

    class TitleBar extends Button {

        public TitleBar(Team team) {
            setGeometry((game.settings.GUI_WIDTH - 600) / 2, 45, 601, 41);
            setColors(0x6A5ACD, 0x8F83D7, 0x372989);
            setText(team.name.toUpperCase(), Font.Align.CENTER, Assets.font14);
            setActive(false);
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
