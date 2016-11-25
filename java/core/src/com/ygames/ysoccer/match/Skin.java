package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Color3;

public class Skin {

    public enum Color {PINK, BLACK, PALE, ASIATIC, ARAB, MULATTO, RED, ALIEN, YODA}

    public static Color3 colors[] = {
            new Color3(0xF89150, 0xB85B26, 0x78311A), // PINK
            new Color3(0x613E21, 0x3C2611, 0x140A01), // BLACK
            new Color3(0xF7AE80, 0xB77651, 0x905440), // PALE
            new Color3(0xF8BF50, 0xB88D26, 0x83641A), // ASIATIC
            new Color3(0xD98B59, 0xB45C29, 0x7C3F1C), // ARAB
            new Color3(0xC97E41, 0x8D643B, 0x634629), // MULATTO
            new Color3(0xF37C58, 0xA9573E, 0x5E3123), // RED
            new Color3(0xD6D6D6, 0xBEBEBD, 0xA5A5A4), // ALIEN
            new Color3(0x34DF00, 0x0A9300, 0x075B01), // YODA
    };
}
