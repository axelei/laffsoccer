package com.ygames.ysoccer.match;

class PlayerState extends State {

    protected final PlayerFsm fsm;
    protected final Player player;
    protected final Ball ball;

    public PlayerState(PlayerFsm.Id id, PlayerFsm fsm, Player player) {
        this.id = id.ordinal();
        this.fsm = fsm;
        this.player = player;
        this.ball = player.ball;

        fsm.addState(this);
    }

    boolean checkId(PlayerFsm.Id id) {
        return (this.id == id.ordinal());
    }
}
