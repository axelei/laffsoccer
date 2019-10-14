package com.ygames.ysoccer.framework;

public class EMath {

    private static final float TO_RADIANS = (float) Math.PI / 180.0f;
    private static final float TO_DEGREES = 180.0f / (float) Math.PI;

    public static boolean isIn(float v, float a, float b) {
        return (b > a) ? (a <= v) && (v <= b) : (b <= v) && (v <= a);
    }

    public static int bound(int value, int valueMin, int valueMax) {
        return Math.min(Math.max(value, valueMin), valueMax);
    }

    public static float aTan2(float y, float x) {
        return (float) (Math.atan2(y, x) * TO_DEGREES);
    }

    public static float cos(float a) {
        return (float) Math.cos(a * TO_RADIANS);
    }

    public static float sin(float a) {
        return (float) Math.sin(a * TO_RADIANS);
    }

    public static float tan(float a) {
        return (float) Math.tan(a * TO_RADIANS);
    }

    public static float hypo(float diffX, float diffY) {
        return (float) Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public static float dist(float ax, float ay, float bx, float by) {
        return hypo(bx - ax, by - ay);
    }

    public static float sqrt(float v) {
        return (float) Math.sqrt(v);
    }

    public static float pow(float v, float v1) {
        return (float) Math.pow(v, v1);
    }

    public static float angle(float x1, float y1, float x2, float y2) {
        return aTan2(y2 - y1, x2 - x1);
    }

    public static float angleDiff(float a1, float a2) {
        return Math.abs((((a1 - a2 + 540) % 360)) - 180);
    }

    public static float signedAngleDiff(float a1, float a2) {
        float an1 = (a1 + 360) % 360;
        float an2 = (a2 + 360) % 360;
        int sign = (an1 - an2 >= 0 && an1 - an2 <= 180) || (an1 - an2 <= -180 && an1 - an2 >= -360) ? 1 : -1;
        return sign * angleDiff(an1, an2);
    }

    public static int rotate(int value, int low, int high, int direction) {
        return ((value - low + (high - low + 1) + direction) % (high - low + 1)) + low;
    }

    public static <T extends Enum<T>> int rotate(Enum<T> value, Enum<T> low, Enum<T> high, int direction) {
        return ((value.ordinal() - low.ordinal() + (high.ordinal() - low.ordinal() + 1) + direction) % (high.ordinal() - low.ordinal() + 1)) + low.ordinal();
    }

    public static <T extends Enum<T>> T rotate(Enum<T> value, Class<T> c, int direction) {
        int values = c.getEnumConstants().length;
        return c.getEnumConstants()[(value.ordinal() + (values) + direction) % values];
    }

    public static int slide(int value, int low, int high, int step) {
        return Math.min(Math.max(value + step, low), high);
    }

    public static int sgn(float value) {
        return (int) Math.signum(value);
    }

    public static int floor(double value) {
        return (int) Math.floor(value);
    }

    public static int ceil(float value) {
        return (int) Math.ceil(value);
    }

    // random integer between min and max (included)
    public static int rand(int min, int max) {
        return min + Assets.random.nextInt(max + 1 - min);
    }

    public static <T> T randomPick(T[] elements) {
        return elements[Assets.random.nextInt(elements.length)];
    }

    public static <T extends Enum<T>> T randomPick(Class<T> c) {
        int values = c.getEnumConstants().length;
        return c.getEnumConstants()[Assets.random.nextInt(values)];
    }

    public static float roundBy(float value, float step) {
        return step * Math.round(value / step);
    }

    public static int min(int... values) {
        int min = values[0];
        for (int v : values) {
            if (v < min) min = v;
        }
        return min;
    }

    public static float max(float... values) {
        float max = values[0];
        for (float v : values) {
            if (v > max) max = v;
        }
        return max;
    }

    public static float clamp(float value, float limit1, float limit2) {
        float diff1 = value - limit1;
        float diff2 = value - limit2;
        return (Math.signum(diff1) != Math.signum(diff2)) ? value : (Math.abs(diff1) < Math.abs(diff2) ? limit1 : limit2);
    }

    public static <T> boolean isAmong(T value, T[] elements) {
        for (T element : elements) {
            if (value == element) return true;
        }
        return false;
    }
}
