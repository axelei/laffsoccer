package com.ygames.ysoccer.framework;

public class GlColor {

    public enum Component {ALPHA, RED, GREEN, BLUE}

    private int ARGB;

    public GlColor(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public GlColor(int red, int green, int blue, int alpha) {
        ARGB = (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255);
    }

    public GlColor(int rgb) {
        this.ARGB = -16777216 | rgb;
    }

    // "#ABCDEF" format
    public GlColor(String hexString) {
        this(Integer.parseInt(hexString.substring(1), 16));
    }

    public int getAlpha() {
        return this.getRGB() >> 24 & 255;
    }

    public int getRed() {
        return this.getRGB() >> 16 & 255;
    }

    public int getGreen() {
        return this.getRGB() >> 8 & 255;
    }

    public int getBlue() {
        return this.getRGB() & 255;
    }

    public int getRGB() {
        return this.ARGB;
    }

    public String toHexString() {
        return String.format("#%02x%02x%02x", getRed(), getGreen(), getBlue());
    }

    public int getComponentValue(Component component) {
        switch (component) {
            case ALPHA:
                return getAlpha();
            case RED:
                return getRed();
            case GREEN:
                return getGreen();
            case BLUE:
                return getBlue();
            default:
                return -1;
        }
    }

    public static int argb(int r, int g, int b, int a) {
        return ((a << 24) | (r << 16) | (g << 8) | b);
    }

    public static int rgb(int r, int g, int b) {
        return ((r << 16) | (g << 8) | b);
    }

    public static int alpha(int rgb) {
        return ((rgb >> 24) & 0xFF);
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

    public boolean equals(Object color) {
        return color instanceof GlColor && ((GlColor) color).getRGB() == this.getRGB();
    }

    public static int brighter(int color) {
        int red = red(color);
        int green = green(color);
        int blue = blue(color);
        int alpha = alpha(color);
        byte b = 3;
        if (red == 0 && green == 0 && blue == 0) {
            return argb(red, green, blue, alpha);
        } else {
            if (red > 0 && red < b) {
                red = b;
            }

            if (green > 0 && green < b) {
                green = b;
            }

            if (blue > 0 && blue < b) {
                blue = b;
            }

            return argb(
                    Math.min((int) ((double) red / 0.7D), 255),
                    Math.min((int) ((double) green / 0.7D), 255),
                    Math.min((int) ((double) blue / 0.7D), 255),
                    alpha
            );
        }
    }

    public static int darker(int color) {
        return darker(color, 0.7D);
    }

    public static int darker(int color, double factor) {
        return argb(
                Math.max((int) ((double) red(color) * factor), 0),
                Math.max((int) ((double) green(color) * factor), 0),
                Math.max((int) ((double) blue(color) * factor), 0),
                alpha(color)
        );
    }

    private static float[] rgbToXyz(int rgb) {
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

        return new float[]{x, y, z};
    }

    private static float[] xyzToLab(float[] xyz) {
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

        return new float[]{l, a, b};
    }

    private static float labDifference(float[] lab1, float[] lab2) {
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
