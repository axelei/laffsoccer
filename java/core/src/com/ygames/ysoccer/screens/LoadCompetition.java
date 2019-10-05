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
        List<Widget> competitionButtonsList = new ArrayList<>();

        ArrayList<FileHandle> folders = new ArrayList<>(Arrays.asList(Assets.savesFolder.list()));
        Collections.sort(folders, Assets.fileComparatorByName);
        for (FileHandle folder : folders) {
            if (folder.isDirectory()) {
                ArrayList<FileHandle> files = new ArrayList<>(Arrays.asList(folder.list()));
                Collections.sort(files, Assets.fileComparatorByName);
                for (FileHandle file : files) {
                    if (!file.isDirectory() && file.extension().equals("json")) {
                        Competition competition = Competition.load(file);
                        competition.filename = file.nameWithoutExtension();

                        w = new CompetitionButton(file.nameWithoutExtension(), competition);
                        competitionButtonsList.add(w);
                        widgets.add(w);
                    }
                }
            }
        }

        Widget.arrange(game.gui.WIDTH, 380, 34, 20, competitionButtonsList);

        w = new AbortButton();
        widgets.add(w);

        if (getSelectedWidget() == null) {
            setSelectedWidget(w);
        }
    }

    private class CompetitionButton extends Button {

        private Competition competition;

        CompetitionButton(String filename, Competition competition) {
            this.competition = competition;
            setSize(540, 30);
            switch (competition.category) {
                case DIY_COMPETITION:
                    setColor(0x376E2F);
                    break;
                case PRESET_COMPETITION:
                    setColor(0x415600);
                    break;
            }
            setText(filename, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setCompetition(competition);
            switch (game.competition.category) {
                case DIY_COMPETITION:
                case PRESET_COMPETITION:
                    switch (game.competition.type) {
                        case LEAGUE:
                            game.setScreen(new PlayLeague(game));
                            break;

                        case CUP:
                            game.setScreen(new PlayCup(game));
                            break;

                        case TOURNAMENT:
                            game.setScreen(new PlayTournament(game));
                            break;
                    }
                    break;
            }
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
