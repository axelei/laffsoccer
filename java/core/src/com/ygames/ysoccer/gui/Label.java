package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.GlGraphics;

public class Label extends Widget {

    @Override
    public void render(GlGraphics glGraphics) {
        if (!isVisible) {
            return;
        }
        drawText(glGraphics);
    }

    private void drawText(GlGraphics glGraphics) {
        int tx = x;
        switch (align) {
            case RIGHT:
                tx += w - font.size;
                break;
            case CENTER:
                tx += w / 2;
                break;
            case LEFT:
                tx += font.size;
        }
        font.draw(glGraphics.batch, getText(), tx + textOffsetX, y + (int) Math.ceil(0.5f * (h - 8 - font.size)), align);
    }

    public String getText() {
        return text;
    }
}
