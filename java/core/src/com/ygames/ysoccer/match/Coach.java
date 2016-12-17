package com.ygames.ysoccer.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import static com.ygames.ysoccer.match.Const.BENCH_X;

public class Coach implements Json.Serializable {

    enum Status {BENCH, STAND, CALL, LOOK_BENCH, SWAP}

    public String name;
    public String nationality;
    public Status status;
    public Team team;
    public int timer;
    public float x;
    public float y;
    public int fmx;

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
                x = BENCH_X;
                fmx = 0;
                break;

            case STAND:
                x = BENCH_X + 8;
                fmx = 0;
                break;

            case CALL:
                fmx = (System.currentTimeMillis() % 400 > 200) ? 2 : 1;
                if (timer == 0) {
                    status = Status.STAND;
                }
                break;

            case LOOK_BENCH:
                fmx = 3;
                if (timer == 0) {
                    status = Status.STAND;
                }
                break;

            case SWAP:
                fmx = (System.currentTimeMillis() % 400 > 200) ? 5 : 4;
                if (timer == 0) {
                    status = Status.STAND;
                }
                break;
        }
    }
}
