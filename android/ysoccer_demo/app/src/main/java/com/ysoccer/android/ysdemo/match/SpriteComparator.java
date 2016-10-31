package com.ysoccer.android.ysdemo.match;

import java.util.Comparator;

class SpriteComparator implements Comparator<Sprite> {

    public int subframe;

    @Override
    public int compare(Sprite sprite1, Sprite sprite2) {
        return sprite1.getY(subframe) - sprite2.getY(subframe);
    }
}
