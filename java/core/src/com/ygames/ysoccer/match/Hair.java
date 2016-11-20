package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlColor3;

public class Hair {

    public enum Color {BLACK, BLOND, BROWN, RED, PLATINUM, GRAY, WHITE, PUNK_FUCHSIA, PUNK_BLOND}

    public static GlColor3[] colors = {
            new GlColor3(Color.BLACK.toString(), 0x2A2A2A, 0x1A1A1A, 0x090909),
            new GlColor3(Color.BLOND.toString(), 0xA2A022, 0x81801A, 0x605F11),
            new GlColor3(Color.BROWN.toString(), 0xA26422, 0x7B4C1A, 0x543411),
            new GlColor3(Color.RED.toString(), 0xE48B00, 0xB26D01, 0x7F4E01),
            new GlColor3(Color.PLATINUM.toString(), 0xFFFD7E, 0xE5E33F, 0xCAC800),
            new GlColor3(Color.GRAY.toString(), 0x7A7A7A, 0x4E4E4E, 0x222222),
            new GlColor3(Color.WHITE.toString(), 0xD5D5D5, 0xADADAD, 0x848484),
            new GlColor3(Color.PUNK_FUCHSIA.toString(), 0xFF00A8, 0x722F2F, 0x421A1A),
            new GlColor3(Color.PUNK_BLOND.toString(), 0xFDFB35, 0x808202, 0x595A05),
    };
}
