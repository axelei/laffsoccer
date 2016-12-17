package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;

class CoachSprite extends Sprite {

    private Coach coach;

    CoachSprite(GLGraphics glGraphics, Coach coach) {
        super(glGraphics);
        this.coach = coach;
    }

    @Override
    public void draw(int subframe) {
        glGraphics.batch.draw(Assets.coach[coach.team.index][coach.fmx], coach.x - 7, coach.y - 25);
    }
}

