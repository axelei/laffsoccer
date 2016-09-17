package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sun.misc.IOUtils;

public class Font {

    RgbPair rgbPair;

    public enum Align {
        RIGHT, CENTER, LEFT
    }

    public int size;
    public Texture texture;
    public int[] widths = new int[1024];
    public TextureRegion[] regions = new TextureRegion[1024];

    public Font(int size) {
        this.size = size;
    }

    public Font(int size, RgbPair rgbPair) {
        this.size = size;
        this.rgbPair = rgbPair;
    }

    public void load() {
        if (rgbPair == null) {
            texture = new Texture("images/font_" + size + ".png");
        } else {
            InputStream in = null;
            try {
                in = Gdx.files.internal("images/font_" + size + ".png").read();

                List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
                rgbPairs.add(rgbPair);
                byte[] bytes = IOUtils.readFully(PngEditor.editPalette(in, rgbPairs), -1, true);

                Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
                texture = new Texture(pixmap);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't load image", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
            }
        }
        loadFontWidths(widths, "font_" + size + ".txt");
        for (int i = 0; i < 1024; i++) {
            switch (size) {
                case 14:
                    regions[i] = new TextureRegion(texture, 16 * (i & 0x3F), 23 * (i >> 6), 16, 22);
                    break;
                case 10:
                    regions[i] = new TextureRegion(texture, 13 * (i & 0x3F), 17 * (i >> 6), 12, 16);
                    break;
            }
            regions[i].flip(false, true);
        }
    }

    protected static void loadFontWidths(int[] fontWidths, String filePath) {
        InputStream in = null;

        try {
            in = Gdx.files.internal(filePath).read();
            setFontWidths(fontWidths, in);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading font widths " + e.getMessage());
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }
    }

    private static void setFontWidths(int[] fontWidths, InputStream in) throws IOException {
        byte[] buffer = new byte[1];
        int s = 0;

        // row
        for (int r = 0; r < 16; r++) {

            // block
            for (int b = 0; b < 8; b++) {

                // column
                for (int c = 0; c < 8; c++) {

                    in.read(buffer);
                    s = buffer[0] & 0xFF;

                    // 0 - 9
                    if (s >= 48 && s <= 57) {
                        s = s - 48;
                    }
                    // A - N
                    else if (s >= 65 && s <= 78) {
                        s = s - 55;
                    } else {
                        s = 30;
                    }

                    fontWidths[r * 64 + 8 * b + c] = s;
                }

                // skip ':' or CR
                in.read(buffer);
                s = buffer[0] & 0xFF;

            }

            // if s = CR then skip LF
            if (s == 0x0D) {
                in.read(buffer);
            }

        }
    }

    public void draw(SpriteBatch batch, String text, int x, int y, Align align) {
        int w = textWidth(text);

        // x position
        switch (align) {
            case RIGHT:
                x = x - w;
                break;
            case CENTER:
                x = x - w / 2;
                break;
            case LEFT:
                // do nothing
                break;
        }

        for (int i = 0; i < text.length(); i++) {

            int c = text.charAt(i);

            if (c == 8364) c = 128; // €

            switch (size) {
                case 14:
                    batch.draw(regions[c], x, y, 16, 22);
                    break;
                case 10:
                    batch.draw(regions[c], x, y, 12, 16);
                    break;
            }

            x = x + widths[c];
        }
    }

    public int textWidth(String text) {
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);

            if (c == 8364) c = 128; // €

            // carriage return/line feed
            if (c == 13 && (i + 1 < text.length()) && text.charAt(i + 1) == 10) {
                break;
            }

            w = w + widths[c];
        }
        return w;
    }

}
