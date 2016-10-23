package com.ysoccer.android.ysdemo.match;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Fsm {

    List<State> states;
    State state;

    public Fsm() {
        states = new ArrayList<State>();
    }

    public State getState() {
        return state;
    }

    public void addState(State s) {
        states.add(s);
    }

    public void think() {
        if (state == null) {
            return;
        }

        state.doActions();

        State new_state = state.checkConditions();
        if (new_state != null) {
            setState(new_state.id);
        }
    }

    public void setState(int id) {
        if (id == -1) {
            state = null;
            return;
        }

        if (state != null) {
            state.exitActions();
        }

        boolean found = false;
        for (int i = 0; i < states.size(); i++) {
            State s = states.get(i);
            if (s.checkId(id)) {
                state = s;
                state.entryActions();
                found = true;
            }
        }

        if (!found) {
            Log.d(this.getClass().getSimpleName(), "Warning! Cannot find state: " + id);
        }
    }

}
