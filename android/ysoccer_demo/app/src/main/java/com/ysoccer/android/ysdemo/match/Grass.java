package com.ysoccer.android.ysdemo.match;

public class Grass {

    public int lightShadow;
    public int darkShadow;
    float friction;
    int bounce;

    public Grass() {
    }

    public Grass(int lightShadow, int darkShadow, int friction, int bounce) {
        this.lightShadow = lightShadow;
        this.darkShadow = darkShadow;
        this.friction = friction;
        this.bounce = bounce;
    }

    public void copy(Grass other) {
        this.lightShadow = other.lightShadow;
        this.darkShadow = other.darkShadow;
        this.friction = other.friction;
        this.bounce = other.bounce;
    }

}
