package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.GLGraphics;

public class Label extends Widget {

    @Override
    public void render(GLGraphics glGraphics) {
        if (!visible) {
            return;
        }

        glGraphics.batch.begin();

        drawText(glGraphics.batch);

        glGraphics.batch.end();
    }

    private void drawText(SpriteBatch batch) {
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
                break;
        }
        font.draw(batch, getText(), tx + textOffsetX, y + (int) Math.ceil(0.5f * (h - 8 - font.size)), align);
    }

    public String getText() {
        return text;
    }
}
