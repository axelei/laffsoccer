package com.ygames.ysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.io.IOException;
import java.io.InputStream;

public class Assets {

    public static Texture font14;
    public static int[] font14w = new int[1024];

    public static void load() {
        font14 = new Texture("images/font_14.png");
        loadFontWidths(font14w, "font_14.txt");
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
}
