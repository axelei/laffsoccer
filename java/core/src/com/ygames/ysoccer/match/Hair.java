package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Color2;
import com.ygames.ysoccer.framework.Color3;

public class Hair {

    public enum Color {BLACK, BLOND, BROWN, RED, PLATINUM, GRAY, WHITE, PUNK_FUCHSIA, PUNK_BLOND}

    public final Color color;
    public final String style;

    public Hair(Color color, String style) {
        this.color = color;
        this.style = style;
    }

    public static Color3[] colors = {
            new Color3(0x2A2A2A, 0x1A1A1A, 0x090909), // BLACK
            new Color3(0xA2A022, 0x81801A, 0x605F11), // BLOND
            new Color3(0xA26422, 0x7B4C1A, 0x543411), // BROWN
            new Color3(0xE48B00, 0xB26D01, 0x7F4E01), // RED
            new Color3(0xFFFD7E, 0xE5E33F, 0xCAC800), // PLATINUM
            new Color3(0x7A7A7A, 0x4E4E4E, 0x222222), // GRAY
            new Color3(0xD5D5D5, 0xADADAD, 0x848484), // WHITE
            new Color3(0xFF00A8, 0x722F2F, 0x421A1A), // PUNK_FUCHSIA
            new Color3(0xFDFB35, 0x808202, 0x595A05), // PUNK_BLOND
    };

    public static Color2[][] shavedColors = {
            // PINK
            {
                    new Color2(0xA1785F, 0x7D5D4A), // BLACK
                    new Color2(0xBFC768, 0x999F54), // BLOND
                    new Color2(0xC79341, 0x94713B), // BROWN
                    new Color2(0xC79341, 0x94713B), // RED
                    new Color2(0xBFC768, 0x999F54), // PLATINUM
                    new Color2(0xA1785F, 0x7D5D4A), // GRAY
                    new Color2(0xD6BE97, 0xAB997C), // WHITE
                    new Color2(0xC79341, 0x94713B), // PUNK_FUCHSIA
                    new Color2(0xBFC768, 0x999F54), // PUNK_BLOND
            },
            // BLACK
            {
                    new Color2(0x423225, 0x312418), // BLACK
                    null, // BLOND
                    null, // BROWN
                    null, // RED
                    null, // PLATINUM
                    null, // GRAY
                    null, // WHITE
                    null, // PUNK_FUCHSIA
                    null, // PUNK_BLOND
            },
            // PALE
            {
                    new Color2(0xA1785F, 0x7D5D4A), // BLACK
                    new Color2(0xBFC768, 0x999F54), // BLOND
                    new Color2(0xC79341, 0x94713B), // BROWN
                    new Color2(0xC79341, 0x94713B), // RED
                    new Color2(0xBFC768, 0x999F54), // PLATINUM
                    new Color2(0xA1785F, 0x7D5D4A), // GRAY
                    new Color2(0xD6BE97, 0xAB997C), // WHITE
                    new Color2(0xC79341, 0x94713B), // PUNK_FUCHSIA
                    new Color2(0xBFC768, 0x999F54), // PUNK_BLOND
            },
            // ASIATIC
            {
                    new Color2(0xA1785F, 0x7D5D4A), // BLACK
                    new Color2(0xBFC768, 0x999F54), // BLOND
                    new Color2(0xC79341, 0x94713B), // BROWN
                    new Color2(0xC79341, 0x94713B), // RED
                    new Color2(0xBFC768, 0x999F54), // PLATINUM
                    new Color2(0xA1785F, 0x7D5D4A), // GRAY
                    new Color2(0xD6BE97, 0xAB997C), // WHITE
                    new Color2(0xC79341, 0x94713B), // PUNK_FUCHSIA
                    new Color2(0xBFC768, 0x999F54), // PUNK_BLOND
            },
            // ARAB
            {
                    new Color2(0x916847, 0x765331), // BLACK
                    null, // BLOND
                    null, // BROWN
                    null, // RED
                    null, // PLATINUM
                    null, // GRAY
                    null, // WHITE
                    null, // PUNK_FUCHSIA
                    null, // PUNK_BLOND
            },
            // MULATTO
            {
                    new Color2(0x916847, 0x765331), // BLACK
                    null, // BLOND
                    null, // BROWN
                    null, // RED
                    null, // PLATINUM
                    null, // GRAY
                    null, // WHITE
                    null, // PUNK_FUCHSIA
                    null, // PUNK_BLOND
            },
            // RED
            {
                    new Color2(0xA1785F, 0x7D5D4A), // BLACK
                    new Color2(0xBFC768, 0x999F54), // BLOND
                    new Color2(0xC79341, 0x94713B), // BROWN
                    new Color2(0xC79341, 0x94713B), // RED
                    new Color2(0xBFC768, 0x999F54), // PLATINUM
                    new Color2(0xA1785F, 0x7D5D4A), // GRAY
                    new Color2(0xD6BE97, 0xAB997C), // WHITE
                    new Color2(0xC79341, 0x94713B), // PUNK_FUCHSIA
                    new Color2(0xBFC768, 0x999F54), // PUNK_BLOND
            },
            // ALIEN
            {
                    null, // BLACK
                    null, // BLOND
                    null, // BROWN
                    null, // RED
                    null, // PLATINUM
                    null, // GRAY
                    null, // WHITE
                    null, // PUNK_FUCHSIA
                    null, // PUNK_BLOND
            },
            // YODA
            {
                    null, // BLACK
                    null, // BLOND
                    null, // BROWN
                    null, // RED
                    null, // PLATINUM
                    null, // GRAY
                    null, // WHITE
                    null, // PUNK_FUCHSIA
                    null, // PUNK_BLOND
            },
    };
}
