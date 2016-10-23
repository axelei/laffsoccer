package com.ysoccer.android.ysdemo.gui;

import com.ysoccer.android.framework.impl.GLGraphics;

public class Picture extends Widget {

    @Override
    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
        setFrame(w, h, 0, 0);
    }

    @Override
    public void render(GLGraphics glGraphics) {
        if (!isVisible) {
            return;
        }

        if (texture != null) {
            glGraphics.drawSubTextureRect(texture, x, y, frameW, frameH, frameX * frameW, frameY * frameH, frameW, frameH);
        }
    }

}
