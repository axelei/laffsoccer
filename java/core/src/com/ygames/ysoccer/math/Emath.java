package com.ygames.ysoccer.math;

public class Emath {

    public static float TO_RADIANS = (float) Math.PI / 180.0f;
    public static float TO_DEGREES = 180.0f / (float) Math.PI;

    static public float aTan2(float y, float x) {
        return (float) (Math.atan2(y, x) * TO_DEGREES);
    }

    static public float cos(float a) {
        return (float) Math.cos(a * TO_RADIANS);
    }

    static public float sin(float a) {
        return (float) Math.sin(a * TO_RADIANS);
    }

    static public float hypo(float diffX, float diffY) {
        return (float) Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public static int rotate(int value, int low, int high, int direction) {
        return ((value - low + (high - low + 1) + direction) % (high - low + 1)) + low;
    }

    public static int slide(int value, int low, int high, int step) {
        return Math.min(Math.max(value + step, low), high);
    }

    public static int floor(double value) {
        return (int) Math.floor(value);
    }

    // random integer between min and max (included)
    public static int rand(int min, int max) {
        return ((int) Math.floor((max - min + 1) * Math.random())) - min;
    }
}