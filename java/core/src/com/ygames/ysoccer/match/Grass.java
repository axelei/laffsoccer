package com.ygames.ysoccer.match;

class Grass {

    int lightShadow;
    int darkShadow;
    float friction;
    int bounce;

    Grass() {
    }

    Grass(int lightShadow, int darkShadow, int friction, int bounce) {
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
