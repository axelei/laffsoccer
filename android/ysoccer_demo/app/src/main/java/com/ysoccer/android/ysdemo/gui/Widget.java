package com.ysoccer.android.ysdemo.gui;

import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.framework.math.Vector2;
import com.ysoccer.android.ysdemo.Assets;

public abstract class Widget {

    // geometry
    int x;
    int y;
    protected int w;
    int h;

    // texture and frame
    Texture texture;
    int frameW;
    int frameH;
    int frameX;
    int frameY;

    // image drawing
    int imageX;
    int imageY;
    int imageW;
    int imageH;

    // colors
    int body;
    int lightBorder;
    int darkBorder;

    // text
    String text;
    int charset; // 10 or 14
    int align; // -1 = right, 0 = center, 1 = left
    int textOffsetX;
    Texture imgUcode;

    // state
    public boolean isVisible;
    public boolean isActive;
    public boolean isSelected;
    boolean isBlinking;
    public boolean entryMode;
    boolean hasChanged;
    protected boolean addShadow;
    private boolean dirty;

    public Widget() {
        isVisible = true;
        isActive = false;
        isSelected = false;
        isBlinking = false;
        entryMode = false;
        hasChanged = false;
        addShadow = false;
        dirty = true;
    }

    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setTexture(Texture texture) {
        this.setTexture(texture, 0, 0, texture.width, texture.height);
    }

    public void setTexture(Texture texture, int imageX, int imageY, int imageW,
                           int imageH) {
        this.texture = texture;
        this.imageX = imageX;
        this.imageY = imageY;
        this.imageW = imageW;
        this.imageH = imageH;
    }

    public void setSizeByText(GLGraphics glGraphics) {
        w = glGraphics.ucodeWidth(text, charset);
        h = glGraphics.ucodeHeight(charset);
    }

    public void setFrame(int frameW, int frameH, int frameX, int frameY) {
        this.frameW = frameW;
        this.frameH = frameH;
        this.frameX = frameX;
        this.frameY = frameY;
    }

    public void setColors(int color) {
        this.body = color;
        this.lightBorder = color;
        this.darkBorder = color;
    }

    public void setColors(int body, int lightBorder, int darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(String text, int align, int charset) {
        this.text = text;
        this.align = align;

        if (charset != 0) {
            this.charset = charset;

            switch (this.charset) {
                case 10:
                    // imgUcode = Assets.ucode10;
                    break;
                case 14:
                    imgUcode = Assets.ucode14;
                    break;
            }
        }
    }

    public void setTextOffsetX(int textOffsetX) {
        this.textOffsetX = textOffsetX;
    }

    // Method mouse_over:Int()
    // If MouseX() < Self.x Return False
    // If MouseX() > Self.x + Self.w Return False
    // If MouseY() < Self.y Return False
    // If MouseY() > Self.y + Self.h Return False
    // Return True
    // End Method

    public void update() {
    }

    public abstract void render(GLGraphics glGraphics);

    // Method set_entry_mode(b:Int)
    // Self.entry_mode = b
    // End Method
    //
    // Method toggle_entry_mode()
    // Self.entry_mode = Not Self.entry_mode
    // End Method
    //
    // Method get_changed:Int()
    // Return Self.changed
    // End Method
    //
    // Method set_changed(b:Int)
    // Self.changed = b
    // End Method

    public boolean isPointInside(Vector2 touchPoint) {
        if (touchPoint.x < x)
            return false;
        if (touchPoint.x > x + w)
            return false;
        if (touchPoint.y < y)
            return false;
        if (touchPoint.y > y + h)
            return false;
        return true;
    }

    public void onFire1Down() {
    }

    public void onFire1Up() {
    }

    public void refresh() {
    }

    public boolean getDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
