package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.gl.Color;
import com.ysoccer.android.framework.gl.RgbPair;

import java.util.List;

public class Kit {

    public int style;
    int shirt1;
    int shirt2;
    int shorts;
    int socks;

    public Kit(int style, int shirt1, int shirt2, int shorts, int socks) {
        this.style = style;
        this.shirt1 = shirt1;
        this.shirt2 = shirt2;
        this.shorts = shorts;
        this.socks = socks;
    }

    public void addKitColors(List<RgbPair> rgbPairs) {

        // shirt1
        rgbPairs.add(new RgbPair(0xE20020, Kit.colors[shirt1][0]));
        rgbPairs.add(new RgbPair(0xBF001B, Kit.colors[shirt1][1]));
        rgbPairs.add(new RgbPair(0x9C0016, Kit.colors[shirt1][2]));
        rgbPairs.add(new RgbPair(0x790011, Kit.colors[shirt1][3]));

        // shirt2
        rgbPairs.add(new RgbPair(0x01FFC6, Kit.colors[shirt2][0]));
        rgbPairs.add(new RgbPair(0x00C79B, Kit.colors[shirt2][1]));
        rgbPairs.add(new RgbPair(0x008B6C, Kit.colors[shirt2][2]));
        rgbPairs.add(new RgbPair(0x006A52, Kit.colors[shirt2][3]));

        // shorts
        rgbPairs.add(new RgbPair(0xF6FF00, Kit.colors[shorts][0]));
        rgbPairs.add(new RgbPair(0xCDD400, Kit.colors[shorts][1]));
        rgbPairs.add(new RgbPair(0xA3A900, Kit.colors[shorts][2]));
        rgbPairs.add(new RgbPair(0x7A7E00, Kit.colors[shorts][3]));

        // socks
        rgbPairs.add(new RgbPair(0x0097EE, Kit.colors[socks][0]));
        rgbPairs.add(new RgbPair(0x0088D6, Kit.colors[socks][1]));
        rgbPairs.add(new RgbPair(0x0079BF, Kit.colors[socks][2]));
        rgbPairs.add(new RgbPair(0x006AA7, Kit.colors[socks][3]));

    }

    public static float colorDifference(Kit homeKit, Kit awayKit) {
        int homeColor1 = colors[homeKit.shirt1][0];
        int homeColor2 = colors[homeKit.shirt2][0];
        int awayColor1 = colors[awayKit.shirt1][0];
        int awayColor2 = colors[awayKit.shirt2][0];
        float differencePair = Color.difference(homeColor1, awayColor1)
                + Color.difference(homeColor2, awayColor2);
        float differenceSwap = Color.difference(homeColor1, awayColor2)
                + Color.difference(homeColor2, awayColor1);
        return Math.min(differencePair, differenceSwap);
    }

    public static final String[] styles = {"plain", "col_sleeves", "vertical",
            "horizontal", "check", "vert_halves", "strip", "spice", "armband",
            "large_strips", "diagonal", "band", "line", "two_stripes",
            "double_stripe", "big_check", "big_v", "cross", "diagonal_half",
            "vert_strip", "side_lines"};

    public static int colors[][] = {
            {0xAFB2B9, 0xA1A4AA, 0x93959B, 0x85878C}, // lgray
            {0xDFE8EB, 0xC9D1D4, 0xB3BABC, 0x9DA3A5}, // white
            {0x1E1E1E, 0x181818, 0x111111, 0x0B0B0B}, // black
            {0xF5732C, 0xD76527, 0xB95722, 0x9B491D}, // orange
            {0xF61C1C, 0xD31919, 0xAF1616, 0x8C1313}, // red
            {0x3033C1, 0x2A2CA8, 0x23268E, 0x1D1F75}, // blue
            {0x993333, 0x663333, 0x551111, 0x330000}, // brown
            {0x47BFEB, 0x41AFD8, 0x3CA0C4, 0x3690B1}, // lblue
            {0x30C52C, 0x2AAD27, 0x249621, 0x1E7E1C}, // green
            {0xE8EF2F, 0xCED42A, 0xB4B926, 0x9A9E21}, // yellow
            {0x5D2399, 0x521F87, 0x471A75, 0x3C1663}, // violet
            {0xF59EE1, 0xE291D0, 0xD085BF, 0xBD78AE}, // pink
            {0x0B7DD5, 0x0B70BE, 0x0A62A6, 0x0A558F}, // kombat blue
            {0x5083B8, 0x4876A5, 0x406892, 0x385B7F}, // steel
            {0x417039, 0x396232, 0x30532A, 0x284523}, // mil.green
            {0x2E247A, 0x261E66, 0x1F1852, 0x17123E}, // dark blue
            {0xA31212, 0x891010, 0x6E0D0D, 0x540B0B}, // garnet
            {0xD000EF, 0xB200CC, 0x9400AA, 0x760087}, // light violet
            {0x7BE2E3, 0x6DC9CA, 0x5FAFB0, 0x519697}, // sky blue
            {0x81FF11, 0x73E111, 0x65C410, 0x57A610}, // bright green
            {0xFFD200, 0xDFB700, 0xBE9D00, 0x9E8200}, // dark yellow
            {0x949494, 0x7E7E7E, 0x686868, 0x525252}, // dark gray
            {0xCAC47D, 0xB0AA6D, 0x95915C, 0x7B774C}, // gold
    };
}
