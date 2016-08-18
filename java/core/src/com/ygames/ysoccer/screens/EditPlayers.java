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

import java.util.Collections;

public class EditPlayers extends GlScreen {

    FileHandle fileHandle;
    League league;
    Team team;
    int selectedPos;
    boolean modified;

    Widget[] selectButtons = new Widget[Const.FULL_TEAM];
    Widget[] numberButtons = new Widget[Const.FULL_TEAM];
    Widget[] nameButtons = new Widget[Const.FULL_TEAM];
    Widget[] shirtNameButtons = new Widget[Const.FULL_TEAM];
    Widget[] nationalityButtons = new Widget[Const.FULL_TEAM];
    Widget[] roleButtons = new Widget[Const.FULL_TEAM];

    Widget newPlayerButton;
    Widget deletePlayerButton;
    Widget saveButton;
    Widget tmpPlayerButton;

    public EditPlayers(GlGame game, FileHandle fileHandle, League league, Team team, Boolean modified) {
        super(game);
        this.fileHandle = fileHandle;
        this.league = league;
        this.team = team;
        selectedPos = -1;
        this.modified = modified;

        background = game.stateBackground;

        Widget w;

        // players
        for (int p = 0; p < Const.FULL_TEAM; p++) {
            w = new PlayerSelectButton(p);
            selectButtons[p] = w;
            updateSelectButton(p);
            widgets.add(w);

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

        w = new TmpPlayerButton();
        tmpPlayerButton = w;
        widgets.add(w);

        w = new TeamNameButton();
        widgets.add(w);

        w = new EditTeamButton();
        widgets.add(w);

        selectedWidget = w;

        w = new NewPlayerButton();
        newPlayerButton = w;
        widgets.add(w);

        w = new DeletePlayerButton();
        deletePlayerButton = w;
        widgets.add(w);

        w = new SaveButton();
        saveButton = w;
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    void setModifiedFlag() {
        modified = true;
        saveButton.setChanged(true);
    }

    private void copyPlayer(Player player) {
        if (game.tmpPlayer == null) {
            game.tmpPlayer = new Player();
            game.tmpPlayer.skills = new Player.Skills();
        }
        game.tmpPlayer.copyFrom(player);
        tmpPlayerButton.setChanged(true);
    }

    private void pastePlayer(Player player) {
        player.copyFrom(game.tmpPlayer);
        game.tmpPlayer = null;
        tmpPlayerButton.setChanged(true);
    }

    private void clearPlayer() {
        game.tmpPlayer = null;
        tmpPlayerButton.setChanged(true);
    }

    class PlayerSelectButton extends Button {

        int pos;
        Player player;

        public PlayerSelectButton(int pos) {
            this.pos = pos;
            setGeometry(180, 96 + 17 * pos, 24, 17);
        }

        @Override
        public void onFire1Down() {
            // select
            if (selectedPos == -1) {
                selectedPos = pos;
                Player player = team.playerAtPosition(selectedPos);
                copyPlayer(player);
            }

            // deselect
            else if (selectedPos == pos) {
                selectedPos = -1;
                clearPlayer();
            }

            // swap
            else {
                int ply1 = team.playerIndexAtPosition(selectedPos);
                int ply2 = team.playerIndexAtPosition(pos);

                Collections.swap(team.players, ply1, ply2);

                int oldSelected = selectedPos;
                selectedPos = -1;

                updatePlayerButtons(oldSelected);
                setModifiedFlag();
            }

            updatePlayerButtons(pos);
            deletePlayerButton.setChanged(true);
        }
    }

    void updateSelectButton(int p) {
        setPlayerWidgetColor(selectButtons[p], p);
        if (p < team.players.size()) {
            ((PlayerSelectButton) selectButtons[p]).player = team.playerAtPosition(p);
        } else {
            ((PlayerSelectButton) selectButtons[p]).player = null;
        }
        selectButtons[p].setActive(p < team.players.size());
    }

    class PlayerNumberButton extends InputButton {

        Player player;

        public PlayerNumberButton(int p) {
            player = team.playerAtPosition(p);
            setGeometry(208, 96 + 17 * p, 52, 17);
            setText("", Font.Align.CENTER, Assets.font10);
            setEntryLimit(3);
        }

        @Override
        public void onUpdate() {
            if (player != null && !player.number.equals(text)) {
                player.number = text;
                setModifiedFlag();
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
            setGeometry(264, 96 + 17 * p, 364, 17);
            setText("", Font.Align.LEFT, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onUpdate() {
            if (player != null && !player.name.equals(text)) {
                player.name = text;
                setModifiedFlag();
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
            setGeometry(632, 96 + 17 * p, 194, 17);
            setText("", Font.Align.LEFT, Assets.font10);
            setEntryLimit(14);
        }

        @Override
        public void onUpdate() {
            if (player != null && !player.shirtName.equals(text)) {
                player.shirtName = text;
                setModifiedFlag();
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
            setGeometry(830, 96 + 17 * p, 56, 17);
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
            if (i != -1) {
                i = Emath.rotate(i, 0, Assets.associations.size() - 1, n);
                player.nationality = Assets.associations.get(i);
            }
            updateNationalityButton(p);
            setModifiedFlag();
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
            setGeometry(890, 96 + 17 * p, 30, 17);
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
            setModifiedFlag();
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
                setModifiedFlag();
            }
        }
    }

    class TmpPlayerButton extends Button {

        public TmpPlayerButton() {
            setGeometry(1100, 30, 24, 17);
            setColors(0x1769BD, 0x3A90E8, 0x10447A);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setVisible(game.tmpPlayer != null);
        }
    }

    class EditTeamButton extends Button {

        public EditTeamButton() {
            setGeometry(168, 660, 160, 36);
            setColors(0x00825F, 0x00C28E, 0x00402F);
            setText(Assets.strings.get("TEAM"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new EditTeam(game, fileHandle, league, team, modified));
        }
    }

    class NewPlayerButton extends Button {

        public NewPlayerButton() {
            setGeometry(338, 660, 210, 36);
            setText(Assets.strings.get("NEW PLAYER"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            if (team.players.size() < Const.FULL_TEAM) {
                setColors(0x1769BD, 0x3A90E8, 0x10447A);
                setActive(true);
            } else {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            Player player = team.newPlayer();

            if (player != null) {
                if (game.tmpPlayer != null) {
                    pastePlayer(player);
                    int oldSelected = selectedPos;
                    selectedPos = -1;
                    if (oldSelected != -1) {
                        updatePlayerButtons(oldSelected);
                    }
                }

                // TODO: create face

                updatePlayerButtons(team.players.size() - 1);
                newPlayerButton.setChanged(true);
                deletePlayerButton.setChanged(true);
                setModifiedFlag();
            }
        }
    }

    class DeletePlayerButton extends Button {

        public DeletePlayerButton() {
            setGeometry(558, 660, 220, 36);
            setText(Assets.strings.get("DELETE PLAYER"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onUpdate() {
            if ((selectedPos != -1) && (team.players.size() > Const.BASE_TEAM)) {
                setColors(0x3217BD, 0x5639E7, 0x221080);
                setActive(true);
            } else {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            if ((selectedPos != -1) && (team.players.size() > Const.BASE_TEAM)) {

                // swap 'selected' and 'last' player
                int ply1 = team.playerIndexAtPosition(selectedPos);
                int ply2 = team.playerIndexAtPosition(team.players.size() - 1);

                Collections.swap(team.players, ply1, ply2);

                int oldSelected = selectedPos;
                selectedPos = -1;
                updatePlayerButtons(oldSelected);

                Player player = team.playerAtPosition(team.players.size() - 1);
                team.deletePlayer(player);
                updatePlayerButtons(team.players.size());

                newPlayerButton.setChanged(true);
                deletePlayerButton.setChanged(true);

                setModifiedFlag();
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
            FileHandle fh = Assets.teamsFolder.child(team.path);
            team.path = null;
            Assets.json.toJson(team, Team.class, fh);

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
        if (selectedPos == ply) {
            b.setColors(0x993333, 0xC24242, 0x5A1E1E);
        }
    }

    void updatePlayerButtons(int p) {
        updateSelectButton(p);
        updateNumberButton(p);

        updateNameButton(p);
        updateShirtNameButton(p);
        updateNationalityButton(p);
        updateRoleButton(p);
    }
}
