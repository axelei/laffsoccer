package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;

import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.match.Goal.Type.OWN_GOAL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class Scorers {

    ArrayList<String>[] rows = new ArrayList[2];

    Scorers() {
        this.rows[HOME] = new ArrayList<String>();
        this.rows[AWAY] = new ArrayList<String>();
    }

    void build(List<Goal> goals) {

        rows[HOME].clear();
        rows[AWAY].clear();

        for (Goal goal : goals) {
            int tm = goal.player.team.index;
            if (goal.type == OWN_GOAL) {
                tm = 1 - tm;
            }

            if (!alreadyScored(goal, tm, goals)) {

                String s = goal.player.shirtName + " " + goal.minute;

                switch (goal.type) {
                    case OWN_GOAL:
                        s += "(" + Assets.strings.get("GOAL TYPE.OWN GOAL") + ")";
                        break;

                    case PENALTY:
                        s += "(" + Assets.strings.get("GOAL TYPE.PENALTY") + ")";
                        break;
                }
                s = addOtherGoals(goal, tm, s, goals);

                rows[tm].add(s);
            }
        }
    }

    private boolean alreadyScored(Goal goal, int team, List<Goal> goals) {
        for (Goal g : goals) {
            if (goal == g) {
                return false;
            }
            int tm = goal.player.team.index;
            if (goal.type == OWN_GOAL) {
                tm = 1 - tm;
            }
            if (tm == team && g.player == goal.player) {
                return true;
            }
        }
        return false;
    }

    private String addOtherGoals(Goal g, int team, String s, List<Goal> goals) {
        for (Goal goal : goals) {
            if (goal != g) {
                int tm = goal.player.team.index;
                if (goal.type == OWN_GOAL) {
                    tm = 1 - tm;
                }
                if (tm == team && goal.player == g.player) {
                    s += "," + goal.minute;
                    switch (goal.type) {
                        case OWN_GOAL:
                            s += "(" + Assets.strings.get("GOAL TYPE.OWN GOAL") + ")";
                            break;

                        case PENALTY:
                            s += "(" + Assets.strings.get("GOAL TYPE.PENALTY") + ")";
                            break;
                    }
                }
            }
        }
        return s;
    }
}
