package com.ysoccer.android.framework.gl;

public class Frame {
    public final float x, y;
    public final float width, height;
    public final float u1, v1;
    public final float u2, v2;
    public final Texture texture;

    public Frame(Texture texture, float x, float y, float width, float height) {
        this.x = width;
        this.y = height;
        this.width = width;
        this.height = height;
        this.u1 = x / texture.width;
        this.v1 = y / texture.height;
        this.u2 = this.u1 + width / texture.width;
        this.v2 = this.v1 + height / texture.height;
        this.texture = texture;
    }
}
