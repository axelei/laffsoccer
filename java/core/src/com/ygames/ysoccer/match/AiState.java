package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

class AiState extends State {

    final Ai ai;
    protected final Player player;

    AiState(Ai ai) {
        this.ai = ai;
        this.player = ai.player;
    }
}
