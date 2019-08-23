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
    }

    public void read() {
        fsm.think();
    }
}
