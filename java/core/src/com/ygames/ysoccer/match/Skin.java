package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Color3;

public class Skin {

    public enum Color {PINK, BLACK, PALE, ASIATIC, ARAB, MULATTO, RED, ALIEN, YODA}

    public static Color3 colors[] = {
            new Color3(Color.PINK.toString(), 0xF89150, 0xB85B26, 0x78311A),
            new Color3(Color.BLACK.toString(), 0x613E21, 0x3C2611, 0x140A01),
            new Color3(Color.PALE.toString(), 0xF7AE80, 0xB77651, 0x905440),
            new Color3(Color.ASIATIC.toString(), 0xF8BF50, 0xB88D26, 0x83641A),
            new Color3(Color.ARAB.toString(), 0xD98B59, 0xB45C29, 0x7C3F1C),
            new Color3(Color.MULATTO.toString(), 0xC97E41, 0x8D643B, 0x634629),
            new Color3(Color.RED.toString(), 0xF37C58, 0xA9573E, 0x5E3123),
            new Color3(Color.ALIEN.toString(), 0xD6D6D6, 0xBEBEBD, 0xA5A5A4),
            new Color3(Color.YODA.toString(), 0x34DF00, 0x0A9300, 0x075B01)
    };
}
