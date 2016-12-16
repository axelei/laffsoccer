package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Coach implements Json.Serializable {

    enum Status {BENCH, STAND, DOWN, SPEAK, CALL}

    public String name;
    public String nationality;
    public Status status;
    public int timer;
    public float x;
    public int fmx;
    public int fmy;

    public Coach() {
        this.status = Status.BENCH;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.readFields(this, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("nationality", nationality);
    }

    public void update() {
        if (timer > 0) {
            timer -= 1;
        }

        switch (status) {
            case BENCH:
            case STAND:
                fmx = 0;
                fmy = 0;
                break;

            case DOWN:
                fmx = 3;
                fmy = 0;
                if (timer == 0) {
                    status = Status.STAND;
                }
                break;

            case SPEAK:
                fmx = (System.currentTimeMillis() % 400 > 200) ? 2 : 1;
                fmy = 0;
                if (timer == 0) {
                    status = Status.STAND;
                }
                break;

            case CALL:
                fmx = (System.currentTimeMillis() % 400 > 200) ? 5 : 4;
                fmy = 0;
                if (timer == 0) {
                    status = Status.STAND;
                }
                break;
        }
    }
}
