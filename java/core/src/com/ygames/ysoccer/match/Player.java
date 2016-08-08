package com.ygames.ysoccer.match;

import java.util.Comparator;

public class Player {
    String name;
    public String shirtName;
    int number;

    public int goals;

    public static class CompareByGoals implements Comparator<Player> {

        @Override
        public int compare(Player o1, Player o2) {
            return o2.goals - o1.goals;
        }
    }
}
