package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;

public class Label extends Widget {

    @Override
    public void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer) {
        if (!visible) {
            return;
        }

        batch.setColor(0xFFFFFF, alpha);
        batch.begin();

        drawText(batch);

        batch.end();
        batch.setColor(0xFFFFFF, 1f);
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
