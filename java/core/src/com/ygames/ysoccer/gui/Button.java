package com.ygames.ysoccer.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.GLColor;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;
import com.ygames.ysoccer.math.Emath;

public class Button extends Widget {

    private static final double sweepSpeed = 0.4;

    private int imageX;
    private int imageY;

    public Button() {
        active = true;
    }

    @Override
    public void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer) {
        if (!visible) {
            return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // body (null = invisible)
        if (color.body != null) {
            shapeRenderer.setColor(color.body, alpha);
            shapeRenderer.rect(x + 2, y + 2, w - 4, h - 4);
        }

        // border (null = invisible)
        if (color.lightBorder != null) {
            drawBorder(shapeRenderer, x, y, w, h, color.lightBorder, color.darkBorder);
        }

        if (selected && active) {
            if (entryMode) {
                drawBorder(shapeRenderer, x, y, w, h, 0xEEEE8A, 0xBFBF6F);
            } else {
                drawAnimatedBorder(shapeRenderer);
            }
        }

        shapeRenderer.end();

        batch.setColor(0xFFFFFF, alpha);

        drawImage(batch);

        if (font != null) {
            batch.begin();
            drawText(batch);
            batch.end();
        }

        batch.setColor(0xFFFFFF, 1f);
    }

    protected void setImagePosition(int imageX, int imageY) {
        this.imageX = imageX;
        this.imageY = imageY;
    }

    protected void drawImage(GLSpriteBatch batch) {
        if (textureRegion == null) {
            return;
        }

        if (addShadow) {
            batch.setColor(0x242424, alpha);
            batch.begin();
            batch.draw(textureRegion, x + 4 + imageX, y + 4 + imageY, 0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), imageScaleX, imageScaleY, 0);
            batch.end();
            batch.setColor(0xFFFFFF, alpha);
        }
        batch.begin();
        batch.draw(textureRegion, x + 2 + imageX, y + 2 + imageY, 0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), imageScaleX, imageScaleY, 0);
        batch.end();
    }

    protected void drawBorder(GLShapeRenderer shapeRenderer, int bx, int by, int bw,
                              int bh, int topLeftColor, int bottomRightColor) {

        // top left border
        shapeRenderer.setColor(topLeftColor, alpha);
        shapeRenderer.triangle(bx + 1, by, bx + bw - 1, by, bx + bw - 1, by + 2);
        shapeRenderer.triangle(bx + bw - 1, by + 2, bx + 1, by + 2, bx + 1, by);
        shapeRenderer.triangle(bx, by + 1, bx, by + bh - 1, bx + 2, by + bh - 1);
        shapeRenderer.triangle(bx + 2, by + bh - 1, bx + 2, by + 2, bx, by + 1);

        // bottom right border
        shapeRenderer.setColor(bottomRightColor, alpha);
        shapeRenderer.triangle(bx + bw - 2, by + 2, bx + bw - 2, by + bh - 1, bx + bw, by + bh - 1);
        shapeRenderer.triangle(bx + bw, by + bh, bx + bw, by + 1, bx + bw - 2, by + 2);
        shapeRenderer.triangle(bx + 2, by + bh - 2, bx + bw - 2, by + bh - 2, bx + bw - 1, by + bh);
        shapeRenderer.triangle(bx + bw - 1, by + bh, bx + 1, by + bh, bx + 2, by + bh - 2);
    }

    protected void drawAnimatedBorder(GLShapeRenderer shapeRenderer) {
        // gray level
        int gl = (int) Math.abs(((sweepSpeed * Math.abs(System.currentTimeMillis())) % 200) - 100) + 100;

        // border color 1
        int bdr1 = GLColor.rgb(gl, gl, gl);

        // border color 2
        gl = gl - 50;
        int bdr2 = GLColor.rgb(gl, gl, gl);

        drawBorder(shapeRenderer, x, y, w, h, bdr1, bdr2);
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
                font.draw(batch, getText(), tx + textOffsetX, y + Emath.ceil(0.5f * (h - 22)), align);
                break;

            case 10:
                font.draw(batch, getText(), tx + textOffsetX, y + Emath.ceil(0.5f * (h - 16)), align);
                break;

            case 6:
                font.draw(batch, getText(), tx + textOffsetX, y + Emath.ceil(0.5f * (h - 12)), align);
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
