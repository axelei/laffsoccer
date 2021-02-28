package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.Texture;

public class Slide {

    public Texture texture;
    public String text;
    public int duration;

    public Slide(Texture texture, String text, int duration) {
        this.texture = texture;
        this.text = text;
        this.duration = duration;
    }
}
