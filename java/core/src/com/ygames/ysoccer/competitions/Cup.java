package com.ygames.ysoccer.competitions;

public class Cup extends Competition {

    public int rounds;

    public Cup() {
        setRounds(4);
    }

    public Type getType() {
        return Type.CUP;
    }

    public void setRounds(int n) {
        rounds = n;
    }
}
