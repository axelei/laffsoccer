package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import sun.misc.IOUtils;


public class Image extends TextureRegion {

    List<RgbPair> rgbPairs;

    public Image(String internalPath) {
        super(new Texture(internalPath));
        flip(false, true);
    }

    public Image(Pixmap pixmap) {
        super(new Texture(pixmap));
        flip(false, true);
    }

    public static Image loadImage(String internalPath, List<RgbPair> rgbPairs) {
        InputStream in = null;
        try {
            in = Gdx.files.internal(internalPath).read();

            byte[] bytes = IOUtils.readFully(PngEditor.editPalette(in, rgbPairs), -1, true);
            Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
            return new Image(pixmap);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load tactics", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }
    }

    public void dispose() {
        getTexture().dispose();
    }
}
