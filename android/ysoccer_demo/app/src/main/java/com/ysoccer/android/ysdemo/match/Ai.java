package com.ysoccer.android.ysdemo.match;

public class Ai extends InputDevice {

    Player player;
    AiFsm fsm;

    public Ai(Player player) {
        this.player = player;
        setType(ID_COMPUTER);

        fsm = new AiFsm(this);
        fsm.setState(AiFsm.STATE_IDLE);
    }

    public void _read() {
        fsm.think();
    }

}
