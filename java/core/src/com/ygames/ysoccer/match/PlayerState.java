package com.ygames.ysoccer.match;

class PlayerState extends State {

    protected final Player player;
    protected final Ball ball;

    public PlayerState(Player player) {
        this.player = player;
        this.ball = player.ball;
    }
}
