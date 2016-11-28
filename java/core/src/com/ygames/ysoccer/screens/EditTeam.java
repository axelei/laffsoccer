package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.GlColor;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.LogoPicture;
import com.ygames.ysoccer.gui.TacticsBoard;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.Collections;

class EditTeam extends GLScreen {

    private FileHandle fileHandle;
    String league;
    Team team;
    private int selectedKit;
    private int selectedPos;
    private boolean modified;

    private Widget[] tacticsButtons = new Widget[18];

    private Widget[] kitSelectionButtons = new Widget[5];
    private Widget[] kitEditButtons = new Widget[17];

    private Widget[] numberButtons = new Widget[Const.TEAM_SIZE];
    private Widget[] faceButtons = new Widget[Const.TEAM_SIZE];
    private Widget[] nameButtons = new Widget[Const.TEAM_SIZE];
    private Widget[] roleButtons = new Widget[Const.TEAM_SIZE];

    private LogoPicture logoWidget;
    private Widget kitWidget;
    private Widget newKitButton;
    private Widget deleteKitButton;
    private Widget saveButton;

    EditTeam(GLGame game, FileHandle fileHandle, String league, Team team, Boolean modified) {
        super(game);
        this.fileHandle = fileHandle;
        this.league = league;
        this.team = team;
        this.modified = modified;

        selectedKit = 0;
        selectedPos = -1;

        background = new Texture("images/backgrounds/menu_edit_team.jpg");

        Widget w;

        for (int t = 0; t < 18; t++) {
            w = new TacticsButton(t);
            tacticsButtons[t] = w;
            widgets.add(w);
        }

        w = new TeamNameButton();
        widgets.add(w);

        logoWidget = new LogoPicture(team, 135, 50);
        widgets.add(logoWidget);

        w = new CoachLabel();
        widgets.add(w);

        w = new CoachButton();
        widgets.add(w);

        w = new CityLabel();
        widgets.add(w);

        w = new CoachNationalityButton();
        widgets.add(w);

        w = new CityButton();
        widgets.add(w);

        w = new StadiumLabel();
        widgets.add(w);

        w = new StadiumButton();
        widgets.add(w);

        if (team.type == Team.Type.CLUB) {
            w = new CountryLabel();
            widgets.add(w);

            w = new CountryButton();
            widgets.add(w);

            w = new LeagueLabel();
            widgets.add(w);

            w = new LeagueButton();
            widgets.add(w);
        }

        w = new TacticsBoard(team);
        w.setPosition(915, 40);
        widgets.add(w);

        for (int i = 0; i < 5; i++) {
            w = new SelectKitButton(i);
            kitSelectionButtons[i] = w;
            widgets.add(w);
        }

        w = new TeamKit(team);
        w.setPosition(321, 343);
        kitWidget = w;
        widgets.add(w);

        w = new StyleLabel();
        widgets.add(w);

        w = new StyleButton();
        kitEditButtons[0] = w;
        widgets.add(w);

        for (int f = 0; f < 4; f++) {
            w = new KitFieldLabel(Kit.Field.values()[f], 528, 416 + 54 * f);
            widgets.add(w);

            w = new HashButton(Kit.Field.values()[f], 528, 418 + 54 * f + 23);
            widgets.add(w);
            kitEditButtons[1 + 4 * f] = w;

            for (int c = 1; c < 4; c++) {
                w = new ColorComponentButton(Kit.Field.values()[f], GlColor.Component.values()[c], 525 + c * 45, 418 + 54 * f + 23);
                kitEditButtons[1 + 4 * f + c] = w;
                widgets.add(w);
            }
        }

        for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
            w = new PlayerNumberButton(pos);
            numberButtons[pos] = w;
            widgets.add(w);

            w = new PlayerFaceButton(pos);
            faceButtons[pos] = w;
            widgets.add(w);

            w = new PlayerNameButton(pos);
            nameButtons[pos] = w;
            widgets.add(w);

            w = new PlayerRoleButton(pos);
            roleButtons[pos] = w;
            widgets.add(w);

            w = new PlayerSelectButton(pos);
            widgets.add(w);
        }

        w = new EditPlayersButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new NewKitButton();
        newKitButton = w;
        widgets.add(w);

        w = new DeleteKitButton();
        deleteKitButton = w;
        widgets.add(w);

        w = new SaveButton();
        saveButton = w;
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private void setModifiedFlag() {
        modified = true;
        saveButton.setDirty(true);
    }

    private class TacticsButton extends Button {

        int t;

        TacticsButton(int t) {
            this.t = t;
            setGeometry(692 + 94 * (t % 2), 44 + 25 * ((int) Math.floor(t / 2)), 90, 22);
            setText(Tactics.codes[t], Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void refresh() {
            if (team.getTacticsIndex() == t) {
                setColors(0x9D7B03, 0xE2B004, 0x675103);
            } else {
                setColors(0xE2B004, 0xFCCE30, 0x9D7B03);
            }
        }

        @Override
        public void onFire1Down() {
            if (team.getTacticsIndex() != t) {
                team.tactics = Tactics.codes[t];
                for (Widget w : tacticsButtons) {
                    w.setDirty(true);
                }
                setModifiedFlag();
                for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
                    updatePlayerButtons(pos);
                }
            }
        }
    }

    private class TeamNameButton extends InputButton {

        TeamNameButton() {
            setGeometry(194, 30, 450, 40);
            setColors(0x9C522A, 0xBB5A25, 0x69381D);
            setText(team.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(16);
        }

        @Override
        public void onChanged() {
            team.name = text;
            setModifiedFlag();
        }
    }

    private class CoachLabel extends Button {

        CoachLabel() {
            setGeometry(90, 290, 182, 32);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("COACH"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class CoachButton extends InputButton {

        CoachButton() {
            setGeometry(280, 290, 364, 32);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(team.coach.name, Font.Align.CENTER, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onChanged() {
            team.coach.name = text;
            setModifiedFlag();
        }
    }

    private class CoachNationalityButton extends Button {

        CoachNationalityButton() {
            setGeometry(652, 290, 60, 32);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void refresh() {
            setText(team.coach.nationality);
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
            int i = Assets.associations.indexOf(team.coach.nationality);
            if (i == -1) i = 0;
            i = Emath.rotate(i, 0, Assets.associations.size() - 1, n);
            team.coach.nationality = Assets.associations.get(i);
            setDirty(true);
            setModifiedFlag();
        }
    }

    private class CityLabel extends Button {

        CityLabel() {
            setGeometry(90, 200, 182, 32);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("CITY"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class CityButton extends InputButton {

        CityButton() {
            setGeometry(280, 200, 364, 32);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(team.city, Font.Align.CENTER, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onChanged() {
            team.city = text;
            setModifiedFlag();
        }
    }

    private class StadiumLabel extends Button {

        StadiumLabel() {
            setGeometry(90, 245, 182, 32);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("STADIUM"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class StadiumButton extends InputButton {

        private StadiumButton() {
            setGeometry(280, 245, 364, 32);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(team.stadium, Font.Align.CENTER, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onChanged() {
            team.stadium = text;
            setModifiedFlag();
        }
    }

    private class CountryLabel extends Button {

        CountryLabel() {
            setGeometry(90, 110, 182, 32);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("COUNTRY"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class CountryButton extends Button {

        private CountryButton() {
            setGeometry(280, 110, 364, 32);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(team.country, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class LeagueLabel extends Button {

        LeagueLabel() {
            setGeometry(90, 155, 182, 32);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("LEAGUE"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class LeagueButton extends InputButton {

        LeagueButton() {
            setGeometry(280, 155, 364, 32);
            setColors(0x10A000);
            setText(team.league, Font.Align.CENTER, Assets.font10);
            setEntryLimit(24);
        }

        @Override
        public void onChanged() {
            team.league = text;
            setModifiedFlag();
        }
    }

    private class SelectKitButton extends Button {

        int kitIndex;

        SelectKitButton(int kitIndex) {
            this.kitIndex = kitIndex;
            setGeometry(90, 364 + 56 * kitIndex, 190, 39);
            String label = "";
            switch (kitIndex) {
                case 0:
                    label = "KITS.HOME";
                    break;

                case 1:
                    label = "KITS.AWAY";
                    break;

                case 2:
                    label = "KITS.THIRD";
                    break;

                case 3:
                    label = "KITS.1ST CHANGE";
                    break;

                case 4:
                    label = "KITS.2ND CHANGE";
                    break;
            }
            setText(Assets.strings.get(label), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            if (kitIndex >= team.kits.size()) {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            } else {
                if (kitIndex == selectedKit) {
                    setColors(0x881845, 0xDC246E, 0x510F29);
                } else {
                    setColors(0xDA2A70, 0xE45C92, 0xA41C52);
                }
                setActive(true);
            }
        }

        @Override
        public void onFire1Down() {
            selectedKit = kitIndex;
            kitWidget.setDirty(true);
            updateKitSelectionButtons();
            updateKitEditButtons();
        }
    }

    private void updateKitSelectionButtons() {
        for (Widget w : kitSelectionButtons) {
            w.setDirty(true);
        }
    }

    private void updateKitEditButtons() {
        for (Widget w : kitEditButtons) {
            w.setDirty(true);
        }
    }

    private class TeamKit extends Button {
        Team team;

        TeamKit(Team team) {
            this.team = team;
            setSize(167, 304);
            setActive(false);
        }

        @Override
        public void refresh() {
            textureRegion = team.kits.get(selectedKit).loadImage();
        }
    }

    private class StyleLabel extends Button {

        StyleLabel() {
            setGeometry(528, 364, 175, 23);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("KITS.STYLE"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class StyleButton extends Button {

        int kitIndex;

        StyleButton() {
            kitIndex = Assets.kits.indexOf(team.kits.get(selectedKit).style);
            setGeometry(528, 364 + 25, 175, 24);
            setColors(0x530DB3, 0x6F12EE, 0x380977);
        }

        @Override
        public void refresh() {
            setText(team.kits.get(selectedKit).style.replace('_', ' '), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateKitStyle(+1);
        }

        @Override
        public void onFire1Hold() {
            updateKitStyle(+1);
        }

        @Override
        public void onFire2Down() {
            updateKitStyle(-1);
        }

        @Override
        public void onFire2Hold() {
            updateKitStyle(-1);
        }

        private void updateKitStyle(int n) {
            kitIndex = Emath.rotate(kitIndex, 0, Assets.kits.size() - 1, n);
            team.kits.get(selectedKit).style = Assets.kits.get(kitIndex);
            setDirty(true);
            kitWidget.setDirty(true);
            if (selectedKit == 0 && !logoWidget.isCustom) {
                logoWidget.setDirty(true);
            }
            setModifiedFlag();
        }
    }

    private class KitFieldLabel extends Button {

        KitFieldLabel(Kit.Field field, int x, int y) {
            setGeometry(x, y, 175, 23);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("KITS." + field.name()), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class HashButton extends Button {

        Kit.Field field;
        int colorIndex;

        HashButton(Kit.Field field, int x, int y) {
            this.field = field;
            setGeometry(x, y, 40, 24);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("#", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateColor(1);
        }

        @Override
        public void onFire1Hold() {
            updateColor(1);
        }

        @Override
        public void onFire2Down() {
            updateColor(-1);
        }

        @Override
        public void onFire2Hold() {
            updateColor(-1);
        }

        private void updateColor(int n) {
            colorIndex = Emath.rotate(colorIndex, 0, Kit.colors.length - 1, n);
            int color = Kit.colors[colorIndex];
            switch (field) {
                case SHIRT1:
                    team.kits.get(selectedKit).shirt1 = color;
                    break;

                case SHIRT2:
                    team.kits.get(selectedKit).shirt2 = color;
                    break;

                case SHORTS:
                    team.kits.get(selectedKit).shorts = color;
                    break;

                case SOCKS:
                    team.kits.get(selectedKit).socks = color;
                    break;
            }
            updateKitEditButtons();
            kitWidget.setDirty(true);
            if (selectedKit == 0 && !logoWidget.isCustom) {
                logoWidget.setDirty(true);
            }
            setModifiedFlag();
        }

        @Override
        public void refresh() {
            int color = 0;
            switch (field) {
                case SHIRT1:
                    color = team.kits.get(selectedKit).shirt1;
                    break;

                case SHIRT2:
                    color = team.kits.get(selectedKit).shirt2;
                    break;

                case SHORTS:
                    color = team.kits.get(selectedKit).shorts;
                    break;

                case SOCKS:
                    color = team.kits.get(selectedKit).socks;
                    break;
            }
            setColors(color);
        }
    }

    private class ColorComponentButton extends Button {

        Kit.Field field;
        GlColor.Component component;
        int value;

        ColorComponentButton(Kit.Field field, GlColor.Component component, int x, int y) {
            this.field = field;
            this.component = component;
            setGeometry(x, y, 43, 24);
            switch (component) {
                case RED:
                    setColors(0xDC2020);
                    break;

                case GREEN:
                    setColors(0x3DBD3D);
                    break;

                case BLUE:
                    setColors(0x2352F2);
                    break;
            }

            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void refresh() {
            this.value = GlColor.getComponentValue(getColor(), component);
            setText(String.format("%02X", value));
        }

        @Override
        public void onFire1Down() {
            updateValue(1);
        }

        @Override
        public void onFire1Hold() {
            updateValue(1);
        }

        @Override
        public void onFire2Down() {
            updateValue(-1);
        }

        @Override
        public void onFire2Hold() {
            updateValue(-1);
        }

        private void updateValue(int n) {
            value = Emath.rotate(value, 0, 255, n);
            int color = getColor();
            switch (component) {
                case RED:
                    color = GlColor.rgb(value, GlColor.green(color), GlColor.blue(color));
                    break;

                case GREEN:
                    color = GlColor.rgb(GlColor.red(color), value, GlColor.blue(color));
                    break;

                case BLUE:
                    color = GlColor.rgb(GlColor.red(color), GlColor.green(color), value);
                    break;
            }
            switch (field) {
                case SHIRT1:
                    team.kits.get(selectedKit).shirt1 = color;
                    break;

                case SHIRT2:
                    team.kits.get(selectedKit).shirt2 = color;
                    break;

                case SHORTS:
                    team.kits.get(selectedKit).shorts = color;
                    break;

                case SOCKS:
                    team.kits.get(selectedKit).socks = color;
                    break;
            }
            updateKitEditButtons();
            kitWidget.setDirty(true);
            if (selectedKit == 0 && !logoWidget.isCustom) {
                logoWidget.setDirty(true);
            }
            setModifiedFlag();
        }

        private int getColor() {
            Kit kit = team.kits.get(selectedKit);
            switch (field) {
                case SHIRT1:
                    return kit.shirt1;

                case SHIRT2:
                    return kit.shirt2;

                case SHORTS:
                    return kit.shorts;

                case SOCKS:
                    return kit.socks;

                default:
                    return 0;
            }
        }
    }

    private class PlayerNumberButton extends Button {

        int pos;

        PlayerNumberButton(int pos) {
            this.pos = pos;
            setGeometry(734, 374 + 24 * pos, 34, 21);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(team.playerAtPosition(pos).number);
        }
    }

    private class PlayerFaceButton extends Button {

        int pos;

        PlayerFaceButton(int pos) {
            this.pos = pos;
            setGeometry(768, 374 + 24 * pos, 24, 21);
            setImagePosition(2, 0);
            setActive(false);
        }

        @Override
        public void refresh() {
            setPlayerWidgetColor(this, pos);
            textureRegion = team.playerAtPosition(pos).createFace();
        }
    }

    private class PlayerNameButton extends Button {

        int pos;

        PlayerNameButton(int pos) {
            this.pos = pos;
            setGeometry(796, 374 + 24 * pos, 364, 21);
            setText("", Font.Align.LEFT, Assets.font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(team.playerAtPosition(pos).name);
            setPlayerWidgetColor(this, pos);
        }
    }

    private class PlayerRoleButton extends Button {

        int pos;

        PlayerRoleButton(int pos) {
            this.pos = pos;
            setGeometry(1160, 374 + 24 * pos, 36, 21);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(pos);
            setText(Assets.strings.get(player.getRoleLabel()));
        }
    }

    private class PlayerSelectButton extends Button {

        int pos;

        PlayerSelectButton(int pos) {
            this.pos = pos;
            setGeometry(734, 374 + 24 * this.pos, 462, 21);
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
                int ply1 = team.playerIndexAtPosition(selectedPos);
                int ply2 = team.playerIndexAtPosition(pos);

                Collections.swap(team.players, ply1, ply2);

                int oldSelected = selectedPos;
                selectedPos = -1;

                updatePlayerButtons(oldSelected);
                setModifiedFlag();
            }

            updatePlayerButtons(pos);
        }
    }

    private void setPlayerWidgetColor(Widget b, int pos) {
        // selected
        if (selectedPos == pos) {
            b.setColors(0x993333, 0xC24242, 0x5A1E1E);
        }
        // goalkeeper
        else if (pos == 0) {
            b.setColors(0x4AC058, 0x81D38B, 0x308C3B);
        }
        // other players
        else {
            b.setColors(0x308C3B, 0x4AC058, 0x1F5926);
        }
    }

    private void updatePlayerButtons(int pos) {
        numberButtons[pos].setDirty(true);
        faceButtons[pos].setDirty(true);
        nameButtons[pos].setDirty(true);
        roleButtons[pos].setDirty(true);
    }

    private class EditPlayersButton extends Button {

        EditPlayersButton() {
            setGeometry(100, 660, 206, 36);
            setColors(0x00825F, 0x00C28E, 0x00402F);
            setText(Assets.strings.get("PLAYERS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new EditPlayers(game, fileHandle, league, team, modified));
        }
    }

    private class NewKitButton extends Button {

        NewKitButton() {
            setGeometry(310, 660, 226, 36);
            setText(Assets.strings.get("NEW KIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            if (team.kits.size() < Team.MAX_KITS) {
                setColors(0x1769BD, 0x3A90E8, 0x10447A);
                setActive(true);
            } else {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            // add a change kit
            Kit kit = team.newKit();

            if (kit == null) return;

            // copy style, shirt1 & shirt2 colors from kit 0 or 1
            Kit other = team.kits.get(team.kits.size() - 4);
            kit.style = other.style;
            kit.shirt1 = other.shirt1;
            kit.shirt2 = other.shirt2;

            // copy shorts & socks colors from kit 1 or 0
            other = team.kits.get(5 - team.kits.size());
            kit.shorts = other.shorts;
            kit.socks = other.socks;

            selectedKit = team.kits.size() - 1;
            kitWidget.setDirty(true);
            updateKitSelectionButtons();
            updateKitEditButtons();
            setDirty(true);
            deleteKitButton.setDirty(true);

            setModifiedFlag();
        }
    }

    private class DeleteKitButton extends Button {

        DeleteKitButton() {
            setGeometry(540, 660, 226, 36);
            setText(Assets.strings.get("DELETE KIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            if (team.kits.size() > Team.MIN_KITS) {
                setColors(0x3217BD, 0x5639E7, 0x221080);
                setActive(true);
            } else {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            }
        }

        @Override
        public void onFire1Down() {
            boolean deleted = team.deleteKit();

            if (!deleted) return;

            if (selectedKit >= team.kits.size() - 1) {
                selectedKit = team.kits.size() - 1;
                kitWidget.setDirty(true);
            }
            updateKitSelectionButtons();
            updateKitEditButtons();
            newKitButton.setDirty(true);
            setDirty(true);

            setModifiedFlag();
        }
    }

    private class SaveButton extends Button {

        SaveButton() {
            setGeometry(770, 660, 196, 36);
            setText(Assets.strings.get("SAVE"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
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
            team.persist();
            game.setScreen(new SelectTeam(game, fileHandle, league));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry(970, 660, 196, 36);
            setColors(0xC84200, 0xFF6519, 0x803300);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectTeam(game, fileHandle, league));
        }
    }
}
