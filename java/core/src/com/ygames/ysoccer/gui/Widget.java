package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.math.Emath;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;

public abstract class Widget {

    // geometry
    public int x;
    public int y;
    public int w;
    public int h;

    public TextureRegion textureRegion;
    int imageX;
    int imageY;
    float imageScaleX;
    float imageScaleY;

    WidgetColor color;

    // text
    protected String text;
    Font font;
    Font.Align align;
    protected int textOffsetX;

    // flags
    public boolean active;
    public boolean selected;
    public boolean entryMode;
    public boolean visible;
    boolean addShadow;
    boolean dirty;

    public enum Event {
        NONE, FIRE1_DOWN, FIRE1_HOLD, FIRE1_UP, FIRE2_DOWN, FIRE2_HOLD, FIRE2_UP
    }

    public Widget() {
        imageScaleX = 1.0f;
        imageScaleY = 1.0f;
        color = new WidgetColor();
        align = Font.Align.CENTER;
        visible = true;
        dirty = true;
    }

    public abstract void render(GLGraphics glGraphics);

    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void setImagePosition(int imageX, int imageY) {
        this.imageX = imageX;
        this.imageY = imageY;
    }

    protected void setImageScale(float scaleX, float scaleY) {
        imageScaleX = scaleX;
        imageScaleY = scaleY;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setColors(int body, int lightBorder, int darkBorder) {
        this.color.set(body, lightBorder, darkBorder);
    }

    public void setColors(Color body, Color lightBorder, Color darkBorder) {
        this.color.set(body.getRGB(), lightBorder.getRGB(), darkBorder.getRGB());
    }

    public void setColors(WidgetColor color) {
        this.color.set(color.body, color.lightBorder, color.darkBorder);
    }

    public void setColors(int color) {
        setColors(new Color(color));
    }

    public void setColors(Color color) {
        setColors(color.getRGB(), color.brighter().getRGB(), color.darker().getRGB());
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(int i) {
        setText("" + i);
    }

    public void setText(String text, Font.Align align, Font font) {
        this.text = text;
        this.align = align;
        this.font = font;
    }

    public void setText(int i, Font.Align align, Font font) {
        this.text = "" + i;
        this.align = align;
        this.font = font;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSelected(boolean selected) {
        if (this.selected && !selected) {
            onDeselect();
        }
        this.selected = selected;
    }

    public void onDeselect() {
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected void setAddShadow(boolean addShadow) {
        this.addShadow = addShadow;
    }

    public void fireEvent(Event widgetEvent) {

        if (!active) return;

        switch (widgetEvent) {
            case FIRE1_DOWN:
                onFire1Down();
                break;

            case FIRE1_HOLD:
                onFire1Hold();
                break;

            case FIRE1_UP:
                onFire1Up();
                break;

            case FIRE2_DOWN:
                onFire2Down();
                break;

            case FIRE2_HOLD:
                onFire2Hold();
                break;

            case FIRE2_UP:
                onFire2Up();
                break;

            case NONE:
                //do nothing
                break;
        }
    }

    protected void onFire1Down() {
    }

    protected void onFire1Hold() {
    }

    protected void onFire1Up() {
    }

    protected void onFire2Down() {
    }

    protected void onFire2Hold() {
    }

    protected void onFire2Up() {
    }

    public void refresh() {
    }

    public boolean getDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public static void arrange(int width, int centerY, int rowHeight, List<Widget> widgetList) {
        Widget w;
        int len = widgetList.size();
        int col1 = Emath.floor(len / 3.0) + ((len % 3) == 2 ? 1 : 0);
        int col2 = Emath.floor(len / 3.0) + ((len % 3) > 0 ? 1 : 0);
        for (int i = 0; i < len; i++) {
            w = widgetList.get(i);
            if (len <= 8) {
                w.x = (width - w.w) / 2;
                w.y = centerY + rowHeight * (i - len / 2);
            } else {
                if (i < col1) {
                    w.x = (width - 3 * w.w) / 2 - 20;
                    w.y = centerY + (int) (rowHeight * (i - col2 / 2.0));
                } else if (i < col1 + col2) {
                    w.x = (width - w.w) / 2;
                    w.y = centerY + (int) (rowHeight * ((i - col1) - col2 / 2.0));
                } else {
                    w.x = (width + w.w) / 2 + 20;
                    w.y = centerY + (int) (rowHeight * ((i - col1 - col2) - col2 / 2.0));
                }
            }
        }
    }

    public static int getRows(List<Widget> widgetList) {
        int len = widgetList.size();
        return len <= 8 ? len : Emath.floor(len / 3.0) + 1;
    }

    public boolean contains(float x0, float y0) {
        return (x0 >= x) && (x0 <= x + w) && (y0 >= y) && (y0 <= y + h);
    }

    public static class CompareByText implements Comparator<Widget> {

        @Override
        public int compare(Widget o1, Widget o2) {
            return o1.text.compareTo(o2.text);
        }
    }
}
