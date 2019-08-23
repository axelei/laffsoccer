package com.ygames.ysoccer.match;

class PlayerState extends State {

    protected final Player player;
    protected final Ball ball;

    public PlayerState(PlayerFsm.Id id, Player player) {
        this.id = id.ordinal();
        this.player = player;
        this.ball = player.ball;
    }

    boolean checkId(PlayerFsm.Id id) {
        return (this.id == id.ordinal());
    }
}
