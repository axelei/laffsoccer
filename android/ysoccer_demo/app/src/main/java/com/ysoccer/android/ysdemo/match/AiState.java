package com.ysoccer.android.ysdemo.match;

public class AiState extends State {

    protected final Ai ai;
    protected final Player player;

    public AiState(Ai ai) {
        this.ai = ai;
        this.player = ai.player;
    }

}
