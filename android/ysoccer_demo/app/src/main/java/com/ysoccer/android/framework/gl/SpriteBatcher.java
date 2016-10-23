package com.ysoccer.android.framework.gl;

import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.framework.math.Vector2;

import javax.microedition.khronos.opengles.GL10;

public class SpriteBatcher {
    final float[] verticesBuffer;
    int bufferIndex;
    final Vertices vertices;
    int numSprites;
    GL10 gl;
    Texture texture;

    public SpriteBatcher(GLGraphics glGraphics, int maxSprites) {
        this.gl = glGraphics.getGL();
        this.verticesBuffer = new float[maxSprites * 4 * 4];
        this.vertices = new Vertices(maxSprites * 4, maxSprites * 6, false,
                true);
        this.bufferIndex = 0;
        this.numSprites = 0;

        short[] indices = new short[maxSprites * 6];
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

    public void beginBatch(Texture texture) {
        this.texture = texture;
        texture.bind();
        numSprites = 0;
        bufferIndex = 0;
    }

    public void endBatch() {
        if (numSprites == 0) {
            return;
        }
        vertices.setVertices(verticesBuffer, 0, bufferIndex);
        vertices.bind(gl);
        vertices.draw(gl, GL10.GL_TRIANGLES, 0, numSprites * 6);
        vertices.unbind(gl);
    }

    public void drawSprite(float x, float y, float width, float height,
                           Frame region) {
        // float halfWidth = width / 2;
        // float halfHeight = height / 2;
        // float x1 = x - halfWidth;
        // float y1 = y + halfHeight;
        // float x2 = x + halfWidth;
        // float y2 = y - halfHeight;
        float x1 = x;
        float y1 = y + height;
        float x2 = x + width;
        float y2 = y;

        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;

        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;

        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;

        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;

        numSprites++;
    }

    public void drawSprite(float x, float y, float w, float h, int fx, int fy,
                           int fw, int fh) {
        float x1 = x;
        float y1 = y + h;
        float x2 = x + w;
        float y2 = y;

        float u1 = ((float) fx) / texture.width;
        float v1 = ((float) fy) / texture.height;
        float u2 = ((float) fx + fw) / texture.width;
        float v2 = ((float) fy + fh) / texture.height;

        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = u1;
        verticesBuffer[bufferIndex++] = v2;

        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = u2;
        verticesBuffer[bufferIndex++] = v2;

        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = u2;
        verticesBuffer[bufferIndex++] = v1;

        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = u1;
        verticesBuffer[bufferIndex++] = v1;

        numSprites++;
    }

    public void drawSprite(float x, float y, float width, float height,
                           float angle, Frame region) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        float rad = angle * Vector2.TO_RADIANS;
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float x1 = -halfWidth * cos - (-halfHeight) * sin;
        float y1 = -halfWidth * sin + (-halfHeight) * cos;
        float x2 = halfWidth * cos - (-halfHeight) * sin;
        float y2 = halfWidth * sin + (-halfHeight) * cos;
        float x3 = halfWidth * cos - halfHeight * sin;
        float y3 = halfWidth * sin + halfHeight * cos;
        float x4 = -halfWidth * cos - halfHeight * sin;
        float y4 = -halfWidth * sin + halfHeight * cos;

        x1 += x;
        y1 += y;
        x2 += x;
        y2 += y;
        x3 += x;
        y3 += y;
        x4 += x;
        y4 += y;

        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;

        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;

        verticesBuffer[bufferIndex++] = x3;
        verticesBuffer[bufferIndex++] = y3;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;

        verticesBuffer[bufferIndex++] = x4;
        verticesBuffer[bufferIndex++] = y4;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;

        numSprites++;
    }
}
