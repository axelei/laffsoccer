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
import com.ygames.ysoccer.match.Team;

public class EditTeam extends GlScreen {

    FileHandle fileHandle;
    League league;
    Team team;
    int selectedPly;
    boolean modified;

    public EditTeam(GlGame game, FileHandle fileHandle, League league, Team team, Boolean modified) {
        super(game);
        this.fileHandle = fileHandle;
        this.league = league;
        this.team = team;
        selectedPly = -1;
        this.modified = modified;

        background = game.stateBackground;

        Widget w;

        w = new TeamNameButton();
        widgets.add(w);

        selectedWidget = w;

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
    }

    void setModified() {
        modified = true;
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
            setGeometry(172, 110, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("CITY"), Font.Align.CENTER, Assets.font10);
            setVisible(team.type == Team.Type.CLUB);
            setActive(false);
        }
    }

    class CityButton extends InputButton {

        public CityButton() {
            setGeometry(362, 110, 364, 30);
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
            setGeometry(172, 160, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("STADIUM"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class StadiumButton extends InputButton {

        public StadiumButton() {
            setGeometry(362, 160, 364, 30);
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
            setGeometry(172, 210, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("COUNTRY"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class CountryButton extends Button {

        public CountryButton() {
            setGeometry(362, 210, 364, 30);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(team.country, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class DivisionLabel extends Button {

        public DivisionLabel() {
            setGeometry(172, 260, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("DIVISION"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class DivisionButton extends Button {

        public DivisionButton() {
            setGeometry(362, 260, 364, 30);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.name, Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class CoachLabel extends Button {

        public CoachLabel() {
            setGeometry(172, 325, 182, 30);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("COACH"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    class CoachButton extends InputButton {

        public CoachButton() {
            setGeometry(362, 325, 364, 30);
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
}
