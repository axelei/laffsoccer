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

class ViewTeam extends GLScreen {

    private Font font10yellow;

    ViewTeam(GLGame game, Team team, Competition competition) {
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
                x = x + 58;
            }

            w = new PlayerRoleLabel(x, p, player);
            widgets.add(w);
            x = x + 34;

            Player.Skill[] orderedSkills = null;
            if (player != null) {
                orderedSkills = player.getOrderedSkills();
            }
            for (int j = 0; j < 3; j++) {
                w = new PlayerSkillLabel(orderedSkills, j, x, p, player);
                widgets.add(w);
                x = x + 13;
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

        setSelectedWidget(w);
    }

    private class PlayerNumberLabel extends Label {

        PlayerNumberLabel(int p, Player player) {
            setGeometry(54, 126 + 22 * p, 30, 20);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText(player.number);
            }
        }
    }

    private class PlayerNameButton extends Button {

        PlayerNameButton(int p, Player player, Team team, Competition competition) {
            setGeometry(84, 126 + 22 * p, 364, 20);
            setText("", Font.Align.LEFT, Assets.font10);
            setPlayerWidgetColor(this, p, team, competition);
            if (player != null) {
                setText(player.name);
            }
            setActive(false);
        }
    }

    private class PlayerNationalityLabel extends Label {

        PlayerNationalityLabel(int x, int p, Player player) {
            setGeometry(x + 3, 126 + 22 * p, 58, 20);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText("(" + player.nationality + ")");
            }
        }
    }

    private class PlayerRoleLabel extends Label {

        PlayerRoleLabel(int x, int p, Player player) {
            setGeometry(x, 126 + 22 * p, 34, 20);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText(Assets.strings.get(player.getRoleLabel()));
            }
        }
    }

    private class PlayerSkillLabel extends Label {

        PlayerSkillLabel(Player.Skill[] skills, int j, int x, int p, Player player) {
            setGeometry(x, 126 + 22 * p, 13, 20);
            setText("", Font.Align.CENTER, font10yellow);
            if (player != null && skills != null) {
                setText(Assets.strings.get(Player.getSkillLabel(skills[j])));
            }
        }
    }

    private class PlayerGoalsLabel extends Label {

        PlayerGoalsLabel(int x, int p, Player player) {
            setGeometry(x, 126 + 22 * p, 30, 20);
            setText("", Font.Align.CENTER, Assets.font10);
            if (player != null) {
                setText(game.competition.getScorerGoals(player));
            }
        }
    }

    private class TitleBar extends Button {

        TitleBar(Team team) {
            setGeometry((game.gui.WIDTH - 600) / 2, 45, 601, 41);
            setColors(0x005DDE);
            setText(team.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry(game.gui.WIDTH - 185 - 30, 660, 145, 40);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new CompetitionViewTeams(game));
        }
    }

    private void setPlayerWidgetColor(Widget w, int pos, Team team, Competition competition) {
        // goalkeeper
        if (pos == 0) {
            w.setColors(0x0094DE);
        }
        // other player
        else if (pos < Const.TEAM_SIZE) {
            w.setColors(0x005DDE);
        }
        // bench / out
        else if (pos < team.players.size()) {
            // bench
            if (pos < Const.TEAM_SIZE + competition.benchSize) {
                w.setColors(0x0046A6);
            }
            // out
            else {
                w.setColors(0x303030);
            }
        }
        // void
        else {
            w.setColors(0x101010);
        }
    }
}
