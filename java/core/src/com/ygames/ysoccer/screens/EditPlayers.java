package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Color3;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.framework.EMath;

import java.util.Collections;

import static com.ygames.ysoccer.framework.Assets.font10;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Assets.teamsRootFolder;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.framework.Font.Align.LEFT;

class EditPlayers extends GLScreen {

    Team team;
    private int selectedPos;
    private boolean modified;

    private TextureRegion[][] imageSkill = new TextureRegion[8][2];

    private Widget[] selectButtons = new Widget[Const.FULL_TEAM];
    private Widget[] priceButtons = new Widget[Const.FULL_TEAM];

    private Widget resetButton;
    private Widget saveExitButton;
    private Widget clipboardPlayerButton;

    EditPlayers(GLGame game, Team team, Boolean modified) {
        super(game);
        this.team = team;
        selectedPos = -1;
        this.modified = modified;

        background = new Texture("images/backgrounds/menu_edit_players.jpg");

        Texture texture = new Texture("images/skill.png");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 2; j++) {
                imageSkill[i][j] = new TextureRegion(texture, 32 * i, 13 * j, 32, 13);
                imageSkill[i][j].flip(false, true);
            }
        }

        Widget w;

        // players
        for (int p = 0; p < Const.FULL_TEAM; p++) {
            w = new HairColorButton(p);
            widgets.add(w);

            w = new HairStyleButton(p);
            widgets.add(w);

            w = new SkinColorButton(p);
            widgets.add(w);

            w = new PlayerSelectButton(p);
            selectButtons[p] = w;
            widgets.add(w);

            w = new PlayerNumberButton(p);
            widgets.add(w);

            w = new PlayerNameButton(p);
            widgets.add(w);

            w = new PlayerShirtNameButton(p);
            widgets.add(w);

            w = new PlayerNationalityButton(p);
            widgets.add(w);

            w = new PlayerRoleButton(p);
            widgets.add(w);

            for (int i = 0; i < 7; i++) {
                w = new SkillButton(p, Player.Skill.values()[i]);
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

        w = new ClipBoardPlayerButton();
        clipboardPlayerButton = w;
        widgets.add(w);

        w = new TeamNameButton();
        widgets.add(w);

        w = new EditTeamButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new NewPlayerButton();
        widgets.add(w);

        w = new DeletePlayerButton();
        widgets.add(w);

        w = new ResetButton();
        resetButton = w;
        widgets.add(w);

        w = new SaveExitButton();
        saveExitButton = w;
        widgets.add(w);
    }

    private void setModifiedFlag() {
        modified = true;
        resetButton.setDirty(true);
        saveExitButton.setDirty(true);
        if (selectedPos != -1) {
            navigation.setClipboardPlayer(team.playerAtPosition(selectedPos));
            clipboardPlayerButton.setDirty(true);
        }
    }

    private class HairColorButton extends Button {

        int pos;

        HairColorButton(int pos) {
            this.pos = pos;
            setGeometry(309, 95 + 21 * pos, 17, 19);
        }

        @Override
        public void refresh() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setActive(false);
            } else {
                Color3 hairColor = Hair.colors[player.hairColor.ordinal()];
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

            int color = player.hairColor.ordinal();
            color = EMath.rotate(color, Hair.Color.BLACK.ordinal(), Hair.Color.PUNK_BLOND.ordinal(), n);
            player.hairColor = Hair.Color.values()[color];

            setDirty(true);
            selectButtons[pos].setDirty(true);
            setModifiedFlag();
        }
    }

    private class HairStyleButton extends Button {

        int pos;

        HairStyleButton(int pos) {
            this.pos = pos;
            setGeometry(328, 95 + 21 * pos, 112, 19);
            setText("", CENTER, font10);
        }

        @Override
        public void refresh() {
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
                i = EMath.rotate(i, 0, Assets.hairStyles.size() - 1, n);
            }
            player.hairStyle = Assets.hairStyles.get(i);
            setDirty(true);
            selectButtons[pos].setDirty(true);
            setModifiedFlag();
        }
    }

    private class SkinColorButton extends Button {

        int pos;

        SkinColorButton(int pos) {
            this.pos = pos;
            setGeometry(290, 95 + 21 * pos, 17, 19);
        }

        @Override
        public void refresh() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setActive(false);
            } else {
                Color3 skinColor = Skin.colors[player.skinColor.ordinal()];
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
            player.skinColor = Skin.Color.values()[EMath.rotate(player.skinColor.ordinal(), Skin.Color.PINK.ordinal(), Skin.Color.RED.ordinal(), n)];

            setDirty(true);
            selectButtons[pos].setDirty(true);
            setModifiedFlag();
        }
    }

    private class PlayerSelectButton extends Button {

        int pos;

        PlayerSelectButton(int pos) {
            this.pos = pos;
            setImagePosition(2, -3);
            setGeometry(264, 95 + 21 * pos, 24, 19);
        }

        @Override
        public void refresh() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                textureRegion = null;
                setActive(false);
            } else {
                textureRegion = player.createFace();
                setActive(true);
            }
        }

        @Override
        public void onFire1Down() {
            // select
            if (selectedPos == -1) {
                selectedPos = pos;
                navigation.setClipboardPlayer(team.playerAtPosition(selectedPos));
            }

            // deselect
            else if (selectedPos == pos) {
                selectedPos = -1;
                navigation.setClipboardPlayer(null);
            }

            // swap
            else {
                int ply1 = team.playerIndexAtPosition(selectedPos);
                int ply2 = team.playerIndexAtPosition(pos);

                Collections.swap(team.players, ply1, ply2);

                selectedPos = -1;
                navigation.setClipboardPlayer(null);
                setModifiedFlag();
            }

            refreshAllWidgets();
        }
    }

    private class PlayerNumberButton extends Button {

        int pos;

        PlayerNumberButton(int pos) {
            this.pos = pos;
            setGeometry(32, 95 + 21 * pos, 34, 19);
            setText("", CENTER, font10);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.number);
                setActive(true);
            }
        }

        @Override
        public void onFire1Down() {
            updateNumber(1);
        }

        @Override
        public void onFire1Hold() {
            updateNumber(1);
        }

        @Override
        public void onFire2Down() {
            updateNumber(-1);
        }

        @Override
        public void onFire2Hold() {
            updateNumber(-1);
        }

        private void updateNumber(int n) {
            Player player = team.playerAtPosition(pos);
            team.rotatePlayerNumber(player, n);
            setDirty(true);
            setModifiedFlag();
        }
    }

    private class PlayerNameButton extends InputButton {

        int pos;

        PlayerNameButton(int pos) {
            this.pos = pos;
            setGeometry(442, 95 + 21 * pos, 364, 19);
            setText("", LEFT, font10);
            setEntryLimit(28);
        }

        @Override
        public void refresh() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.name);
                setActive(true);
            }
        }

        @Override
        public void onChanged() {
            team.playerAtPosition(pos).name = text;
            setModifiedFlag();
        }
    }

    private class PlayerShirtNameButton extends InputButton {

        int pos;

        PlayerShirtNameButton(int pos) {
            this.pos = pos;
            setGeometry(68, 95 + 21 * pos, 194, 19);
            setText("", LEFT, font10);
            setEntryLimit(14);
        }

        @Override
        public void refresh() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.shirtName);
                setActive(true);
            }
        }

        @Override
        public void onChanged() {
            team.playerAtPosition(pos).shirtName = text;
            setModifiedFlag();
        }
    }

    private class PlayerNationalityButton extends InputButton {

        private int pos;

        PlayerNationalityButton(int pos) {
            this.pos = pos;
            setGeometry(808, 95 + 21 * pos, 56, 19);
            setText("", CENTER, font10);
            setEntryLimit(3);
            setInputFilter("[A-Z]");
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.nationality);
                setActive(team.type == Team.Type.CLUB);
            }
        }

        @Override
        public void onChanged() {
            team.playerAtPosition(pos).nationality = text;
            setModifiedFlag();
        }
    }

    private class PlayerRoleButton extends Button {

        int pos;

        PlayerRoleButton(int pos) {
            this.pos = pos;
            setGeometry(866, 95 + 21 * pos, 30, 19);
            setText("", CENTER, font10);
        }

        @Override
        public void refresh() {
            if (pos < team.players.size()) {
                Player player = team.playerAtPosition(pos);
                setText(gettext(player.getRoleLabel()));
            } else {
                setText("");
            }
            setActive(pos < team.players.size());
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
            Player player = team.playerAtPosition(pos);
            player.role = Player.Role.values()[EMath.rotate(player.role.ordinal(), Player.Role.GOALKEEPER.ordinal(), Player.Role.ATTACKER.ordinal(), n)];
            refreshAllWidgets();
            setModifiedFlag();
        }
    }


    private class SkillLabel extends Label {

        SkillLabel(Player.Skill skill) {
            setGeometry(898 + 38 * skill.ordinal(), 95 - 21, 36, 19);
            setText(gettext(Player.getSkillLabel(skill)), CENTER, font10);
        }
    }

    private class SkillButton extends Button {

        int pos;
        Player.Skill skill;

        SkillButton(int pos, Player.Skill skill) {
            this.pos = pos;
            this.skill = skill;
            setGeometry(898 + 38 * skill.ordinal(), 95 + 21 * pos, 36, 19);
        }

        @Override
        public void refresh() {
            setPlayerWidgetColor(this, pos);
            Player player = team.playerAtPosition(pos);
            if (player != null && player.role != Player.Role.GOALKEEPER) {
                boolean isBest = player.bestSkills.contains(skill);
                textureRegion = imageSkill[player.getSkillValue(skill)][isBest ? 1 : 0];
                setActive(true);
            } else {
                textureRegion = null;
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            updateSkill();
        }

        @Override
        public void onFire1Hold() {
            updateSkill();
        }

        private void updateSkill() {
            Player player = team.playerAtPosition(pos);
            int value = EMath.rotate(player.getSkillValue(skill), 0, 7, 1);
            player.setSkillValue(skill, value);
            setDirty(true);
            priceButtons[pos].setDirty(true);
            setModifiedFlag();
        }

        @Override
        public void onFire2Down() {
            if (team.playerAtPosition(pos).toggleBestSkill(skill)) {
                setDirty(true);
                setModifiedFlag();
            }
        }
    }

    private class PlayerPriceButton extends Button {

        int pos;

        PlayerPriceButton(int pos) {
            this.pos = pos;
            setGeometry(1164, 95 + 21 * pos, 90, 19);
            setText("", LEFT, font10);
        }

        @Override
        public void refresh() {
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
            player.value = EMath.slide(player.value, 0, 49, n);
            setDirty(true);
            setModifiedFlag();
        }
    }

    private class TeamNameButton extends InputButton {

        TeamNameButton() {
            setGeometry((game.gui.WIDTH - 450) / 2, 30, 450, 40);
            setColors(0x9C522A, 0xBB5A25, 0x69381D);
            setText(team.name, CENTER, font14);
            setEntryLimit(16);
        }

        @Override
        public void onChanged() {
            team.name = text;
            setModifiedFlag();
        }
    }

    private class ClipBoardPlayerButton extends Button {

        ClipBoardPlayerButton() {
            setGeometry(62, 30, 236, 40);
            setText("", LEFT, font10);
            setActive(false);
            setImageScale(2f, 2f);
            setImagePosition(w - 40, -1);
            setTextOffsetX(6);
        }

        @Override
        public void refresh() {
            Player player = navigation.getClipboardPlayer();
            if (player == null) {
                setColors(0x6B8EB5, 0x10447A, 0x10447A);
                setText("");
                textureRegion = null;
            } else {
                setColors(0x1769BD, 0x10447A, 0x10447A);
                setText(player.shirtName);
                textureRegion = player.createFace();
            }
        }
    }

    private class EditTeamButton extends Button {

        EditTeamButton() {
            setGeometry(80, 660, 206, 36);
            setColors(0x00825F, 0x00C28E, 0x00402F);
            setText(gettext("TEAM"), CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new EditTeam(game, team, modified));
        }
    }

    private class NewPlayerButton extends Button {

        NewPlayerButton() {
            setGeometry(310, 660, 226, 36);
            setText(gettext("NEW PLAYER"), CENTER, font14);
        }

        @Override
        public void refresh() {
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
                if (navigation.getClipboardPlayer() != null) {
                    player.copyFrom(navigation.getClipboardPlayer());
                    navigation.setClipboardPlayer(null);
                    selectedPos = -1;
                }
                refreshAllWidgets();
                setModifiedFlag();
            }
        }
    }

    private class DeletePlayerButton extends Button {

        DeletePlayerButton() {
            setGeometry(540, 660, 226, 36);
            setText(gettext("DELETE PLAYER"), CENTER, font14);
        }

        @Override
        public void refresh() {
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

                selectedPos = -1;

                Player player = team.playerAtPosition(team.players.size() - 1);
                team.deletePlayer(player);

                refreshAllWidgets();
                setModifiedFlag();
            }
        }
    }

    private class ResetButton extends Button {

        ResetButton() {
            setGeometry(790, 660, 196, 36);
            setText(gettext("EDIT.RESET"), CENTER, font14);
        }

        @Override
        public void refresh() {
            if (modified) {
                setColor(0xBDBF2F);
                setActive(true);
            } else {
                setColor(0x666666);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            FileHandle file = teamsRootFolder.child(team.path);
            if (file.exists()) {
                Team team = Assets.json.fromJson(Team.class, file.readString("UTF-8"));
                team.path = Assets.getRelativeTeamPath(file);
                game.setScreen(new EditPlayers(game, team, false));
            }
        }
    }

    private class SaveExitButton extends Button {

        SaveExitButton() {
            setGeometry(990, 660, 196, 36);
            setText(gettext(""), CENTER, font14);
        }

        @Override
        public void refresh() {
            if (modified) {
                setColor(0xDC0000);
                setText(gettext("SAVE"));
            } else {
                setColor(0xC84200);
                setText(gettext("EXIT"));
            }
        }

        @Override
        public void onFire1Down() {
            if (modified) {
                team.persist();
            }
            game.setScreen(new SelectTeam(game));
        }
    }

    private void setPlayerWidgetColor(Widget w, int pos) {
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
}
