package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Font {

    private RgbPair rgbPair;

    public enum Align {
        RIGHT, CENTER, LEFT
    }

    public int size;

    private int columnWidth;
    private int rowHeight;

    private int regionWidth;
    public int regionHeight;

    public Texture texture;

    private int[] widths = new int[1024];

    private TextureRegion[] regions = new TextureRegion[1024];

    public Font(int size, int columnWidth, int rowHeight, int regionWidth, int regionHeight) {
        this.size = size;
        this.columnWidth = columnWidth;
        this.rowHeight = rowHeight;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
    }

    public Font(int size, int columnWidth, int rowHeight, int regionWidth, int regionHeight, RgbPair rgbPair) {
        this(size, columnWidth, rowHeight, regionWidth, regionHeight);
        this.rgbPair = rgbPair;
    }

    public void load() {
        if (rgbPair == null) {
            texture = new Texture("images/font_" + size + ".png");
        } else {
            InputStream in = null;
            List<RgbPair> rgbPairs = new ArrayList<RgbPair>();
            rgbPairs.add(rgbPair);
            try {
                in = Gdx.files.internal("images/font_" + size + ".png").read();

                ByteArrayInputStream inputStream = PngEditor.editPalette(in, rgbPairs);

                byte[] bytes = FileUtils.inputStreamToBytes(inputStream);

                Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
                texture = new Texture(pixmap);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't load texture", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        Gdx.app.error(getClass().toString(), e.toString());
                    }
            }
        }

        loadFontWidths(widths, "configs/font_" + size + ".txt");

        for (int i = 0; i < 1024; i++) {
            regions[i] = new TextureRegion(texture, (i & 0x3F) * columnWidth, (i >> 6) * rowHeight, regionWidth, regionHeight);
            regions[i].flip(false, true);
        }
    }

    private static void loadFontWidths(int[] fontWidths, String filePath) {
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
                    Gdx.app.error("loadFontWidths", e.toString());
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

            if (c >= regions.length) {
                c = 0;
            }

            batch.draw(regions[c], x, y, regionWidth, regionHeight);

            x = x + widths[c];
        }
    }

    public void draw(SpriteBatch batch, int i, int x, int y, Align align) {
        draw(batch, "" + i, x, y, align);
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

            if (c >= widths.length) {
                c = 0;
            }

            w = w + widths[c];
        }
        return w;
    }
}
