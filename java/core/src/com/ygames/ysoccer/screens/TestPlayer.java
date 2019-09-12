package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Color3;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLColor;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Data;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.PlayerSprite;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

class TestPlayer extends GLScreen {

    private enum Animation {ANIMATION_OFF, HORIZONTAL, VERTICAL}

    private enum Shadows {NONE, DAY, NIGHT}

    private Animation animation;
    private int animationLength;
    private int animationSpeed;

    private Shadows shadows;

    private final int displayedRows = 7;

    private Team team;
    private int selectedKit = 0;
    private Player player;
    private PlayerSprite playerSprite;
    private int fmx, fmy;
    private int fmx2 = 0;
    private int fmy2 = 0;
    private int cursorY;

    TestPlayer(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_edit_team.jpg");

        animation = Animation.ANIMATION_OFF;
        animationLength = 8;
        animationSpeed = 3;

        shadows = Shadows.NONE;

        Widget w;

        team = new Team();
        Kit kit = new Kit();
        kit.style = "PLAIN";
        kit.shirt1 = Kit.colors[2];
        kit.shirt2 = Kit.colors[5];
        kit.shirt3 = Kit.colors[1];
        team.kits.add(kit);
        player = new Player();
        fmx = 2;
        fmy = 2;
        cursorY = fmy;
        player.team = team;
        player.isVisible = true;
        player.data[0] = new Data();
        player.save(0);
        player.skinColor = Skin.Color.PINK;
        player.hairColor = Hair.Color.BLACK;
        player.hairStyle = "SMOOTH_A";
        reloadPlayer();
        reloadHair();
        playerSprite = new PlayerSprite(game.glGraphics, player);

        int x = 12;
        int y = 50;

        w = new StyleLabel(x, y);
        widgets.add(w);

        y += 25;
        w = new StyleButton(x, y);
        widgets.add(w);

        y += 25;
        w = new KitFieldLabel("KITS.SHIRT", x, y);
        widgets.add(w);

        y += 25;
        for (int f = 0; f < 3; f++) {
            w = new HashButton(Kit.Field.values()[f], x, y);
            widgets.add(w);

            w = new KitColorButton(Kit.Field.values()[f], x + 42, y);
            widgets.add(w);

            y += 26;
        }

        y += 3;
        for (int f = 3; f < 5; f++) {
            String label = "";
            switch (Kit.Field.values()[f]) {
                case SHORTS:
                    label = "KITS.SHORTS";
                    break;
                case SOCKS:
                    label = "KITS.SOCKS";
                    break;
            }
            w = new KitFieldLabel(label, x, y);
            widgets.add(w);

            y += 25;

            w = new HashButton(Kit.Field.values()[f], x, y);
            widgets.add(w);

            w = new KitColorButton(Kit.Field.values()[f], x + 42, y);
            widgets.add(w);

            y += 29;
        }

        y += 20;
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

        w = new ExitButton(x);
        widgets.add(w);

        setSelectedWidget(w);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.setToOrtho(true, game.gui.screenWidth / 2f, game.gui.screenHeight / 2f); // 640 x 360
        camera.translate(-game.gui.originX / 2f, -game.gui.originY / 2f);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.setColor(0xFFFFFF, 1f);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(0x2AA748, 1f);

        int x0 = 120;
        int y0 = 5;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(100, 0, 540, 360);
        shapeRenderer.setColor(0x444444, 1f);
        shapeRenderer.rect(x0 - 18, y0 + 50f * displayedRows * (fmy - cursorY) / getPlayerRows(), 4, 50f * displayedRows * displayedRows / getPlayerRows());
        shapeRenderer.setColor(0xFCFC00, 1f);
        switch (animation) {
            case ANIMATION_OFF:
                shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * cursorY, x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * cursorY);
                shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * cursorY + 50, x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * cursorY + 50);
                shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * cursorY, x0 - 1 + 50 * fmx, y0 - 1 + 50 * cursorY + 50);
                shapeRenderer.line(x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * cursorY, x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * cursorY + 50);
                break;
            case HORIZONTAL:
                for (int i = 0; i < animationLength; i++) {
                    int x = (fmx + i) % 8;
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * cursorY, x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * cursorY);
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * cursorY + 50, x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * cursorY + 50);
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * cursorY, x0 - 1 + 50 * (x), y0 - 1 + 50 * cursorY + 50);
                    shapeRenderer.line(x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * cursorY, x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * cursorY + 50);
                }
                break;
            case VERTICAL:
                for (int i = 0; i < animationLength; i++) {
                    int y = (cursorY + i) % (getPlayerRows());
                    shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y));
                    shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y + 1), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y + 1));
                    shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y), x0 - 1 + 50 * fmx, y0 - 1 + 50 * (y + 1));
                    shapeRenderer.line(x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (y + 1));
                }
                break;
        }
        shapeRenderer.end();

        batch.begin();
        for (int j = 0; j < displayedRows; j++) {
            for (int i = 0; i < 8; i++) {
                player.fmx = i;
                player.fmy = j + fmy - cursorY;
                player.x = x0 + 7 + 50 * i + PlayerSprite.offsets[(int) Math.abs(Math.floor(player.fmy))][Math.round(player.fmx)][0];
                player.y = y0 + 10 + 50 * j + PlayerSprite.offsets[(int) Math.abs(Math.floor(player.fmy))][Math.round(player.fmx)][1];
                player.save(0);
                drawPlayerShadow();
                playerSprite.draw(0);
            }
        }

        switch (animation) {
            case ANIMATION_OFF:
                fmx2 = 0;
                fmy2 = 0;
                break;

            case HORIZONTAL:
                if (Gdx.graphics.getFrameId() % (6 - animationSpeed) == 0) {
                    fmx2 = Emath.rotate(fmx2, 0, 5 * animationLength - 1, 1);
                }
                fmy2 = 0;
                break;

            case VERTICAL:
                fmx2 = 0;
                if (Gdx.graphics.getFrameId() % (6 - animationSpeed) == 0) {
                    fmy2 = Emath.rotate(fmy2, 0, 5 * animationLength - 1, 1);
                }
                break;
        }

        // selected player (x2)
        player.x = 572;
        player.y = 120;
        player.fmx = (fmx + Emath.floor(fmx2 / 5f)) % 8;
        player.fmy = (fmy + Emath.floor(fmy2 / 5f)) % getPlayerRows();
        player.save(0);
        drawPlayerShadow();
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
        drawPlayerShadow();
        playerSprite.draw(0);
        batch.end();

        Data d = player.data[0];
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.line(d.x - 1, d.y, d.x + 1, d.y);
        shapeRenderer.line(d.x, d.y - 1, d.x, d.y + 1);
        shapeRenderer.end();
    }

    private void drawPlayerShadow() {
        if (shadows != Shadows.NONE) {
            for (int s = 0; s < (shadows == Shadows.NIGHT ? 4 : 1); s++) {
                Data d = player.data[0];
                if (d.isVisible) {
                    float offsetX = PlayerSprite.offsets[d.fmy][d.fmx][0];
                    float offsetY = PlayerSprite.offsets[d.fmy][d.fmx][1];
                    float mX = (s == 0 || s == 3) ? 0.65f : -0.65f;
                    float mY = (s == 0 || s == 1) ? 0.46f : -0.46f;
                    batch.setColor(0xFFFFFF, 0.5f);
                    batch.draw(Assets.playerShadow[d.fmx][d.fmy][s], d.x - offsetX + mX * d.z, d.y - offsetY + 5 + mY * d.z);
                }
            }
        }
        batch.setColor(0xFFFFFF, 1);
    }

    private class StyleLabel extends Button {

        StyleLabel(int x, int y) {
            setGeometry(x, y, 175, 23);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("KITS.STYLE"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class StyleButton extends Button {

        int kitIndex;

        StyleButton(int x, int y) {
            kitIndex = Assets.kits.indexOf(team.kits.get(0).style);
            setGeometry(x, y, 175, 24);
            setColor(0x881845);
        }

        @Override
        public void refresh() {
            setText(team.kits.get(0).style.replace('_', ' '), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            updateKitStyle(+1);
        }

        @Override
        public void onFire1Hold() {
            updateKitStyle(+1);
        }

        @Override
        public void onFire2Down() {
            updateKitStyle(-1);
        }

        @Override
        public void onFire2Hold() {
            updateKitStyle(-1);
        }

        private void updateKitStyle(int n) {
            kitIndex = Emath.rotate(kitIndex, 0, Assets.kits.size() - 1, n);
            team.kits.get(0).style = Assets.kits.get(kitIndex);

            reloadPlayer();

            refreshAllWidgets();
        }
    }

    private class KitFieldLabel extends Button {

        KitFieldLabel(String label, int x, int y) {
            setGeometry(x, y, 175, 23);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get(label), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class HashButton extends Button {

        Kit.Field field;
        int colorIndex;

        HashButton(Kit.Field field, int x, int y) {
            this.field = field;
            setGeometry(x, y, 40, 24);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("#", Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void refresh() {
            int color = 0;
            switch (field) {
                case SHIRT1:
                    color = team.kits.get(selectedKit).shirt1;
                    break;

                case SHIRT2:
                    color = team.kits.get(selectedKit).shirt2;
                    break;

                case SHIRT3:
                    color = team.kits.get(selectedKit).shirt3;
                    break;

                case SHORTS:
                    color = team.kits.get(selectedKit).shorts;
                    break;

                case SOCKS:
                    color = team.kits.get(selectedKit).socks;
                    break;
            }
            setColor(color);
        }

        @Override
        public void onFire1Down() {
            updateColor(1);
        }

        @Override
        public void onFire1Hold() {
            updateColor(1);
        }

        @Override
        public void onFire2Down() {
            updateColor(-1);
        }

        @Override
        public void onFire2Hold() {
            updateColor(-1);
        }

        private void updateColor(int n) {
            colorIndex = Emath.rotate(colorIndex, 0, Kit.colors.length - 1, n);
            int color = Kit.colors[colorIndex];
            switch (field) {
                case SHIRT1:
                    team.kits.get(selectedKit).shirt1 = color;
                    break;

                case SHIRT2:
                    team.kits.get(selectedKit).shirt2 = color;
                    break;

                case SHIRT3:
                    team.kits.get(selectedKit).shirt3 = color;
                    break;

                case SHORTS:
                    team.kits.get(selectedKit).shorts = color;
                    break;

                case SOCKS:
                    team.kits.get(selectedKit).socks = color;
                    break;
            }
            reloadPlayer();
            refreshAllWidgets();
        }
    }

    private class KitColorButton extends InputButton {

        Kit.Field field;

        KitColorButton(Kit.Field field, int x, int y) {
            this.field = field;
            setGeometry(x, y, 133, 24);
            setText("", Font.Align.CENTER, Assets.font10);
            setEntryLimit(6);
            setInputFilter("[A-F0-9]");
        }

        @Override
        public void refresh() {
            int color = getColor();
            setText(GLColor.toHexString(color).substring(1).toUpperCase());
            setColor(color);
        }

        @Override
        public void onChanged() {
            int color = text.length() == 0 ? 0 : GLColor.valueOf("#" + text);
            switch (field) {
                case SHIRT1:
                    team.kits.get(selectedKit).shirt1 = color;
                    break;

                case SHIRT2:
                    team.kits.get(selectedKit).shirt2 = color;
                    break;

                case SHIRT3:
                    team.kits.get(selectedKit).shirt3 = color;
                    break;

                case SHORTS:
                    team.kits.get(selectedKit).shorts = color;
                    break;

                case SOCKS:
                    team.kits.get(selectedKit).socks = color;
                    break;
            }
            reloadPlayer();
            refreshAllWidgets();
        }

        private int getColor() {
            Kit kit = team.kits.get(selectedKit);
            switch (field) {
                case SHIRT1:
                    return kit.shirt1;

                case SHIRT2:
                    return kit.shirt2;

                case SHIRT3:
                    return kit.shirt3;

                case SHORTS:
                    return kit.shorts;

                case SOCKS:
                    return kit.socks;

                default:
                    return 0;
            }
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
            player.skinColor = Skin.Color.values()[Emath.rotate(player.skinColor.ordinal(), Skin.Color.PINK.ordinal(), Skin.Color.RED.ordinal(), n)];

            setDirty(true);

            reloadPlayer();
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
            color = Emath.rotate(color, Hair.Color.BLACK.ordinal(), Hair.Color.PUNK_BLOND.ordinal(), n);
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
                i = Emath.rotate(i, 0, Assets.hairStyles.size() - 1, n);
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
            fmx = Emath.rotate(fmx, 0, 7, n);
            setDirty(true);
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
            cursorY = Emath.slide(cursorY, 0, displayedRows - 1, n);
            fmy = Emath.slide(fmy, 0, getPlayerRows() - 1, n);
            setDirty(true);
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
            animation = Animation.values()[Emath.rotate(animation, Animation.ANIMATION_OFF, Animation.VERTICAL, n)];
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
            animationLength = Emath.slide(animationLength, 2, 8, n);
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
            animationSpeed = Emath.slide(animationSpeed, 1, 5, n);
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
            shadows = Emath.rotate(shadows, Shadows.class, n);
            setDirty(true);
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
            game.setScreen(new DevTools(game));
        }
    }

    private void reloadPlayer() {
        Assets.unloadPlayer(player);
        Assets.loadPlayer(player);
    }

    private void reloadHair() {
        Assets.unloadHair(player);
        Assets.loadHair(player);
    }

    private int getPlayerRows() {
        return player.role == Player.Role.GOALKEEPER ? 19 : 16;
    }
}
