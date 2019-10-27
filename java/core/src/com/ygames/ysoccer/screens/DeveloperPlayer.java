package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Color3;
import com.ygames.ysoccer.framework.EMath;
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

import static com.ygames.ysoccer.framework.Assets.font10;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;

class DeveloperPlayer extends GLScreen {

    private enum Animation {ANIMATION_OFF, HORIZONTAL, VERTICAL}

    private enum Shadows {NONE, DAY, NIGHT}

    private Animation animation;
    private int animationLength;
    private int animationSpeed;

    private Shadows shadows;

    private final int displayedRows = 7;

    private final Team team;
    private final int selectedKit = 0;
    private final Player player;
    private final PlayerSprite playerSprite;
    private int fmx, fmy;
    private int fmx2 = 0;
    private int fmy2 = 0;
    private int firstRow;

    private final Widget originValue;
    private final Widget saveOriginsButton;
    private final Widget resetOriginsButton;

    private boolean hasExited = false;

    private String savedOrigins;

    DeveloperPlayer(GLGame game) {
        super(game);

        Gdx.input.setInputProcessor(new InputProcessor());

        background = new Texture("images/backgrounds/menu_edit_team.jpg");

        animation = Animation.ANIMATION_OFF;
        animationLength = 8;
        animationSpeed = 3;

        shadows = Shadows.DAY;

        Widget w;

        team = new Team();
        Kit kit = new Kit();
        kit.style = "PLAIN";
        kit.shirt1 = Kit.colors[2];
        kit.shirt2 = Kit.colors[5];
        kit.shirt3 = Kit.colors[1];
        team.kits.add(kit);
        player = new Player();
        fmx = 0;
        fmy = 0;
        firstRow = 0;
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
        savedOrigins = Assets.json.toJson(Assets.playerOrigins);

        int x = 10;
        int y = 20;

        w = new StyleLabel(x, y);
        widgets.add(w);

        y += 26;
        w = new StyleButton(x, y);
        widgets.add(w);

        y += 28;
        w = new KitFieldLabel("KITS.SHIRT", x, y, 76);
        widgets.add(w);

        for (int f = 0; f < 3; f++) {
            w = new HashButton(Kit.Field.values()[f], x, y);
            widgets.add(w);

            w = new KitColorButton(Kit.Field.values()[f], x, y);
            widgets.add(w);

            y += 26;
        }

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
            w = new KitFieldLabel(label, x, y, 24);
            widgets.add(w);

            w = new HashButton(Kit.Field.values()[f], x, y);
            widgets.add(w);

            w = new KitColorButton(Kit.Field.values()[f], x, y);
            widgets.add(w);

            y += 26;
        }

        y += 16;
        w = new SkinColorButton(x, y);
        widgets.add(w);

        w = new HairColorButton(x + 30, y);
        widgets.add(w);

        w = new HairStyleButton(x + 60, y);
        widgets.add(w);

        y += 40;
        w = new ColumnButton(x, y);
        widgets.add(w);

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
        w = new OriginLabel(x, y);
        widgets.add(w);

        w = new MoveOriginButton(x + 75, y, 0, -1);
        widgets.add(w);

        y += 34;
        w = new MoveOriginButton(x + 20, y, -1, 0);
        widgets.add(w);

        originValue = new OriginValue(x + 54, y);
        widgets.add(originValue);

        w = new MoveOriginButton(x + 130, y, +1, 0);
        widgets.add(w);

        y += 34;
        w = new MoveOriginButton(x + 75, y, 0, +1);
        widgets.add(w);

        saveOriginsButton = new saveOriginsButton(x, y);
        widgets.add(saveOriginsButton);

        resetOriginsButton = new ResetOriginsButton(x + 109, y);
        widgets.add(resetOriginsButton);

        y += 46;
        w = new ShadowsButton(x, y);
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
        shapeRenderer.setColor(0x2AA748, 1f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(100, 0, 540, 360);

        int x0 = 120;
        int y0 = 5;

        // scroll bar
        shapeRenderer.setColor(0x444444, 1f);
        shapeRenderer.rect(x0 - 18, y0 - 50f * displayedRows * firstRow / getPlayerRows(), 4, 50f * displayedRows * displayedRows / getPlayerRows());

        // animation grid
        shapeRenderer.setColor(0xFCFC00, 1f);
        switch (animation) {
            case ANIMATION_OFF:
                shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (fmy + firstRow), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (fmy + firstRow));
                shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (fmy + firstRow) + 50, x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (fmy + firstRow) + 50);
                shapeRenderer.line(x0 - 1 + 50 * fmx, y0 - 1 + 50 * (fmy + firstRow), x0 - 1 + 50 * fmx, y0 - 1 + 50 * (fmy + firstRow) + 50);
                shapeRenderer.line(x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (fmy + firstRow), x0 - 1 + 50 * (fmx + 1), y0 - 1 + 50 * (fmy + firstRow) + 50);
                break;
            case HORIZONTAL:
                for (int i = 0; i < animationLength; i++) {
                    int x = (fmx + i) % 8;
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + firstRow), x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + firstRow));
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + firstRow) + 50, x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + firstRow) + 50);
                    shapeRenderer.line(x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + firstRow), x0 - 1 + 50 * (x), y0 - 1 + 50 * (fmy + firstRow) + 50);
                    shapeRenderer.line(x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + firstRow), x0 - 1 + 50 * (x + 1), y0 - 1 + 50 * (fmy + firstRow) + 50);
                }
                break;
            case VERTICAL:
                for (int i = 0; i < animationLength; i++) {
                    int y = (fmy + firstRow + i) % (getPlayerRows());
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
                player.fmy = j - firstRow;
                player.x = x0 + 7 + 50 * i + Assets.playerOrigins[(int) Math.abs(Math.floor(player.fmy))][Math.round(player.fmx)][0];
                player.y = y0 + 10 + 50 * j + Assets.playerOrigins[(int) Math.abs(Math.floor(player.fmy))][Math.round(player.fmx)][1];
                player.save(0);
                drawPlayerShadow();
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

        // origin
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
                    Integer[] origin = Assets.playerOrigins[d.fmy][d.fmx];
                    float mX = (s == 0 || s == 3) ? 0.65f : -0.65f;
                    float mY = (s == 0 || s == 1) ? 0.46f : -0.46f;
                    batch.setColor(0xFFFFFF, 0.5f);
                    batch.draw(Assets.playerShadow[d.fmx][d.fmy][s], d.x - origin[0] + mX * d.z, d.y - origin[1] + 5 + mY * d.z);
                }
            }
        }
        batch.setColor(0xFFFFFF, 1);
    }

    private class StyleLabel extends Button {

        StyleLabel(int x, int y) {
            setGeometry(x, y, 180, 24);
            setColor(0x666666);
            setText(Assets.strings.get("KITS.STYLE"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class StyleButton extends Button {

        int kitIndex;

        StyleButton(int x, int y) {
            kitIndex = Assets.kits.indexOf(team.kits.get(0).style);
            setGeometry(x, y, 180, 24);
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
            kitIndex = EMath.rotate(kitIndex, 0, Assets.kits.size() - 1, n);
            team.kits.get(0).style = Assets.kits.get(kitIndex);

            reloadPlayer();

            refreshAllWidgets();
        }
    }

    private class KitFieldLabel extends Button {

        KitFieldLabel(String label, int x, int y, int h) {
            setGeometry(x, y, 80, h);
            setColor(0x666666);
            setText(Assets.strings.get(label), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class HashButton extends Button {

        final Kit.Field field;
        int colorIndex;

        HashButton(Kit.Field field, int x, int y) {
            this.field = field;
            setGeometry(x + 82, y, 18, 24);
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
            colorIndex = EMath.rotate(colorIndex, 0, Kit.colors.length - 1, n);
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

        final Kit.Field field;

        KitColorButton(Kit.Field field, int x, int y) {
            this.field = field;
            setGeometry(x + 102, y, 78, 24);
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
            setGeometry(x, y, 28, 24);
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

            reloadPlayer();
        }
    }

    private class HairColorButton extends Button {

        HairColorButton(int x, int y) {
            setGeometry(x, y, 28, 24);
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
            setGeometry(x, y, 115, 24);
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
            setGeometry(x, y, 88, 24);
            setColors(0x308C3B, 0x4AC058, 0x1F5926);
        }

        @Override
        public void refresh() {
            setText("COL: " + fmx, Font.Align.CENTER, Assets.font10);
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
            setDirty(true);
        }
    }

    private class RowButton extends Button {

        RowButton(int x, int y) {
            setGeometry(x + 4 + 88, y, 88, 24);
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
            setGeometry(x, y, 180, 24);
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
            setGeometry(x, y, 180, 24);
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
            setGeometry(x, y, 180, 24);
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
            setGeometry(x, y, 180, 24);
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

    private class OriginLabel extends Button {

        OriginLabel(int x, int y) {
            setGeometry(x, y, 64, 24);
            setText("ORIGIN", CENTER, font10);
            setActive(false);
        }
    }

    private class MoveOriginButton extends Button {

        final int xDir, yDir;

        MoveOriginButton(int x, int y, int xDir, int yDir) {
            this.xDir = xDir;
            this.yDir = yDir;
            setGeometry(x, y, 30, 30);
            setColor(0x2AA748);
            int fx = (yDir != 0 ? 2 : 0) + (xDir < 0 || yDir < 0 ? 4 : 0);
            textureRegion = Assets.wind[fx][0];
            setImagePosition(-3, -3);
        }

        @Override
        public void onFire1Down() {
            updateOrigin();
        }

        @Override
        public void onFire1Hold() {
            updateOrigin();
        }

        private void updateOrigin() {
            Assets.playerOrigins[fmy][fmx][0] += xDir;
            Assets.playerOrigins[fmy][fmx][1] += yDir;
            originValue.setDirty(true);
            saveOriginsButton.setDirty(true);
            resetOriginsButton.setDirty(true);
        }
    }

    private class OriginValue extends Button {

        OriginValue(int x, int y) {
            setGeometry(x, y, 72, 30);
            setColor(0x666666);
            setText("", CENTER, font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(Assets.playerOrigins[fmy][fmx][0] + ", " + Assets.playerOrigins[fmy][fmx][1]);
        }
    }

    private class saveOriginsButton extends Button {

        saveOriginsButton(int x, int y) {
            setGeometry(x, y, 71, 30);
            setText("SAVE", CENTER, font10);
        }

        @Override
        public void refresh() {
            String current = Assets.json.toJson(Assets.playerOrigins);
            boolean modified = !current.equals(savedOrigins);
            setColor(modified ? 0xCC0000 : 0x666666);
            setActive(modified);
        }

        @Override
        public void onFire1Down() {
            Assets.savePlayerOrigins();
            savedOrigins = Assets.json.toJson(Assets.playerOrigins);
            setDirty(true);
            resetOriginsButton.setDirty(true);
        }

    }

    private class ResetOriginsButton extends Button {

        ResetOriginsButton(int x, int y) {
            setGeometry(x, y, 71, 30);
            setText("RESET", CENTER, font10);
        }

        @Override
        public void refresh() {
            String current = Assets.json.toJson(Assets.playerOrigins);
            boolean modified = !current.equals(savedOrigins);
            setColor(modified ? 0xCCCC00 : 0x666666);
            setActive(modified);
        }

        @Override
        public void onFire1Down() {
            Assets.loadPlayerOrigins();
            setDirty(true);
            saveOriginsButton.setDirty(true);
            originValue.setDirty(true);
        }

    }

    private class FrameButton extends Button {

        private final int column, row;

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
            boolean selected = (row == fmy + firstRow) && (column == fmx);
            setColors(null, selected ? 0xFF0000 : null, selected ? 0xFF0000 : null);
        }

        @Override
        public void onFire1Down() {
            updateSelected();
        }

        private void updateSelected() {
            fmx = column;
            fmy = row - firstRow;
            refreshAllWidgets();
        }
    }

    private class ExitButton extends Button {

        ExitButton(int x) {
            setGeometry(x, 660, 180, 32);
            setColor(0xC84200);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            hasExited = true;
            Assets.unloadPlayer(player);
            Gdx.input.setInputProcessor(null);
            game.setScreen(new DeveloperTools(game));
        }
    }

    private void reloadPlayer() {
        Assets.unloadPlayer(player);
        Assets.loadPlayer(player, team.kits.get(team.kitIndex));
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
            firstRow = EMath.slide(firstRow, displayedRows - getPlayerRows(), 0, -n);
            refreshAllWidgets();
            return true;
        }
    }

}
