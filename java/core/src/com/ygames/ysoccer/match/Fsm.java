package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

class Fsm {

    private List<State> states;
    protected State state;

    Fsm() {
        states = new ArrayList<>();
    }

    public State getState() {
        return state;
    }

    void addState(State s) {
        states.add(s);
    }

    public void think() {
        if (state == null) {
            return;
        }

        state.doActions();

        State newState = state.checkConditions();
        if (newState != null) {
            setState(newState.id);
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
            Gdx.app.log(this.getClass().getSimpleName(), "Warning! Cannot find state: " + id);
        }
    }
}
