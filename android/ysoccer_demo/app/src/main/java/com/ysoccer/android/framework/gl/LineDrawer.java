package com.ysoccer.android.framework.gl;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class LineDrawer {
    final float[] verticesBuffer;
    final Vertices vertices;

    public LineDrawer() {
        this.verticesBuffer = new float[4 * 2];
        this.vertices = new Vertices(4, 6, false, false);

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

    public void drawLine(GL10 gl, float x, float y, float w, float h) {

        float x1 = x;
        float y1 = y + h;
        float x2 = x + w;
        float y2 = y;

        int i = 0;

        verticesBuffer[i++] = x1;
        verticesBuffer[i++] = y1;

        verticesBuffer[i++] = x2;
        verticesBuffer[i++] = y1;

        verticesBuffer[i++] = x2;
        verticesBuffer[i++] = y2;

        verticesBuffer[i++] = x1;
        verticesBuffer[i++] = y2;

        vertices.setVertices(verticesBuffer, 0, i);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        vertices.bind(gl);
        vertices.draw(gl, GL11.GL_LINE_LOOP, 0, 6);
        vertices.unbind(gl);
    }

}
