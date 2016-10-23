package com.ysoccer.android.framework.gl;

public class Font {
    public final Texture texture;
    public final int glyphWidth;
    public final int glyphHeight;
    public final Frame[] glyphs = new Frame[1024];

    public Font(Texture texture,
                int offsetX, int offsetY,
                int glyphsPerRow, int glyphWidth, int glyphHeight) {
        this.texture = texture;
        this.glyphWidth = glyphWidth;
        this.glyphHeight = glyphHeight;
        int x = offsetX;
        int y = offsetY;
        for (int i = 0; i < glyphs.length; i++) {
            glyphs[i] = new Frame(texture, x, y, glyphWidth, glyphHeight);
            x += glyphWidth;
            if (x == offsetX + glyphsPerRow * glyphWidth) {
                x = offsetX;
                y += glyphHeight;
            }
        }
    }

    public void drawText(SpriteBatcher batcher, String text, float x, float y) {
        drawText(batcher, text, x, y, 0);
    }

    public void drawText(SpriteBatcher batcher, String text, float x, float y, int align) {
        int len = text.length();
        x += glyphWidth / 2;
        y += glyphHeight / 2;
        switch (align) {
            case -1:
                x -= len * glyphWidth;
                break;
            case 0:
                x -= len * glyphWidth / 2;
                break;
            default:
                ;
        }

        for (int i = 0; i < len; i++) {
            int c = text.charAt(i);
            if (c >= 0 && c < glyphs.length) {
                Frame glyph = glyphs[c];
                batcher.drawSprite(x, y, glyphWidth, glyphHeight, glyph);
            }
            x += glyphWidth;
        }
    }
}
