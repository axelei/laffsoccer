package com.ygames.ysoccer.competitions.tournament.groups;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;
import java.util.Collections;

public class Group implements Json.Serializable {

    private Groups groups;
    public ArrayList<Match> calendar;

    Group() {
        calendar = new ArrayList<Match>();
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        Match[] calendarArray = json.readValue("calendar", Match[].class, jsonData);
        Collections.addAll(calendar, calendarArray);
    }

    @Override
    public void write(Json json) {
        json.writeValue("calendar", calendar, Match[].class, Match.class);
    }
}
