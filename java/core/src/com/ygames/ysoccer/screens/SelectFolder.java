package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.List;

public class SelectFolder extends GlScreen {

    private FileHandle fileHandle;
    private boolean isDataRoot;

    public SelectFolder(GlGame game, FileHandle fileHandle) {
        super(game);
        this.fileHandle = fileHandle;
        isDataRoot = (fileHandle.path().equals(Assets.teamsFolder.path()));

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        List<Widget> list = new ArrayList<Widget>();
        FileHandle[] files = fileHandle.list();
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                list.add(w);
                widgets.add(w);
            }
        }

        if (list.size() > 0) {
            Widget.arrange(game.settings, 350, 50, list);
            selectedWidget = list.get(0);
        } else {
            FileHandle leagueFile = fileHandle.child("leagues.json");
            if (leagueFile.exists()) {
                League[] leagues = Assets.json.fromJson(League[].class, leagueFile.readString());
                for (League league : leagues) {
                    w = new LeagueButton(league);
                    list.add(w);
                    widgets.add(w);
                }
                if (leagues.length > 0) {
                    Widget.arrange(game.settings, 350, 50, list);
                    selectedWidget = list.get(0);
                }
            }
        }

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            selectedWidget = w;
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            int diff = game.competition.numberOfTeams - game.teamList.size();
            String title = Assets.strings.get((diff == 0) ? "CHANGE TEAMS FOR" : "CHOOSE TEAMS FOR")
                    + " " + game.competition.name.toUpperCase();
            if (!isDataRoot) {
                title += " - " + fileHandle.name().toUpperCase();
            }
            int w = Math.max(960, 80 + 16 * title.length());
            setGeometry((game.settings.GUI_WIDTH - w) / 2, 30, w, 40);
            setColors(game.stateColor);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class FolderButton extends Button {

        FileHandle fileHandle;

        public FolderButton(FileHandle fileHandle) {
            this.fileHandle = fileHandle;
            setSize(340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(fileHandle.name().toUpperCase(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.competition.absolutePath = fileHandle.path();
            game.setScreen(new SelectFolder(game, fileHandle));
        }
    }

    class LeagueButton extends Button {

        League league;

        public LeagueButton(League league) {
            this.league = league;
            setSize(340, 40);
            setColors(0x1B4D85, 0x256AB7, 0x001D3E);
            setText(league.name.toUpperCase(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectTeams(game, fileHandle, league));
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            if (isDataRoot) {
                setColors(0xC8000E, 0xFF1929, 0x74040C);
                setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
            } else {
                setColors(0xC84200, 0xFF6519, 0x803300);
                setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
            }
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
        }

        @Override
        public void onFire1Down() {
            if (isDataRoot) {
                game.setScreen(new Main(game));
            } else {
                game.setScreen(new SelectFolder(game, fileHandle.parent()));
            }
        }
    }
}