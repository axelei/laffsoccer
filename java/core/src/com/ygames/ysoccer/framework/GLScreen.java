package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class GLScreen implements Screen {

    protected GLGame game;
    protected OrthographicCamera camera;
    protected GLSpriteBatch batch;
    protected GLShapeRenderer shapeRenderer;

    protected Texture background;
    protected List<Widget> widgets;
    protected Widget selectedWidget;
    protected boolean playMenuMusic;
    protected InputDevice lastFireInputDevice;

    protected static class Navigation {
        public FileHandle folder;
        public String league;
        public Team team;
        public MatchSettings matchSettings;
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

        widgets = new ArrayList<>();
        playMenuMusic = true;
    }

    @Override
    public void render(float delta) {

        game.menuMusic.update(playMenuMusic ? game.settings.musicVolume : 0);

        camera.setToOrtho(true, game.gui.screenWidth, game.gui.screenHeight);
        camera.translate(-game.gui.originX, -game.gui.originY);
        camera.update();

        if (game.settings.mouseEnabled) {
            game.mouse.read(camera);

            int len = widgets.size();
            for (int i = 0; i < len; i++) {
                Widget widget = widgets.get(i);
                if (widget.contains(game.mouse.position.x, game.mouse.position.y)) {
                    setSelectedWidget(widget);
                }
            }
        }

        for (InputDevice inputDevice : game.inputDevices) {
            inputDevice.update();
        }

        readMenuInput();

        int len = widgets.size();
        for (int i = 0; i < len; i++) {
            Widget widget = widgets.get(i);
            if (widget.getDirty()) {
                widget.refresh();
            }
            widget.setDirty(false);
        }

        GLGame.MenuInput menuInput = game.menuInput;
        Widget.Event widgetEvent = Widget.Event.NONE;

        // fire 1 events
        if (!menuInput.fire1Old && menuInput.fire1) {
            widgetEvent = Widget.Event.FIRE1_DOWN;
        }
        if (menuInput.fire1 && menuInput.fire1Old && menuInput.fire1Timer == 0) {
            widgetEvent = Widget.Event.FIRE1_HOLD;
        }
        if (menuInput.fire1Old && !menuInput.fire1) {
            widgetEvent = Widget.Event.FIRE1_UP;
        }

        // fire 2 events
        if (!menuInput.fire2Old && menuInput.fire2) {
            widgetEvent = Widget.Event.FIRE2_DOWN;
        }
        if (menuInput.fire2 && menuInput.fire2Old && menuInput.fire2Timer == 0) {
            widgetEvent = Widget.Event.FIRE2_HOLD;
        }
        if (menuInput.fire2Old && !menuInput.fire2) {
            widgetEvent = Widget.Event.FIRE2_UP;
        }

        if (selectedWidget != null) {
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

    private void readMenuInput() {
        GLGame.MenuInput menuInput = game.menuInput;

        // fire 1 delay
        if (menuInput.fire1) {
            if (!menuInput.fire1Old) {
                menuInput.fire1Timer = 20;
            } else if (menuInput.fire1Timer == 0) {
                menuInput.fire1Timer = 6;
            }
        } else {
            if (!menuInput.fire1Old) {
                menuInput.fire1Timer = 0;
            }
        }

        if (menuInput.fire1Timer > 0) {
            menuInput.fire1Timer -= 1;
        }

        // fire 2 delay
        if (menuInput.fire2) {
            if (!menuInput.fire2Old) {
                menuInput.fire2Timer = 20;
            } else if (menuInput.fire2Timer == 0) {
                menuInput.fire2Timer = 6;
            }
        } else {
            if (!menuInput.fire2Old) {
                menuInput.fire2Timer = 0;
            }
        }

        if (menuInput.fire2Timer > 0) {
            menuInput.fire2Timer -= 1;
        }

        // old values
        menuInput.xOld = menuInput.x;
        menuInput.yOld = menuInput.y;
        menuInput.fire1Old = menuInput.fire1;
        menuInput.fire2Old = menuInput.fire2;

        menuInput.x = 0;
        menuInput.y = 0;
        menuInput.fire1 = false;
        menuInput.fire2 = false;

        int len = game.inputDevices.size();
        for (int i = 0; i < len; i++) {
            InputDevice inputDevice = game.inputDevices.get(i);

            // x movement
            int x = inputDevice.x1;
            if (x != 0) {
                menuInput.x = x;
            }

            // y movement
            int y = inputDevice.y1;
            if (y != 0) {
                menuInput.y = y;
            }

            // fire 1
            if (inputDevice.fire11) {
                menuInput.fire1 = true;
                lastFireInputDevice = inputDevice;
            }

            // fire 2
            if (inputDevice.fire21) {
                menuInput.fire2 = true;
                lastFireInputDevice = inputDevice;
            }
        }

        if (game.settings.mouseEnabled
                && selectedWidget != null
                && selectedWidget.contains(game.mouse.position.x, game.mouse.position.y)) {
            if (game.mouse.button1) {
                menuInput.fire1 = true;
                lastFireInputDevice = null;
            }
            if (game.mouse.button2) {
                menuInput.fire2 = true;
                lastFireInputDevice = null;
            }
        }

        // up/down
        int bias = 1;
        if (selectedWidget != null) {
            if (menuInput.y == -1 && menuInput.yTimer == 0) {
                Widget current = selectedWidget;
                float distMin = 50000;
                float distance;
                for (Widget w : widgets) {
                    if ((w.y + w.h) <= current.y) {
                        distance = Emath.hypo(bias * ((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w)), (w.y + 0.5f * w.h) - (current.y + 0.5f * current.h));
                        if (distance < distMin && setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            } else if (menuInput.y == 1 && menuInput.yTimer == 0) {
                Widget current = selectedWidget;
                float distMin = 50000;
                float distance;
                for (Widget w : widgets) {
                    if (w.y >= (current.y + current.h)) {
                        distance = Emath.hypo(bias * ((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w)), (w.y + 0.5f * w.h) - (current.y + 0.5f * current.h));
                        if (distance < distMin && setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            }
        }

        // left/right
        bias = 9;
        if (selectedWidget != null) {
            if (menuInput.x == -1 && menuInput.xTimer == 0) {
                Widget current = selectedWidget;
                float distMin = 50000;
                float distance;
                for (Widget w : widgets) {
                    if ((w.x + w.w) <= current.x) {
                        distance = Emath.hypo((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w), bias * ((w.y + 0.5f * w.h) - (current.y + 0.5f * current.h)));
                        if (distance < distMin && setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            } else if (menuInput.x == 1 && menuInput.xTimer == 0) {
                Widget current = selectedWidget;
                float distMin = 50000;
                float distance;
                for (Widget w : widgets) {
                    if (w.x >= (current.x + current.w)) {
                        distance = Emath.hypo((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w), bias * ((w.y + 0.5f * w.h) - (current.y + 0.5f * current.h)));
                        if (distance < distMin && setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            }
        }

        // x-y delays
        if (menuInput.x != 0) {
            if (menuInput.xOld == 0) {
                menuInput.xTimer = 8;
            } else if (menuInput.xTimer == 0) {
                menuInput.xTimer = 2;
            }
        } else {
            menuInput.xTimer = 0;
        }
        if (menuInput.y != 0) {
            if (menuInput.yOld == 0) {
                menuInput.yTimer = 8;
            } else if (menuInput.yTimer == 0) {
                menuInput.yTimer = 2;
            }
        } else {
            menuInput.yTimer = 0;
        }

        if (menuInput.xTimer > 0) {
            menuInput.xTimer -= 1;
        }
        if (menuInput.yTimer > 0) {
            menuInput.yTimer -= 1;
        }
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
