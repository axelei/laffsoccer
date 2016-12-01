package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class LoadCompetition extends GLScreen {

    LoadCompetition(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_competition.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("LOAD OLD COMPETITION"), 0x2898c7);
        widgets.add(w);

        // Competitions buttons
        List<Widget> competitionButtonsList = new ArrayList<Widget>();
        List<Widget> categoryLabelsList = new ArrayList<Widget>();

        ArrayList<FileHandle> folders = new ArrayList<FileHandle>(Arrays.asList(Assets.savesFolder.list()));
        Collections.sort(folders, Assets.fileComparatorByName);
        for (FileHandle folder : folders) {
            if (folder.isDirectory()) {
                ArrayList<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(folder.list()));
                Collections.sort(files, Assets.fileComparatorByName);
                for (FileHandle file : files) {
                    if (!file.isDirectory() && file.extension().equals("json")) {
                        Competition competition = Assets.json.fromJson(Competition.class, file);

                        w = new CompetitionButton(file.nameWithoutExtension(), competition);
                        competitionButtonsList.add(w);
                        widgets.add(w);

                        w = new CategoryLabel(competition);
                        categoryLabelsList.add(w);
                        widgets.add(w);
                    }
                }
            }
        }

        if (competitionButtonsList.size() > 0) {
            int len = competitionButtonsList.size();
            for (int i = 0; i < len; i++) {
                Widget b = competitionButtonsList.get(i);
                Widget l = categoryLabelsList.get(i);
                b.x = (game.gui.WIDTH - b.w - l.w - 4) / 2;
                b.y = 355 + 34 * (i - len / 2);
                l.x = (game.gui.WIDTH + b.w - l.w + 4) / 2;
                l.y = 355 + 34 * (i - len / 2);
            }
            setSelectedWidget(competitionButtonsList.get(0));
        }

        w = new AbortButton();
        widgets.add(w);

        if (selectedWidget == null) {
            setSelectedWidget(w);
        }
    }

    private class CompetitionButton extends Button {

        private Competition competition;

        CompetitionButton(String filename, Competition competition) {
            this.competition = competition;
            setSize(540, 30);
            setColors(0x1B4D85, 0x256AB7, 0x001D3E);
            setText(filename, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setCompetition(competition);
            switch (game.competition.category) {
                case DIY_COMPETITION:
                case PRESET_COMPETITION:
                    game.setState(GLGame.State.COMPETITION, game.competition.category);
                    switch (game.competition.type) {
                        case LEAGUE:
                            game.setScreen(new PlayLeague(game));
                            break;

                        case CUP:
                            game.setScreen(new PlayCup(game));
                            break;
                    }
                    break;
            }
        }
    }

    private class CategoryLabel extends Button {

        CategoryLabel(Competition competition) {
            setSize(180, 30);
            setText(competition.getCategoryFolder(), Font.Align.CENTER, Assets.font14);
            switch (competition.category) {
                case DIY_COMPETITION:
                    setColors(0x376E2F);
                    break;
                case PRESET_COMPETITION:
                    setColors(0x415600);
                    break;
            }
            setActive(false);
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setColors(0xC8000E, 0xFF1929, 0x74040C);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
