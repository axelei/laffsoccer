package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.framework.EMath;

class DesignFriendly extends GLScreen {

    private Friendly friendly;
    private Widget substitutesButton;

    DesignFriendly(GLGame game) {
        super(game);

        friendly = new Friendly();

        game.setState(GLGame.State.FRIENDLY, null);

        background = game.stateBackground;

        Widget w;

        w = new TitleBar(Assets.strings.get("FRIENDLY"), 0x2D855D);
        widgets.add(w);

        w = new SubstitutesLabel();
        widgets.add(w);

        w = new SubstitutesButton();
        widgets.add(w);
        substitutesButton = w;

        w = new BenchSizeLabel();
        widgets.add(w);

        w = new BenchSizeButton();
        widgets.add(w);

        w = new OkButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 280, 440, 40);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 280, 240, 40);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(friendly.substitutions);
        }

        @Override
        public void onFire1Down() {
            updateSubstitutes(1);
        }

        @Override
        public void onFire1Hold() {
            updateSubstitutes(1);
        }

        @Override
        public void onFire2Down() {
            updateSubstitutes(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSubstitutes(-1);
        }

        private void updateSubstitutes(int n) {
            friendly.substitutions = EMath.slide(friendly.substitutions, 2, friendly.benchSize, n);
            setDirty(true);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 335, 440, 40);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 335, 240, 40);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            setText(friendly.benchSize);
        }

        @Override
        public void onFire1Down() {
            updateBenchSize(1);
        }

        @Override
        public void onFire1Hold() {
            updateBenchSize(1);
        }

        @Override
        public void onFire2Down() {
            updateBenchSize(-1);
        }

        @Override
        public void onFire2Hold() {
            updateBenchSize(-1);
        }

        private void updateBenchSize(int n) {
            friendly.benchSize = EMath.slide(friendly.benchSize, 2, 12, n);
            friendly.substitutions = Math.min(friendly.substitutions, friendly.benchSize);
            setDirty(true);
            substitutesButton.setDirty(true);
        }
    }

    private class OkButton extends Button {

        OkButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry((game.gui.WIDTH - 180) / 2, 605, 180, 36);
            setText(Assets.strings.get("OK"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            navigation.folder = Assets.teamsRootFolder;
            navigation.competition = friendly;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}