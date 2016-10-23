package com.ysoccer.android.framework.gl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.ysoccer.android.framework.FileIO;
import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.impl.GLGraphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Texture {
    GLGraphics glGraphics;
    FileIO fileIO;
    String fileName;
    String paletteFile;
    List<RgbPair> rgbPairs;
    int textureId;
    int minFilter;
    int magFilter;
    public int width;
    public int height;

    public Texture(GLGame glGame, String fileName) {
        this.glGraphics = glGame.getGLGraphics();
        this.fileIO = glGame.getFileIO();
        this.fileName = fileName;
        load();
    }

    public Texture(GLGame glGame, String fileName, String paletteFile) {
        this.glGraphics = glGame.getGLGraphics();
        this.fileIO = glGame.getFileIO();
        this.fileName = fileName;
        this.paletteFile = paletteFile;
        load();
    }

    public Texture(GLGame glGame, String fileName, List<RgbPair> rgbPairs) {
        this.glGraphics = glGame.getGLGraphics();
        this.fileIO = glGame.getFileIO();
        this.fileName = fileName;
        this.rgbPairs = rgbPairs;
        load();
    }

    public void setPaletteFile(String paletteFile) {
        this.paletteFile = paletteFile;
    }

    private void load() {
        GL10 gl = glGraphics.getGL();
        int[] textureIds = new int[1];
        gl.glGenTextures(1, textureIds, 0);
        textureId = textureIds[0];

        InputStream in = null;
        try {
            in = fileIO.readAsset(fileName);
            Bitmap bitmap;
            if (this.paletteFile != null) {
                InputStream palette = fileIO.readAsset(paletteFile);
                bitmap = BitmapFactory.decodeStream(PngEditor.swapPalette(in, palette));
            } else if (this.rgbPairs != null) {
                bitmap = BitmapFactory.decodeStream(PngEditor.editPalette(in, rgbPairs));
            } else {
                bitmap = BitmapFactory.decodeStream(in);
            }
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            setFilters(GL10.GL_NEAREST, GL10.GL_NEAREST);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load texture '" + fileName + "'", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }
    }

    public void reload() {
        load();
        bind();
        setFilters(minFilter, magFilter);
        glGraphics.getGL().glBindTexture(GL10.GL_TEXTURE_2D, 0);
    }

    public void setFilters(int minFilter, int magFilter) {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        GL10 gl = glGraphics.getGL();
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
    }

    public void bind() {
        GL10 gl = glGraphics.getGL();
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
    }

    public void dispose() {
        GL10 gl = glGraphics.getGL();
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        int[] textureIds = {textureId};
        gl.glDeleteTextures(1, textureIds, 0);
    }
}
