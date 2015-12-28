package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.GlGraphics;

public class Button {

    // geometry
    int x;
    int y;
    int w;
    int h;

    // colors
    int body;
    int lightBorder;
    int darkBorder;

    public void render(GlGraphics glGraphics) {
        ShapeRenderer shapeRenderer = glGraphics.shapeRenderer;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // body (0x000000 = invisible)
        if (body != 0x000000) {
            shapeRenderer.setColor(new Color(body));
            shapeRenderer.rect(x + 2, y + 2, w - 4, h - 4);
        }

        // border ($000000 = invisible)
        if (lightBorder != 0x000000) {
            drawBorder(shapeRenderer, x, y, w, h, lightBorder, darkBorder);
        }

        shapeRenderer.end();
    }

    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setColors(int body, int lightBorder, int darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    private void drawBorder(ShapeRenderer shapeRenderer, int bx, int by, int bw,
                            int bh, int topLeftColor, int bottomRightColor) {

        // top left border
        shapeRenderer.setColor(new Color(topLeftColor));
        shapeRenderer.triangle(bx, by, bx + bw, by, bx + bw - 1, by + 2);
        shapeRenderer.triangle(bx + bw - 1, by + 2, bx + 1, by + 2, bx, by);
        shapeRenderer.triangle(bx, by + 1, bx, by + bh, bx + 2, by + bh - 1);
        shapeRenderer.triangle(bx + 2, by + bh - 1, bx + 2, by + 2, bx, by + 1);

        // bottom right border
        shapeRenderer.setColor(new Color(bottomRightColor));
        shapeRenderer.triangle(bx + bw - 2, by + 2, bx + bw - 2, by + bh - 1, bx + bw, by + bh);
        shapeRenderer.triangle(bx + bw, by + bh, bx + bw, by + 1, bx + bw - 2, by + 2);
        shapeRenderer.triangle(bx + 2, by + bh - 2, bx + bw - 2, by + bh - 2, bx + bw - 1, by + bh);
        shapeRenderer.triangle(bx + bw - 1, by + bh, bx + 1, by + bh, bx + 2, by + bh - 2);
    }
}
