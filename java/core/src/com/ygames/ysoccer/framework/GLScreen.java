package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Gui;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class GLScreen implements Screen {

    protected final GLGame game;
    protected final OrthographicCamera camera;
    protected final GLSpriteBatch batch;
    protected final GLShapeRenderer shapeRenderer;
    protected Gui gui;

    protected Texture background;
    protected final List<Widget> widgets;
    private Widget selectedWidget;
    protected Widget.Event widgetEvent;
    protected boolean usesMouse;
    protected boolean playMenuMusic;
    protected InputDevice lastFireInputDevice;

    protected static class Navigation {
        public FileHandle folder;
        public String league;
        public Team team;
        public Competition competition;
        private Player clipboardPlayer;

        public Player getClipboardPlayer() {
            return clipboardPlayer;
        }

        public void setClipboardPlayer(Player player) {
            if (player != null) {
                if (clipboardPlayer == null) {
                    clipboardPlayer = new Player();
                    clipboardPlayer.skills = new Player.Skills();
                }
                clipboardPlayer.copyFrom(player);
            } else {
                clipboardPlayer = null;
            }
        }
    }

    protected static final Navigation navigation = new Navigation();

    public GLScreen(GLGame game) {
        this.game = game;
        camera = game.glGraphics.camera;
        batch = game.glGraphics.batch;
        shapeRenderer = game.glGraphics.shapeRenderer;
        this.gui = game.getGui();

        widgets = new ArrayList<>();
        usesMouse = true;
        playMenuMusic = true;
    }

    @Override
    public void render(float delta) {

        game.menuMusic.update(playMenuMusic ? game.settings.musicVolume : 0);

        camera.setToOrtho(true, game.gui.screenWidth, game.gui.screenHeight);
        camera.translate(-game.gui.originX, -game.gui.originY);
        camera.update();

        if (game.mouse.enabled) {
            game.mouse.read(camera);

            int len = widgets.size();
            for (int i = 0; i < len; i++) {
                Widget widget = widgets.get(i);
                if (widget.contains(game.mouse.position.x, game.mouse.position.y)) {
                    setSelectedWidget(widget);
                }
            }
        } else if (usesMouse && game.mouse.isActioned()) {
            game.mouse.enable();
        }

        for (InputDevice inputDevice : game.inputDevices) {
            inputDevice.update();
        }

        game.menuInput.read(this);

        int len = widgets.size();
        for (int i = 0; i < len; i++) {
            Widget widget = widgets.get(i);
            if (widget.getDirty()) {
                widget.refresh();
            }
            widget.setDirty(false);
        }

        widgetEvent = game.menuInput.getWidgetEvent();

        if (widgetEvent != null && selectedWidget != null) {
            selectedWidget.fireEvent(widgetEvent);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.setColor(0xFFFFFF, 1f);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(0xFFFFFF, 1f);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // background
        if (background != null) {
            batch.disableBlending();
            batch.begin();
            batch.draw(background, 0, 0, game.gui.WIDTH, game.gui.HEIGHT, 0, 0, background.getWidth(), background.getHeight(), false, true);
            batch.end();
            batch.enableBlending();
        }

        len = widgets.size();
        for (int i = 0; i < len; i++) {
            widgets.get(i).render(game.glGraphics.batch, game.glGraphics.shapeRenderer);
        }

        if (game.menuMusic.isPlaying()) {
            batch.begin();
            String s = "" + (char) (16 + (int) (Gdx.graphics.getFrameId() % 24) / 6);
            if (game.menuMusic.playList.size() > 1) {
                s += " " + game.menuMusic.getCurrentTrackName();
            }
            Assets.font10.draw(batch, s, 8, game.gui.HEIGHT - 20, Font.Align.LEFT);
            batch.end();
        }

        if (Settings.development && Settings.showJavaHeap) {
            batch.begin();
            Assets.font10.draw(batch, String.format(Locale.getDefault(), "%,d", Gdx.app.getJavaHeap()), game.gui.WIDTH - 120, 10, Font.Align.LEFT);
            batch.end();
        }
    }

    public Widget getSelectedWidget() {
        return selectedWidget;
    }

    public boolean setSelectedWidget(Widget widget) {
        if (widget == null || widget == selectedWidget || !widget.visible || !widget.active) {
            return false;
        }
        if (selectedWidget != null) {
            if (selectedWidget.entryMode) {
                return false;
            }
            selectedWidget.setSelected(false);
        }
        selectedWidget = widget;
        selectedWidget.setSelected(true);
        return true;
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        game.gui.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        refreshAllWidgets();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
    }

    protected void refreshAllWidgets() {
        for (Widget widget : widgets) {
            widget.setDirty(true);
        }
    }

    protected class TitleBar extends Button {

        public TitleBar(String text, int color) {
            setGeometry((game.gui.WIDTH - 960) / 2, 30, 960, 40);
            setColor(color);
            setText(text, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
