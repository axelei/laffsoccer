package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

public class DesignFriendly extends GlScreen {

    Friendly friendly;
    Widget substitutesButton;

    public DesignFriendly(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_friendly.jpg");

        friendly = new Friendly();
        friendly.name = Assets.strings.get("FRIENDLY");

        game.state = GlGame.State.FRIENDLY;
        game.stateColor.set(0x2D855D, 0x3DB37D, 0x1E5027);
        game.competition = friendly;

        Widget w;
        w = new TitleBar();
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
        selectedWidget = w;

        w = new ExitButton();
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            String title = Assets.strings.get("FRIENDLY");
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 30, 340, 40);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesLabel extends Button {

        public SubstitutesLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 250, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesButton extends Button {

        public SubstitutesButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 250, 240, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
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
            friendly.substitutions = Emath.slide(friendly.substitutions, 2, friendly.benchSize, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(friendly.substitutions);
        }
    }

    class BenchSizeLabel extends Button {

        public BenchSizeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 305, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeButton extends Button {

        public BenchSizeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 305, 240, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
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
            friendly.benchSize = Emath.slide(friendly.benchSize, 2, 5, n);
            friendly.substitutions = Math.min(friendly.substitutions, friendly.benchSize);
            setChanged(true);
            substitutesButton.setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(friendly.benchSize);
        }
    }

    class OkButton extends Button {

        public OkButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 590, 180, 36);
            setText(Assets.strings.get("OK"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectFolder(game, Assets.dataFolder));
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
            game.setScreen(new Main(game));
        }
    }
}