package com.ysoccer.android.framework.impl;

import android.opengl.GLSurfaceView;

import com.ysoccer.android.framework.gl.Color;
import com.ysoccer.android.framework.gl.LineDrawer;
import com.ysoccer.android.framework.gl.RectDrawer;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.gl.TextureDrawer;
import com.ysoccer.android.ysdemo.Assets;

import javax.microedition.khronos.opengles.GL11;

public class GLGraphics {
    GLSurfaceView glView;
    GL11 gl;
    TextureDrawer textureDrawer;
    LineDrawer lineDrawer;
    RectDrawer rectDrawer;
    public int light;
    float[] params;

    GLGraphics(GLSurfaceView glView) {
        this.glView = glView;
        textureDrawer = new TextureDrawer();
        lineDrawer = new LineDrawer();
        rectDrawer = new RectDrawer();
        light = 0;
        params = new float[4];
    }

    public GL11 getGL() {
        return gl;
    }

    void setGL(GL11 gl) {
        this.gl = gl;
    }

    public int getWidth() {
        return glView.getWidth();
    }

    public int getHeight() {
        return glView.getHeight();
    }

    public void setColor(int color) {
        gl.glGetFloatv(GL11.GL_CURRENT_COLOR, params, 0);
        gl.glColor4f(Color.red(color) * light / 65025.0f, Color.green(color)
                        * light / 65025.0f, Color.blue(color) * light / 65025.0f,
                params[3]);
    }

    public void setColor(int color, float a) {
        gl.glColor4x(((int) (Color.red(color) * light / 255.0f)) << 8,
                ((int) (Color.green(color) * light / 255.0f)) << 8,
                ((int) (Color.blue(color) * light / 255.0f)) << 8,
                ((int) (a * 255)) << 8);
    }

    public void setColor(int r, int g, int b) {
        setColor(r, g, b, 255);
    }

    public void setColor(int r, int g, int b, int a) {
        gl.glColor4x(((int) (r * light / 255.0f)) << 8,
                ((int) (g * light / 255.0f)) << 8,
                ((int) (b * light / 255.0f)) << 8, a << 8);
    }

    public void setAlpha(float alpha) {
        gl.glGetFloatv(GL11.GL_CURRENT_COLOR, params, 0);
        gl.glColor4f(params[0], params[1], params[2], alpha);
    }

    public void drawRect(float x, float y, float w, float h) {
        rectDrawer.drawRect(gl, x, y, w, h);
    }

    public void drawRect(float x1, float y1, float x2, float y2, float x3,
                         float y3, float x4, float y4) {
        rectDrawer.drawRect(gl, x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void drawTexture(Texture texture, float x, float y) {
        textureDrawer.drawTexture(texture, x, y);
    }

    public void drawTextureRect(Texture texture, float x, float y, float w, float h) {
        textureDrawer.drawTextureRect(texture, x, y, w, h);
    }

    public void drawTextureRect(Texture texture, float x, float y, float fx, float fy,
                                float fw, float fh) {
        textureDrawer.drawTextureRect(texture, x, y, fx, fy, fw, fh);
    }

    public void drawSubTextureRect(Texture texture, float x, float y, float w, float h,
                                   float fx, float fy, float fw, float fh) {
        textureDrawer.drawSubTextureRect(texture, x, y, w, h, fx, fy, fw, fh);
    }

    // TEXT14U :draw a text with a charset of size 14
    // caption :text
    // x, y :position
    // img_ucode :charset image
    // align :-1=right, 0=center, 1=left
    public void text14u(String caption, int x, int y, Texture imgUcode,
                        int align) {

        int w = ucodeWidth(caption, 14);

        // x position
        switch (align) {
            case -1:
                x = x - w;
                break;
            case 0:
                x = x - w / 2;
                break;
            case 1:
                // do nothing
                break;
        }

        for (int i = 0; i < caption.length(); i++) {

            int c = caption.charAt(i);

            // drawSubTextureRect(imgUcode, x, y, 16, 22, 16*(c & 0x3F), 23*(c
            // >> 6), 16, 22);
            drawTextureRect(imgUcode, x, y, 16 * (c & 0x3F), 23 * (c >> 6), 16,
                    22);

            // TODO
            // rimuovere dipendenza framework-Assets
            x = x + Assets.ucode14w[c];
        }

    }

    public int ucodeWidth(String text, int charset) {
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            // carriage return/line feed
            // TODO
            // If (Mid(text, i+1, 2) = "~r~n") Then Exit
            switch (charset) {
                case 10:
                    w = w + Assets.ucode10w[c];
                    break;
                case 14:
                    w = w + Assets.ucode14w[c];
                    break;
            }
        }
        return w;
    }

    public int ucodeHeight(int charset) {
        int w = 0;
        switch (charset) {
            case 10:
                w = 16;
                break;
            case 14:
                w = 26;
                break;
        }
        return w;
    }

    public void fadeRect(int x0, int y0, int x1, int y1, float alpha, int color) {
        setAlpha(alpha);
        setColor(color);
        drawRect(x0, y0, x1 - x0, y1 - y0);
        setAlpha(1.0f);
    }

    public void drawLine(float x, float y, float w, float h) {
        lineDrawer.drawLine(gl, x, y, w, h);
    }

    private void plot(float x, float y) {

    }

    public void drawFrame(int x, int y, int w, int h) {

        int r = x + w;
        int b = y + h;

        // top
        drawRect(x + 6, y, r - 4, y, r, y + 2, x + 2, y + 2);

        // top-left
        drawRect(x + 2, y + 2, 4, 1);
        drawRect(x + 2, y + 3, 1, 3);
        drawRect(x + 3, y + 3, 1, 1);

        // top-right
        drawRect(r - 4, y + 2, 4, 1);
        drawRect(r - 1, y + 3, 1, 3);
        drawRect(r - 2, y + 3, 1, 1);

        // left
        drawRect(x, y + 6, x + 2, y + 2, x + 2, b, x, b - 4);

        // right
        drawRect(r, y + 2, r + 2, y + 6, r + 2, b - 4, r, b);

        // bottom-left
        drawRect(x + 2, b - 4, 1, 3);
        drawRect(x + 2, b - 1, 4, 1);
        drawRect(x + 3, b - 2, 1, 1);

        // bottom-right
        drawRect(r - 1, b - 4, 1, 3);
        drawRect(r - 4, b - 1, 4, 1);
        drawRect(r - 2, b - 2, 1, 1);

        // bottom
        drawRect(x + 2, b, r, b, r - 4, b + 2, x + 6, b + 2);

    }

}
