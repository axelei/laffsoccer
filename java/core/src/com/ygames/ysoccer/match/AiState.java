package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

class AiState extends State {

    final Ai ai;
    protected final Player player;

    AiState(AiFsm.Id id, Ai ai) {
        this.id = id.ordinal();
        this.ai = ai;
        this.player = ai.player;
    }
}
