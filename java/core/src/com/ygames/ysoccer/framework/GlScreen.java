package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.YSoccer;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.List;

public abstract class GlScreen implements Screen {

    protected GLGame game;
    protected Image background;
    protected List<Widget> widgets;
    protected Widget selectedWidget;

    public GlScreen(GLGame game) {
        this.game = game;
        widgets = new ArrayList<Widget>();
    }

    @Override
    public void render(float delta) {

        game.mouse.read(game.glGraphics.camera);

        if (selectedWidget == null || !selectedWidget.entryMode) {
            for (Widget w : widgets) {
                if (w.contains(game.mouse.position.x, game.mouse.position.y) && w.isVisible && w.isActive) {
                    selectedWidget = w;
                }
            }
        }

        int len = game.inputDevices.size();
        for (int i = 0; i < len; i++) {
            game.inputDevices.get(i).update();
        }

        selectedWidget = readMenuInput();

        len = widgets.size();
        for (int i = 0; i < len; i++) {
            Widget widget = widgets.get(i);
            widget.update();
            if (widget.getChanged()) {
                widget.onUpdate();
            }
            widget.setChanged(false);
        }

        YSoccer.MenuInput menuInput = game.menuInput;
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

        if (selectedWidget != null && selectedWidget.isActive) {
            selectedWidget.fireEvent(widgetEvent);
        }

        OrthographicCamera camera = game.glGraphics.camera;
        SpriteBatch batch = game.glGraphics.batch;
        ShapeRenderer shapeRenderer = game.glGraphics.shapeRenderer;

        camera.update();

        // background
        batch.setProjectionMatrix(camera.combined);
        if (background != null) {
            batch.begin();
            batch.draw(background, 0, 0, 1280, 720);
            batch.end();
        }

        // widgets
        shapeRenderer.setProjectionMatrix(camera.combined);
        len = widgets.size();
        for (int i = 0; i < len; i++) {
            widgets.get(i).render(game.glGraphics);
        }
    }

    private Widget readMenuInput() {
        int len = widgets.size();
        for (int i = 0; i < len; i++) {
            Widget widget = widgets.get(i);
            widget.isSelected = false;
        }

        YSoccer.MenuInput menuInput = game.menuInput;

        // fire 1 delay
        if (menuInput.fire1) {
            if (!menuInput.fire1Old) {
                menuInput.fire1Timer = 10;
            } else if (menuInput.fire1Timer == 0) {
                menuInput.fire1Timer = 4;
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
                menuInput.fire2Timer = 10;
            } else if (menuInput.fire2Timer == 0) {
                menuInput.fire2Timer = 4;
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

        len = game.inputDevices.size();
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
            }

            // fire 2
            if (inputDevice.fire21) {
                menuInput.fire2 = true;
            }
        }

        if (selectedWidget != null && selectedWidget.contains(game.mouse.position.x, game.mouse.position.y)) {
            if (game.mouse.button1) {
                menuInput.fire1 = true;
            }
            if (game.mouse.button2) {
                menuInput.fire2 = true;
            }
        }

        // up/down
        int bias = 1;
        if (menuInput.y == -1 && menuInput.yTimer == 0) {
            Widget current = selectedWidget;
            float distMin = 50000;
            float distance;
            int len2 = widgets.size();
            for (int j = 0; j < len2; j++) {
                Widget w = widgets.get(j);
                if (w == current || !w.isVisible || !w.isActive) {
                    continue;
                }
                if ((w.y + w.h) <= current.y) {
                    distance = Emath.hypo(bias * ((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w)), (w.y + 0.5f * w.h) - (current.y + 0.5f * current.h));
                    if (distance < distMin) {
                        distMin = distance;
                        selectedWidget = w;
                    }
                }
            }
        } else if (menuInput.y == 1 && menuInput.yTimer == 0) {
            Widget current = selectedWidget;
            float distMin = 50000;
            float distance;
            int len2 = widgets.size();
            for (int j = 0; j < len2; j++) {
                Widget w = widgets.get(j);
                if (w == current || !w.isVisible || !w.isActive) {
                    continue;
                }
                if (w.y >= (current.y + current.h)) {
                    distance = Emath.hypo(bias * ((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w)), (w.y + 0.5f * w.h) - (current.y + 0.5f * current.h));
                    if (distance < distMin) {
                        distMin = distance;
                        selectedWidget = w;
                    }
                }
            }
        }

        // left/right
        bias = 9;
        if (menuInput.x == -1 && menuInput.xTimer == 0) {
            Widget current = selectedWidget;
            float distMin = 50000;
            float distance;
            int len2 = widgets.size();
            for (int j = 0; j < len2; j++) {
                Widget w = widgets.get(j);
                if (w == current || !w.isVisible || !w.isActive) {
                    continue;
                }
                if ((w.x + w.w) <= current.x) {
                    distance = Emath.hypo((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w), bias * ((w.y + 0.5f * w.h) - (current.y + 0.5f * current.h)));
                    if (distance < distMin) {
                        distMin = distance;
                        selectedWidget = w;
                    }
                }
            }
        } else if (menuInput.x == 1 && menuInput.xTimer == 0) {
            Widget current = selectedWidget;
            float distMin = 50000;
            float distance;
            int len2 = widgets.size();
            for (int j = 0; j < len2; j++) {
                Widget w = widgets.get(j);
                if (w == current || !w.isVisible || !w.isActive) {
                    continue;
                }
                if (w.x >= (current.x + current.w)) {
                    distance = Emath.hypo((w.x + 0.5f * w.w) - (current.x + 0.5f * current.w), bias * ((w.y + 0.5f * w.h) - (current.y + 0.5f * current.h)));
                    if (distance < distMin) {
                        distMin = distance;
                        selectedWidget = w;
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

        if (selectedWidget != null) {
            selectedWidget.isSelected = true;
        }

        return selectedWidget;
    }


    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
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
}
