package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGraphics;

public abstract class Widget {

    // geometry
    public int x;
    public int y;
    public int w;
    public int h;

    // colors
    int body;
    int lightBorder;
    int darkBorder;

    // text
    String text;
    Font font;
    Font.Align align;
    int textOffsetX;

    // state
    public boolean isActive;
    public boolean isSelected;
    protected boolean entryMode;
    public boolean isVisible;

    public enum Event {
        NONE, FIRE1_DOWN, FIRE1_HOLD, FIRE1_UP, FIRE2_DOWN, FIRE2_HOLD, FIRE2_UP
    }

    public Widget() {
        isVisible = true;
    }

    public abstract void render(GlGraphics glGraphics);

    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setColors(int body, int lightBorder, int darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(String text, Font.Align align, Font font) {
        this.text = text;
        this.align = align;
        this.font = font;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void onFire1Down() {
    }

    public void onFire1Hold() {
    }

    public void onFire1Up() {
    }

    public void onFire2Down() {
    }

    public void onFire2Hold() {
    }

    public void onFire2Up() {
    }
}
