package com.ygames.ysoccer.match;

abstract class PlayerState extends State {

    protected final PlayerFsm fsm;
    protected final Player player;
    protected final Scene scene;
    protected final Ball ball;

    public PlayerState(PlayerFsm.Id id, PlayerFsm fsm) {
        this.id = id.ordinal();
        this.fsm = fsm;
        this.player = fsm.player;
        this.scene = player.scene;
        this.ball = player.ball;

        fsm.addState(this);
    }

    boolean checkId(PlayerFsm.Id id) {
        return (this.id == id.ordinal());
    }
}
