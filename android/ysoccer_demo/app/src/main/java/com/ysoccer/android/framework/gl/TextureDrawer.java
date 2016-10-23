package com.ysoccer.android.framework.gl;

import javax.microedition.khronos.opengles.GL10;

public class TextureDrawer {
    final float[] verticesBuffer;
    final Vertices vertices;

    public TextureDrawer() {
        this.verticesBuffer = new float[4 * 4];
        this.vertices = new Vertices(4, 6, false, true);

        short[] indices = new short[6];
        int len = indices.length;
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i + 0] = (short) (j + 0);
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);
            indices[i + 3] = (short) (j + 2);
            indices[i + 4] = (short) (j + 3);
            indices[i + 5] = (short) (j + 0);
        }
        vertices.setIndices(indices, 0, indices.length);
    }

    public void drawTexture(Texture texture, float x, float y) {
        drawTextureRect(texture, x, y, texture.width, texture.height);
    }

    public void drawTextureRect(Texture texture, float x, float y, float w, float h) {
        GL10 gl = texture.glGraphics.getGL();

        texture.bind();
        float x1 = x;
        float y1 = y + h;
        float x2 = x + w;
        float y2 = y;

        int i = 0;

        verticesBuffer[i++] = x1;
        verticesBuffer[i++] = y1;
        verticesBuffer[i++] = 0;// region.u1;
        verticesBuffer[i++] = 1;// region.v2;

        verticesBuffer[i++] = x2;
        verticesBuffer[i++] = y1;
        verticesBuffer[i++] = 1;// region.u2;
        verticesBuffer[i++] = 1;// region.v2;

        verticesBuffer[i++] = x2;
        verticesBuffer[i++] = y2;
        verticesBuffer[i++] = 1;// region.u2;
        verticesBuffer[i++] = 0;// region.v1;

        verticesBuffer[i++] = x1;
        verticesBuffer[i++] = y2;
        verticesBuffer[i++] = 0;// region.u1;
        verticesBuffer[i++] = 0;// region.v1;

        vertices.setVertices(verticesBuffer, 0, i);
        vertices.bind(gl);
        vertices.draw(gl, GL10.GL_TRIANGLES, 0, 6);
        vertices.unbind(gl);
    }

    public void drawTextureRect(Texture texture, float x, float y, float fx, float fy,
                                float fw, float fh) {
        drawSubTextureRect(texture, x, y, fw, fh, fx, fy, fw, fh);
    }

    public void drawSubTextureRect(Texture texture, float x, float y, float w, float h, float fx, float fy,
                                   float fw, float fh) {
        GL10 gl = texture.glGraphics.getGL();

        texture.bind();
        float x1 = x;
        float y1 = y + h;
        float x2 = x + w;
        float y2 = y;

        int i = 0;

        float u1 = ((float) fx) / texture.width;
        float v1 = ((float) fy) / texture.height;
        float u2 = ((float) fx + fw) / texture.width;
        float v2 = ((float) fy + fh) / texture.height;

        verticesBuffer[i++] = x1;
        verticesBuffer[i++] = y1;
        verticesBuffer[i++] = u1;
        verticesBuffer[i++] = v2;

        verticesBuffer[i++] = x2;
        verticesBuffer[i++] = y1;
        verticesBuffer[i++] = u2;
        verticesBuffer[i++] = v2;

        verticesBuffer[i++] = x2;
        verticesBuffer[i++] = y2;
        verticesBuffer[i++] = u2;
        verticesBuffer[i++] = v1;

        verticesBuffer[i++] = x1;
        verticesBuffer[i++] = y2;
        verticesBuffer[i++] = u1;
        verticesBuffer[i++] = v1;

        vertices.setVertices(verticesBuffer, 0, i);
        vertices.bind(gl);
        vertices.draw(gl, GL10.GL_TRIANGLES, 0, 6);
        vertices.unbind(gl);
    }
}
