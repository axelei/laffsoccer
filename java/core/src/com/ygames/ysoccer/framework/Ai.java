package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.match.Player;

public class Ai extends InputDevice {

    Player player;

    public Ai(Player player) {
        this.player = player;
        setType(ID_COMPUTER);
    }

    public void read() {
    }
}
