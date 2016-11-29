package com.ygames.ysoccer.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.GlColor;

public class Button extends Widget {

    private static final double sweepSpeed = 0.4;
    private static final float alpha = 0.92f;

    public Button() {
        active = true;
    }

    @Override
    public void render(GLGraphics glGraphics) {
        if (!visible) {
            return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        ShapeRenderer shapeRenderer = glGraphics.shapeRenderer;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // body (0x000000 = invisible)
        if (color.body != 0x000000) {
            GLGraphics.setShapeRendererColor(shapeRenderer, color.body, alpha);
            shapeRenderer.rect(x + 2, y + 2, w - 4, h - 4);
        }

        // border ($000000 = invisible)
        if (color.lightBorder != 0x000000) {
            drawBorder(shapeRenderer, x, y, w, h, color.lightBorder, color.darkBorder);
        }

        if (selected) {
            if (entryMode) {
                drawBorder(glGraphics.shapeRenderer, x, y, w, h, 0xEEEE8A, 0xBFBF6F);
            } else {
                drawAnimatedBorder(glGraphics);
            }
        }

        shapeRenderer.end();

        glGraphics.batch.setColor(1, 1, 1, alpha);

        if (textureRegion != null) {
            drawImage(glGraphics.batch);
        }

        if (font != null) {
            glGraphics.batch.begin();
            drawText(glGraphics.batch);
            glGraphics.batch.end();
        }

        glGraphics.batch.setColor(1, 1, 1, 1);
    }

    private void drawImage(SpriteBatch batch) {
        if (addShadow) {
            batch.setColor(0.15f, 0.15f, 0.15f, alpha);
            batch.begin();
            batch.draw(textureRegion, x + 4 + imageX, y + 4 + imageY, 0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), imageScaleX, imageScaleY, 0);
            batch.end();
            batch.setColor(1, 1, 1, alpha);
        }
        batch.begin();
        batch.draw(textureRegion, x + 2 + imageX, y + 2 + imageY, 0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), imageScaleX, imageScaleY, 0);
        batch.end();
    }

    private void drawBorder(ShapeRenderer shapeRenderer, int bx, int by, int bw,
                            int bh, int topLeftColor, int bottomRightColor) {

        // top left border
        GLGraphics.setShapeRendererColor(shapeRenderer, topLeftColor, alpha);
        shapeRenderer.triangle(bx, by, bx + bw, by, bx + bw - 1, by + 2);
        shapeRenderer.triangle(bx + bw - 1, by + 2, bx + 1, by + 2, bx, by);
        shapeRenderer.triangle(bx, by + 1, bx, by + bh, bx + 2, by + bh - 1);
        shapeRenderer.triangle(bx + 2, by + bh - 1, bx + 2, by + 2, bx, by + 1);

        // bottom right border
        GLGraphics.setShapeRendererColor(shapeRenderer, bottomRightColor, alpha);
        shapeRenderer.triangle(bx + bw - 2, by + 2, bx + bw - 2, by + bh - 1, bx + bw, by + bh);
        shapeRenderer.triangle(bx + bw, by + bh, bx + bw, by + 1, bx + bw - 2, by + 2);
        shapeRenderer.triangle(bx + 2, by + bh - 2, bx + bw - 2, by + bh - 2, bx + bw - 1, by + bh);
        shapeRenderer.triangle(bx + bw - 1, by + bh, bx + 1, by + bh, bx + 2, by + bh - 2);
    }

    private void drawAnimatedBorder(GLGraphics glGraphics) {
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
                break;
        }
        switch (font.size) {
            case 14:
                font.draw(batch, getText(), tx + textOffsetX, y + (int) Math.ceil(0.5f * (h - 22)), align);
                break;
            case 10:
                font.draw(batch, getText(), tx + textOffsetX, y + (int) Math.ceil(0.5f * (h - 17)), align);
                break;
        }
    }

    public String getText() {
        return text;
    }

    protected void autoWidth() {
        w = font.textWidth(text) + 3 * font.size;
    }
}
