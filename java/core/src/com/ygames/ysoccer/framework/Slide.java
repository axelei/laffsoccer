package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.Texture;

public class Slide {

    public Texture texture;
    public String text;
    public float duration;
    public float fade = 1.5F;

    public Slide() { super(); }

    public Slide(Texture texture, String text, float duration, float fade) {
        this.texture = texture;
        this.text = text;
        this.duration = duration;
        this.fade = fade;
    }
}
