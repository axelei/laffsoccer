package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.GlColor;
import com.ygames.ysoccer.framework.GlGraphics;
import com.ygames.ysoccer.framework.GlShapeRenderer;

public class Button extends Widget {

    private static final double sweepSpeed = 0.4;
    private static final float alpha = 0.9f;

    @Override
    public void render(GlGraphics glGraphics) {
        GlShapeRenderer shapeRenderer = glGraphics.shapeRenderer;
        shapeRenderer.begin(GlShapeRenderer.ShapeType.Filled);

        // body (0x000000 = invisible)
        if (body != 0x000000) {
            shapeRenderer.setColor(body, alpha);
            shapeRenderer.rect(x + 2, y + 2, w - 4, h - 4);
        }

        // border ($000000 = invisible)
        if (lightBorder != 0x000000) {
            drawBorder(shapeRenderer, x, y, w, h, lightBorder, darkBorder);
            if (isSelected && !entryMode) {
                drawAnimatedBorder(glGraphics);
            }
        }

        shapeRenderer.end();

        drawText(glGraphics);
    }

    private void drawBorder(GlShapeRenderer shapeRenderer, int bx, int by, int bw,
                            int bh, int topLeftColor, int bottomRightColor) {

        // top left border
        shapeRenderer.setColor(topLeftColor, alpha);
        shapeRenderer.triangle(bx, by, bx + bw, by, bx + bw - 1, by + 2);
        shapeRenderer.triangle(bx + bw - 1, by + 2, bx + 1, by + 2, bx, by);
        shapeRenderer.triangle(bx, by + 1, bx, by + bh, bx + 2, by + bh - 1);
        shapeRenderer.triangle(bx + 2, by + bh - 1, bx + 2, by + 2, bx, by + 1);

        // bottom right border
        shapeRenderer.setColor(bottomRightColor, alpha);
        shapeRenderer.triangle(bx + bw - 2, by + 2, bx + bw - 2, by + bh - 1, bx + bw, by + bh);
        shapeRenderer.triangle(bx + bw, by + bh, bx + bw, by + 1, bx + bw - 2, by + 2);
        shapeRenderer.triangle(bx + 2, by + bh - 2, bx + bw - 2, by + bh - 2, bx + bw - 1, by + bh);
        shapeRenderer.triangle(bx + bw - 1, by + bh, bx + 1, by + bh, bx + 2, by + bh - 2);
    }

    private void drawAnimatedBorder(GlGraphics glGraphics) {
        // gray level
        int gl = (int) Math.abs(((sweepSpeed * Math.abs(System.currentTimeMillis())) % 200) - 100) + 100;

        // border color 1
        int bdr1 = GlColor.rgb(gl, gl, gl);

        // border color 2
        gl = gl - 50;
        int bdr2 = GlColor.rgb(gl, gl, gl);

        drawBorder(glGraphics.shapeRenderer, x, y, w, h, bdr1, bdr2);
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
        font.draw(glGraphics.batch, text, tx + textOffsetX, y + (int) Math.ceil(0.5f * (h - 8 - font.size)), align);
    }
}
