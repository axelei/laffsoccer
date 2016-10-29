package com.ygames.ysoccer.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.GlColor;
import com.ygames.ysoccer.framework.GlGraphics;
import com.ygames.ysoccer.framework.GlShapeRenderer;

public class Button extends Widget {

    private static final double sweepSpeed = 0.4;
    private static final float alpha = 0.9f;

    public Button() {
        isActive = true;
    }

    @Override
    public void render(GlGraphics glGraphics) {
        if (!isVisible) {
            return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        GlShapeRenderer shapeRenderer = glGraphics.shapeRenderer;
        shapeRenderer.begin(GlShapeRenderer.ShapeType.Filled);

        // body (0x000000 = invisible)
        if (color.body != 0x000000) {
            shapeRenderer.setColor(color.body, alpha);
            shapeRenderer.rect(x + 2, y + 2, w - 4, h - 4);
        }

        // border ($000000 = invisible)
        if (color.lightBorder != 0x000000) {
            drawBorder(shapeRenderer, x, y, w, h, color.lightBorder, color.darkBorder);
        }

        if (isSelected && !entryMode) {
            drawAnimatedBorder(glGraphics);
        }

        shapeRenderer.end();

        glGraphics.batch.setColor(1, 1, 1, alpha);
        glGraphics.batch.begin();

        if (image != null) {
            drawImage(glGraphics.batch);
        }

        if (font != null) {
            drawText(glGraphics.batch);
        }

        glGraphics.batch.end();
        glGraphics.batch.setColor(1, 1, 1, 1);
    }

    private void drawImage(SpriteBatch batch) {
        batch.draw(image, x + 2 + imageX, y + 2 + imageY, 0, 0, image.getRegionWidth(), image.getRegionHeight(), imageScaleX, imageScaleY, 0);
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
        }
        font.draw(batch, getText(), tx + textOffsetX, y + (int) Math.ceil(0.5f * (h - 8 - font.size)), align);
    }

    public String getText() {
        return text;
    }
}
