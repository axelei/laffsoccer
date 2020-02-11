package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;

class AnimatedObjectSprite extends ObjectSprite {

    public TextureRegion[] anim;
    public int fps = 20;
    public Long startRender = System.currentTimeMillis();;
    public boolean oneShoot = false;

    AnimatedObjectSprite(GLGraphics glGraphics, TextureRegion[] anim, int x, int y) {
        super(glGraphics);
        this.anim = anim;
        this.x = x;
        this.y = y;
        this.priority = -1;
    }

    @Override
    public void draw(int subFrame) {

        long now = System.currentTimeMillis();

        int frame = (int) (((now - startRender) / 1000f ) * fps);

        if (oneShoot && frame >= anim.length) {
            alive = false;
            return;
        }

        frame %= anim.length;
        this.textureRegion = anim[frame];

        super.draw(subFrame);

    }

    public static AnimatedObjectSprite explosion(GLGraphics glGraphics, float x, float y) {
        AnimatedObjectSprite explosion = new AnimatedObjectSprite(glGraphics, Assets.explosion, (int) x, (int) y);
        explosion.oneShoot = true;
        return explosion;
    }


}

