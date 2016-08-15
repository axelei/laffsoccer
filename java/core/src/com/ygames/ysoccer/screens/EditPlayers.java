package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

public class EditPlayers extends GlScreen {

    FileHandle fileHandle;
    League league;
    Team team;
    int selectedPly;
    boolean modified;

    Widget[] numberButtons = new Widget[Const.FULL_TEAM];
    Widget[] nameButtons = new Widget[Const.FULL_TEAM];
    Widget[] shirtNameButtons = new Widget[Const.FULL_TEAM];
    Widget[] nationalityButtons = new Widget[Const.FULL_TEAM];
    Widget[] roleButtons = new Widget[Const.FULL_TEAM];

    Widget saveButton;

    public EditPlayers(GlGame game, FileHandle fileHandle, League league, Team team, Boolean modified) {
        super(game);
        this.fileHandle = fileHandle;
        this.league = league;
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

            w = new PlayerNationalityButton(p);
            nationalityButtons[p] = w;
            updateNationalityButton(p);
            widgets.add(w);

            w = new PlayerRoleButton(p);
            roleButtons[p] = w;
            updateRoleButton(p);
            widgets.add(w);
        }

        w = new TeamNameButton();
        widgets.add(w);

        w = new SaveButton();
        saveButton = w;
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        selectedWidget = w;
    }

    void setModified() {
        modified = true;
        saveButton.setChanged(true);
    }

    class PlayerNumberButton extends InputButton {

        Player player;

        public PlayerNumberButton(int p) {
            player = team.playerAtPosition(p);
            setGeometry(248, 86 + 17 * p, 52, 17);
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
            setGeometry(304, 86 + 17 * p, 364, 17);
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
            setGeometry(672, 86 + 17 * p, 194, 17);
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

    class PlayerNationalityButton extends Button {

        int p;

        public PlayerNationalityButton(int p) {
            this.p = p;
            setGeometry(870, 86 + 17 * p, 56, 17);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateNationality(1);
        }

        @Override
        public void onFire1Hold() {
            updateNationality(1);
        }

        @Override
        public void onFire2Down() {
            updateNationality(-1);
        }

        @Override
        public void onFire2Hold() {
            updateNationality(-1);
        }

        private void updateNationality(int n) {
            Player player = team.playerAtPosition(p);
            int i = Assets.associations.indexOf(player.nationality);
            if (i != 1) {
                i = Emath.rotate(i, 0, Assets.associations.size(), n);
                player.nationality = Assets.associations.get(i);
            }
            updateNationalityButton(p);
            setModified();
        }
    }

    void updateNationalityButton(int p) {
        if (p < team.players.size()) {
            Player player = team.playerAtPosition(p);
            nationalityButtons[p].setText("(" + player.nationality + ")");
        } else {
            nationalityButtons[p].setText("");
        }
        nationalityButtons[p].setActive((p < team.players.size()) && (team.type == Team.Type.CLUB));
    }

    class PlayerRoleButton extends Button {

        int p;

        public PlayerRoleButton(int p) {
            this.p = p;
            setGeometry(930, 86 + 17 * p, 30, 17);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateRole(1);
        }

        @Override
        public void onFire1Hold() {
            updateRole(1);
        }

        @Override
        public void onFire2Down() {
            updateRole(-1);
        }

        @Override
        public void onFire2Hold() {
            updateRole(-1);
        }

        private void updateRole(int n) {
            Player player = team.playerAtPosition(p);
            player.role = Player.Role.values()[Emath.rotate(player.role.ordinal(), Player.Role.GOALKEEPER.ordinal(), Player.Role.ATTACKER.ordinal(), n)];
            updateRoleButton(p);
            setModified();
        }
    }

    void updateRoleButton(int p) {
        if (p < team.players.size()) {
            Player player = team.playerAtPosition(p);
            roleButtons[p].setText(Assets.strings.get(player.getRoleLabel()));
        } else {
            roleButtons[p].setText("");
        }
        roleButtons[p].setActive(p < team.players.size());
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

    class SaveButton extends Button {

        public SaveButton() {
            setGeometry(788, 660, 160, 36);
            setText(Assets.strings.get("SAVE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            if (modified) {
                setColors(0xDC0000, 0xFF4141, 0x8C0000);
                setActive(true);
            } else {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            // TODO: save team
            game.setScreen(new SelectTeam(game, fileHandle, league));
        }
    }

    class ExitButton extends Button {
        public ExitButton() {
            setGeometry(958, 660, 160, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectTeam(game, fileHandle, league));
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
