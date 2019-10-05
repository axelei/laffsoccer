package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Color3;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Data;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.PlayerSprite;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.framework.EMath;

class TestKeeper extends GLScreen {

    private enum Animation {ANIMATION_OFF, HORIZONTAL, VERTICAL}

    private enum Shadows {NONE, DAY, NIGHT}

    private Animation animation;
    private int animationLength;
    private int animationSpeed;

    private Shadows shadows;

    private final int displayedRows = 7;

    private Player player;
    private PlayerSprite playerSprite;
    private int fmx, fmy;
    private int fmx2 = 0;
    private int fmy2 = 0;
    private int offsetY;

    private boolean hasExited = false;

    TestKeeper(GLGame game) {
        super(game);

        Gdx.input.setInputProcessor(new InputProcessor());

        animation = Animation.ANIMATION_OFF;
        animationLength = 8;
        animationSpeed = 3;

        shadows = Shadows.DAY;

        Widget w;

        w = new PanelButton();
        widgets.add(w);

        Team team = new Team();
        player = new Player();
        fmx = 2;
        fmy = 2;
        offsetY = 0;
        player.team = team;
        player.role = Player.Role.GOALKEEPER;
        player.isVisible = true;
        player.data[0] = new Data();
        player.save(0);
        player.skinColor = Skin.Color.PINK;
        player.hairColor = Hair.Color.BLACK;
        player.hairStyle = "SMOOTH_A";
        reloadKeeper();
        reloadHair();
        playerSprite = new PlayerSprite(game.glGraphics, player);

        int x = 12;
        int y = 50;

        w = new SkinColorButton(x, y);
        widgets.add(w);

        w = new HairColorButton(x + 30, y);
        widgets.add(w);

        w = new HairStyleButton(x + 60, y);
        widgets.add(w);

        y += 40;
        w = new ColumnButton(x, y);
        widgets.add(w);

        y += 25;
        w = new RowButton(x, y);
        widgets.add(w);

        y += 40;
        w = new AnimationTypeButton(x, y);
        widgets.add(w);

        y += 25;
        w = new AnimationLengthButton(x, y);
        widgets.add(w);

        y += 25;
        w = new AnimationSpeedButton(x, y);
        widgets.add(w);

        y += 40;
        w = new ShadowsButton(x, y);
        widgets.add(w);

        w = new PitchButton();
        widgets.add(w);

        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 7; r++) {
                w = new FrameButton(c, r);
                widgets.add(w);
            }
        }

        w = new ExitButton(x);
        widgets.add(w);

        setSelectedWidget(w);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // temporary workaround
        if (hasExited) return;

        camera.setToOrtho(true, game.gui.screenWidth / 2f, game.gui.screenHeight / 2f); // 640 x 360
        camera.translate(-game.gui.originX / 2f, -game.gui.originY / 2f);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.setColor(0xFFFFFF, 1f);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int x0 = 120;
        int y0 = 5;

        // scroll bar
        shapeRenderer.setColor(0x444444, 1f);
        shapeRenderer.rect(x0 - 18, y0 - 50f * displayedRows * offsetY / getPlayerRows(), 4, 50f * displayedRows * displayedRows / getPlayerRows());

        // animation grid
        shapeRenderer.setColor(0xFCFC00, 1f);
        switch (animation) {
            case HORIZONTAL:
                for (int i = 0; i < animationLength; i++) {
                    int x = (fmx + i) % 8;
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + offsetY), x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + offsetY));
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + offsetY) + 50, x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + offsetY) + 50);
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + offsetY), x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + offsetY) + 50);
                    shapeRenderer.line(x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + offsetY), x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + offsetY) + 50);
                }
                break;
            case VERTICAL:
                for (int i = 0; i < animationLength; i++) {
                    int y = (fmy + offsetY + i) % (getPlayerRows());
                    shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y));
                    shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y + 1), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y + 1));
                    shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y), x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y + 1));
                    shapeRenderer.line(x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y + 1));
                }
                break;
        }
        shapeRenderer.end();

        // player grid
        batch.begin();
        for (int j = 0; j < displayedRows; j++) {
            for (int i = 0; i < 8; i++) {
                player.fmx = i;
                player.fmy = j - offsetY;
                player.x = x0 + 24 + 50 * i;
                player.y = y0 + 36 + 50 * j;
                player.save(0);
                drawKeeperShadow();
                playerSprite.draw(0);
            }
        }

        // selected player animation
        switch (animation) {
            case ANIMATION_OFF:
                fmx2 = 0;
                fmy2 = 0;
                break;

            case HORIZONTAL:
                if (Gdx.graphics.getFrameId() % (6 - animationSpeed) == 0) {
                    fmx2 = EMath.rotate(fmx2, 0, 5 * animationLength - 1, 1);
                }
                fmy2 = 0;
                break;

            case VERTICAL:
                fmx2 = 0;
                if (Gdx.graphics.getFrameId() % (6 - animationSpeed) == 0) {
                    fmy2 = EMath.rotate(fmy2, 0, 5 * animationLength - 1, 1);
                }
                break;
        }

        // selected player (x2)
        player.x = 572;
        player.y = 120;
        player.fmx = (fmx + EMath.floor(fmx2 / 5f)) % 8;
        player.fmy = (fmy + EMath.floor(fmy2 / 5f)) % getPlayerRows();
        player.save(0);
        drawKeeperShadow();
        playerSprite.draw(0);

        batch.end();

        // selected player (x4)
        camera.setToOrtho(true, game.gui.screenWidth / 4f, game.gui.screenHeight / 4f); // 320 x 180
        camera.translate(-game.gui.originX / 4f, -game.gui.originY / 4f);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.setColor(0xFFFFFF, 1f);

        player.x = 286;
        player.y = 130;
        player.save(0);
        batch.begin();
        drawKeeperShadow();
        playerSprite.draw(0);
        batch.end();

        // origin
        Data d = player.data[0];
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.line(d.x - 1, d.y, d.x + 1, d.y);
        shapeRenderer.line(d.x, d.y - 1, d.x, d.y + 1);
        shapeRenderer.end();
    }

    private void drawKeeperShadow() {
        if (shadows != Shadows.NONE) {
            for (int s = 0; s < (shadows == Shadows.NIGHT ? 4 : 1); s++) {
                Data d = player.data[0];
                if (d.isVisible) {
                    float offsetX = 24;//PlayerSprite.offsets[d.fmy][d.fmx][0];
                    float offsetY = 39;//PlayerSprite.offsets[d.fmy][d.fmx][1];
                    float mX = (s == 0 || s == 3) ? 0.65f : -0.65f;
                    float mY = (s == 0 || s == 1) ? 0.46f : -0.46f;
                    batch.setColor(0xFFFFFF, 0.5f);
                    batch.draw(Assets.keeperShadow[d.fmx][d.fmy][s], d.x - offsetX + mX * d.z, d.y - offsetY + 5 + mY * d.z);
                }
            }
        }
        batch.setColor(0xFFFFFF, 1);
    }

    private class PanelButton extends Button {

        PanelButton() {
            setGeometry(0, 0, 200, 720);
            setColors(0x444444, 0x444444, 0x444444);
            setActive(false);
        }
    }

    private class SkinColorButton extends Button {

        SkinColorButton(int x, int y) {
            setGeometry(x, y, 28, 23);
        }

        @Override
        public void refresh() {
            Color3 skinColor = Skin.colors[player.skinColor.ordinal()];
            setColors(skinColor.color2, skinColor.color1, skinColor.color3);
        }

        @Override
        public void onFire1Down() {
            updateSkinColor(1);
        }

        @Override
        public void onFire1Hold() {
            updateSkinColor(1);
        }

        @Override
        public void onFire2Down() {
            updateSkinColor(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSkinColor(-1);
        }

        private void updateSkinColor(int n) {
            player.skinColor = Skin.Color.values()[EMath.rotate(player.skinColor.ordinal(), Skin.Color.PINK.ordinal(), Skin.Color.RED.ordinal(), n)];

            setDirty(true);

            reloadKeeper();
        }
    }

    private class HairColorButton extends Button {

        HairColorButton(int x, int y) {
            setGeometry(x, y, 28, 23);
        }

        @Override
        public void refresh() {
            Color3 hairColor = Hair.colors[player.hairColor.ordinal()];
            setColors(hairColor.color2, hairColor.color1, hairColor.color3);
        }

        @Override
        public void onFire1Down() {
            updateHairColor(1);
        }

        @Override
        public void onFire1Hold() {
            updateHairColor(1);
        }

        @Override
        public void onFire2Down() {
            updateHairColor(-1);
        }

        @Override
        public void onFire2Hold() {
            updateHairColor(-1);
        }

        private void updateHairColor(int n) {
            int color = player.hairColor.ordinal();
            color = EMath.rotate(color, Hair.Color.BLACK.ordinal(), Hair.Color.PUNK_BLOND.ordinal(), n);
            player.hairColor = Hair.Color.values()[color];

            setDirty(true);

            reloadHair();
        }
    }

    private class HairStyleButton extends Button {

        HairStyleButton(int x, int y) {
            setGeometry(x, y, 115, 23);
            setColors(0x308C3B, 0x4AC058, 0x1F5926);
            setText("", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void refresh() {
            setText(player.hairStyle.replace('_', ' '));
        }

        @Override
        public void onFire1Down() {
            updateHairStyle(1);
        }

        @Override
        public void onFire1Hold() {
            updateHairStyle(1);
        }

        @Override
        public void onFire2Down() {
            updateHairStyle(-1);
        }

        @Override
        public void onFire2Hold() {
            updateHairStyle(-1);
        }

        private void updateHairStyle(int n) {
            int i = Assets.hairStyles.indexOf(player.hairStyle);
            if (i == -1) {
                i = 0; // not found, start from 0
            } else {
                i = EMath.rotate(i, 0, Assets.hairStyles.size() - 1, n);
            }
            player.hairStyle = Assets.hairStyles.get(i);

            setDirty(true);

            reloadHair();
        }
    }

    private class ColumnButton extends Button {

        ColumnButton(int x, int y) {
            setGeometry(x, y, 175, 23);
            setColors(0x308C3B, 0x4AC058, 0x1F5926);
        }

        @Override
        public void refresh() {
            setText("COLUMN: " + fmx, Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateColumn(1);
        }

        @Override
        public void onFire1Hold() {
            updateColumn(1);
        }

        @Override
        public void onFire2Down() {
            updateColumn(-1);
        }

        @Override
        public void onFire2Hold() {
            updateColumn(-1);
        }

        private void updateColumn(int n) {
            fmx = EMath.rotate(fmx, 0, 7, n);
            refreshAllWidgets();
        }
    }

    private class RowButton extends Button {

        RowButton(int x, int y) {
            setGeometry(x, y, 175, 23);
            setColors(0x308C3B, 0x4AC058, 0x1F5926);
        }

        @Override
        public void refresh() {
            setText("ROW: " + fmy, Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateRow(1);
        }

        @Override
        public void onFire1Hold() {
            updateRow(1);
        }

        @Override
        public void onFire2Down() {
            updateRow(-1);
        }

        @Override
        public void onFire2Hold() {
            updateRow(-1);
        }

        private void updateRow(int n) {
            fmy = EMath.slide(fmy, 0, getPlayerRows() - 1, n);
            refreshAllWidgets();
        }
    }

    private class AnimationTypeButton extends Button {

        AnimationTypeButton(int x, int y) {
            setGeometry(x, y, 175, 23);
            setColor(0x2B4A61);
        }

        @Override
        public void refresh() {
            setText(animation.toString().replace('_', ' '), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateRow(1);
        }

        @Override
        public void onFire1Hold() {
            updateRow(1);
        }

        @Override
        public void onFire2Down() {
            updateRow(-1);
        }

        @Override
        public void onFire2Hold() {
            updateRow(-1);
        }

        private void updateRow(int n) {
            animation = Animation.values()[EMath.rotate(animation, Animation.ANIMATION_OFF, Animation.VERTICAL, n)];
            setDirty(true);
        }
    }

    private class AnimationLengthButton extends Button {

        AnimationLengthButton(int x, int y) {
            setGeometry(x, y, 175, 23);
            setColor(0x2B4A61);
        }

        @Override
        public void refresh() {
            setText("LENGTH: " + animationLength, Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateRow(1);
        }

        @Override
        public void onFire1Hold() {
            updateRow(1);
        }

        @Override
        public void onFire2Down() {
            updateRow(-1);
        }

        @Override
        public void onFire2Hold() {
            updateRow(-1);
        }

        private void updateRow(int n) {
            animationLength = EMath.slide(animationLength, 2, 8, n);
            setDirty(true);
        }
    }

    private class AnimationSpeedButton extends Button {

        AnimationSpeedButton(int x, int y) {
            setGeometry(x, y, 175, 23);
            setColor(0x2B4A61);
        }

        @Override
        public void refresh() {
            setText("SPEED: " + animationSpeed, Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateRow(1);
        }

        @Override
        public void onFire1Hold() {
            updateRow(1);
        }

        @Override
        public void onFire2Down() {
            updateRow(-1);
        }

        @Override
        public void onFire2Hold() {
            updateRow(-1);
        }

        private void updateRow(int n) {
            animationSpeed = EMath.slide(animationSpeed, 1, 5, n);
            setDirty(true);
        }
    }

    private class ShadowsButton extends Button {

        ShadowsButton(int x, int y) {
            setGeometry(x, y, 175, 23);
            setColor(0x8F5902);
        }

        @Override
        public void refresh() {
            setText("SHADOWS: " + shadows, Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateRow(1);
        }

        @Override
        public void onFire2Down() {
            updateRow(-1);
        }

        private void updateRow(int n) {
            shadows = EMath.rotate(shadows, Shadows.class, n);
            setDirty(true);
        }
    }

    private class PitchButton extends Button {

        PitchButton() {
            setGeometry(200, 0, 1080, 720);
            setColors(0x2AA748, 0x2AA748, 0x2AA748);
            setActive(false);
        }
    }

    private class FrameButton extends Button {

        private int column, row;

        FrameButton(int column, int row) {
            this.column = column;
            this.row = row;
            int x0 = 238;
            int y0 = 8;
            setGeometry(x0 + 100 * column, y0 + 100 * row, 100, 100);
            setColors(0x2AA748, 0x2AA748, 0x2AA748);
        }

        @Override
        public void refresh() {
            boolean selected = (row == fmy + offsetY) && (column == fmx);
            setColors(null, selected ? 0xFF0000 : null, selected ? 0xFF0000 : null);
        }

        @Override
        public void onFire1Down() {
            updateSelected();
        }

        private void updateSelected() {
            fmx = column;
            fmy = row - offsetY;
            refreshAllWidgets();
        }
    }

    private class ExitButton extends Button {

        ExitButton(int x) {
            setGeometry(x, 660, 175, 32);
            setColor(0xC84200);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            hasExited = true;
            Assets.unloadKeeper(player);
            Gdx.input.setInputProcessor(null);
            game.setScreen(new DeveloperTools(game));
        }
    }

    private void reloadKeeper() {
        Assets.unloadKeeper(player);
        Assets.loadKeeper(player);
    }

    private void reloadHair() {
        Assets.unloadHair(player);
        Assets.loadHair(player);
    }

    private int getPlayerRows() {
        return player.role == Player.Role.GOALKEEPER ? 19 : 16;
    }

    private class InputProcessor extends InputAdapter {

        @Override
        public boolean scrolled(int n) {
            offsetY = EMath.slide(offsetY, displayedRows - getPlayerRows(), 0, -n);
            refreshAllWidgets();
            return true;
        }
    }

}
