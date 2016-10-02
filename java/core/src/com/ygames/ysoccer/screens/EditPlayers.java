package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlColor3;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
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
    Image[] imageSkill = new Image[8];

    Widget[] hairColorButtons = new Widget[Const.FULL_TEAM];
    Widget[] hairStyleButtons = new Widget[Const.FULL_TEAM];
    Widget[] skinColorButtons = new Widget[Const.FULL_TEAM];
    Widget[] selectButtons = new Widget[Const.FULL_TEAM];
    Widget[] numberButtons = new Widget[Const.FULL_TEAM];
    Widget[] nameButtons = new Widget[Const.FULL_TEAM];
    Widget[] shirtNameButtons = new Widget[Const.FULL_TEAM];
    Widget[] nationalityButtons = new Widget[Const.FULL_TEAM];
    Widget[] roleButtons = new Widget[Const.FULL_TEAM];
    Widget[][] skillButtons = new Widget[Const.FULL_TEAM][7];
    Widget[] priceButtons = new Widget[Const.FULL_TEAM];

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

        background = new Image("images/backgrounds/menu_edit_players.jpg");

        Texture texture = new Texture("images/skill.png");
        for (int i = 0; i < 8; i++) {
            imageSkill[i] = new Image(texture, 32 * i, 0, 32, 13);
        }

        Widget w;

        // players
        for (int p = 0; p < Const.FULL_TEAM; p++) {
            w = new HairColorButton(p);
            hairColorButtons[p] = w;
            widgets.add(w);

            w = new HairStyleButton(p);
            hairStyleButtons[p] = w;
            widgets.add(w);

            w = new SkinColorButton(p);
            skinColorButtons[p] = w;
            widgets.add(w);

            w = new PlayerSelectButton(p);
            selectButtons[p] = w;
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
            widgets.add(w);

            w = new PlayerRoleButton(p);
            roleButtons[p] = w;
            updateRoleButton(p);
            widgets.add(w);

            for (int i = 0; i < 7; i++) {
                w = new SkillButton(p, Player.Skill.values()[i]);
                skillButtons[p][i] = w;
                widgets.add(w);
            }

            w = new PlayerPriceButton(p);
            priceButtons[p] = w;
            widgets.add(w);
        }

        for (int i = 0; i < 7; i++) {
            w = new SkillLabel(Player.Skill.values()[i]);
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

    class HairColorButton extends Button {

        int pos;

        public HairColorButton(int pos) {
            this.pos = pos;
            setGeometry(309, 98 + 20 * pos, 17, 18);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setActive(false);
            } else {
                GlColor3 hairColor = Assets.getHairColorByName(player.hairColor);
                setColors(hairColor.color2, hairColor.color1, hairColor.color3);
                setActive(true);
            }
        }

        @Override
        public void onFire1Down() {
            updateHairColor(1);
        }

        @Override
        public void onFire1Hold() {
            updateHairColor(1);
        }

        @Override
        public void onFire2Down() {
            updateHairColor(-1);
        }

        @Override
        public void onFire2Hold() {
            updateHairColor(-1);
        }

        private void updateHairColor(int n) {
            Player player = team.playerAtPosition(pos);
            GlColor3 hairColor = Assets.getHairColorByName(player.hairColor);

            int i = Assets.hairColors.indexOf(hairColor);
            i = Emath.rotate(i, 0, Assets.hairColors.size() - 1, n);
            player.hairColor = Assets.hairColors.get(i).name;

            setChanged(true);
            selectButtons[pos].setChanged(true);
            setModifiedFlag();
        }
    }

    class HairStyleButton extends Button {

        int pos;

        public HairStyleButton(int pos) {
            this.pos = pos;
            setGeometry(328, 98 + 20 * pos, 112, 18);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.hairStyle.replace('_', ' '));
                setActive(true);
            }
        }

        @Override
        public void onFire1Down() {
            updateHairStyle(1);
        }

        @Override
        public void onFire1Hold() {
            updateHairStyle(1);
        }

        @Override
        public void onFire2Down() {
            updateHairStyle(-1);
        }

        @Override
        public void onFire2Hold() {
            updateHairStyle(-1);
        }

        private void updateHairStyle(int n) {
            Player player = team.playerAtPosition(pos);

            int i = Assets.hairStyles.indexOf(player.hairStyle);
            if (i == -1) {
                i = 0; // not found, start from 0
            } else {
                i = Emath.rotate(i, 0, Assets.hairStyles.size() - 1, n);
            }
            player.hairStyle = Assets.hairStyles.get(i);
            setChanged(true);
            selectButtons[pos].setChanged(true);
            setModifiedFlag();
        }
    }

    class SkinColorButton extends Button {

        int pos;

        public SkinColorButton(int pos) {
            this.pos = pos;
            setGeometry(290, 98 + 20 * pos, 17, 18);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setActive(false);
            } else {
                GlColor3 skinColor = Assets.getSkinColorByName(player.skinColor);
                setColors(skinColor.color2, skinColor.color1, skinColor.color3);
                setActive(true);
            }
        }

        @Override
        public void onFire1Down() {
            updateSkinColor(1);
        }

        @Override
        public void onFire1Hold() {
            updateSkinColor(1);
        }

        @Override
        public void onFire2Down() {
            updateSkinColor(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSkinColor(-1);
        }

        private void updateSkinColor(int n) {
            Player player = team.playerAtPosition(pos);
            GlColor3 skinColor = Assets.getSkinColorByName(player.skinColor);

            int i = Assets.skinColors.indexOf(skinColor);
            i = Emath.rotate(i, 0, Assets.skinColors.size() - 1, n);
            player.skinColor = Assets.skinColors.get(i).name;

            setChanged(true);
            selectButtons[pos].setChanged(true);
            setModifiedFlag();
        }
    }

    class PlayerSelectButton extends Button {

        int pos;

        public PlayerSelectButton(int pos) {
            this.pos = pos;
            setImagePosition(2, -3);
            setGeometry(264, 98 + 20 * pos, 24, 18);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                image = null;
                setActive(false);
            } else {
                image = player.createFace();
                setActive(true);
            }
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
                clearPlayer();

                updatePlayerButtons(oldSelected);
                setModifiedFlag();
            }

            updatePlayerButtons(pos);
            deletePlayerButton.setChanged(true);
        }
    }

    class PlayerNumberButton extends InputButton {

        Player player;

        public PlayerNumberButton(int p) {
            player = team.playerAtPosition(p);
            setGeometry(32, 98 + 20 * p, 34, 18);
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
            setGeometry(442, 98 + 20 * p, 364, 18);
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
            setGeometry(68, 98 + 20 * p, 194, 18);
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

        int pos;

        public PlayerNationalityButton(int pos) {
            this.pos = pos;
            setGeometry(808, 98 + 20 * pos, 56, 18);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onUpdate() {
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText("(" + player.nationality + ")");
                setActive(team.type == Team.Type.CLUB);
            }
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
            Player player = team.playerAtPosition(pos);
            int i = Assets.associations.indexOf(player.nationality);
            if (i == -1) {
                i = 0; // not found, start from 0
            } else {
                i = Emath.rotate(i, 0, Assets.associations.size() - 1, n);
            }
            player.nationality = Assets.associations.get(i);

            setChanged(true);
            setModifiedFlag();
        }
    }

    class PlayerRoleButton extends Button {

        int p;

        public PlayerRoleButton(int p) {
            this.p = p;
            setGeometry(866, 98 + 20 * p, 30, 18);
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
            updatePlayerButtons(p);
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

    class SkillLabel extends Label {

        public SkillLabel(Player.Skill skill) {
            setGeometry(898 + 38 * skill.ordinal(), 98 - 20, 36, 18);
            setText(Assets.strings.get(Player.getSkillLabel(skill)), Font.Align.CENTER, Assets.font10);
        }
    }

    class SkillButton extends Button {

        int pos;
        Player.Skill skill;

        public SkillButton(int pos, Player.Skill skill) {
            this.pos = pos;
            this.skill = skill;
            setGeometry(898 + 38 * skill.ordinal(), 98 + 20 * pos, 36, 18);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player != null && player.role != Player.Role.GOALKEEPER) {
                image = imageSkill[player.getSkillValue(skill)];
                setActive(true);
            } else {
                image = null;
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            updateSkill(1);
        }

        @Override
        public void onFire1Hold() {
            updateSkill(1);
        }

        @Override
        public void onFire2Down() {
            updateSkill(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSkill(-1);
        }

        private void updateSkill(int n) {
            Player player = team.playerAtPosition(pos);
            int value = Emath.slide(player.getSkillValue(skill), 0, 7, n);
            player.setSkillValue(skill, value);
            setChanged(true);
            priceButtons[pos].setChanged(true);
            setModifiedFlag();
        }

    }

    class PlayerPriceButton extends Button {

        int pos;

        public PlayerPriceButton(int pos) {
            this.pos = pos;
            setGeometry(1164, 98 + 20 * pos, 90, 18);
            setText("", Font.Align.LEFT, Assets.font10);
        }

        @Override
        public void onUpdate() {
            Player player = team.playerAtPosition(pos);
            if (player != null) {
                setText(game.settings.currency + " " + player.getPrice(game.settings.maxPlayerValue));
                setActive(player.role == Player.Role.GOALKEEPER);
            } else {
                setText("");
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            updatePrice(1);
        }

        @Override
        public void onFire1Hold() {
            updatePrice(1);
        }

        @Override
        public void onFire2Down() {
            updatePrice(-1);
        }

        @Override
        public void onFire2Hold() {
            updatePrice(-1);
        }

        private void updatePrice(int n) {
            Player player = team.playerAtPosition(pos);
            player.value = Emath.slide(player.value, 0, 49, n);
            setChanged(true);
            setModifiedFlag();
        }
    }

    class TeamNameButton extends InputButton {

        public TeamNameButton() {
            setGeometry(194, 30, 450, 40);
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
            setGeometry(1000, 30, 194, 24);
            setColors(0x1769BD, 0x3A90E8, 0x10447A);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            if (game.tmpPlayer == null) {
                setText("");
                setVisible(false);
            } else {
                setText(game.tmpPlayer.shirtName);
                setVisible(true);
            }
        }
    }

    class EditTeamButton extends Button {

        public EditTeamButton() {
            setGeometry(100, 660, 206, 36);
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
            setGeometry(310, 660, 226, 36);
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

                updatePlayerButtons(team.players.size() - 1);
                setChanged(true);
                deletePlayerButton.setChanged(true);
                setModifiedFlag();
            }
        }
    }

    class DeletePlayerButton extends Button {

        public DeletePlayerButton() {
            setGeometry(540, 660, 226, 36);
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
                setChanged(true);

                setModifiedFlag();
            }
        }
    }

    class SaveButton extends Button {

        public SaveButton() {
            setGeometry(770, 660, 196, 36);
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
            setGeometry(970, 660, 196, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectTeam(game, fileHandle, league));
        }
    }

    void setPlayerWidgetColor(Widget w, int pos) {
        // goalkeeper
        if (pos == 0) {
            w.setColors(0x4AC058, 0x81D38B, 0x308C3B);
        }

        // other players
        else if (pos < Const.TEAM_SIZE) {
            w.setColors(0x308C3B, 0x4AC058, 0x1F5926);
        }

        // bench
        else if (pos < Const.TEAM_SIZE + game.settings.benchSize) {
            w.setColors(0x2C7231, 0x40984A, 0x19431C);
        }

        // reserve
        else if (pos < team.players.size()) {
            w.setColors(0x404040, 0x606060, 0x202020);
        }

        // void
        else {
            w.setColors(0x202020, 0x404040, 0x101010);
        }

        // selected
        if (selectedPos == pos) {
            w.setColors(0x993333, 0xC24242, 0x5A1E1E);
        }
    }

    void updatePlayerButtons(int pos) {
        hairColorButtons[pos].setChanged(true);
        hairStyleButtons[pos].setChanged(true);
        skinColorButtons[pos].setChanged(true);
        selectButtons[pos].setChanged(true);
        updateNumberButton(pos);
        updateNameButton(pos);
        updateShirtNameButton(pos);
        nationalityButtons[pos].setChanged(true);
        updateRoleButton(pos);
        for (Widget w : skillButtons[pos]) {
            w.setChanged(true);
        }
        priceButtons[pos].setChanged(true);
    }
}
