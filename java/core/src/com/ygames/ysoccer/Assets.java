package com.ygames.ysoccer;

import com.ygames.ysoccer.framework.Font;

public class Assets {

    public static Font font14;

    public static void load() {
        font14 = new Font(14);
        font14.load();
    }
}
