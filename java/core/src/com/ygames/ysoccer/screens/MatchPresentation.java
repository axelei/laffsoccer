package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

public class MatchPresentation extends GlScreen {

    private FileHandle fileHandle;
    private League league;
    private Competition competition;
    private Team homeTeam;
    private Team awayTeam;

    public MatchPresentation(GlGame game, FileHandle fileHandle, League league, Competition competition, Team homeTeam, Team awayTeam) {
        super(game);

        this.fileHandle = fileHandle;
        this.league = league;
        this.competition = competition;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        background = new Image("images/backgrounds/menu_match_presentation.jpg");

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        w = new PlayMatchButton();
        widgets.add(w);

        selectedWidget = w;
    }

    class TitleButton extends Button {

        public TitleButton() {
            setGeometry((game.settings.GUI_WIDTH - 840) / 2, 30, 840, 44);
            setColors(0x008080, 0x00B2B4, 0x004040);
            setText(competition.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PlayMatchButton extends Button {

        public PlayMatchButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 375, 340, 40);
            setColors(0xDC0000, 0xFF4141, 0x8C0000);
            setText(Assets.strings.get("PLAY MATCH"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            // TODO: game.setScreen(new MatchLoading(game, homeTeam, awayTeam, matchSettings));
        }
    }
}
