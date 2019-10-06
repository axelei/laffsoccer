package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.gui.Widget;

import static com.ygames.ysoccer.gui.Widget.Event.FIRE1_DOWN;
import static com.ygames.ysoccer.gui.Widget.Event.FIRE1_HOLD;
import static com.ygames.ysoccer.gui.Widget.Event.FIRE1_UP;
import static com.ygames.ysoccer.gui.Widget.Event.FIRE2_DOWN;
import static com.ygames.ysoccer.gui.Widget.Event.FIRE2_HOLD;
import static com.ygames.ysoccer.gui.Widget.Event.FIRE2_UP;

class MenuInput {

    // values
    private int x;
    private int y;
    private boolean fire1;
    private boolean fire2;

    private boolean fire1Old;
    private boolean fire2Old;

    // timers
    private int xTimer;
    private int yTimer;
    private int fire1Timer;
    private int fire2Timer;

    private final GLGame game;

    MenuInput(GLGame game) {
        this.game = game;
    }

    void read(GLScreen screen) {

        // fire 1 delay
        if (fire1) {
            if (!fire1Old) {
                fire1Timer = 20;
            } else if (fire1Timer == 0) {
                fire1Timer = 6;
            }
        } else {
            if (!fire1Old) {
                fire1Timer = 0;
            }
        }

        if (fire1Timer > 0) {
            fire1Timer -= 1;
        }

        // fire 2 delay
        if (fire2) {
            if (!fire2Old) {
                fire2Timer = 20;
            } else if (fire2Timer == 0) {
                fire2Timer = 6;
            }
        } else {
            if (!fire2Old) {
                fire2Timer = 0;
            }
        }

        if (fire2Timer > 0) {
            fire2Timer -= 1;
        }

        // old values
        int xOld = x;
        int yOld = y;
        fire1Old = fire1;
        fire2Old = fire2;

        x = 0;
        y = 0;
        fire1 = false;
        fire2 = false;

        int len = game.inputDevices.size();
        for (int i = 0; i < len; i++) {
            InputDevice inputDevice = game.inputDevices.get(i);

            // x axis
            int newX = inputDevice.x1;
            if (newX != 0) {
                x = newX;
                game.disableMouse();
            }

            // y axis
            int newY = inputDevice.y1;
            if (newY != 0) {
                y = newY;
                game.disableMouse();
            }

            // fire 1
            if (inputDevice.fire11) {
                fire1 = true;
                screen.lastFireInputDevice = inputDevice;
                game.disableMouse();
            }

            // fire 2
            if (inputDevice.fire21) {
                fire2 = true;
                screen.lastFireInputDevice = inputDevice;
                game.disableMouse();
            }
        }

        // mouse buttons
        Widget selectedWidget = screen.getSelectedWidget();
        if (game.mouse.enabled
                && selectedWidget != null
                && selectedWidget.contains(game.mouse.position.x, game.mouse.position.y)) {
            if (game.mouse.buttonLeft) {
                fire1 = true;
                screen.lastFireInputDevice = null;
            }
            if (game.mouse.buttonRight) {
                fire2 = true;
                screen.lastFireInputDevice = null;
            }
        }

        // up / down
        int bias = 1;
        if (selectedWidget != null) {
            if (y == -1 && yTimer == 0) {
                float distMin = 50000;
                float distance;
                for (Widget w : screen.widgets) {
                    if ((w.y + w.h) <= selectedWidget.y) {
                        distance = EMath.hypo(bias * ((w.x + 0.5f * w.w) - (selectedWidget.x + 0.5f * selectedWidget.w)), (w.y + 0.5f * w.h) - (selectedWidget.y + 0.5f * selectedWidget.h));
                        if (distance < distMin && screen.setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            } else if (y == 1 && yTimer == 0) {
                float distMin = 50000;
                float distance;
                for (Widget w : screen.widgets) {
                    if (w.y >= (selectedWidget.y + selectedWidget.h)) {
                        distance = EMath.hypo(bias * ((w.x + 0.5f * w.w) - (selectedWidget.x + 0.5f * selectedWidget.w)), (w.y + 0.5f * w.h) - (selectedWidget.y + 0.5f * selectedWidget.h));
                        if (distance < distMin && screen.setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            }
        }

        // left / right
        bias = 9;
        if (selectedWidget != null) {
            if (x == -1 && xTimer == 0) {
                float distMin = 50000;
                float distance;
                for (Widget w : screen.widgets) {
                    if ((w.x + w.w) <= selectedWidget.x) {
                        distance = EMath.hypo((w.x + 0.5f * w.w) - (selectedWidget.x + 0.5f * selectedWidget.w), bias * ((w.y + 0.5f * w.h) - (selectedWidget.y + 0.5f * selectedWidget.h)));
                        if (distance < distMin && screen.setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            } else if (x == 1 && xTimer == 0) {
                float distMin = 50000;
                float distance;
                for (Widget w : screen.widgets) {
                    if (w.x >= (selectedWidget.x + selectedWidget.w)) {
                        distance = EMath.hypo((w.x + 0.5f * w.w) - (selectedWidget.x + 0.5f * selectedWidget.w), bias * ((w.y + 0.5f * w.h) - (selectedWidget.y + 0.5f * selectedWidget.h)));
                        if (distance < distMin && screen.setSelectedWidget(w)) {
                            distMin = distance;
                        }
                    }
                }
            }
        }

        // x-y delays
        if (x != 0) {
            if (xOld == 0) {
                xTimer = 12;
            } else if (xTimer == 0) {
                xTimer = 4;
            }
        } else {
            xTimer = 0;
        }
        if (y != 0) {
            if (yOld == 0) {
                yTimer = 12;
            } else if (yTimer == 0) {
                yTimer = 4;
            }
        } else {
            yTimer = 0;
        }

        if (xTimer > 0) {
            xTimer -= 1;
        }
        if (yTimer > 0) {
            yTimer -= 1;
        }
    }

    Widget.Event getWidgetEvent() {

        if (fire1) {
            if (fire1Old) {
                if (fire1Timer == 0) {
                    return FIRE1_HOLD;
                }
            } else {
                return FIRE1_DOWN;
            }
        } else {
            if (fire1Old) {
                return FIRE1_UP;
            }
        }

        if (fire2) {
            if (fire2Old) {
                if (fire2Timer == 0) {
                    return FIRE2_HOLD;
                }
            } else {
                return FIRE2_DOWN;
            }
        } else {
            if (fire2Old) {
                return FIRE2_UP;
            }
        }

        return null;
    }
}
