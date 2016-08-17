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
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.match.Team;

import java.util.Collections;

public class EditTeam extends GlScreen {

    FileHandle fileHandle;
    League league;
    Team team;
    int selectedPos;
    boolean modified;

    Widget[] tacticsButtons = new Widget[18];

    Widget[] numberButtons = new Widget[Const.TEAM_SIZE];
    Widget[] faceButtons = new Widget[Const.TEAM_SIZE];
    Widget[] nameButtons = new Widget[Const.TEAM_SIZE];
    Widget[] roleButtons = new Widget[Const.TEAM_SIZE];

    Widget saveButton;

    public EditTeam(GlGame game, FileHandle fileHandle, League league, Team team, Boolean modified) {
        super(game);
        this.fileHandle = fileHandle;
        this.league = league;
        this.team = team;
        selectedPos = -1;
        this.modified = modified;

        background = game.stateBackground;

        Widget w;

        for (int t = 0; t < 18; t++) {
            w = new TacticsButton(t);
            tacticsButtons[t] = w;
            widgets.add(w);
        }

        w = new TeamNameButton();
        widgets.add(w);

        w = new CityLabel();
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

            w = new DivisionLabel();
            widgets.add(w);

            w = new DivisionButton();
            widgets.add(w);
        }

        w = new CoachLabel();
        widgets.add(w);

        w = new CoachButton();
        widgets.add(w);

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

        selectedWidget = w;

        w = new SaveButton();
        saveButton = w;
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    void setModified() {
        modified = true;
        saveButton.setChanged(true);
    }

    class TacticsButton extends Button {

        int t;

        public TacticsButton(int t) {
            this.t = t;
            setGeometry(678 + 110 * ((int) Math.floor(t / 9)), 148 + 24 * (t % 9), 90, 22);
            setText(Tactics.codes[t], Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onUpdate() {
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
                    w.setChanged(true);
                }
                setModified();
                for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
                    updatePlayerButtons(pos);
                }
            }
        }
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

    class CityLabel extends Button {

        public CityLabel() {
            setGeometry(100, 110, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("CITY"), Font.Align.CENTER, Assets.font10);
            setVisible(team.type == Team.Type.CLUB);
            setActive(false);
        }
    }

    class CityButton extends InputButton {

        public CityButton() {
            setGeometry(290, 110, 364, 30);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(team.city, Font.Align.CENTER, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onUpdate() {
            if (!team.city.equals(text)) {
                team.city = text;
                setModified();
            }
        }
    }

    class StadiumLabel extends Button {

        public StadiumLabel() {
            setGeometry(100, 160, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("STADIUM"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class StadiumButton extends InputButton {

        public StadiumButton() {
            setGeometry(290, 160, 364, 30);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(team.stadium, Font.Align.CENTER, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onUpdate() {
            if (!team.stadium.equals(text)) {
                team.stadium = text;
                setModified();
            }
        }
    }

    class CountryLabel extends Button {

        public CountryLabel() {
            setGeometry(100, 210, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("COUNTRY"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class CountryButton extends Button {

        public CountryButton() {
            setGeometry(290, 210, 364, 30);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(team.country, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class DivisionLabel extends Button {

        public DivisionLabel() {
            setGeometry(100, 260, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("DIVISION"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class DivisionButton extends Button {

        public DivisionButton() {
            setGeometry(290, 260, 364, 30);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.name, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class CoachLabel extends Button {

        public CoachLabel() {
            setGeometry(100, 325, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("COACH"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class CoachButton extends InputButton {

        public CoachButton() {
            setGeometry(290, 325, 364, 30);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(team.coach.name, Font.Align.CENTER, Assets.font10);
            setEntryLimit(28);
        }

        @Override
        public void onUpdate() {
            if (!team.coach.name.equals(text)) {
                team.coach.name = text;
                setModified();
            }
        }
    }

    class PlayerNumberButton extends Button {

        int pos;

        public PlayerNumberButton(int pos) {
            this.pos = pos;
            setGeometry(676, 376 + 24 * pos, 34, 21);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(team.playerAtPosition(pos).number);
        }
    }

    class PlayerFaceButton extends Button {

        int pos;
        Player player;

        public PlayerFaceButton(int pos) {
            this.pos = pos;
            setGeometry(710, 376 + 24 * pos, 24, 21);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setPlayerWidgetColor(this, pos);
            player = team.playerAtPosition(pos);
        }
    }

    class PlayerNameButton extends Button {

        int pos;

        public PlayerNameButton(int pos) {
            this.pos = pos;
            setGeometry(738, 376 + 24 * pos, 364, 21);
            setText("", Font.Align.LEFT, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(team.playerAtPosition(pos).name);
            setPlayerWidgetColor(this, pos);
        }
    }

    class PlayerRoleButton extends Button {

        int pos;

        public PlayerRoleButton(int pos) {
            this.pos = pos;
            setGeometry(1102, 376 + 24 * pos, 34, 21);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            Player player = team.playerAtPosition(pos);
            setText(Assets.strings.get(player.getRoleLabel()));
        }
    }

    class PlayerSelectButton extends Button {

        int pos;

        public PlayerSelectButton(int pos) {
            this.pos = pos;
            setGeometry(676, 376 + 24 * this.pos, 460, 21);
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
                setModified();
            }

            updatePlayerButtons(pos);
        }
    }

    void setPlayerWidgetColor(Widget b, int pos) {
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

    void updatePlayerButtons(int pos) {
        numberButtons[pos].setChanged(true);
        faceButtons[pos].setChanged(true);
        nameButtons[pos].setChanged(true);
        roleButtons[pos].setChanged(true);
    }

    class EditPlayersButton extends Button {

        public EditPlayersButton() {
            setGeometry(168, 660, 160, 36);
            setColors(0x00825F, 0x00C28E, 0x00402F);
            setText(Assets.strings.get("PLAYERS"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new EditPlayers(game, fileHandle, league, team, modified));
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
}
