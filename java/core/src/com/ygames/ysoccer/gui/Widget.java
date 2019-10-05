package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLColor;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;
import com.ygames.ysoccer.framework.EMath;

import java.util.Comparator;
import java.util.List;

public abstract class Widget {

    protected static final float alpha = 0.92f;

    // geometry
    public int x;
    public int y;
    public int w;
    public int h;

    public TextureRegion textureRegion;
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
    private boolean dirty;

    public enum Event {
        FIRE1_DOWN, FIRE1_HOLD, FIRE1_UP, FIRE2_DOWN, FIRE2_HOLD, FIRE2_UP
    }

    public Widget() {
        imageScaleX = 1.0f;
        imageScaleY = 1.0f;
        color = new WidgetColor();
        align = Font.Align.CENTER;
        visible = true;
        dirty = true;
    }

    public abstract void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer);

    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void setImageScale(float scaleX, float scaleY) {
        imageScaleX = scaleX;
        imageScaleY = scaleY;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setColors(Integer body, Integer lightBorder, Integer darkBorder) {
        this.color.set(body, lightBorder, darkBorder);
    }

    public void setColors(WidgetColor color) {
        if (color == null) {
            this.color.set(null, null, null);
        } else {
            this.color.set(color.body, color.lightBorder, color.darkBorder);
        }
    }

    public void setColor(int color) {
        setColors(color, GLColor.brighter(color), GLColor.darker(color, 0.7D));
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

    public void setTextOffsetX(int textOffsetX) {
        this.textOffsetX = textOffsetX;
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

    public static void arrange(int width, int centerY, int rowHeight, int spacing, List<Widget> widgetList) {
        Widget w;
        int maxRows = 8;
        int len = widgetList.size();
        if (len <= maxRows) {
            for (int i = 0; i < len; i++) {
                w = widgetList.get(i);
                w.x = (width - w.w) / 2;
                w.y = centerY - rowHeight * len / 2 + rowHeight * i;
            }
        } else if (len <= 2 * maxRows) {
            int col1 = EMath.floor((len + 1) / 2.0);
            for (int i = 0; i < len; i++) {
                w = widgetList.get(i);
                if (i < col1) {
                    w.x = (width - 2 * w.w - spacing) / 2;
                    w.y = centerY + (int) (rowHeight * (i - col1 / 2.0));
                } else {
                    w.x = (width + spacing) / 2;
                    w.y = centerY + (int) (rowHeight * ((i - col1) - col1 / 2.0));
                }
            }
        } else {
            int col1 = EMath.floor(len / 3.0) + ((len % 3) == 2 ? 1 : 0);
            int col2 = EMath.floor(len / 3.0) + ((len % 3) > 0 ? 1 : 0);
            for (int i = 0; i < len; i++) {
                w = widgetList.get(i);
                if (i < col1) {
                    w.x = (width - 3 * w.w) / 2 - spacing;
                    w.y = centerY + (int) (rowHeight * (i - col2 / 2.0));
                } else if (i < col1 + col2) {
                    w.x = (width - w.w) / 2;
                    w.y = centerY + (int) (rowHeight * ((i - col1) - col2 / 2.0));
                } else {
                    w.x = (width + w.w) / 2 + spacing;
                    w.y = centerY + (int) (rowHeight * ((i - col1 - col2) - col2 / 2.0));
                }
            }
        }
    }


    public static int getRows(List<Widget> widgetList) {
        int len = widgetList.size();
        return len <= 8 ? len : EMath.floor(len / 3.0) + 1;
    }

    public boolean contains(float x0, float y0) {
        return (x0 >= x) && (x0 <= x + w) && (y0 >= y) && (y0 <= y + h);
    }

    public static Comparator<Widget> widgetComparatorByText = new CompareByText();

    private static class CompareByText implements Comparator<Widget> {

        @Override
        public int compare(Widget o1, Widget o2) {
            return o1.text.compareTo(o2.text);
        }
    }
}
