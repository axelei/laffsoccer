package com.ygames.ysoccer.screens;

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

    private Team team;
    private int selectedKit = 0;
    private Player player;
    private PlayerSprite playerSprite;
    private int offset;

    TestPlayer(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_edit_team.jpg");

        Widget w;

        team = new Team();
        Kit kit = new Kit();
        kit.style = "PLAIN";
        kit.shirt1 = Kit.colors[2];
        kit.shirt2 = Kit.colors[5];
        kit.shirt3 = Kit.colors[1];
        team.kits.add(kit);
        player = new Player();
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

        w = new StyleLabel();
        widgets.add(w);

        w = new StyleButton();
        widgets.add(w);

        int x = 110;
        int y = 118;
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

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.setToOrtho(true, game.gui.screenWidth / 2, game.gui.screenHeight / 2); // 640 x 360
        camera.translate(-game.gui.originX / 2, -game.gui.originY / 2);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.setColor(0xFFFFFF, 1f);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(0x2AA748, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(200, 0, 440, 360);
        shapeRenderer.end();

        batch.begin();
        for (int j = offset; j < offset + 10; j++) {
            for (int i = 0; i < 8; i++) {
                player.x = 240 + 32 * i;
                player.y = 40 + 36 * j;
                player.fmx = i;
                player.fmy = j;
                player.save(0);
                playerSprite.draw(0);
            }
        }

        // selected player (x2)
        player.x = 560;
        player.y = 80;
        player.fmx = 2;
        player.fmy = 2;
        player.save(0);
        playerSprite.draw(0);

        batch.end();

        // selected player (x4)
        camera.setToOrtho(true, game.gui.screenWidth / 4, game.gui.screenHeight / 4); // 320 x 180
        camera.translate(-game.gui.originX / 4, -game.gui.originY / 4);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.setColor(0xFFFFFF, 1f);

        player.x = 280;
        player.y = 80;
        player.fmx = 2;
        player.fmy = 2;
        player.save(0);
        batch.begin();
        playerSprite.draw(0);
        batch.end();

        // selected player (x8)
        camera.setToOrtho(true, game.gui.screenWidth / 8, game.gui.screenHeight / 8); // 160 x 90
        camera.translate(-game.gui.originX / 8, -game.gui.originY / 8);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.setColor(0xFFFFFF, 1f);

        player.x = 140;
        player.y = 80;
        player.fmx = 2;
        player.fmy = 2;
        player.save(0);
        batch.begin();
        playerSprite.draw(0);
        batch.end();
    }

    private class StyleLabel extends Button {

        StyleLabel() {
            setGeometry(110, 64, 175, 23);
            setColors(0x808080, 0xC0C0C0, 0x404040);
            setText(Assets.strings.get("KITS.STYLE"), Font.Align.CENTER, Assets.font10);
            setActive(false);
        }
    }

    private class StyleButton extends Button {

        int kitIndex;

        StyleButton() {
            kitIndex = Assets.kits.indexOf(team.kits.get(0).style);
            setGeometry(110, 64 + 25, 175, 24);
            setColors(0x881845);
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
            setColors(color);
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
            setColors(color);
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

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry(110, 660, 175, 32);
            setColors(0xC84200);
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
}
