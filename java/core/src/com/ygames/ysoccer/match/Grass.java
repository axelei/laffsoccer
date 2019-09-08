package com.ygames.ysoccer.match;

public class Grass {

    public int lightShadow;
    public int darkShadow;
    float friction;
    float bounce;

    Grass() {
    }

    Grass(int lightShadow, int darkShadow, float friction, float bounce) {
        this.lightShadow = lightShadow;
        this.darkShadow = darkShadow;
        this.friction = friction;
        this.bounce = bounce;
    }

    void copy(Grass other) {
        this.lightShadow = other.lightShadow;
        this.darkShadow = other.darkShadow;
        this.friction = other.friction;
        this.bounce = other.bounce;
    }
}
