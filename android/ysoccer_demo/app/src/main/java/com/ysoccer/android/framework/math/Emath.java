package com.ysoccer.android.framework.math;

public class Emath {

    public static float TO_RADIANS = (float) Math.PI / 180.0f;
    public static float TO_DEGREES = 180.0f / (float) Math.PI;

    static public boolean isIn(float x, float a, float b) {
        return (b > a) ? (a <= x) && (x <= b) : (b <= x) && (x <= a);
    }

    static public int bound(int value, int valueMin, int valueMax) {
        return Math.min(Math.max(value, valueMin), valueMax);
    }

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

    static public float dist(float x1, float y1, float x2, float y2) {
        return hypo(x2 - x1, y2 - y1);
    }

    public static int rotate(int value, int low, int high, int direction) {
        return ((value - low + (high - low + 1) + direction) % (high - low + 1))
                + low;
    }

    public static int slide(int value, int low, int high, int step) {
        return Math.min(Math.max(value + step, low), high);
    }

    public static float slide(float value, float low, float high, float step) {
        return Math.min(Math.max(value + step, low), high);
    }

    public static int sgn(float value) {
        return value > 0 ? 1 : (value < 0 ? -1 : 0);
    }

    public static int floor(double value) {
        return (int) Math.floor(value);
    }
}
