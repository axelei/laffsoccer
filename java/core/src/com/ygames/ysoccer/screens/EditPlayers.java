package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

public class EditPlayers extends GlScreen {

    Team team;
    int selectedPly;
    boolean modified;

    Widget[] numberButtons = new Widget[Const.FULL_TEAM];
    Widget[] nameButtons = new Widget[Const.FULL_TEAM];
    Widget[] shirtNameButtons = new Widget[Const.FULL_TEAM];

    public EditPlayers(GlGame game, Team team, Boolean modified) {
        super(game);
        this.team = team;
        selectedPly = -1;
        this.modified = modified;

        background = game.stateBackground;

        Widget w;

        // players
        for (int p = 0; p < Const.FULL_TEAM; p++) {
            w = new PlayerNumberButton(p);
            numberButtons[p] = w;
            updateNumberButton(p);
            widgets.add(w);

            w = new PlayerNameButton(p);
            nameButtons[p] = w;
            updateNameButton(p);
            widgets.add(w);

            w = new PlayerShirtNameButton(p);
            shirtNameButtons[p] = w;
            updateShirtNameButton(p);
            widgets.add(w);
        }

        w = new TeamNameButton();
        widgets.add(w);

        selectedWidget = w;
    }

    void setModified() {
        modified = true;
    }

    class PlayerNumberButton extends InputButton {

        Player player;

        public PlayerNumberButton(int p) {
            player = team.playerAtPosition(p);
            setGeometry(248, 86 + 18 * p, 52, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            setEntryLimit(3);
        }

        @Override
        public void onUpdate() {
            if (player != null && !player.number.equals(text)) {
                player.number = text;
                setModified();
            }
        }
    }

    void updateNumberButton(int p) {
        if (p < team.players.size()) {
            Player player = team.playerAtPosition(p);
            numberButtons[p].setText(player.number);
        } else {
            numberButtons[p].setText("");
        }
        numberButtons[p].setActive(p < team.players.size());
    }

    class PlayerNameButton extends InputButton {

        Player player;

        public PlayerNameButton(int p) {
            player = team.playerAtPosition(p);
            setGeometry(304, 86 + 18 * p, 364, 17);
            setText("", Font.Align.LEFT, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onUpdate() {
            if (player != null && !player.name.equals(text)) {
                player.name = text;
                setModified();
            }
        }
    }

    void updateNameButton(int p) {
        setPlayerWidgetColor(nameButtons[p], p);
        if (p < team.players.size()) {
            Player player = team.playerAtPosition(p);
            nameButtons[p].setText(player.name);
        } else {
            nameButtons[p].setText("");
        }
        nameButtons[p].setActive(p < team.players.size());
    }

    class PlayerShirtNameButton extends InputButton {

        Player player;

        public PlayerShirtNameButton(int p) {
            player = team.playerAtPosition(p);
            setGeometry(672, 86 + 18 * p, 194, 17);
            setText("", Font.Align.LEFT, Assets.font10);
            setEntryLimit(14);
        }

        @Override
        public void onUpdate() {
            if (player != null && !player.shirtName.equals(text)) {
                player.shirtName = text;
                setModified();
            }
        }
    }

    void updateShirtNameButton(int p) {
        setPlayerWidgetColor(shirtNameButtons[p], p);
        if (p < team.players.size()) {
            Player player = team.playerAtPosition(p);
            shirtNameButtons[p].setText(player.shirtName);
        } else {
            shirtNameButtons[p].setText("");
        }
        shirtNameButtons[p].setActive(p < team.players.size());
    }

    class TeamNameButton extends InputButton {

        public TeamNameButton() {
            setGeometry(188, 30, 520, 40);
            setColors(0x9C522A, 0xBB5A25, 0x69381D);
            setText(team.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(16);
        }

        @Override
        public void onUpdate() {
            if (!team.name.equals(text)) {
                team.name = text;
                setModified();
            }
        }
    }

    void setPlayerWidgetColor(Widget b, int ply) {
        // goalkeeper
        if (ply == 0) {
            b.setColors(0x4AC058, 0x81D38B, 0x308C3B);
        }

        // other players
        else if (ply < 11) {
            b.setColors(0x308C3B, 0x4AC058, 0x1F5926);
        }

        // bench
        else if (ply < Const.TEAM_SIZE + game.settings.benchSize) {
            b.setColors(0x2C7231, 0x40984A, 0x19431C);
        }

        // reserve
        else if (ply < team.players.size()) {
            b.setColors(0x404040, 0x606060, 0x202020);
        }

        // void
        else {
            b.setColors(0x202020, 0x404040, 0x101010);
        }

        // selected
        if (selectedPly == ply) {
            b.setColors(0x993333, 0xC24242, 0x5A1E1E);
        }
    }
}
