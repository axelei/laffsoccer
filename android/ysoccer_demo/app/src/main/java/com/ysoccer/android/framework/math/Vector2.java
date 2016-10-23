package com.ysoccer.android.framework.math;

public class Vector2 {
    public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
    public static float TO_DEGREES = (1 / (float) Math.PI) * 180;
    public float x, y;
    public float v, a;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        setXY(x, y);
    }

    public Vector2(float x, float y, boolean polarCoordinates) {
        if (polarCoordinates) {
            setVA(x, y);
        } else {
            setXY(x, y);
        }
    }

    public Vector2(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void setX(float x) {
        this.x = x;
        updatePolar();
    }

    public void setY(float y) {
        this.y = y;
        updatePolar();
    }

    public void setV(float v) {
        this.v = v;
        updateCartesian();
    }

    public void setA(float a) {
        this.a = a;
        updateCartesian();
    }

    public void setXY(float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public void setVA(float v, float a) {
        this.setV(v);
        this.setA(a);
    }

    private void updatePolar() {
        v = (float) Math.sqrt(x * x + y * y);
        a = (float) Math.atan2(y, x) * TO_DEGREES;
    }

    private void updateCartesian() {
        float r = a * TO_RADIANS;
        x = (float) (v * Math.cos(r));
        y = (float) (v * Math.sin(r));
    }

    public Vector2 cpy() {
        return new Vector2(x, y);
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
        updatePolar();
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        updatePolar();
        return this;
    }

    public Vector2 add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
        updatePolar();
        return this;
    }

    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        updatePolar();
        return this;
    }

    public Vector2 sub(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
        updatePolar();
        return this;
    }

    public Vector2 mul(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        updatePolar();
        return this;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 nor() {
        float len = len();
        if (len != 0) {
            this.x /= len;
            this.y /= len;
        }
        return this;
    }

    public float angle() {
        float angle = (float) Math.atan2(y, x) * TO_DEGREES;
        if (angle < 0)
            angle += 360;
        return angle;
    }

    public Vector2 rotate(float angle) {
        float rad = angle * TO_RADIANS;
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    public float dist(Vector2 other) {
        float distX = this.x - other.x;
        float distY = this.y - other.y;
        return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public float dist(float x, float y) {
        float distX = this.x - x;
        float distY = this.y - y;
        return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public float distSquared(Vector2 other) {
        float distX = this.x - other.x;
        float distY = this.y - other.y;
        return distX * distX + distY * distY;
    }

    public float distSquared(float x, float y) {
        float distX = this.x - x;
        float distY = this.y - y;
        return distX * distX + distY * distY;
    }

}
