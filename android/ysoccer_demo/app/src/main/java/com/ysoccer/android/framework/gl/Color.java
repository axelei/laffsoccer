package com.ysoccer.android.framework.gl;

public class Color {

    public static int rgb(int r, int g, int b) {
        return ((r << 16) | (g << 8) | b);
    }

    public static int red(int rgb) {
        return ((rgb >> 16) & 0xFF);
    }

    public static int green(int rgb) {
        return ((rgb >> 8) & 0xFF);
    }

    public static int blue(int rgb) {
        return (rgb & 0xFF);
    }

    // set alpha value in a 32-bit argb color
    public static int setAlpha(int rgb, int a) {
        return (rgb & 0xFFFFFF) | (a << 24);
    }

    public static float[] rgb2cmy(int[] rgb) {
        // RGB values from 0 to 255
        // CMY results from 0 to 1

        float c = 1 - (rgb[0] / 255.0f);
        float m = 1 - (rgb[1] / 255.0f);
        float y = 1 - (rgb[2] / 255.0f);
        float[] cmy = {c, m, y};
        return cmy;
    }

    public static float[] cmy2cmyk(float[] cmy) {
        // CMYK and CMY values from 0 to 1

        float c, m, y;
        float k = 1;

        if (cmy[0] < k)
            k = cmy[0];
        if (cmy[1] < k)
            k = cmy[1];
        if (cmy[2] < k)
            k = cmy[2];

        if (k == 1) { // Black
            c = 0;
            m = 0;
            y = 0;
        } else {
            c = (cmy[0] - k) / (1 - k);
            m = (cmy[1] - k) / (1 - k);
            y = (cmy[2] - k) / (1 - k);
        }
        float[] cmyk = {c, m, y, k};
        return cmyk;
    }

    public static float[] cmyk2cmy(float[] cmyk) {
        // CMYK and CMY values from 0 to 1

        float c = (cmyk[0] * (1 - cmyk[3]) + cmyk[3]);
        float m = (cmyk[1] * (1 - cmyk[3]) + cmyk[3]);
        float y = (cmyk[2] * (1 - cmyk[3]) + cmyk[3]);

        float[] cmy = {c, m, y};
        return cmy;
    }

    public static int[] cmy2rgb(float[] cmy) {
        // CMY values from 0 to 1
        // RGB results from 0 to 255

        int r = (int) ((1 - cmy[0]) * 255);
        int g = (int) ((1 - cmy[1]) * 255);
        int b = (int) ((1 - cmy[2]) * 255);
        int[] rgb = {r, g, b};
        return rgb;
    }

    public static int blackMultiply(int rgb, float m) {
        int[] rgb0 = {Color.red(rgb), Color.green(rgb), Color.blue(rgb)};
        float[] cmyk = cmy2cmyk(rgb2cmy(rgb0));
        cmyk[3] *= m;
        int[] rgb1 = cmy2rgb(cmyk2cmy(cmyk));
        return Color.rgb(rgb1[0], rgb1[1], rgb1[2]);
    }

    public static float[] rgbToXyz(int rgb) {
        float r = red(rgb) / 255.0f;
        float g = green(rgb) / 255.0f;
        float b = blue(rgb) / 255.0f;

        if (r > 0.04045f) {
            r = (float) Math.pow((r + 0.055f) / 1.055f, 2.4f);
        } else {
            r = r / 12.92f;
        }

        if (g > 0.04045f) {
            g = (float) Math.pow((g + 0.055f) / 1.055f, 2.4f);
        } else {
            g = g / 12.92f;
        }

        if (b > 0.04045) {
            b = (float) Math.pow((b + 0.055f) / 1.055f, 2.4f);
        } else {
            b = b / 12.92f;
        }

        float x = 100.0f * (r * 0.4124f + g * 0.3576f + b * 0.1805f);
        float y = 100.0f * (r * 0.2126f + g * 0.7152f + b * 0.0722f);
        float z = 100.0f * (r * 0.0193f + g * 0.1192f + b * 0.9505f);

        float[] xyz = {x, y, z};
        return xyz;
    }

    public static float[] xyzToLab(float[] xyz) {
        float x = xyz[0] / 95.047f;
        float y = xyz[1] / 100.0f;
        float z = xyz[2] / 108.883f;

        if (x > 0.008856f) {
            x = (float) Math.pow(x, 1.0f / 3.0f);
        } else {
            x = 7.787f * x + 4.0f / 29.0f;
        }

        if (y > 0.008856f) {
            y = (float) Math.pow(y, 1.0f / 3.0f);
        } else {
            y = 7.787f * y + 4.0f / 29.0f;
        }

        if (z > 0.008856) {
            z = (float) Math.pow(z, 1.0f / 3.0f);
        } else {
            z = 7.787f * z + 4.0f / 29.0f;
        }

        float l = 116.0f * y - 16.0f;
        float a = 500.0f * (x - y);
        float b = 200.0f * (y - z);

        float[] lab = {l, a, b};
        return lab;
    }

    public static float labDifference(float[] lab1, float[] lab2) {
        float c1 = (float) Math.sqrt(lab1[1] * lab1[1] + lab1[2] * lab1[2]);
        float c2 = (float) Math.sqrt(lab2[1] * lab2[1] + lab2[2] * lab2[2]);

        float dc = c1 - c2;
        float dl = lab1[0] - lab2[0];
        float da = lab1[1] - lab2[1];
        float db = lab1[2] - lab2[2];

        float dh = (float) Math.sqrt(da * da + db * db - dc * dc);
        float a = dl;
        float b = dc / (1 + 0.045f * c1);
        float c = dh / (1 + 0.015f * c1);

        return (float) Math.sqrt(a * a + b * b + c * c);
    }

    public static float difference(int rgb1, int rgb2) {
        return labDifference(xyzToLab(rgbToXyz(rgb1)), xyzToLab(rgbToXyz(rgb2)));
    }

}
