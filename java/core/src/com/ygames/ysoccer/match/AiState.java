package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

class AiState extends State {

    protected final AiFsm fsm;
    protected final Ai ai;
    protected final Player player;

    AiState(AiFsm.Id id, AiFsm fsm) {
        this.id = id.ordinal();
        this.fsm = fsm;
        this.ai = fsm.ai;
        this.player = ai.player;

        fsm.addState(this);
    }
}
