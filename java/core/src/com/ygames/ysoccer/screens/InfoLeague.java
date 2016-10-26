package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Time;

public class InfoLeague extends GlScreen {

    League league;

    public InfoLeague(GlGame game) {
        super(game);

        background = game.stateBackground;

        league = (League) game.competition;

        Widget w;

        w = new TitleBar();
        widgets.add(w);

        w = new SeasonPitchTypeButton();
        widgets.add(w);

        w = new SeasonStartButton();
        widgets.add(w);

        w = new SeasonSeparatorButton();
        widgets.add(w);

        w = new SeasonEndButton();
        widgets.add(w);

        w = new PitchTypeButton();
        widgets.add(w);

        w = new TimeLabel();
        widgets.add(w);

        w = new TimeButton();
        widgets.add(w);

        w = new NumberOfTeamsLabel();
        widgets.add(w);

        w = new NumberOfTeamsButton();
        widgets.add(w);

        w = new PlayEachTeamLabel();
        widgets.add(w);

        w = new PlayEachTeamButton();
        widgets.add(w);

        w = new PointsForAWinLabel();
        widgets.add(w);

        w = new PointsForAWinButton();
        widgets.add(w);

        w = new SubstitutesLabel();
        widgets.add(w);

        w = new SubstitutesButton();
        widgets.add(w);

        w = new BenchSizeLabel();
        widgets.add(w);

        w = new BenchSizeButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        selectedWidget = w;
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 680) / 2, 30, 680, 40);
            setColors(game.stateColor);
            setText(league.name, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SeasonPitchTypeButton extends Button {

        public SeasonPitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 175, 290, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(league.getBySeasonLabel()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SeasonStartButton extends Button {

        public SeasonStartButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 50, 175, 180, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.monthNames[league.seasonStart]), Font.Align.CENTER, Assets.font14);
            setVisible(league.bySeason);
            setActive(false);
        }
    }

    class SeasonSeparatorButton extends Button {

        public SeasonSeparatorButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 132, 175, 36, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setActive(false);
            setVisible(league.bySeason);
        }
    }

    class SeasonEndButton extends Button {

        public SeasonEndButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 170, 175, 180, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.monthNames[league.seasonEnd]), Font.Align.CENTER, Assets.font14);
            setVisible(league.bySeason);
            setActive(false);
        }
    }

    class PitchTypeButton extends Button {

        public PitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 50, 175, 400, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Pitch.names[league.pitchType.ordinal()]), Font.Align.CENTER, Assets.font14);
            setVisible(!league.bySeason);
            setActive(false);
        }
    }

    class TimeLabel extends Button {

        public TimeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 230, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TimeButton extends Button {

        public TimeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 230, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Time.names[league.time]), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class NumberOfTeamsLabel extends Button {

        public NumberOfTeamsLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 285, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("NUMBER OF TEAMS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class NumberOfTeamsButton extends Button {

        public NumberOfTeamsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 285, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.numberOfTeams, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PlayEachTeamLabel extends Button {

        public PlayEachTeamLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 340, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("PLAY EACH TEAM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PlayEachTeamButton extends Button {

        public PlayEachTeamButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 340, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(((char) 215) + "" + league.rounds, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PointsForAWinLabel extends Button {

        public PointsForAWinLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 395, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("POINTS FOR A WIN"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PointsForAWinButton extends Button {

        public PointsForAWinButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 395, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.pointsForAWin, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesLabel extends Button {

        public SubstitutesLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 450, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesButton extends Button {

        public SubstitutesButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 450, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.substitutions, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeLabel extends Button {

        public BenchSizeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 505, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeButton extends Button {

        public BenchSizeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 505, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.benchSize, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }
}
