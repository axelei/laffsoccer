package com.ygames.ysoccer.math;

public class Emath {

    public static float TO_DEGREES = 180.0f / (float) Math.PI;

    static public float aTan2(float y, float x) {
        return (float) (Math.atan2(y, x) * TO_DEGREES);
    }

    static public float hypo(float diffX, float diffY) {
        return (float) Math.sqrt(diffX * diffX + diffY * diffY);
    }
}