package com.ysoccer.android.ysdemo.gui;

import com.ysoccer.android.framework.gl.Color;
import com.ysoccer.android.framework.impl.GLGraphics;

import javax.microedition.khronos.opengles.GL10;

public class Button extends Widget {

    private static final double sweepSpeed = 0.4;
    private static final float alpha = 0.9f;

    public Button() {
        isActive = true;
    }

    @Override
    public void render(GLGraphics glGraphics) {
        if (!isVisible) {
            return;
        }

        GL10 gl = glGraphics.getGL();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        glGraphics.setAlpha(alpha);

        // body (0x000000 = invisible)
        if (body != 0x000000) {
            glGraphics.setColor(body);
            glGraphics.drawRect(x + 2, y + 2, w - 4, h - 4);
        }

        // border ($000000 = invisible)
        if (lightBorder != 0x000000) {
            drawBorder(glGraphics, x, y, w, h, lightBorder, darkBorder);
            if (isSelected) {
                drawAnimatedBorder(glGraphics);
            }
        }

        glGraphics.setColor(0xFFFFFF);

        drawImage(glGraphics);

        glGraphics.setAlpha(1.0f);

        if (isBlinking && ((Math.abs(System.currentTimeMillis()) % 800) < 400)) {
            return;
        }

        switch (charset) {
            case 14:
                draw14u(glGraphics);
                break;
            case 10:
                draw10u(glGraphics);
        }
    }

    private void drawImage(GLGraphics glGraphics) {
        if (texture != null) {
            if (addShadow) {
                glGraphics.setColor(0x242424);
                glGraphics.drawSubTextureRect(texture, x + 4 + imageX, y + 4
                        + imageY, imageW, imageH, frameX * frameW, frameY
                        * frameH, frameW, frameH);
                glGraphics.setColor(0xFFFFFF);
            }
            glGraphics.drawSubTextureRect(texture, x + 2 + imageX, y + 2
                            + imageY, imageW, imageH, frameX * frameW, frameY * frameH,
                    frameW, frameH);
        }
    }

    private void draw10u(GLGraphics glGraphics) {

        glGraphics.setColor(0xFFFFFF);

        int tx = x;
        switch (align) {
            case -1: // right
                tx = tx + w - 10;
                break;
            case 0: // center
                tx = tx + w / 2;
                break;
            case 1: // left
                tx = tx + 10;
                break;
        }
        // TODO
        // text10u(getText(), tx + textOffsetX, y + Math.ceil(0.5f * (h - 18)),
        // imgUcode, align);

    }

    // draw a button with characters of height 14
    private void draw14u(GLGraphics glGraphics) {

        glGraphics.setColor(0xFFFFFF);

        int tx = x;
        switch (align) {
            case -1: // right
                tx = tx + w - 14;
                break;
            case 0: // center
                tx = tx + w / 2;
                break;
            case 1: // left
                tx = tx + 14;
        }

        glGraphics.text14u(getText(), tx + textOffsetX,
                y + (int) Math.ceil(0.5f * (h - 22)), imgUcode, align);

    }

    private String getText() {
        return text;
    }

    private void drawAnimatedBorder(GLGraphics glGraphics) {
        if (entryMode) {
            return;
        }

        // graylevel
        int gl = (int) Math.abs(((sweepSpeed * Math.abs(System
                .currentTimeMillis())) % 200) - 100) + 100;

        // border color 1
        int bdr1 = Color.rgb(gl, gl, gl);

        // border color 2
        gl = gl - 50;
        int bdr2 = Color.rgb(gl, gl, gl);

        drawBorder(glGraphics, x, y, w, h, bdr1, bdr2);

    }

    private void drawBorder(GLGraphics glGraphics, int bx, int by, int bw,
                            int bh, int topLeftColor, int bottomRightColor) {

        // top left border
        glGraphics.setColor(topLeftColor);
        glGraphics.drawRect(bx, by, bx + bw, by, bx + bw - 1, by + 2, bx + 1,
                by + 2);
        glGraphics.drawRect(bx, by + 1, bx, by + bh, bx + 2, by + bh - 1,
                bx + 2, by + 2);

        // bottom right border
        glGraphics.setColor(bottomRightColor);
        glGraphics.drawRect(bx + bw - 2, by + 2, bx + bw - 2, by + bh - 1, bx
                + bw, by + bh, bx + bw, by + 1);
        glGraphics.drawRect(bx + 2, by + bh - 2, bx + bw - 2, by + bh - 2, bx
                + bw - 1, by + bh, bx + 1, by + bh);
    }

}
