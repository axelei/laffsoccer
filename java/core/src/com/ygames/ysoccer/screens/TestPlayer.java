package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Data;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.PlayerSprite;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.match.Team;

class TestPlayer extends GLScreen {

    private Player player;
    private PlayerSprite playerSprite;
    private int offset;

    TestPlayer(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;

        Team team = new Team();
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
        Assets.loadPlayer(player);
        Assets.loadHair(player);
        playerSprite = new PlayerSprite(game.glGraphics, player);

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

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
            setGeometry(200 - 180 / 2, 660, 180, 32);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font10);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DevTools(game));
        }
    }

}
