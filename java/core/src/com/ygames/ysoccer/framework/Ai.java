package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.match.AiFsm;
import com.ygames.ysoccer.match.Player;

public class Ai extends InputDevice {

    Player player;
    AiFsm fsm;

    public Ai(Player player) {
        this.player = player;
        setType(ID_COMPUTER);

        fsm = new AiFsm(this);
        fsm.setState(AiFsm.STATE_IDLE);
    }

    public void read() {
        fsm.think();
    }
}
