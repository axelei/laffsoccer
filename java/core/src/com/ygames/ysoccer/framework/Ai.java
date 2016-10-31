package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.match.AiFsm;
import com.ygames.ysoccer.match.Player;

public class Ai extends InputDevice {

    public Player player;
    public AiFsm fsm;

    public Ai(Player player) {
        super(Type.COMPUTER, 0);
        this.player = player;

        fsm = new AiFsm(this);
        fsm.setState(AiFsm.STATE_IDLE);
    }

    public void read() {
        fsm.think();
    }
}
